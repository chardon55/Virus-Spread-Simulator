import javax.swing.*;

import java.util.List;
import java.util.Random;

/**
 * Main Class
 *
 */
public class Main {

    public static void main(String[] args) {
        ArgSolver.cliInit(args);
        initHospital();
        initPanel();
        initInfected();
    }

    /**
     * Canvas Initialization
     */
    private static void initPanel() {
        MyPanel p = new MyPanel();
        Thread panelThread = new Thread(p);
        JFrame frame = new JFrame();
        frame.add(p);
        frame.setSize(Constants.CITY_WIDTH + hospitalWidth + 300, Constants.CITY_HEIGHT);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        frame.setTitle("Plague Spread Simulator");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        panelThread.start();// Start canvas thread
    }

    private static int hospitalWidth;

    /**
     * Arguments Initialization
     */
    private static void initHospital() {
        hospitalWidth = Hospital.getInstance().getWidth();
    }

    /**
     * Infected people initialization
     */
    private static void initInfected() {
        List<Person> people = PersonPool.getInstance().getPersonList();// Get all people
        for (int i = 0; i < Constants.ORIGINAL_COUNT; i++) {
            Person person;
            do {
                person = people.get(new Random().nextInt(people.size() - 1));// Select a person randomly
            } while (person.isInfected());// Reselect one if it's infected
            person.beInfected();// Infect it
        }
    }

}
