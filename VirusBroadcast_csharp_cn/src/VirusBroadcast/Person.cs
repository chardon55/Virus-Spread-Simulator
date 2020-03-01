using System;
using System.Collections.Generic;
using System.Runtime.CompilerServices;
using System.Text;

namespace VirusBroadcast
{
    public class Person : Point
    {

        public City City { get; }

        private MoveTarget moveTarget;

        private readonly int SIGMA = 1; // σ

        private double targetXU;
        private double targetYU;
        private double targetSigma = 50;

        public enum State
        {
            NORMAL, SUSPECTED, SHADOW, CONFIRMED, FREEZE, DEATH
        }

        public Person(City city, int x, int y) : base(x, y)
        {
            City = city;
            targetXU = new Random().NextGaussian(100, x);
            targetYU = new Random().NextGaussian(100, y);
        }

        public bool WantMove() => new Random().NextGaussian(SIGMA, Constants.MU) > 0;

        public State CurState { get; set; } = State.NORMAL;

        private int infectedTime = 0;
        private int confirmedTime = 0;
        private int dieMoment = 0; // 死亡时刻，0表示未知

        public bool IsInfected() => CurState.CompareTo(State.SHADOW) >= 0;

        public void BeInfected()
        {
            CurState = State.SHADOW;
            infectedTime = MyPanel.WorldTime;
        }

        public double GetDistance(Person person)
        {
            return Math.Sqrt(Math.Pow(X - person.X, 2) + Math.Pow(Y - person.Y, 2));
        }

        private void Freeze()
        {
            CurState = State.FREEZE;
        }

        private void Action()
        {
            if (CurState == State.FREEZE || CurState == State.DEATH || !WantMove())
            {
                return;
            }

            if (moveTarget is null || moveTarget.IsArrived)
            {
                var targetX = new Random().NextGaussian(targetSigma, targetXU);
                var targetY = new Random().NextGaussian(targetSigma, targetYU);
                moveTarget = new MoveTarget((int)targetX, (int)targetY);
            }

            var dX = moveTarget.X - X;
            var dY = moveTarget.Y - Y;

            double length = Math.Sqrt(Math.Pow(dX, 2) + Math.Pow(dY, 2));

            if (length < 1)
            {
                moveTarget.IsArrived = true;
                return;
            }

            int udX = (int)(dX / length);
            if (udX == 0 && dX != 0)
            {
                udX = Round(dX);
            }

            int udY = (int)(dY / length);
            if (udY == 0 && dY != 0)
            {
                udY = Round(dY);
            }

            if (X >= Constants.CITY_WIDTH || X <= 0)
            {
                moveTarget = null;
                if (udX > 0)
                {
                    udX = -udX;
                }
            }

            if (Y >= Constants.CITY_HEIGHT || Y <= 0)
            {
                moveTarget = null;
                if (udY > 0)
                {
                    udY = -udY;
                }
            }

            MoveTo(udX, udY);
        }

        private int Round(int value)
        {
            if (value > 0)
                return 1;
            return -1;
        }

        public Bed useBed;

        private readonly double SAFE_DIST = Constants.SAFE_DIST;

        public void Update()
        {

            if (CurState == State.DEATH)
            {
                if (Hospital.Instance.InHospital(X, Y))
                {
                    X = -10;
                    Y = -10;
                }
                return;
            }

            if (CurState == State.FREEZE)
            {
                var success = new Random().NextDouble();
                if (success < Constants.RECOVERY_RATE)
                {
                    CurState = State.NORMAL;

                    var rand = new Random();
                    int x = (int)rand.NextGaussian(100, City.CenterX);
                    int y = (int)rand.NextGaussian(100, City.CenterY);
                    if (x > Constants.CITY_WIDTH)
                    {
                        x = Constants.CITY_WIDTH;
                    }
                    if (x < -Constants.CITY_WIDTH)
                    {
                        x = -Constants.CITY_WIDTH;
                    }
                    if (y > Constants.CITY_HEIGHT)
                    {
                        y = Constants.CITY_HEIGHT;
                    }
                    if (y < -Constants.CITY_HEIGHT)
                    {
                        y = -Constants.CITY_HEIGHT;
                    }

                    X = x;
                    Y = y;
                    Hospital.Instance.LeaveBed(useBed);
                    useBed = null;
                    PersonPool.RECOVERED++;
                }
            }

            if (CurState == State.CONFIRMED && MyPanel.WorldTime - confirmedTime >= Constants.HOSPITAL_RECEIVE_TIME)
            {
                var bed = Hospital.Instance.PickBed();
                if (!(bed is null))
                {
                    useBed = bed;
                    Freeze();
                    X = bed.X;
                    Y = bed.Y;
                    bed.IsEmpty = false;
                }
                else
                {

                }
            }

            if ((CurState == State.CONFIRMED || CurState == State.FREEZE) && MyPanel.WorldTime >= dieMoment)
            {
                var fatal = new Random().NextDouble();
                if (fatal < Constants.FATALITY_RATE)
                {
                    CurState = State.DEATH;
                    Hospital.Instance.LeaveBed(useBed);
                    useBed = null;
                }
            }

            double stdRnShadowTime = new Random().NextGaussian(25, Constants.SHADOW_TIME / 2);
            if (MyPanel.WorldTime - infectedTime > stdRnShadowTime && CurState == State.SHADOW)
            {
                CurState = State.CONFIRMED;
                confirmedTime = MyPanel.WorldTime;
            }
            Action();
            var people = PersonPool.Instance.PersonList;
            if (CurState == State.NORMAL && !Hospital.Instance.InHospital(X, Y))
            {
                foreach (var person in people)
                {
                    if (person.CurState != State.SHADOW && person.CurState != State.CONFIRMED)
                    {
                        continue;
                    }
                    var rand = new Random().NextDouble();
                    if (rand < Constants.BROAD_RATE && GetDistance(person) < SAFE_DIST && person != this)
                    {
                        BeInfected();
                        //break;
                    }
                }
            }
        }
    }

}
