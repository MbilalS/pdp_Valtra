package com.valtra.valtraapp.activities;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.valtra.valtraapp.R;
import com.valtra.valtraapp.utils.Utils;

import java.util.concurrent.TimeUnit;

public class SafteyDialog {

    private long timeCountInMilliSeconds = 60000;

    private enum TimerStatus {
        STARTED,
        STOPPED
    }
    private TimerStatus timerStatus = TimerStatus.STOPPED;

    private ProgressBar progressBarCircle;
    private TextView textViewTime;
    private CountDownTimer countDownTimer;
    private TextView title;
    private Dialog dialog;
    private Context context;
    private double latitude,longitude;
    private Button yes, no;



    public void showDialog(Context context, double lat, double lang){
        this.latitude = lat;
        this.longitude = lang;
        this.context = context;
        dialog = new Dialog(context);
        dialog.setContentView(R.layout.safety_alert_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


        long timeCountInMilliSeconds = 60000;



        progressBarCircle = dialog.findViewById(R.id.progressBarCircle);
        textViewTime = dialog.findViewById(R.id.textViewTime);
        title = dialog.findViewById(R.id.title);
        yes = dialog.findViewById(R.id.btn_yes);
        no =dialog.findViewById(R.id.btn_no);


        //region Setting Font Type Face

        textViewTime.setTypeface(new Utils(context).headingTypeFace());
        title.setTypeface(new Utils(context).headingTypeFace());

        //endregion


        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopCountDownTimer();
                dialog.dismiss();
                MainActivity.flag = true;

            }
        });

        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleDanger();
                stopCountDownTimer();
            }
        });

        startStop();

        dialog.show();
    }

    private void startStop() {
        if (timerStatus == TimerStatus.STOPPED) {

            // call to initialize the timer values
            setTimerValues();
            // call to initialize the progress bar values
            setProgressBarValues();
            timerStatus = TimerStatus.STARTED;
            // call to start the count down timer
            startCountDownTimer();

        } else {
            timerStatus = TimerStatus.STOPPED;
            stopCountDownTimer();

        }

    }


    private void setTimerValues() {
        int time = 1;
        timeCountInMilliSeconds = time * 60 * 1000;
    }


    private void stopCountDownTimer() {
        countDownTimer.cancel();
    }

    /**
     * method to set circular progress bar values
     */
    private void setProgressBarValues() {

        progressBarCircle.setMax((int) timeCountInMilliSeconds / 1000);
        progressBarCircle.setProgress((int) timeCountInMilliSeconds / 1000);


    }



    /**
     * method to start count down timer
     */
    private void startCountDownTimer() {

        countDownTimer = new CountDownTimer(timeCountInMilliSeconds, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

                textViewTime.setText(hmsTimeFormatter(millisUntilFinished));

                progressBarCircle.setProgress((int) (millisUntilFinished / 1000));

            }

            @Override
            public void onFinish() {

                textViewTime.setText(hmsTimeFormatter(timeCountInMilliSeconds));
//                // call to initialize the progress bar values
//                setProgressBarValues();
                timerStatus = TimerStatus.STOPPED;
                handleDanger();

            }

        }.start();
        countDownTimer.start();
    }

    /**
     * method to convert millisecond to time format
     *
     * @param milliSeconds
     * @return HH:mm:ss time formatted string
     */
    private String hmsTimeFormatter(long milliSeconds) {

        @SuppressLint("DefaultLocale")
        String hms = String.format("%02d",
                TimeUnit.MILLISECONDS.toSeconds(milliSeconds) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milliSeconds)));

        return hms;


    }

    private void handleDanger(){
        dialog.dismiss();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference mRef =  database.getReference("Broadcast").child("Alert");
        mRef.child("Danger").setValue("true");
        mRef.child("Latitude").setValue(latitude);
        mRef.child("Longitude").setValue(longitude);

        context.startActivity(new Intent(context, MainActivityMap.class));
    }
}