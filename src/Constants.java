/**
 * Simulation Arguments
 *
 */
public class Constants {

    public static int ORIGINAL_COUNT = 10;// Original Infected Number
    public static float BROAD_RATE = 0.8f;// Spread Rate
    public static float SHADOW_TIME = 140;// Incubation Period，14 days => 140
    public static int HOSPITAL_RECEIVE_TIME = 10;// Admission Response Time of the Hospital
    public static int BED_COUNT = 500;// Number of Beds
    /**
     * Flow Inclination Average (Suggested range: [-0.99,0.99])
     */
    public static float u = 0.99f;
    public static int POPULATION = 5000;// Population
    public static float FATALITY_RATE = 0.02f;// Fatality Rate
    public static int DIE_TIME = 100;// Death Time Average : 30 days after symptom appears
    public static double DIE_VARIANCE = 1; // Death Time Variance 
    /**
     * Bordor of the city
     */
    public static final int CITY_WIDTH = 700;
    public static final int CITY_HEIGHT = 700;

    public static float SAFE_DIST = 2f; // Safe Distance

    public static float RECOVERY_RATE = 0.004f; // Recovery Rate
}
