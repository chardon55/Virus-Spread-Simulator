import java.util.Random;

/**
 * Math Utilities
 *
 */
public class MathUtil {
    /**
     * Random generator
     */
    private static final Random randomGen = new Random();

    /**
     * Normal Distribution (Gaussian Distribution) Method
     * <p>
     * Predicate whether a person inclines to move according to if the number is positive
     * <p>
     * Supposing X is under normal distribution, sigma(σ) is the standard deviation that affects the distribution, u(mu,μ) is the average.
     * <p>
     * <p>
     * Derivation: 
     * StdX = (X-u)/sigma
     * X = sigma * StdX + u
     *
     * @param sigma Standard Deviation
     * @param u     Average
     * @return
     */
    public static double stdGaussian(double sigma, double u) {
        double X = randomGen.nextGaussian();
        return sigma * X + u;
    }

}
