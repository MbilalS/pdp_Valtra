using System;
using System.Diagnostics;

using Android.App;
using Android.Content;
using Android.Runtime;
using Android.Views;
using Android.Widget;
using Android.OS;
using Android.Graphics;
using Android.Animation;
using Android.Support.Wearable.Views;
using Android.Support.V4.App;
using Android.Support.V4.View;
using Java.Interop;
using Android.Views.Animations;

using Android.Gms.Wearable;
using Android.Gms.Common.Apis;
using Android.Gms.Common;
using System.Linq;
using Android.Hardware;
using Xamarin.Android;

using TaskStackBuilder = Android.Support.V4.App.TaskStackBuilder;

namespace DataSendingFromWear
{
    [Activity(Label = "DataApiWear", MainLauncher = true, Icon = "@drawable/icon")]
    public class MainActivity : Activity, IDataApiDataListener, IGoogleApiClientConnectionCallbacks, IGoogleApiClientOnConnectionFailedListener
    {
        int heartRate;
        int previousLayout;
        const string syncPath = "/DataApi/Test";
        private IGoogleApiClient _client;
        Sensor heartRateSensor;
        SensorListener sl;
        SensorManager sensorManager;
        TextView heartRateTextView;

        ViewFlipper flipper;
        MyCountDownTimer timer;

