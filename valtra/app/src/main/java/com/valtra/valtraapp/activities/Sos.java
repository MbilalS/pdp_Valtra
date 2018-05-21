package com.valtra.valtraapp.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.valtra.valtraapp.R;

public class Sos {

    private Dialog dialog;
    private Button yes;
    private TextView tv;

    public Sos(final Context context, final double latitude, final double longitude) {

        dialog = new Dialog(context);
        dialog.setContentView(R.layout.sos);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        yes = dialog.findViewById(R.id.btn_yes);
        tv = dialog.findViewById(R.id.title);

        tv.setText("Help is own its way!!");
        dialog.show();

        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference mRef =  database.getReference("SOS");
                mRef.child("Latitude").setValue(latitude);
                mRef.child("Longitude").setValue(longitude);

                Toast.makeText(context, ""+latitude+", "+longitude, Toast.LENGTH_SHORT).show();

            }
        });

    }
}
