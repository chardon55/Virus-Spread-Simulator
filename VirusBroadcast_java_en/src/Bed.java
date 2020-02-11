/**
 * Bed
 *
 */
public class Bed extends Point {
    public Bed(int x, int y) {
        super(x, y);
    }

    /**
     * Whether the bed is empty
     */
    private boolean isEmpty = true;

    public boolean isEmpty() {
        return isEmpty;
    }

    public void setEmpty(boolean empty) {
        isEmpty = empty;
    }
}
