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

namespace DataSendingFromWear
{
    [Activity(Label = "HelpActivity")]
    public class HelpActivity : Activity
    {
        protected override void OnCreate(Bundle savedInstanceState)
        {
            base.OnCreate(savedInstanceState);

            SetContentView(Resource.Layout.Help_screen);

            //assign timer to the progressbar
            var pb = FindViewById<ProgressBar>(Resource.Id.helpProgressBar);
            var tv = FindViewById<TextView>(Resource.Id.helpNumberTextView);

            //set timer to 5 seconds and start it
            /*timer = new MyCountDownTimer(5000, 10, pb, tv);
            timer.Start();*/

            var cancelButton = FindViewById<Button>(Resource.Id.helpCancelButton);
            cancelButton.Click += delegate
            {
                //close this activity after buttonpress
                Finish();
            };
        }
    }
}