package com.valtra.valtraapp.activities;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.valtra.valtraapp.R;
import com.valtra.valtraapp.utils.RoundCorneredImage;
import com.valtra.valtraapp.utils.Utils;

import java.io.IOException;
import java.io.InputStream;

//bluetooth stuff (Sampo 11.5.)

import org.json.JSONException;
import org.json.JSONObject;

import static java.lang.Thread.sleep;


public class MainActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private TextView tv_name, tv_status, tv, tvBluetooth;
    private ImageView img_profilepic, map;
    public static final int PICK_IMAGE = 1;
    Uri img;
    private FirebaseAuth mAuth;
    private String uid;
    private Button btn_signout, btn_tasks, btn_sos;
    Target target;

    private GoogleMap mMap;
    private GoogleApiClient client;
    private LocationRequest locationRequest;
    private Location lastlocation;
    private Marker currentLocationmMarker;
    public static final int REQUEST_LOCATION_CODE = 99;
    double latitude, longitude;
    SupportMapFragment mapFragment;
    DatabaseReference userRef;
    String currentNotification;
    static String passName;
    static boolean flag = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //region Making Activity Full Screen

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //endregion

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //region Fire base Auth
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            uid = currentUser.getUid();
            //Toast.makeText(this, uid, Toast.LENGTH_SHORT).show();

        } else {
            finish();
            startActivity(new Intent(MainActivity.this, Login.class));
        }


        //endregion

//        new Thread() {
//            public void run() {
//                MainActivity.this.runOnUiThread(new Runnable() {
//                    public void run() {
//                       // while(true) {
//                            if (!WalkNotification.isTimerOn) {
//                                new WalkNotification(MainActivity.this, 10000, "You have been working for a while now, take a short walk to refresh yourself!!");
//                            }
//                       // }
//                    }
//                });
//            }
//        }.start();

//        if (!WalkNotification.isTimerOn) {
//            new WalkNotification(MainActivity.this, 30000, "You have been working for a while now, take a short walk to refresh yourself!!");
//        }

        if (!DehydrationAlert.isTimerOn) {
            new DehydrationAlert(MainActivity.this, 50000, "Drink some water to avoid dehydration!!");
        }

        //region Initializations

        img_profilepic = findViewById(R.id.img_profilepic);
        tv_name = findViewById(R.id.tv_name);
        tv_status = findViewById(R.id.tv_status);
        btn_signout = findViewById(R.id.btn_signout);
        btn_sos = findViewById(R.id.btn_sos);
        btn_tasks = findViewById(R.id.btn_tasks);

        //endregion

        //region Setting Font Type Face

        tv_status.setTypeface(new Utils(this).bodyTypeFace());
        tv_name.setTypeface(new Utils(this).headingTypeFace());

        //endregion


        img_profilepic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //following line is used to change profile pic
                startActivity(new Intent(MainActivity.this, Gallery.class));
