import java.util.ArrayList;
import java.util.List;

/**
 * Hospital
 *
 */
public class Hospital extends Point {
    public static final int HOSPITAL_X = 720;
    public static final int HOSPITAL_Y = 80;
    private int width;
    private int height = 600;

    public int getWidth() {
        return width;
    }


    public int getHeight() {
        return height;
    }

    private static Hospital hospital = new Hospital();

    public static Hospital getInstance() {
        return hospital;
    }

    private Point point = new Point(HOSPITAL_X, HOSPITAL_Y);// Position of the first bed
    private List<Bed> beds = new ArrayList<>();

    /**
     * Get all beds
     *
     * @return
     */
    public List<Bed> getBeds() {
        return beds;
    }

    private Hospital() {
        // Rectangle of the hospital
        super(HOSPITAL_X, HOSPITAL_Y + 10);
        // Adjust size of the hospital
        if (Constants.BED_COUNT == 0) {
            width = 0;
            height = 0;
        }
        // Calculate width
        // 100 bed per column
        int column = Constants.BED_COUNT / 100;
        width = column * 6;
        // Initialize other beds
        for (int i = 0; i < column; i++) {

            for (int j = 10; j <= 604; j += 6) {
                beds.add(new Bed(point.getX() + i * 6, point.getY() + j));
            }

        }
    }

    /**
     * Use bed
     *
     * @return
     */
    public Bed pickBed() {
        for (Bed bed : beds) {
            if (bed.isEmpty()) {
                return bed;
            }
        }
        return null;
    }

    /**
     * Return bed (When a person is recovered or dead)
     *
     * @param bed
     * @return
     */
    public void returnBed(Bed bed) {
        if (bed != null) {
            bed.setEmpty(true);
        }
    }

    /**
     * Whether the person is in hospital
     */
    public boolean inHospital(int x, int y) {
        return x >= HOSPITAL_X && x <= HOSPITAL_X + width && y >= HOSPITAL_Y && y <= HOSPITAL_Y + height;
    }
}
