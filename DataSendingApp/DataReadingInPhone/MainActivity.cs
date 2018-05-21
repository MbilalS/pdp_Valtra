using Android.App;
using Android.Widget;
using Android.OS;
using Android.Content;
using Android.Support.V4.Content;

namespace DataSendingFromWear
{
    [Activity(Label = "DataApiPhone", MainLauncher = true)]
    public class MainActivity : Activity
    {
        public int number;
        TextView tv;

        protected override void OnCreate(Bundle savedInstanceState)
        {
            base.OnCreate(savedInstanceState);

            //set up variables
            number = 0;

            // Set our view from the "main" layout resource
            SetContentView(Resource.Layout.Main);

            //set the layout
            tv = FindViewById<TextView>(Resource.Id.txtMessage);

            //set up the MessageReceiver
            IntentFilter filter = new IntentFilter(Intent.ActionSend);
            MessageReceiver receiver = new MessageReceiver(this);
            LocalBroadcastManager.GetInstance(this).RegisterReceiver(receiver, filter);

        }
        public void ProcessData(Intent i)
        {
            tv.Text = "Data from apilayer: " + i.GetIntExtra("HeartRateFromWear",0);
        }

        //make internal class for messagereceiver
        internal class MessageReceiver : BroadcastReceiver
        {
            MainActivity main;
            public MessageReceiver(MainActivity owner)
            {
                this.main = owner;
            }
            public override void OnReceive(Context context, Intent intent)
            {
                main.ProcessData(intent);
            }
        }
    }
}
