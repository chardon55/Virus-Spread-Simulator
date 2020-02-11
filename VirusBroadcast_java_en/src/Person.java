import java.util.List;
import java.util.Random;

/**
 * Person that ables to move
 * 
 */

public class Person extends Point {
    private City city;

    private MoveTarget moveTarget;
    /**
     * Coefficient: standard deviation of flow inclination
     */
    int sig = 1;

    /**
     * Move destination
     */

    double targetXU;// x average
    double targetYU;// y average
    double targetSig = 50;// Standard deviation

    /**
     * 市民的状态
     * 
     * @author dy55
     */
    public enum State {
        NORMAL, SUSPECTED, SHADOW, CONFIRMED, FREEZE, DEATH
    }

    public Person(City city, int x, int y) {
        super(x, y);
        this.city = city;
        // Get random number under normal distribution
        targetXU = MathUtil.stdGaussian(100, x);
        targetYU = MathUtil.stdGaussian(100, y);

    }

    /**
     * Standardize
     * <p>
     * Generate people's flow inclination
     * <p>
     * Through predicating whether the number is positive
     * <p>
     * u: average, sigma: standard deviation. Higher u causes people more active, vice versa
     * <p>
     * value derivation：
     * StdX = (X-u)/sigma
     * X = sigma * StdX + u
     *
     * @return
     */
    public boolean wantMove() {
        return MathUtil.stdGaussian(sig, Constants.u) > 0;
    }

    private State state = State.NORMAL;

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    int infectedTime = 0;// Infected time
    int confirmedTime = 0;// Confirmed sick time
    int dieMoment = 0;// Death time, 0 = unknown


    public boolean isInfected() {
        return state.compareTo(State.SHADOW) >= 0;
    }

    public void beInfected() {
        state = State.SHADOW;
        infectedTime = MyPanel.worldTime;
    }

    /**
     * Distance Calculation
     *
     * @param person
     * @return
     */
    public double distance(Person person) {
        return Math.sqrt(Math.pow(getX() - person.getX(), 2) + Math.pow(getY() - person.getY(), 2));
    }

    /**
     * Get isolated
     */
    private void freeze() {
        state = State.FREEZE;
    }

    /**
     * Action of a person
     */
    private void action() {
        // If it's isolated, dead or not wanting to move
        if (state == State.FREEZE || state == State.DEATH || !wantMove()) {
            return;
        }
        // Start move if there is flow inclination which under normal distribution
        if (moveTarget == null || moveTarget.isArrived()) {
            //在想要移动并且没有目标时，将自身移动目标设置为随机生成的符合正态分布的目标点
            //产生N(a,b)的数：Math.sqrt(b)*random.nextGaussian()+a
            double targetX = MathUtil.stdGaussian(targetSig, targetXU);
            double targetY = MathUtil.stdGaussian(targetSig, targetYU);
            moveTarget = new MoveTarget((int) targetX, (int) targetY);

        }

        // Calculate displacement
        int dX = moveTarget.getX() - getX();
        int dY = moveTarget.getY() - getY();

        double length = Math.sqrt(Math.pow(dX, 2) + Math.pow(dY, 2));// Distance to the destination

        // Predicate if arrived at the destination
        if (length < 1) {
            moveTarget.setArrived(true);
            return;
        }

        // dX: displacement, udX: Direction (on x axis)
        int udX = (int) (dX / length);
        if (udX == 0 && dX != 0) {
            udX = round(dX);
        }

        // dY: displacement, udY: Direction (on y axis)
        int udY = (int) (dY / length);
        if (udY == 0 && dY != 0) {
            udY = round(dY);
        }

        // X border
        if (getX() >= Constants.CITY_WIDTH || getX() <= 0) {
            moveTarget = null;
            if (udX > 0) {
                udX = -udX;
            }
        }
        // Y border
        if (getY() >= Constants.CITY_HEIGHT || getY() <= 0) {
            moveTarget = null;
            if (udY > 0) {
                udY = -udY;
            }
        }
        moveTo(udX, udY);

    }
    
    private int round(int value) {
        if (value > 0) {
            return 1;
        }
        return -1;
    }

    public Bed useBed;

    private final float SAFE_DIST = Constants.SAFE_DIST;//Safe Distance

    /**
     * Update conditions of people
     */
    public void update() {
        //@TODO State Machine

        if (state == State.DEATH) {
            // If died in the hospital
            if (Hospital.getInstance().inHospital(getX(), getY())) {
                // Move out of the world
                setX(-10);
                setY(-10);
                // Hospital.getInstance().returnBed(useBed);
            }
            return;
        }

        if (state == State.FREEZE) {
            // Calculate the success of recovery
            float success = new Random().nextFloat();
            if (success < Constants.RECOVERY_RATE) {
                // Recover successfully
                state = State.NORMAL;

                // Select location under normal distribution
                Random random = new Random();
                int x = (int) (100 * random.nextGaussian() + city.getCenterX());
                int y = (int) (100 * random.nextGaussian() + city.getCenterY());
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

                // Move the person into the city
                setX(x);
                setY(y);
                Hospital.getInstance().returnBed(useBed);
                useBed = null;
                PersonPool.RECOVERED++; // Recovered time += 1
            }
        }

        // Confirmed people
        if (state == State.CONFIRMED && dieMoment == 0) {
            
        }


        if (state == State.CONFIRMED
                && MyPanel.worldTime - confirmedTime >= Constants.HOSPITAL_RECEIVE_TIME) {
            // Isolate the sick person
            Bed bed = Hospital.getInstance().pickBed();// Select empty bed
            if (bed == null) {

                // No bed empty

            } else {
                // Put sick there
                useBed = bed;
                freeze();
                setX(bed.getX());
                setY(bed.getY());
                bed.setEmpty(false);
            }
        }

        // Decide death
        if ((state == State.CONFIRMED || state == State.FREEZE) && MyPanel.worldTime >= dieMoment) {
            float fatal = new Random().nextFloat();
            if (fatal < Constants.FATALITY_RATE) {
                state = State.DEATH;// Die
                Hospital.getInstance().returnBed(useBed);// Return the bed
                useBed = null;
            }
        }

        // Incubation time under normal distribution
        double stdRnShadowtime = MathUtil.stdGaussian(25, Constants.SHADOW_TIME / 2);
        // Decide sick who is under incubation period
        if (MyPanel.worldTime - infectedTime > stdRnShadowtime && state == State.SHADOW) {
            state = State.CONFIRMED;// Get sick
            confirmedTime = MyPanel.worldTime;// Update time
        }
        // Movement who are not isolated
        action();
        // Decide infection
        List<Person> people = PersonPool.getInstance().personList;
        if (state == State.NORMAL && !Hospital.getInstance().inHospital(getX(), getY())) {
            for (Person person : people) {
                if (person.getState() != State.SHADOW && person.getState() != State.CONFIRMED) {
                    continue;
                }
                float random = new Random().nextFloat();
                if (random < Constants.BROAD_RATE && distance(person) < SAFE_DIST) {
                    this.beInfected();
                    break;
                }
            }
        }
    }
}
