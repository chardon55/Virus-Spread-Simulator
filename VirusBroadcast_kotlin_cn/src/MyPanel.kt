import Person.State
import kotlin.collections.HashMap;

import javax.swing.*
import java.awt.*
import java.util.*
import java.util.Timer

/**
 * 主面板。
 *
 * @ClassName: MyPanel
 * @Description: 主面板
 * @author: Bruce Young
 * @date: 2020年02月02日 17:03
 */
class MyPanel : JPanel(), Runnable {
    private val timer = Timer()
    companion object {
        var worldTime = 0//世界时间
//        private val colorMap: Map<State, Color> = HashMap()
    }
    init {
        this.background = Color(0x444444)
    }

    internal inner class MyTimerTask : TimerTask() {
        override fun run() {
            this@MyPanel.repaint()
            worldTime++
        }
    }
    override fun run() {
        timer.schedule(MyTimerTask(), 0, 100)//启动世界计时器，时间开始流动（突然脑补DIO台词：時は停た）
    }

    override fun paint(g: Graphics?) {
        super.paint(g)
        g!!.color = Color(0x00ff00)//设置医院边界颜色
        //绘制医院边界
        g.drawRect(Hospital.hospital.x, Hospital.hospital.y,
                Hospital.hospital.width, Hospital.hospital.height)
        g.font = Font("等线", Font.BOLD, 16)
        g.color = Color(0x00ff00)
        g.drawString("医院", Hospital.hospital.x + Hospital.hospital.width / 4, Hospital.hospital.y - 16)
        //绘制代表人类的圆点
        val people = PersonPool.personPool.personList
//        if (people.size == 0) return
        for (person in people) {
            when (person.state) {
                State.NORMAL -> g.color = Color(0xdddddd) //健康人
                State.SHADOW -> g.color = Color(0xffee00) //潜伏期感染者
                State.CONFIRMED -> g.color = Color(0xff0000) //确诊患者
                State.FREEZE -> g.color = Color(0x48FFFC)    //已隔离者
                State.DEATH -> g.color = Color(0x000000)     //死亡患者
                else -> { }
            }
            person.update()//对各种状态的市民进行不同的处理
            g.fillOval(person.x, person.y, 3, 3)
        }

        val captionStartOffsetX = 700 + Hospital.hospital.width + 40
        var captionStartOffsetY = 40
        val captionSize = 24
        captionStartOffsetY -= captionSize

        //显示数据信息
        g.color = Color(0xffffff)
        captionStartOffsetY += captionSize
        g.drawString("世界时间（天）：" + (worldTime / 10.0).toInt(), captionStartOffsetX, captionStartOffsetY)

        g.color = Color.WHITE
        captionStartOffsetY += captionSize
        g.drawString("城市总人数：" + Constants.CITY_PERSON_SIZE, captionStartOffsetX, captionStartOffsetY)

        g.color = Color(0xdddddd)
        captionStartOffsetY += captionSize
        g.drawString("健康者人数：" + PersonPool.personPool.getPeopleSize(State.NORMAL), captionStartOffsetX, captionStartOffsetY)

        g.color = Color(0xffee00)
        captionStartOffsetY += captionSize
        g.drawString("潜伏期人数：" + PersonPool.personPool.getPeopleSize(State.SHADOW), captionStartOffsetX, captionStartOffsetY)

        g.color = Color(0xff0000)
        captionStartOffsetY += captionSize
        g.drawString("发病者人数：" + PersonPool.personPool.getPeopleSize(State.CONFIRMED), captionStartOffsetX, captionStartOffsetY)

        g.color = Color(0x48FFFC)
        captionStartOffsetY += captionSize
        g.drawString("已隔离人数：" + PersonPool.personPool.getPeopleSize(State.FREEZE), captionStartOffsetX, captionStartOffsetY)

        g.color = Color(0x00ff00)
        captionStartOffsetY += captionSize
        g.drawString("空余病床：" + Math.max(Constants.BED_COUNT - PersonPool.personPool.getPeopleSize(State.FREEZE), 0), captionStartOffsetX, captionStartOffsetY)

        g.color = Color(0xE39476)
        // 急需病床数量 = 确诊发病者数量 + 已隔离住院数量 - 床位总数
        val needBeds = PersonPool.personPool.getPeopleSize(State.CONFIRMED) + PersonPool.personPool.getPeopleSize(State.FREEZE) - Constants.BED_COUNT
        captionStartOffsetY += captionSize
        g.drawString("急需病床：" + if (needBeds > 0) needBeds else 0, captionStartOffsetX, captionStartOffsetY)

        g.color = Color(0xccbbcc)
        captionStartOffsetY += captionSize
        g.drawString("病死人数：" + PersonPool.personPool.getPeopleSize(State.DEATH), captionStartOffsetX, captionStartOffsetY)
    }


}