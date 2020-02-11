import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.regex.Pattern;

/**
 * 参数传递工具
 * 
 * @author dy55
 */
public class ArgSolver {

	private static int cursor = 0;

	private static Map<String, Consumer<String[]>> actionMap = new HashMap<>();

	static {
		// 初始感染数量
		actionMap.put("-o", e -> Constants.ORIGINAL_COUNT = Integer.valueOf(e[++cursor]));
		actionMap.put("--original", actionMap.get("-o"));
		// 传播率
		actionMap.put("-b", e -> Constants.BROAD_RATE = Float.valueOf(e[++cursor]));
		actionMap.put("--broad-rate", actionMap.get("-b"));
		// 潜伏时间
		actionMap.put("-s", e -> Constants.SHADOW_TIME = Float.valueOf(e[++cursor]));
		actionMap.put("--shadow", actionMap.get("-s"));
		// 医院收治时间
		actionMap.put("-r", e -> Constants.HOSPITAL_RECEIVE_TIME = Integer.valueOf(e[++cursor]));
		actionMap.put("--receive", actionMap.get("-r"));
		// 床位
		actionMap.put("-c", e -> Constants.BED_COUNT = Integer.valueOf(e[++cursor]));
		actionMap.put("--bed-count", actionMap.get("-c"));
		// 流动意向平均值μ
		actionMap.put("-m", e -> Constants.u = Float.valueOf(e[++cursor]));
		actionMap.put("--move-mu", actionMap.get("-m"));
		// 总人口
		actionMap.put("-p", e -> Constants.POPULATION = Integer.valueOf(e[++cursor]));
		actionMap.put("--population", actionMap.get("-p"));
		// 安全距离
		actionMap.put("-d", e -> Constants.SAFE_DIST = Float.valueOf(e[++cursor]));
		actionMap.put("--safe-dist", actionMap.get("-d"));
		// 帮助
		actionMap.put("-h", e -> {
			getHelp();
			System.exit(0);
		});
		actionMap.put("-?", actionMap.get("-h"));
	}

	/**
	 * 初始化
	 * 
	 * @param args 传入参数
	 */
	public static void cliInit(String[] args) {
		for (; cursor < args.length; cursor++) {
			if (!Pattern.matches("-.+", args[cursor])) {
				errReport("传入参数错误");
				System.exit(0);
			}

			try {
				actionMap.get(args[cursor].trim().toLowerCase()).accept(args);
			} catch (NullPointerException e) {
				errReport("传入参数错误");
				System.exit(0);
			}
		}

		System.out.println("-------------------");
		System.out.println("参数信息：\n");

		System.out.println("初始感染数量：" + Constants.ORIGINAL_COUNT);
		System.out.println("传播率：" + Constants.BROAD_RATE);
		System.out.println("潜伏时间：" + Constants.SHADOW_TIME);
		System.out.println("收治响应时间：" + Constants.HOSPITAL_RECEIVE_TIME);
		System.out.println("医院床位：" + Constants.BED_COUNT);
		System.out.println("流动意向平均值：" + Constants.u);
		System.out.println("城市总人口：" + Constants.POPULATION);
		System.out.println("安全距离：" + Constants.SAFE_DIST);

		System.out.println("--------------------");
	}

	/**
	 * 获取帮助信息
	 */
	private static void getHelp() {
		System.out.println("瘟疫传播模拟程序");
		System.out.println("----------------------------------------------------");
		System.out.println("可用命令：");
		System.out.println("            -h, -?    获取帮助信息\n");

		System.out.println("    -o, --original    自定义初始感染数量");
		System.out.println("  -b, --broad-rate    自定义传播率（0 ~ 1的小数）");
		System.out.println("      -s, --shadow    自定义潜伏时间");
		System.out.println("     -r, --receive    自定义医院收治时间");
		System.out.println("   -c, --bed-count    自定义床位");
		System.out.println("      -m, --move-mu    自定义流动意向平均值（-0.99 ~ 0.99）");
		System.out.println("  -p, --population    自定义城市人口（建议在5000左右）");
		System.out.println("   -d, --safe-dist    自定义安全距离");
		System.out.println("----------------------------------------------------");
	}

	/**
	 * 错误输出
	 * 
	 * @param message 错误信息
	 */
	private static void errReport(String message) {
		System.out.println();
		System.err.println("错误：");
		System.err.println(message);
	}
}