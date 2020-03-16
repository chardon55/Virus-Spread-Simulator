import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Map;
import java.util.HashMap;

/**
 * Main Panel
 *
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

    // Statistics Update Thread
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
        g.setColor(new Color(0x00ff00));// Border Color
        // Draw Border of the hospital
        g.drawRect(Hospital.getInstance().getX(), Hospital.getInstance().getY(), Hospital.getInstance().getWidth(),
                Hospital.getInstance().getHeight());
        g.setFont(new Font("Segoe UI", Font.BOLD, 16));
        g.setColor(new Color(0x00ff00));
        g.drawString("Hospital", Hospital.getInstance().getX() + Hospital.getInstance().getWidth() / 2 - 32,
                Hospital.getInstance().getY() - 16);
        // Draw people in dots
        List<Person> people = PersonPool.getInstance().getPersonList();
        if (people == null) {
            return;
        }

        for (Person person : people) {
            g.setColor(colorMap.get(person.getState()));
            person.update();// Update condition of people
            g.fillOval(person.getX(), person.getY(), 3, 3);

        }

        int captionStartOffsetX = 700 + Hospital.getInstance().getWidth() + 40;
        int captionStartOffsetY = 40;
        int captionSize = 24;
        captionStartOffsetY -= captionSize; // Offset Adjustment

        // Show statistics data
        g.setColor(new Color(0xffffff));
        g.drawString("World Time: Day " + (int) (worldTime / 10.0), captionStartOffsetX,
                captionStartOffsetY += captionSize);

        g.drawString("Population: " + (Constants.POPULATION - toll), captionStartOffsetX,
                captionStartOffsetY += captionSize);

        g.setColor(colorMap.get(Person.State.NORMAL));
        g.drawString("Healthy: " + normalCount, captionStartOffsetX, captionStartOffsetY += captionSize);

        g.setColor(colorMap.get(Person.State.SHADOW));
        g.drawString("In Incubation Period: " + incubationCount, captionStartOffsetX,
                captionStartOffsetY += captionSize);

        g.setColor(colorMap.get(Person.State.CONFIRMED));
        g.drawString("Sick: " + sickCount, captionStartOffsetX, captionStartOffsetY += captionSize);

        g.setColor(colorMap.get(Person.State.FREEZE));
        g.drawString("Isolated: " + isolatedCount, captionStartOffsetX, captionStartOffsetY += captionSize);

        g.setColor(new Color(0x00a1ff));
        g.drawString("Empty Beds: " + Math.max(Constants.BED_COUNT - isolatedCount, 0), captionStartOffsetX,
                captionStartOffsetY += captionSize);

        g.setColor(new Color(0xE39476));
        // Beds needed = Sick + Isolated - Beds total
        //
        int needBeds = sickCount + isolatedCount - Constants.BED_COUNT;

        g.drawString("Bed Gap: " + Math.max(needBeds, 0), captionStartOffsetX, captionStartOffsetY += captionSize);

        g.setColor(new Color(0xccbbcc));
        g.drawString("Death Toll: " + toll, captionStartOffsetX, captionStartOffsetY += captionSize);
        // One person may be cured many times, which are all recorded

        g.setColor(new Color(0x00ff23));
        g.drawString("Recovered: " + PersonPool.RECOVERED, captionStartOffsetX,
                captionStartOffsetY += captionSize);
        worldTime++;
    }

    public static int worldTime = 0;// World Time
    private JButton closeBtn = new JButton("Click to Exit");
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
        timer.schedule(new MyTimerTask(), 0, 100);// Start timer and the time elapsed
        // End Check Thread
        new Thread((Runnable) () -> {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e1) {
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
                } catch (InterruptedException e2) {
                    // e.printStackTrace();
                }
            }
        }, "EndCheckThread").start();

        updateThread.start();
    }


}
