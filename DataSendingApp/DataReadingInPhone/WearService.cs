using System.Linq;
using System.Text;

using Android.App;
using Android.Content;
using Android.OS;
using Android.Runtime;
using Android.Views;
using Android.Widget;

using Android.Gms.Wearable;
using Android.Gms.Common.Apis;
using Android.Support.V4.Content;

namespace DataSendingFromWear
{
    [Service]
    [IntentFilter(new[] { "com.google.android.gms.wearable.BIND_LISTENER" })]
    class Listener : WearableListenerService
    {
        const string syncPath = "/DataApi/Test";

        public override void OnCreate()
        {
            base.OnCreate();
        }
        public override void OnDataChanged(DataEventBuffer dataEvents)
        {
            var dataEvent = Enumerable.Range(0, dataEvents.Count)
                                      .Select(i => dataEvents.Get(i).JavaCast<IDataEvent>())
                                      .FirstOrDefault(x => x.Type == DataEvent.TypeChanged && x.DataItem.Uri.Path.Equals(syncPath));

            if (dataEvent == null)
                return;

            //get data from wearable  
            var dataMapItem = DataMapItem.FromDataItem(dataEvent.DataItem);
            var map = dataMapItem.DataMap;
            int heartRate = dataMapItem.DataMap.GetInt("heartratedata");
            System.Diagnostics.Debug.WriteLine("luku: " + heartRate);

            //send data to MainActivity
            Intent intent = new Intent();
            intent.SetAction(Intent.ActionSend);
            intent.PutExtra("HeartRateFromWear", heartRate);
            LocalBroadcastManager.GetInstance(this).SendBroadcast(intent);
        }
    }
}