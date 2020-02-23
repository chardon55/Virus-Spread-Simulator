import java.util.Random
import kotlin.math.pow
import kotlin.math.sqrt

/**
 * @ClassName:
 * @Description:
 * @Author: cnctemaR
 * @Date: 2020/2/7 2:43
 * */

class Person(private val city: City, override var x: Int, override var y: Int) : Point(x, y) {
    /**
     * 正态分布N(mu,sigma)随机位移目标位置
     */
    private val targetXU: Double = MathUtil.stdGaussian(100.0, x.toDouble()) //x方向的均值mu
    private val targetYU: Double = MathUtil.stdGaussian(100.0, y.toDouble()) //y方向的均值mu
    private val targetSig: Double = 50.0 //方差sigma
    private var moveTarget: MoveTarget? = null

    private val sig = 1 //人群流动意愿影响系数：正态分布方差sigma
    private val SAFE_DIST = Constants.SAFE_DIST//安全距离
    var useBed: Bed? = null

    var state = State.NORMAL
    var infectedTime = 0//感染时刻
    var confirmedTime = 0//确诊时刻
    var dieMoment = 0//死亡时刻，为0代表未确定

    enum class State{
        NORMAL, SUSPECTED, SHADOW, CONFIRMED, FREEZE, DEATH
    }

    /**
     * 流动意愿标准化
     *
     * 根据标准正态分布生成随机人口流动意愿
     *
     * 流动意愿标准化后判断是在0的左边还是右边从而决定是否流动。
     *
     * 设X随机变量为服从正态分布，sigma是影响分布形态的系数，从而影响整体人群流动意愿分布
     * u值决定正态分布的中轴是让更多人群偏向希望流动或者希望懒惰。
     *
     * value的推导：
     * StdX = (X-u)/sigma
     * X = sigma * StdX + u
     *
     * @return
     */
    fun wantMove(): Boolean = MathUtil.stdGaussian(sig.toDouble(), Constants.u) > 0

    fun isInfected(): Boolean = state >= State.SHADOW

    fun beInfected() {
        state = State.SHADOW
        infectedTime = MyPanel.worldTime
    }
    /**
     * 住院
     */
    private fun freeze() {
        state = State.FREEZE
    }

    /**
     * 计算两点之间的直线距离
     *
     * @param person
     * @return
     */
    fun distance(person: Person): Double = sqrt((x - person.x).toDouble().pow(2.0) + (y - person.y).toDouble().pow(2.0))

    /**
     * 不同状态下的单个人实例运动行为
     */
    private fun action() {
        // 如果处于隔离或死亡状态，或者不想移动，则无法行动
        if (state == State.FREEZE || state == State.DEATH || !wantMove()) {
            return
        }
        //存在流动意愿的，将进行流动，流动位移仍然遵循标准正态分布
        if (moveTarget == null || moveTarget!!.arrived) {
            //在想要移动并且没有目标时，将自身移动目标设置为随机生成的符合正态分布的目标点
            //产生N(a,b)的数：Math.sqrt(b)*random.nextGaussian()+a
            val targetX = MathUtil.stdGaussian(targetSig, targetXU)
            val targetY = MathUtil.stdGaussian(targetSig, targetYU)
            moveTarget = MoveTarget(targetX.toInt(), targetY.toInt())

        }

        //计算运动位移
        val dX = moveTarget!!.x - x
        val dY = moveTarget!!.y - y

        val length = sqrt(dX.toDouble().pow(2.0) + dY.toDouble().pow(2.0))//与目标点的距离

        if (length < 1) {
            //判断是否到达目标点
            moveTarget!!.arrived = true
            return
        }

        var udX = (dX / length).toInt()//x轴dX为位移量，符号为沿x轴前进方向, 即udX为X方向表示量
        if (udX == 0 && dX != 0)
            udX = if (dX > 0) 1 else -1


        var udY = (dY / length).toInt()//y轴dY为位移量，符号为沿x轴前进方向，即udY为Y方向表示量
        //FIXED: 修正一处错误
        if (udY == 0 && dY != 0)
            udY = if (dY > 0) 1 else -1

        //横向运动边界
        if (x > Constants.CITY_WIDTH || x < 0) {
            moveTarget = null
            if (udX > 0)
                udX = -udX
        }
        //纵向运动边界
        if (y > Constants.CITY_HEIGHT || y < 0) {
            moveTarget = null
            if (udY > 0)
                udY = -udY
        }
        moveTo(udX, udY)

    }


    /**
     * 对各种状态的人进行不同的处理，更新发布市民健康状态
     */
    fun update() {
        //@TODO 找时间改为状态机

        if(state == State.FREEZE) {
            val success: Float = Random().nextFloat()
            if(success < Constants.RECOVERY_RATE) {
                state = State.NORMAL

                val rand = Random()
                var x: Int = (100 * rand.nextGaussian() + city.centerX).toInt()
                var y: Int = (100 * rand.nextGaussian() + city.centerY).toInt()
                if (x > Constants.CITY_WIDTH) {
                    x = Constants.CITY_WIDTH;
                }
                if (x < -Constants.CITY_WIDTH) {
                    x = -Constants.CITY_WIDTH;
                }
                if (y > Constants.CITY_HEIGHT) {
                    y = Constants.CITY_HEIGHT;
                }
                if (y < -Constants.CITY_HEIGHT) {
                    y = -Constants.CITY_HEIGHT;
                }

                this.x = x;
                this.y = y;
                Hospital.hospital.returnBed(useBed)
                useBed = null
                PersonPool.personPool.RECOVERED++
            }
        }



        if (state == State.CONFIRMED && MyPanel.worldTime - confirmedTime >= Constants.HOSPITAL_RECEIVE_TIME) {
            //如果患者已经确诊，且（世界时刻-确诊时刻）大于医院响应时间，即医院准备好病床了，可以抬走了
            val bed = Hospital.hospital.pickBed()//查找空床位
            if (bed != null) {
                //安置病人
                useBed = bed
                freeze()
                x = bed.x
                y = bed.y
                bed.isEmpty = false
            }
//            else {
//                //没有床位了，报告需求床位数
//            }
        }

        //处理病死者
        if ((state == State.CONFIRMED || state == State.FREEZE) && MyPanel.worldTime >= dieMoment && dieMoment > 0) {
            val fatal: Float = Random().nextFloat()
            if(fatal < Constants.FATALITY_RATE) {
                state = State.DEATH//患者死亡
                Hospital.hospital.returnBed(useBed)//归还床位
                useBed = null
                if (Hospital.hospital.inHospital(x, y)) {
                    x = -10
                    y = -10
                }
            }
        }

        //增加一个正态分布用于潜伏期内随机发病时间
        val stdRnShadowTime = MathUtil.stdGaussian(25.0, Constants.SHADOW_TIME / 2)
        //处理发病的潜伏期感染者
        if (MyPanel.worldTime - infectedTime > stdRnShadowTime && state == State.SHADOW) {
            state = State.CONFIRMED//潜伏者发病
            confirmedTime = MyPanel.worldTime//刷新时间
        }
        //处理未隔离者的移动问题
        action()
        //处理健康人被感染的问题
        val people = PersonPool.personPool.personList
        if (state >= State.SHADOW) {
            return
        }
        //通过一个随机幸运值和安全距离决定感染其他人
        for (person in people) {
            if (person.state != State.SHADOW && person.state != State.CONFIRMED) {
                continue
            }
            val random = Random().nextFloat()
            if (random < Constants.BROAD_RATE && distance(person) < SAFE_DIST && person != this) {
                this.beInfected()
//                break
            }
        }
    }
}