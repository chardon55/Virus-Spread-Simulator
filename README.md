# Plague Spread Simulation Program

<p align="center"><b>Stay strong! We can contain the virus!</b></p>

## Introduction

This is a simulator created by Bruce Young to simulate spread of SARS-CoV-2 (2019-nCoV) 
and tell everyone on BiliBili why it's safer to stay home while the coronavirus is breaking out.  

The code has been modified. If you want to see the original code, please go to https://github.com/KikiLetGo/VirusBroadcast.https://github.com/cnctemaR/VirusBroadcast)

**English edition is now available.**

---

Specially thank Bruce Young and his "Ele shiyanshi" (Hungry Lab) for releasing such an educative program.

[Original Video Link

|||
| --: | :-- |
| 原始库 | [KikiLetGo/VirusBroadcast](https://github.com/KikiLetGo/VirusBroadcast) |
| 使用语言 | Java |
| 原始开发者 | Bruce Young |

## Usage

1. 直接打开可执行JAR包
2. 通过Shell打开
	```bash
		cd <当前目录>
		java -jar VirusBroadcast.jar <参数>
	```

	| 可用命令 ||
	| --: | :-- |
	| -h, -? | 获取帮助信息 |
	| -o, --original | 自定义初始感染数量 |
	| -b, --broad-rate | 自定义传播率（0 ~ 1的小数） |
	| -s, --shadow | 自定义潜伏时间 |
	| -r, --receive | 自定义医院收治时间 |
	| -c, --bed-count | 自定义床位 |
	| -m, --move-u | 自定义流动意向平均值（-0.99 ~ 0.99） |
	| -d, --safe-dist | 自定义安全距离 |
	| -p, --population | 自定义城市人口（建议在5000左右） |