//                new SafteyDialog().showDialog(MainActivity.this,latitude, longitude);
            }
        });

        btn_sos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //new Sos(MainActivity.this, latitude, longitude);
                new SafteyDialog().showDialog(MainActivity.this, latitude, longitude);
            }
        });

        btn_signout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference mRefstatus = database.getReference("Users").child(uid).child("Status");
                mRefstatus.setValue("Offline");
                mAuth.signOut();
                if (mAuth.getCurrentUser() != null) {
                    Toast.makeText(MainActivity.this, "SignOut Failed!!", Toast.LENGTH_SHORT).show();
                } else
                    startActivity(new Intent(MainActivity.this, SplashScreen.class));
            }
        });

        btn_tasks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, TasksList.class));
            }
        });


        //region Firebase - getting the profile items

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        userRef = database.getReference("Users").child(uid);
        DatabaseReference mRef = database.getReference("Users").child(uid).child("Name");
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String value = dataSnapshot.getValue(String.class);
                passName = value;
                tv_name.setText(value);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                tv_name.setText("failed to read!!");

            }
        });

        DatabaseReference mRefstatus = database.getReference("Users").child(uid).child("Status");
        mRefstatus.setValue("Online");
        mRefstatus.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String value = dataSnapshot.getValue(String.class);
                tv_status.setText(value);
                tv_status.setTextColor(Color.parseColor("#2E7D32"));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                tv_status.setText("failed to read!!");

            }
        });


        DatabaseReference mRefphotoUrl = database.getReference("Users").child(uid).child("PhotoUrl");
        mRefphotoUrl.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String value = dataSnapshot.getValue(String.class);
                Picasso.get().load(value).into(target);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                img_profilepic.setImageResource(R.drawable.profile);

            }
        });

        final AlertDialog notificationDialog = new AlertDialog.Builder(MainActivity.this).create();
        notificationDialog.setTitle("Notification");
        notificationDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });

        DatabaseReference notificatoin = database.getReference("Broadcast").child("Message").child("Current");
        notificatoin.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                currentNotification = dataSnapshot.getValue(String.class);

                Toast.makeText(MainActivity.this, ""+currentNotification, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        final DatabaseReference checknotificatoin = database.getReference("Broadcast").child("Message").child("Check");
        checknotificatoin.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String checkNotification = dataSnapshot.getValue(String.class);
                if(checkNotification != null && checkNotification.equals("true")) {
                    notificationDialog.setMessage(currentNotification);
                    notificationDialog.show();
                    checknotificatoin.setValue("false");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        DatabaseReference mRefAlert = database.getReference("Broadcast").child("Alert").child("Danger");
        final DatabaseReference setIDref = database.getReference("Broadcast").child("Alert") ;
        final AlertDialog finalAlertDialog = new AlertDialog.Builder(MainActivity.this).create();
        finalAlertDialog.setTitle("Alert");
        finalAlertDialog.setMessage("Your fellow farmer is in danger!!");
//        finalAlertDialog.setIcon(R.drawable.tick);
        finalAlertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        setIDref.child("HelperID").setValue(uid);
                        setIDref.child("HelperLongitude").setValue(longitude);
                        setIDref.child("HelperLatitide").setValue(latitude);

//                        setIDref.child("HelperLongitude").setValue(26.720469);
//                        setIDref.child("HelperLatitide").setValue(58.372432);

                        startActivity(new Intent(MainActivity.this, MainActivityMap.class));
                        dialog.dismiss();
                    }
                });
        mRefAlert.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String value = dataSnapshot.getValue(String.class);
                if (value != null && value.equals("true"))
                    finalAlertDialog.show();

