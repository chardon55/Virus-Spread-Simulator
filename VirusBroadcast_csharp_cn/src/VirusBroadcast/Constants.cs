using System;
using System.Collections.Generic;
using System.Text;

namespace VirusBroadcast {
	public static class Constants {
		
        public static int ORIGINAL_COUNT = 10;//初始感染数量
        public static double BROAD_RATE = 0.8;//传播率
        public static double SHADOW_TIME = 140;//潜伏时间，14天为140
        public static int HOSPITAL_RECEIVE_TIME = 10;//医院收治响应时间
        public static int BED_COUNT = 1000;//医院床位
        /**
         * 流动意向平均值，建议调整范围：[-0.99,0.99]
         */
        public static double MU = 0.99;
        public static int POPULATION = 5000;//城市总人口数量
        public static double FATALITY_RATE = 0.50;//fatality_rate病死率，根据2月6日数据估算（病死数/确诊数）为0.02
        public static int DIE_TIME = 100;//死亡时间均值，30天，从发病（确诊）时开始计时
        public static double DIE_VARIANCE = 1;//死亡时间方差
        /**
         * 城市大小即窗口边界，限制不允许出城
         */
        public static readonly int CITY_WIDTH = 700;
        public static readonly int CITY_HEIGHT = 800;

        public static double SAFE_DIST = 2; // 安全距离

        public static double RECOVERY_RATE = 0.001; // 治愈率
    }
}
