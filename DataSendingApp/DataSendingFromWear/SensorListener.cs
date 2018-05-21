using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

using Android.App;
using Android.Content;
using Android.OS;
using Android.Runtime;
using Android.Views;
using Android.Widget;

using Android.Hardware;

namespace DataSendingFromWear
{
    public class SensorListener : Java.Lang.Object, ISensorEventListener
    {
        MainActivity _main;
        public SensorListener(MainActivity main)
        {
            _main = main;
        }
        public void OnAccuracyChanged(Sensor sensor, [GeneratedEnum] SensorStatus accuracy)
        {
            //doesn't have to do anything?
        }

        public void OnSensorChanged(SensorEvent e)
        {
            //tell mainactivity the new value if it is running
            if(_main!=null)
                _main.OnSensorValueChanged((int)e.Values[0]);
        }
    }
}