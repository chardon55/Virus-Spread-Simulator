using System;
using System.Collections.Generic;
using System.Text;
using System.Threading;
using System.Windows;

namespace VirusBroadcast {
	class MainClass {
		[STAThread]
		static void Main(string[] args) {
			Initialize();
		}

		protected static void Initialize() {
			InitHospital();
			InitPanel();
			InitInfected();
		}

		private static void InitPanel() {
			var p = new MyPanel();
			var panelThread = new Thread(() => p.Run());
			new Window {
				Content = p,
				RenderSize = new Size(Constants.CITY_WIDTH + hospitalWidth + 300, Constants.CITY_HEIGHT),
				WindowStartupLocation = WindowStartupLocation.CenterScreen,
				Title = "瘟疫传播模拟",
				WindowState = WindowState.Maximized
			}.Show();
			panelThread.Start();
		}

		private static int hospitalWidth;

		private static void InitHospital() {
			hospitalWidth = Hospital.Instance.Width;
		}

		private static void InitInfected() {
			var people = PersonPool.Instance.PersonList;
			for(var i = 0; i < Constants.ORIGINAL_COUNT; i++) {
				Person person;
				do {
					person = people[new Random().Next(people.Count - 1)];
				} while (person.IsInfected());
			}
		}
	}
}
