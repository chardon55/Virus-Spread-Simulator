import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.regex.Pattern;

/**
 * Arguments Transfer
 * 
 * @author dy55
 */
public class ArgSolver {

	private static int cursor = 0;

	private static Map<String, Consumer<String[]>> actionMap = new HashMap<>();

	static {
		// Original Infected Number
		actionMap.put("-o", e -> Constants.ORIGINAL_COUNT = Integer.valueOf(e[++cursor]));
		actionMap.put("--original", actionMap.get("-o"));
		// Spread Rate
		actionMap.put("-b", e -> Constants.BROAD_RATE = Float.valueOf(e[++cursor]));
		actionMap.put("--broad-rate", actionMap.get("-b"));
		// Incubation Period
		actionMap.put("-s", e -> Constants.SHADOW_TIME = Float.valueOf(e[++cursor]));
		actionMap.put("--shadow", actionMap.get("-s"));
		// Admission Response Time of the Hospital
		actionMap.put("-r", e -> Constants.HOSPITAL_RECEIVE_TIME = Integer.valueOf(e[++cursor]));
		actionMap.put("--receive", actionMap.get("-r"));
		// Number of Beds
		actionMap.put("-c", e -> Constants.BED_COUNT = Integer.valueOf(e[++cursor]));
		actionMap.put("--bed-count", actionMap.get("-c"));
		// Flow Inclination Average μ
		actionMap.put("-m", e -> Constants.u = Float.valueOf(e[++cursor]));
		actionMap.put("--move-mu", actionMap.get("-m"));
		// Population
		actionMap.put("-p", e -> Constants.POPULATION = Integer.valueOf(e[++cursor]));
		actionMap.put("--population", actionMap.get("-p"));
		// Safe Distance
		actionMap.put("-d", e -> Constants.SAFE_DIST = Float.valueOf(e[++cursor]));
		actionMap.put("--safe-dist", actionMap.get("-d"));
		// Get Help
		actionMap.put("-h", e -> {
			getHelp();
			System.exit(0);
		});
		actionMap.put("-?", actionMap.get("-h"));
	}

	/**
	 * Initialization
	 * 
	 * @param args Input Arguments
	 */
	public static void cliInit(String[] args) {
		for (; cursor < args.length; cursor++) {
			if (!Pattern.matches("-.+", args[cursor])) {
				errReport("Incorrect input arguments");
				System.exit(0);
			}

			try {
				actionMap.get(args[cursor].trim().toLowerCase()).accept(args);
			} catch (NullPointerException e) {
				errReport("Incorrect input arguments");
				System.exit(0);
			}
		}

		System.out.println("-------------------");
		System.out.println("Arguments info:\n");

		System.out.println("Original Infected Number: " + Constants.ORIGINAL_COUNT);
		System.out.println("Spread Rate: " + Constants.BROAD_RATE);
		System.out.println("Incubation Period: " + Constants.SHADOW_TIME);
		System.out.println("Admission Response Time: " + Constants.HOSPITAL_RECEIVE_TIME);
		System.out.println("Number of Beds: " + Constants.BED_COUNT);
		System.out.println("Flow Inclination Average: " + Constants.u);
		System.out.println("Population: " + Constants.POPULATION);
		System.out.println("Safe Distance: " + Constants.SAFE_DIST);

		System.out.println("--------------------");
	}

	/**
	 * Get Help
	 */
	private static void getHelp() {
		System.out.println("Plague Spread Simulation Program");
		System.out.println("----------------------------------------------------");
		System.out.println("Available Commands");
		System.out.println("            -h, -?    Get Help\n");

		System.out.println("    -o, --original    Custom Original Infected Number");
		System.out.println("  -b, --broad-rate    Custom Spread Rate（0 ~ 1 Float）");
		System.out.println("      -s, --shadow    Custom Incubation Period");
		System.out.println("     -r, --receive    Custom Admission Response Time of the Hospital");
		System.out.println("   -c, --bed-count    Custom Number of Beds");
		System.out.println("     -m, --move-mu    Custom Flow Inclination Average （-0.99 ~ 0.99）");
		System.out.println("  -p, --population    Custom Population (Suggested 5000)");
		System.out.println("   -d, --safe-dist    Custom Safe Distance");
		System.out.println("----------------------------------------------------");
	}

	/**
	 * Error output
	 * 
	 * @param message Error info
	 */
	private static void errReport(String message) {
		System.out.println();
		System.err.println("Error: ");
		System.err.println(message);
	}
}