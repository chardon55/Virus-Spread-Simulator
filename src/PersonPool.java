import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Person Pool
 *
 */
public class PersonPool {
    private static PersonPool personPool = new PersonPool();

    public static PersonPool getInstance() {
        return personPool;
    }

    List<Person> personList = new ArrayList<Person>();

    public List<Person> getPersonList() {
        return personList;
    }


    /**
     * @param state Condition of people Person.State, null = total
     * @return Population of the certain condition of people
     */
    public int getPeopleSize(Person.State state) {
        if (state == null) {
            return personList.size();
        }
        int i = 0;
        for (Person person : personList) {
            if (person.getState().compareTo(state) == 0) {
                i++;
            }
        }
        return i;
    }

    public static int RECOVERED = 0; // Recovered times

    private PersonPool() {
        City city = new City(Constants.CITY_WIDTH / 2, Constants.CITY_HEIGHT / 2);
        // Add people
        for (int i = 0; i < Constants.POPULATION; i++) {
            Random random = new Random();
            //Generate number under N(a,b)：Math.sqrt(b)*random.nextGaussian()+a
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
            personList.add(new Person(city, x, y));
        }
    }
}
