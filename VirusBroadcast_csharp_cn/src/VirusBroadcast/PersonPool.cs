using System;
using System.Collections.Generic;
using System.Text;

namespace VirusBroadcast {
	public class PersonPool {

		public static PersonPool Instance { get; } = new PersonPool();

		public List<Person> PersonList { get; } = new List<Person>();

		public int GetPeopleSize(Person.State state) {
			int i = 0;
			foreach (var person in PersonList) {
				if (person.CurState.CompareTo(state) == 0) {
					i++;
				}
			}
			return i;
		}

		public int GetPeopleSize() => PersonList.Count;

		public static int RECOVERED { get; set; } = 0;

		private PersonPool() {
			var city = new City(Constants.CITY_WIDTH, Constants.CITY_HEIGHT);
			// 添加城市居民
			for (var i = 0; i < Constants.POPULATION; i++) {
				var rand = new Random();
				int x = (int)rand.NextGaussian(100, city.CenterX);
				int y = (int)rand.NextGaussian(100, city.CenterY);
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
				PersonList.Add(new Person(city, x, y));
			}
		}
	}
}
