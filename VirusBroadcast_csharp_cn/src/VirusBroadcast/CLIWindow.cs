using System;
using System.Collections.Generic;
using System.Text;
using System.Text.RegularExpressions;

namespace VirusBroadcast {
    public static class CLIWindow {

        private static int cursor = 0;

        private static readonly Dictionary<string, Action<Queue<string>>> actionDict = new Dictionary<string, Action<Queue<string>>>();

        private static readonly Queue<string> argQueue = new Queue<string>();

        static CLIWindow() {
            // 初始感染数量
            actionDict.Add("-o", e => Constants.ORIGINAL_COUNT = Convert.ToInt32(e.Dequeue()));
            actionDict.Add("--original", actionDict["-o"]);
            // 传播率
            actionDict.Add("-b", e => Constants.BROAD_RATE = Convert.ToDouble(e.Dequeue()));
            actionDict.Add("--broad-rate", actionDict["-b"]);

        }

        public static void CliInit(string[] args) {
            foreach (var i in args) {
                argQueue.Enqueue(i);
            }

            for(; cursor < args.Length; cursor++) {
                if(!Regex.IsMatch(args[cursor], "-.+")) {

                }
            }
        }

        private static void GetHelp() {
            Console.WriteLine("瘟疫传播模拟程序");
            Console.WriteLine("----------------------------------------------------");
            Console.WriteLine("可用命令：");
            Console.WriteLine("            -h, -?    获取帮助信息\n");

            Console.WriteLine("    -o, --original    自定义初始感染数量");
            Console.WriteLine("  -b, --broad-rate    自定义传播率（0 ~ 1的小数）");
            Console.WriteLine("      -s, --shadow    自定义潜伏时间");
            Console.WriteLine("     -r, --receive    自定义医院收治时间");
            Console.WriteLine("   -c, --bed-count    自定义床位");
            Console.WriteLine("      -m, --move-u    自定义流动意向平均值（-0.99 ~ 0.99）");
            Console.WriteLine("  -p, --population    自定义城市人口（建议在5000左右）");
            Console.WriteLine("   -d, --safe-dist    自定义安全距离");
            Console.WriteLine("----------------------------------------------------");
        }

        private static void ErrReport(string message) {
            Console.WriteLine();
            Console.WriteLine("错误：");
            Console.WriteLine(message);
        }
    }
}