//                    new SafteyDialog().showDialog(MainActivity.this, latitude, longitude);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        target = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                img_profilepic.setImageBitmap(RoundCorneredImage.getRoundedCornerBitmap(bitmap, 1500));
            }

            @Override
            public void onBitmapFailed(Exception e, Drawable errorDrawable) {

            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        };

        //endregion

        //region Google Maps

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();

        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //endregion


        //sampo
        //set textview for displaying heartrate data on screen
        tv = findViewById(R.id.txtMessage);
        tvBluetooth = findViewById(R.id.txtMessageBluetooth);

        //set up messagereceiver
        IntentFilter filter = new IntentFilter(Intent.ACTION_SEND);
        MessageReceiver receiver = new MessageReceiver(this);
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, filter);

        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new SafteyDialog().showDialog(MainActivity.this, latitude, longitude);

            }
        });


        //sampo
        Intent intent = new Intent(MainActivity.this,BluetoothService.class);
        startService(intent);
        //set up messagereceiver
        IntentFilter bluetoothFilter = new IntentFilter(Intent.ACTION_SEND);
        MessageReceiverBluetooth bluetoothreceiver = new MessageReceiverBluetooth(this);
        LocalBroadcastManager.getInstance(this).registerReceiver(bluetoothreceiver, bluetoothFilter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // If the resultCode is RESULT_OK and there is some data we know that an image was picked.
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            try {
                final Uri imageUri = data.getData();
                final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);

                map.setImageBitmap(selectedImage);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }
    }


    protected synchronized void buildGoogleApiClient() {
        client = new GoogleApiClient.Builder(this).addConnectionCallbacks(this).addOnConnectionFailedListener(this).addApi(LocationServices.API).build();
        client.connect();
    }

    @Override
    public void onLocationChanged(Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        lastlocation = location;
        if (currentLocationmMarker != null) {
            currentLocationmMarker.remove();
        }
        Log.d("lat = ", "" + latitude);
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 13));

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(location.getLatitude(), location.getLongitude()))      // Sets the center of the map to location user
                .zoom(17)                   // Sets the zoom
                .build();                   // Creates a CameraPosition from the builder
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        userRef.child("Latitude").setValue(latitude);
        userRef.child("Longitude").setValue(longitude);


        if (client != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(client, this);
        }

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(100);
        locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(client, locationRequest, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    public boolean checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_CODE);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_CODE);
            }
            return false;

        } else
            return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_LOCATION_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        if (client == null) {
                            buildGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }
                } else {
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_LONG).show();
                }
        }
    }


    //sampo

    public void processData(Intent i) {

        int HRvalue = i.getIntExtra("HeartRateFromWear",0);

        if(HRvalue != 0) {
            tv.setText(String.format("HR: %d", HRvalue));
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference HR = database.getReference("Users").child(uid);
            HR.child("HeartRate").setValue("HR: " + HRvalue);

            if(HRvalue > 105){
                new SafteyDialog().showDialog(MainActivity.this, latitude, longitude);
            }
        }
    }

    public class MessageReceiver extends BroadcastReceiver {
        MainActivity main;

        @Override
        public void onReceive(Context context, Intent intent) {
            main.processData(intent);
        }


        public MessageReceiver(MainActivity owner){
            this.main = owner;
        }
    }



    public void BluetoothProcessData(Intent i) {

        //accelorometer data

        byte[] mmBuffer = new byte[1024];
        Log.d("Vibrationvalue = ", "impact: " + i);
        mmBuffer = i.getByteArrayExtra("acceleration_values");
        if(mmBuffer != null) {
            String json = new String(mmBuffer);
            String amplitude = null, frequency = null;

            JSONObject obj = null;
            try {
                obj = new JSONObject(json);
                try {
                    if (obj != null) {
                        obj = obj.getJSONObject("hookmeasurement");
                        String impact = (String) obj.get("impact");
                        JSONObject obj2 = obj.getJSONObject("vibration");
                        frequency = (String) obj2.get("frequency");
                        amplitude = (String) obj2.get("amplitude");

                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        DatabaseReference name = database.getReference("Users").child(uid);
                        name.child("Vibration").setValue("impact: " + impact + ", frequency: " + frequency + ", amplitude: " + amplitude);

                        if (impact != null && impact.equals("1") && flag == true && GlobalVars.count == 0) {
                            flag = false;
                            new SafteyDialog().showDialog(MainActivity.this, latitude, longitude);
                            GlobalVars.count = 1;
                        }
                        if(impact.equals("0")) {
                            GlobalVars.count = 0;
                        }

                        Log.d("Vibrationvalue = ", "impact: " + impact + " frequency: " + frequency + " amplitude: " + amplitude);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } catch (Throwable t) {
                Log.e("My App", "Could not parse malformed JSON: \"" + json + "\"");
            }


            tvBluetooth.setText(String.format("Vibration: %s@%sHz", amplitude, frequency));
        }


        //String Vibrationvalue = json;
        //tv.setText(String.format("HR: %d, Vibration: %s@%sHz", HRvalue, amplitude, frequency));

        //Log.d("Vibrationvalue = ", "" + Vibrationvalue);

        //  String[] parts = Vibrationvalue.split("\n");
//        String part1 = parts[0];
//        String part2 = parts[1];
//        String part3 = parts[2];

//        Log.d("Vibrationvalue = ", "parts: Start " + parts[0] + "           End");
//        Log.d("Vibrationvalue = ", "parts:  Start" + parts[1] + "           End");
    }


    public class MessageReceiverBluetooth extends BroadcastReceiver {
        MainActivity main;

        @Override
        public void onReceive(Context context, Intent intent) {
            main.BluetoothProcessData(intent);
        }


        public MessageReceiverBluetooth(MainActivity owner){
            this.main = owner;
        }
    }
}
