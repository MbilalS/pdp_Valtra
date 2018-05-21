package com.valtra.valtraapp.activities;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.WearableListenerService;

import java.awt.font.NumericShaper;

public class Listener extends WearableListenerService{

    String syncPath = "/DataApi/Test";

    @Override
    public void onCreate() {
        super.onCreate();
    }


    @Override
    public void onDataChanged(DataEventBuffer dataEvents){

        for (DataEvent dataevent : dataEvents)
        {
            if (dataevent == null)
                return;
            if (dataevent.getType() == DataEvent.TYPE_CHANGED)
            {
                //data item changed
                DataMapItem dataMapItem = DataMapItem.fromDataItem(dataevent.getDataItem());
                int heartRate = dataMapItem.getDataMap().getInt("heartratedata");

                //send data to MainActivity
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_SEND);
                intent.putExtra("HeartRateFromWear", heartRate);
                LocalBroadcastManager.getInstance(this).sendBroadcast(intent);

                break;
            }
        }
    }

}
