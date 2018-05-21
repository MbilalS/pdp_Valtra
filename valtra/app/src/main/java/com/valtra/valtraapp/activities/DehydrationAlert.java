package com.valtra.valtraapp.activities;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.valtra.valtraapp.R;

public class DehydrationAlert {
    private Context context;
    private int time;
    private Dialog dialog;
    private Button yes;
    private TextView tv;

    public static boolean isTimerOn = false;

    public DehydrationAlert(final Context context, int time, final String message) {
        WalkNotification.isTimerOn = true;
        this.context = context;
        this.time = time;

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                dialog = new Dialog(context);
                dialog.setContentView(R.layout.dehydration_alert);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                yes = dialog.findViewById(R.id.btn_yes);
                tv = dialog.findViewById(R.id.title);

                tv.setText(message);
                WalkNotification.isTimerOn = false;
                dialog.show();

                yes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                        //MainActivity.flag = true;
                    }
                });
            }
        }, time);
    }
}
