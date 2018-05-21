
using Android.App;
using Android.OS;
using Android.Widget;

namespace DataSendingFromWear
{
    [Activity(Label = "DrinkActivity")]
    public class DrinkActivity : Activity
    {
        protected override void OnCreate(Bundle savedInstanceState)
        {
            base.OnCreate(savedInstanceState);

            // This activity starts when user taps remember to drink-notification
            SetContentView(Resource.Layout.Drink_screen);

            //var v = FindViewById<LinearLayout>(Resource.Layout.Walk_screen)
            var okButton = FindViewById<Button>(Resource.Id.drinkOkButton);
            okButton.Click += delegate
            {
                //close this activity after buttonpress
                Finish();
            };

        }
    }
}