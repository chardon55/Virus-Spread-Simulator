import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JButton;
import javax.swing.JPanel;

/**
 * 主面板。
 *
 * @ClassName: MyPanel
 * @Description: 主面板
 * @author: Bruce Young
 * @date: 2020年02月02日 17:03
 */
public class MyPanel extends JPanel implements Runnable {

    /**
     *
     */
    private static final long serialVersionUID = -5865496544871400953L;

    public MyPanel() {
        super();
        this.setBackground(new Color(0x444444));
    }

    private static Map<Person.State, Color> colorMap = new HashMap<>();

    static {
        colorMap.put(Person.State.NORMAL, new Color(0xdddddd));
        colorMap.put(Person.State.SHADOW, new Color(0xffee00));
        colorMap.put(Person.State.CONFIRMED, new Color(0xff0000));
        colorMap.put(Person.State.FREEZE, new Color(0x48fffc));
        colorMap.put(Person.State.DEATH, new Color(0x000000));
    }

    private int normalCount;
    private int incubationCount;
    private int sickCount;
    private int isolatedCount;
    private int toll;

    // 更新统计数据线程
    private final Thread updateThread = new Thread(new Runnable() {
        @Override
        public synchronized void run() {
            while (true) {
                toll = PersonPool.getInstance().getPeopleSize(Person.State.DEATH);
                isolatedCount = PersonPool.getInstance().getPeopleSize(Person.State.FREEZE);
                sickCount = PersonPool.getInstance().getPeopleSize(Person.State.CONFIRMED);
                incubationCount = PersonPool.getInstance().getPeopleSize(Person.State.SHADOW);
                normalCount = PersonPool.getInstance().getPeopleSize(Person.State.NORMAL);
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    // e.printStackTrace();
                }
            }
        }
    }, "UpdateThread");

    @Override
    public synchronized void paint(Graphics g) {
        super.paint(g);
        g.setColor(new Color(0x00ff00));// 设置医院边界颜色
        // 绘制医院边界
        g.drawRect(Hospital.getInstance().getX(), Hospital.getInstance().getY(), Hospital.getInstance().getWidth(),
                Hospital.getInstance().getHeight());
        g.setFont(new Font("等线", Font.BOLD, 16));
        g.setColor(new Color(0x00ff00));
        g.drawString("医院", Hospital.getInstance().getX() + Hospital.getInstance().getWidth() / 2 - 16,
                Hospital.getInstance().getY() - 16);
        // 绘制代表人类的圆点
        List<Person> people = PersonPool.getInstance().getPersonList();
        if (people == null) {
            return;
        }
        for (Person person : people) {
            g.setColor(colorMap.get(person.getState()));
            person.update();// 对各种状态的市民进行不同的处理
            g.fillOval(person.getX(), person.getY(), 3, 3);

        }

        int captionStartOffsetX = 700 + Hospital.getInstance().getWidth() + 40;
        int captionStartOffsetY = 40;
        int captionSize = 24;
        captionStartOffsetY -= captionSize; // 参数初始调整

        // 显示数据信息
        g.setColor(new Color(0xffffff));
        g.drawString("世界时间（天）：" + (int) (worldTime / 10.0), captionStartOffsetX, captionStartOffsetY += captionSize);
       
        g.drawString("城市总人数：" + (Constants.POPULATION - toll), captionStartOffsetX, captionStartOffsetY += captionSize);
       
        g.setColor(colorMap.get(Person.State.NORMAL));
        g.drawString("健康者人数：" + normalCount, captionStartOffsetX,
                captionStartOffsetY += captionSize);

        g.setColor(colorMap.get(Person.State.SHADOW));
        g.drawString("潜伏期人数：" + incubationCount, captionStartOffsetX,
                captionStartOffsetY += captionSize);

        g.setColor(colorMap.get(Person.State.CONFIRMED));
        g.drawString("发病者人数：" + sickCount, captionStartOffsetX, captionStartOffsetY += captionSize);

        g.setColor(colorMap.get(Person.State.FREEZE));
        g.drawString("已隔离人数：" + isolatedCount, captionStartOffsetX, captionStartOffsetY += captionSize);

        g.setColor(new Color(0x00a1ff));
        g.drawString("空余病床：" + Math.max(Constants.BED_COUNT - isolatedCount, 0), captionStartOffsetX,
                captionStartOffsetY += captionSize);

        g.setColor(new Color(0xE39476));
        // 急需病床数量 = 确诊发病者数量 + 已隔离住院数量 - 床位总数
        //
        int needBeds = sickCount + isolatedCount - Constants.BED_COUNT;

        g.drawString("病床缺口：" + Math.max(needBeds, 0), captionStartOffsetX, captionStartOffsetY += captionSize);

        g.setColor(new Color(0xccbbcc));
        g.drawString("死亡人数：" + toll, captionStartOffsetX, captionStartOffsetY += captionSize);

        g.setColor(new Color(0x00ff23));
        g.drawString("治愈人次：" + PersonPool.RECOVERED, captionStartOffsetX, captionStartOffsetY += captionSize);
        worldTime++;
    }

    public static int worldTime = 0;// 世界时间
    private JButton closeBtn = new JButton("点击退出");
    {
        closeBtn.addActionListener(e -> System.exit(0));
        this.add(closeBtn);
        closeBtn.setVisible(false);
        closeBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }
    public Timer timer = new Timer();

    class MyTimerTask extends TimerTask {
        @Override
        public void run() {
            MyPanel.this.repaint();
        }
    }

    @Override
    public void run() {
        timer.schedule(new MyTimerTask(), 0, 100);// 启动世界计时器，时间开始流动
        // 结束检查线程
        new Thread(() -> {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                // e.printStackTrace();
            }
            while (true) {
                if (incubationCount + sickCount + isolatedCount == 0) {
                    try {
                        Thread.sleep(300);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    timer.cancel();
                    closeBtn.setVisible(true);
                    updateThread.interrupt();
                    return;
                }
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    // e.printStackTrace();
                }
            }
        }, "EndCheckThread").start();

        updateThread.start();
    }
}
