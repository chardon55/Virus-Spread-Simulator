using System;
using System.Collections.Generic;
using System.Text;
using System.Windows.Controls;
using System.Windows.Media;
using System.Globalization;
using System.Windows.Input;
using System.Threading;
using System.Runtime.CompilerServices;

namespace VirusBroadcast {
	public class MyPanel : Panel {

		public MyPanel(): base() {
			colorDict.Add(Person.State.NORMAL, Color.FromRgb(221, 221, 221));
			colorDict.Add(Person.State.SHADOW, Color.FromRgb(255, 238, 0));
			colorDict.Add(Person.State.CONFIRMED, Color.FromRgb(255, 0, 0));
			colorDict.Add(Person.State.FREEZE, Color.FromRgb(72, 255, 252));
			colorDict.Add(Person.State.DEATH, Color.FromRgb(0, 0, 0));

			Background = new SolidColorBrush(Color.FromRgb(68, 68, 68));

			closeBtn.Click += (sender, e) => Environment.Exit(0);
			Children.Add(closeBtn);
			closeBtn.Visibility = System.Windows.Visibility.Collapsed;
			closeBtn.Cursor = Cursors.Hand;

		}

		private readonly static Dictionary<Person.State, Color> colorDict = new Dictionary<Person.State, Color>();

		private static int normalCount;
		private static int incubationCount;
		private static int sickCount;
		private static int isolatedCount;
		private static int toll;

		private readonly Thread updateThread = new Thread(new ThreadStart(UpdateThreadRun));

		[MethodImpl(MethodImplOptions.Synchronized), MTAThread]
		private static void UpdateThreadRun() {
			while (true) {
				toll = PersonPool.Instance.GetPeopleSize(Person.State.DEATH);
				isolatedCount = PersonPool.Instance.GetPeopleSize(Person.State.FREEZE);
				sickCount = PersonPool.Instance.GetPeopleSize(Person.State.CONFIRMED);
				incubationCount = PersonPool.Instance.GetPeopleSize(Person.State.SHADOW);
				normalCount = PersonPool.Instance.GetPeopleSize(Person.State.NORMAL);
				Thread.Sleep(100);
			}
		}



		public static int WorldTime { get; private set; }

		private readonly SolidColorBrush brush = new SolidColorBrush();
		private readonly Pen pen = new Pen();
		private readonly CultureInfo cultureInfo = new CultureInfo("zh-CN", false);
		private readonly Typeface typeface = new Typeface("等线");
		private readonly double ppd = 1.5;
		private readonly double dotRadius = 3.0;

		private readonly int captionStartOffsetX = 700 + Hospital.Instance.Width + 40;
		private readonly int captionStartOffsetY = 40;
		private readonly int captionSize = 24;