        protected override void OnCreate(Bundle bundle)
        {
            base.OnCreate(bundle);

            // Set our view from the "main" layout resource
            SetContentView(Resource.Layout.Flipper);

            //set viewflipper to switch between pages
            flipper = FindViewById<ViewFlipper>(Resource.Id.simpleflipper);
            flipper.SetInAnimation(this, Android.Resource.Animation.FadeIn);
            flipper.SetOutAnimation(this, Android.Resource.Animation.FadeOut);

            //set up layouts actions
            var v = flipper.GetChildAt(0); //starting screen
            v.Touch += delegate
            {
                flipper.DisplayedChild = 1;
            };
            v = flipper.GetChildAt(1); //welcome screen
            v.Touch += delegate
            {
                flipper.DisplayedChild = 2;
            };
            v = flipper.GetChildAt(2); //connecting screen
            v.Touch += delegate
            {
                flipper.DisplayedChild = 3;
            };
            v = flipper.GetChildAt(3); //connecting mobile screen
            v.Touch += delegate
            {
                ShowDrinkNotification();
                ShowWalkNotification();
                flipper.DisplayedChild = 4;
            };
            //for mainmenu screen, all the buttons has to lead somewhere
            v = flipper.GetChildAt(4);
            //fetch the button press animation from resource and set it to all the buttons

            //recycle var button for every button and set actions for them
            var button1 = FindViewById<ImageButton>(Resource.Id.mainmenu_button1_id); //taskbutton
            button1.Click += OnTaskButtonClicked;

            var button2 = FindViewById<ImageButton>(Resource.Id.mainmenu_button2_id); //gearbutton
            //button2.Click += OnTaskButtonClicked;

            var button3 = FindViewById<ImageButton>(Resource.Id.mainmenu_button3_id); //creepyguybutton
            //button3.Click += OnTaskButtonClicked;

            var button4 = FindViewById<ImageButton>(Resource.Id.mainmenu_button4_id); //helpbutton
            button4.Click += OnHelpButtonClicked;

            //in the tasks-screen, we make tasks dynamically based on what kind of- and how many tasks need to be done
            v = flipper.GetChildAt(5);

            var task_storage = FindViewById<LinearLayout>(Resource.Id.task_storage_layout);
            //add taks to the task storgae (Linearlayout)
            task_storage.AddView(CreateTask("jfdslkkkkjndfkjnfd fds ds df kfd hdf hfd f ddhf dfh fh hfd hd hdf hfd hfd hdf hffdh fdh fdhfdfdh dfh fdh fd df fsdeg ewdsf ds ss df sd sd sd sd fe ge  sd d er  werferw f er fer f er f erf er fe f re   r"));
            task_storage.AddView(CreateTask("moj"));

            //init heartrate related stuff
            heartRate = 0;
            sensorManager = (SensorManager)GetSystemService(SensorService);
            heartRateSensor = sensorManager.GetDefaultSensor(SensorType.HeartRate);
            sl = new SensorListener(this);

            //create and connect googleapi client
            _client = new GoogleApiClientBuilder(this, this, this).AddApi(WearableClass.Api).Build();
            _client.Connect();
        }
        protected override void OnStart()
        {
            base.OnStart();
        }
        protected override void OnPause()
        {
            base.OnPause();
            _client.Disconnect();
        }
        protected override void OnResume()
        {
            base.OnResume();
            if (!_client.IsConnected)
                _client.Connect();
            //register sensor listener
            if (sensorManager.RegisterListener(sl, heartRateSensor, SensorDelay.Normal))
            {
                System.Diagnostics.Debug.WriteLine("SensorListener registered");
            }
        }
        protected override void OnStop()
        {
            base.OnStop();
            _client.Disconnect();
            //register sensor listener
            sensorManager.UnregisterListener(sl);
            System.Diagnostics.Debug.WriteLine("SensorListener unregistered");
        }
        public void OnConnected(Bundle p0)
        {
        }
        public void OnConnectionFailed(ConnectionResult p0)
        {
            throw new NotImplementedException();
        }
        public void OnConnectionSuspended(int p0)
        {
            throw new NotImplementedException();
        }
        public void OnDataChanged(DataEventBuffer p0)
        {
            throw new NotImplementedException();
        }
        public void SendData()
        {
            //send data to DataApi layer
            if (!_client.IsConnected)
                return;

            PutDataMapRequest mrequest = PutDataMapRequest.Create(syncPath);
            mrequest.DataMap.PutInt("heartratedata", heartRate);
            PutDataRequest request = mrequest.AsPutDataRequest();

            WearableClass.DataApi.PutDataItem(_client, request);
        }
        public void OnSensorValueChanged(int newVal)
        {
            //change heartrate textview text and send data to data layer
            heartRate = newVal;
            if (heartRateTextView != null)
                heartRateTextView.Text = "HeartRate: " + heartRate;
            System.Diagnostics.Debug.WriteLine("Sensor values: " + heartRate);
            SendData();
            //Wait(1000);
        }
        private void OnTaskButtonClicked(object sender,EventArgs args)
        {
            //start animation when button is pressed
            ImageButton ib = sender as ImageButton;

            Animation buttonPressAnim = new ScaleAnimation(1, 0.8f, 1, 0.8f,50,50);
            buttonPressAnim.Duration = 100;

            ib.StartAnimation(buttonPressAnim);
            //do something else when animation finishes
            ib.Animation.AnimationEnd += delegate { flipper.DisplayedChild = 5; /*goto tasks-screen*/ };
        }
        private View CreateTask(String msg)
        {
            //create task view from scratch
            var ret = new RelativeLayout(this); //container for textview and done-buttons
            ret.LayoutParameters = new ViewGroup.LayoutParams(240, 240);
            ret.Background = GetDrawable(Resource.Drawable.task_layout);
            
            //set dimensions for ret (fullscreen view)
            ret.SetMinimumHeight(240);
            ret.SetMinimumWidth(240);
            ret.SetPadding(15, 25, 5, 5);

            //linearlayout to ocntain scrollview
            LinearLayout textcontainer = new LinearLayout(this);
            textcontainer.LayoutParameters = new ViewGroup.LayoutParams(210, 140);

            //scrollview to cointain task message
            var sv = new ScrollView(this);
            sv.VerticalFadingEdgeEnabled = false;

            //textview to contain the message
            var tv = new TextView(this);
            tv.TextSize = 16;
            tv.SetMaxWidth(210);
            tv.SetMinWidth(210);
            tv.SetPadding(5,0,5,0);
            tv.Text = msg;
            tv.SetTextColor(Android.Graphics.Color.Black);
            sv.AddView(tv);
            textcontainer.AddView(sv);

            ret.AddView(textcontainer);

            //done-button
            LinearLayout buttoncontainer = new LinearLayout(this);
            buttoncontainer.SetX(10);
            buttoncontainer.SetY(137);
            var db = new Button(this);
            db.Text = "Done";
            //db.Typeface = Typeface.CreateFromAsset(this.Assets,)
            db.SetMinWidth(190);
            db.SetMaxWidth(190);
            db.SetMinHeight(25);
            db.SetMaxHeight(25);
            db.Background = null;
            db.Click += delegate { System.Diagnostics.Debug.WriteLine("buttonia painettu"); };
            buttoncontainer.AddView(db);
            ret.AddView(buttoncontainer);
            return ret;
        }
        private void OnHelpButtonClicked(object sender, EventArgs args)
        {
            //start animation when button is pressed
            ImageButton ib = sender as ImageButton;

            Animation buttonPressAnim = new ScaleAnimation(1, 0.8f, 1, 0.8f, 50, 50);
            buttonPressAnim.Duration = 100;

            ib.StartAnimation(buttonPressAnim);
            //do something else when animation finishes
            ib.Animation.AnimationEnd += delegate { StartHelpActivity();/*flipper.DisplayedChild = 7; goto tasks-screen*/ };
        }
        private void StartHelpActivity()
        {

        }
        private void ShowDrinkNotification()
        {
            // When the user clicks the notification, SecondActivity will start up.
            Intent resultIntent = new Intent(this, typeof(DrinkActivity));

            // Construct a back stack for cross-task navigation:
            TaskStackBuilder stackBuilder = TaskStackBuilder.Create(this);
            stackBuilder.AddParentStack(Java.Lang.Class.FromType(typeof(DrinkActivity)));
            stackBuilder.AddNextIntent(resultIntent);

            // Create the PendingIntent with the back stack:            
            PendingIntent resultPendingIntent = stackBuilder.GetPendingIntent(0, (int)PendingIntentFlags.UpdateCurrent);

            //create notification for walk-screen
            NotificationCompat.Builder nBuilder = new NotificationCompat.Builder(this)
                .SetSmallIcon(Resource.Drawable.Icon)
                .SetContentTitle("Remember to drink!")
                .SetContentIntent(resultPendingIntent)
                .SetContentText("You have to drink to stay alive goddammit!")
                .SetPriority((int)NotificationPriority.High)//priority is high because user can be in danger!
                .SetAutoCancel(true);

            //publish the notification
            NotificationManager notificationManager = (NotificationManager)GetSystemService(Context.NotificationService);
            notificationManager.Notify(10000, nBuilder.Build());
        }
        private void ShowWalkNotification()
        {
            // When the user clicks the notification, SecondActivity will start up.
            Intent resultIntent = new Intent(this, typeof(WalkActivity));

            // Construct a back stack for cross-task navigation:
            TaskStackBuilder stackBuilder = TaskStackBuilder.Create(this);
            stackBuilder.AddParentStack(Java.Lang.Class.FromType(typeof(WalkActivity)));
            stackBuilder.AddNextIntent(resultIntent);

            // Create the PendingIntent with the back stack:            
            PendingIntent resultPendingIntent = stackBuilder.GetPendingIntent(0, (int)PendingIntentFlags.UpdateCurrent);

            //create notification for walk-screen
            NotificationCompat.Builder nBuilder = new NotificationCompat.Builder(this)
                .SetSmallIcon(Resource.Drawable.Icon)
                .SetContentTitle("Are you stressed?")
                .SetContentIntent(resultPendingIntent)
                .SetContentText("Maybe a short walk would do good?")
                .SetPriority((int)NotificationPriority.High)//priority is high because user can be in danger!
                .SetAutoCancel(true);

            //publish the notification
            NotificationManager notificationManager = (NotificationManager)GetSystemService(Context.NotificationService);
            notificationManager.Notify(10001, nBuilder.Build());
        }
    }
}
