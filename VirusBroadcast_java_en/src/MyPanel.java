import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

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

    @Override
    public void paint(Graphics g) {
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
            switch (person.getState()) {
            case NORMAL:
                // Healthy
                g.setColor(new Color(0xdddddd));
                break;

            case SHADOW:
                // In incubation period
                g.setColor(new Color(0xffee00));
                break;

            case CONFIRMED:
                // Confirmed sick
                g.setColor(new Color(0xff0000));
                break;

            case FREEZE:
                // Isolated
                g.setColor(new Color(0x48FFFC));
                break;

            case DEATH:
                // Dead
                g.setColor(new Color(0x000000));
                break;

            default:
                break;
            }
            person.update();// Update condition of people
            g.fillOval(person.getX(), person.getY(), 3, 3);

        }

        int captionStartOffsetX = 700 + Hospital.getInstance().getWidth() + 40;
        int captionStartOffsetY = 40;
        int captionSize = 24;

        // Show statistics data
        g.setColor(Color.WHITE);
        g.setColor(new Color(0xffffff));
        g.drawString("World Time (day): " + (int) (worldTime / 10.0), captionStartOffsetX, captionStartOffsetY);
        g.drawString("Population: " + Constants.POPULATION, captionStartOffsetX, captionStartOffsetY + captionSize);
        g.setColor(new Color(0xdddddd));
        g.drawString("Healthy: " + PersonPool.getInstance().getPeopleSize(Person.State.NORMAL), captionStartOffsetX,
                captionStartOffsetY + 2 * captionSize);
        g.setColor(new Color(0xffee00));
        g.drawString("In incubation period: " + PersonPool.getInstance().getPeopleSize(Person.State.SHADOW), captionStartOffsetX,
                captionStartOffsetY + 3 * captionSize);
        g.setColor(new Color(0xff0000));
        int sick = PersonPool.getInstance().getPeopleSize(Person.State.CONFIRMED);
        g.drawString("Sick: " + sick, captionStartOffsetX,
                captionStartOffsetY + 4 * captionSize);
        g.setColor(new Color(0x48FFFC));
        int isolated = PersonPool.getInstance().getPeopleSize(Person.State.FREEZE);
        g.drawString("Isolated: " + isolated, captionStartOffsetX,
                captionStartOffsetY + 5 * captionSize);
        g.setColor(new Color(0x00a1ff));
        g.drawString("Empty Beds: "
                + Math.max(Constants.BED_COUNT - PersonPool.getInstance().getPeopleSize(Person.State.FREEZE), 0),
                captionStartOffsetX, captionStartOffsetY + 6 * captionSize);

        g.setColor(new Color(0xE39476));
        // Beds needed = Sick + Isolated - Beds total
        // 
        int needBeds = sick + isolated - Constants.BED_COUNT;

        g.drawString("Beds Needed: " + (needBeds > 0 ? needBeds : 0), captionStartOffsetX,
                captionStartOffsetY + 7 * captionSize);
        g.setColor(new Color(0xccbbcc));
        g.drawString("Death Toll: " + PersonPool.getInstance().getPeopleSize(Person.State.DEATH), captionStartOffsetX,
                captionStartOffsetY + 8 * captionSize);
        // One person may be cured many times, which are all recorded
        g.setColor(new Color(0x00ff23));
        g.drawString("Recovered Times: " + PersonPool.RECOVERED, captionStartOffsetX, captionStartOffsetY + 9 * captionSize);

    }

    public static int worldTime = 0;// World Time
    private JButton closeBtn = new JButton("Click to Exit");
    {
        closeBtn.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }

        });
        this.add(closeBtn);
        closeBtn.setVisible(false);
        closeBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }
    public Timer timer = new Timer();

    class MyTimerTask extends TimerTask {
        @Override
        public void run() {
            if (PersonPool.getInstance().getPeopleSize(Person.State.SHADOW)
                    + PersonPool.getInstance().getPeopleSize(Person.State.CONFIRMED)
                    + PersonPool.getInstance().getPeopleSize(Person.State.FREEZE) == 0) {
                timer.cancel();
                closeBtn.setVisible(true);
            }
            MyPanel.this.repaint();
            worldTime++;
        }
    }

    @Override
    public void run() {
        timer.schedule(new MyTimerTask(), 0, 100);// Start timer and the time elapsed
    }


}