		[MethodImpl(MethodImplOptions.Synchronized)]
		protected override void OnRender(DrawingContext dc) {
			base.OnRender(dc);
			brush.Color = Color.FromRgb(0, 255, 0);
			var clonePen = pen.Clone();
			clonePen.Brush = brush.Clone();
			clonePen.Thickness = 1;
			dc.DrawRectangle(null, clonePen, new System.Windows.Rect(Hospital.HOSPITAL_X, Hospital.HOSPITAL_Y, Hospital.Instance.Width, Hospital.Instance.Height));
			brush.Color = Color.FromRgb(0, 255, 0);
			dc.DrawText(new FormattedText("医院", cultureInfo, FlowDirection, typeface, 16, brush.Clone(), ppd), new System.Windows.Point(Hospital.HOSPITAL_X + Hospital.Instance.Width / 2 - 16, Hospital.HOSPITAL_Y - 16));

			var people = PersonPool.Instance.PersonList;
			if(people is null) {
				return;
			}

			foreach(var person in people) {
				brush.Color = colorDict[person.CurState];
				person.Update();
				dc.DrawEllipse(brush.Clone(), pen, new System.Windows.Point(person.X, person.Y), dotRadius, dotRadius);
			}

			var point = new System.Windows.Point(captionStartOffsetX, captionStartOffsetY - captionSize);

			brush.Color = Color.FromRgb(255, 255, 255);
			point.Y += captionSize;
			dc.DrawText(new FormattedText($"世界时间（天）：{Convert.ToInt32(WorldTime / 10.0)}", cultureInfo, FlowDirection, typeface, 16, brush.Clone(), ppd), point);
			
			point.Y += captionSize;
			dc.DrawText(new FormattedText($"城市总人数：{Constants.POPULATION - toll}", cultureInfo, FlowDirection, typeface, 16, brush.Clone(), ppd), point);

			brush.Color = colorDict[Person.State.NORMAL];
			point.Y += captionSize;
			dc.DrawText(new FormattedText($"健康者人数：{normalCount}", cultureInfo, FlowDirection, typeface, 16, brush.Clone(), ppd), point);
			
			brush.Color = colorDict[Person.State.SHADOW];
			point.Y += captionSize;
			dc.DrawText(new FormattedText($"潜伏期人数：{incubationCount}", cultureInfo, FlowDirection, typeface, 16, brush.Clone(), ppd), point);

			brush.Color = colorDict[Person.State.CONFIRMED];
			point.Y += captionSize;
			dc.DrawText(new FormattedText($"发病者人数：{sickCount}", cultureInfo, FlowDirection, typeface, 16, brush.Clone(), ppd), point);

			brush.Color = colorDict[Person.State.FREEZE];
			point.Y += captionSize;
			dc.DrawText(new FormattedText($"已隔离人数：{isolatedCount}", cultureInfo, FlowDirection, typeface, 16, brush.Clone(), ppd), point);

			brush.Color = Color.FromRgb(0, 161, 255);
			point.Y += captionSize;
			dc.DrawText(new FormattedText($"空余病床：{Math.Max(Constants.BED_COUNT - isolatedCount, 0)}", cultureInfo, FlowDirection, typeface, 16, brush.Clone(), ppd), point);

			int needBeds = sickCount + isolatedCount - Constants.BED_COUNT;
			brush.Color = Color.FromRgb(227, 148, 118);
			point.Y += captionSize;
			dc.DrawText(new FormattedText($"病床缺口：{Math.Max(needBeds, 0)}", cultureInfo, FlowDirection, typeface, 16, brush.Clone(), ppd), point);

			brush.Color = Color.FromRgb(204, 187, 204);
			point.Y += captionSize;
			dc.DrawText(new FormattedText($"死亡人数：{toll}", cultureInfo, FlowDirection, typeface, 16, brush.Clone(), ppd), point);

			brush.Color = Color.FromRgb(0, 255, 35);
			point.Y += captionSize;
			dc.DrawText(new FormattedText($"治愈人次：{PersonPool.RECOVERED}", cultureInfo, FlowDirection, typeface, 16, brush.Clone(), ppd), point);

		}

		private readonly Button closeBtn = new Button();

		public System.Timers.Timer timer = new System.Timers.Timer();

		public void Run() {
			timer.Enabled = true;
			timer.Interval = 100;
			timer.AutoReset = true;
			timer.Elapsed += (sender, e) => {
				Dispatcher.Invoke(() => { });
				WorldTime++;
			};
			timer.Start();

			var endCheckThread = new Thread(() => {
				Thread.Sleep(500);
				while (true) {
					if (PersonPool.Instance.GetPeopleSize(Person.State.SHADOW) + PersonPool.Instance.GetPeopleSize(Person.State.CONFIRMED) + PersonPool.Instance.GetPeopleSize(Person.State.FREEZE) == 0) {
						timer.Stop();
						timer.Close();
						Dispatcher.Invoke(() => {
							closeBtn.Visibility = System.Windows.Visibility.Visible;
						});
						
					}
					Thread.Sleep(100);
				}
			}) {
				Name = "EndCheckThread"
			};
			endCheckThread.Start();

			updateThread.Name = "UpdateThread";
			updateThread.Start();
		}

	}
}
