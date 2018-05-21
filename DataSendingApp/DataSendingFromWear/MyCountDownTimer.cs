using System;
using Debug = System.Diagnostics.Debug;
using System.Collections.Generic;
using System.Linq;
using System.Text;

using Android.App;
using Android.Content;
using Android.OS;
using Android.Runtime;
using Android.Views;
using Android.Widget;

namespace DataSendingFromWear
{
    public class MyCountDownTimer : CountDownTimer
    {
        ProgressBar pb;
        TextView tv;
        public MyCountDownTimer(long millis,long interval, ProgressBar pb, TextView tv):base(millis,interval)
        {
            this.pb = pb;
            this.tv = tv;
        }
        public override void OnFinish()
        {
            throw new NotImplementedException();
        }

        public override void OnTick(long millisUntilFinished)
        {
            if (pb != null)
                pb.Progress = (int)millisUntilFinished ;
            if (tv != null)
                tv.Text = (millisUntilFinished / 1000).ToString();
        }
    }
}