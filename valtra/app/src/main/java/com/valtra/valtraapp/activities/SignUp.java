package com.valtra.valtraapp.activities;

import android.animation.TimeInterpolator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.valtra.valtraapp.R;
import com.valtra.valtraapp.utils.Utils;

import java.util.Random;

import static com.valtra.valtraapp.utils.Constants.MyPREFERENCES;
import static com.valtra.valtraapp.utils.Constants.Name;

public class SignUp extends AppCompatActivity {

    //region variables

    RelativeLayout rl_signup;

    EditText et_signup_username, et_signup_email, et_signup_pass, et_signup_re_pass;
    Button btn_SignUp;

    TextView tv_signup_title, tv_Signup_login;


    //Variables for Activity Transition
    private static final TimeInterpolator sDecelerator = new DecelerateInterpolator();
    private static final TimeInterpolator sAccelerator = new AccelerateInterpolator();
    private static final String TAG_NAME = "activity_login_screen";
    private static final int ANIM_DURATION = 500;
    int mLeftDelta, mLeftDeltaEmail, mLeftDeltaPass;
    int mTopDelta, mTopDeltaEmail, mTopDeltaPass;
    float mWidthScale;
    float mHeightScale;
    private int mOriginalOrientation_email;
    ColorDrawable mBackground;

    public static Boolean isComingBackToScreen1 = false;

    //FireBase
    private FirebaseAuth mAuth;

    //Shared Preference
    SharedPreferences sharedpreferences;


    //endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //region Making Activity Full Screen

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //endregion

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        //Region Firebase initialization

        mAuth = FirebaseAuth.getInstance();
        //endregion

        //region Initializations

        rl_signup = findViewById(R.id.rl_SignUp);
        btn_SignUp = findViewById(R.id.btn_signup);
        et_signup_username =  findViewById(R.id.et_signup_username);
        et_signup_email = findViewById(R.id.et_signup_email);
        et_signup_pass = findViewById(R.id.et_signup_password);
        et_signup_re_pass =  findViewById(R.id.et_signup_re_password);
        tv_signup_title = findViewById(R.id.tv_signup_title);
        tv_Signup_login = findViewById(R.id.tv_Signup_login);


        //endregion

        //region Setting Font Type Face

        tv_signup_title.setTypeface(new Utils(this).headingTypeFace());
        btn_SignUp.setTypeface(new Utils(this).headingTypeFace());

        //endregion

        //region Getting The Intent Bundle

        Bundle bundle = getIntent().getExtras();

        final int bgTop_email = bundle.getInt(TAG_NAME + ".top_email");
        final int bgLeft_email = bundle.getInt(TAG_NAME + ".left_email");
        final int bgWidth_email = bundle.getInt(TAG_NAME + ".width_email");
        final int bgHeight_email = bundle.getInt(TAG_NAME + ".height_email");


        final int bgTop_pass = bundle.getInt(TAG_NAME + ".top_pass");
        final int bgLeft_pass = bundle.getInt(TAG_NAME + ".left_pass");
        final int bgWidth_pass = bundle.getInt(TAG_NAME + ".width_pass");
        final int bgHeight_pass = bundle.getInt(TAG_NAME + ".height_pass");


        final int bgTop_btn = bundle.getInt(TAG_NAME + ".top_login");
        final int bgLeft_btn = bundle.getInt(TAG_NAME + ".left_login");
        final int bgWidth_btn = bundle.getInt(TAG_NAME + ".width_login");
        final int bgHeight_btn = bundle.getInt(TAG_NAME + ".height_login");


        mOriginalOrientation_email = bundle.getInt(TAG_NAME + ".orientation");


        mBackground = new ColorDrawable(Color.WHITE);
        rl_signup.setBackground(mBackground);

        //endregion

        //region Setting our view Equal to other view coming from previous Activity

        // Only run the animation if we're coming from the parent activity, not if
        // we're recreated automatically by the window manager (e.g., device rotation)

        //Email
        if (savedInstanceState == null) {

            isComingBackToScreen1 = false;

            ViewTreeObserver observer1 = et_signup_email.getViewTreeObserver();
            observer1.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {

                @Override
                public boolean onPreDraw() {
                    et_signup_email.getViewTreeObserver().removeOnPreDrawListener(this);

                    // Figure out where the thumbnail and full size versions are, relative to the screen and each other
                    int[] screenLocation = new int[2];
                    et_signup_email.getLocationOnScreen(screenLocation);
                    mLeftDelta = bgLeft_email - screenLocation[0];
                    mTopDelta = bgTop_email - screenLocation[1];

                    mLeftDeltaEmail = mLeftDelta;
                    mTopDeltaEmail = mTopDelta;

                    // Scale factors to make the large version the same size as the thumbnail
                    mWidthScale = (float) bgWidth_email / et_signup_email.getWidth();
                    mHeightScale = (float) bgHeight_email / et_signup_email.getHeight();

                    et_signup_email.setPivotX(0);
                    et_signup_email.setPivotY(0);
                    et_signup_email.setScaleX(mWidthScale);
                    et_signup_email.setScaleY(mHeightScale);
                    et_signup_email.setTranslationX(mLeftDelta);
                    et_signup_email.setTranslationY(mTopDelta);

                    // Animate scale and translation to go from bg to full size
                    et_signup_email.animate().setDuration(500).
                            scaleX(1).scaleY(1).
                            translationX(0).translationY(0).
                            setInterpolator(sDecelerator);

                    return true;
                }
            });

        }


        ViewTreeObserver observer9 = et_signup_username.getViewTreeObserver();
        observer9.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {

            @Override
            public boolean onPreDraw() {
                et_signup_username.getViewTreeObserver().removeOnPreDrawListener(this);

                et_signup_username.setTranslationX(new Utils().screenDimension(SignUp.this)[0]);

                return true;
            }
        });

        ViewTreeObserver observer11 = et_signup_re_pass.getViewTreeObserver();
        observer11.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {

            @Override
            public boolean onPreDraw() {
                et_signup_re_pass.getViewTreeObserver().removeOnPreDrawListener(this);

                et_signup_re_pass.setTranslationX(-new Utils().screenDimension(SignUp.this)[0]);

                return true;
            }
        });

        //Password
        if (savedInstanceState == null) {

            isComingBackToScreen1 = false;

            ViewTreeObserver observer3 = et_signup_pass.getViewTreeObserver();
            observer3.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {

                @Override
                public boolean onPreDraw() {
                    et_signup_pass.getViewTreeObserver().removeOnPreDrawListener(this);

                    // Figure out where the thumbnail and full size versions are, relative to the screen and each other
                    int[] screenLocation = new int[2];
                    et_signup_pass.getLocationOnScreen(screenLocation);
                    mLeftDelta = bgLeft_pass - screenLocation[0];
                    mTopDelta = bgTop_pass - screenLocation[1];

                    mLeftDeltaPass = mLeftDelta;
                    mTopDeltaPass = mTopDelta;

                    // Scale factors to make the large version the same size as the thumbnail
                    mWidthScale = (float) bgWidth_pass / et_signup_pass.getWidth();
                    mHeightScale = (float) bgHeight_pass / et_signup_pass.getHeight();

                    et_signup_pass.setPivotX(0);
                    et_signup_pass.setPivotY(0);
                    et_signup_pass.setScaleX(mWidthScale);
                    et_signup_pass.setScaleY(mHeightScale);
                    et_signup_pass.setTranslationX(mLeftDelta);
                    et_signup_pass.setTranslationY(mTopDelta);

                    // Animate scale and translation to go from bg to full size
                    et_signup_pass.animate().setDuration(500).
                            scaleX(1).scaleY(1).
                            translationX(0).translationY(0).
                            setInterpolator(sDecelerator);

                    return true;
                }
            });

        }

        //Re-enter Password
     /*   if (savedInstanceState == null) {

            isComingBackToScreen1 = false;

            ViewTreeObserver observer4 = et_signup_re_pass.getViewTreeObserver();
            observer4.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {

                @Override
                public boolean onPreDraw() {
                    et_signup_re_pass.getViewTreeObserver().removeOnPreDrawListener(this);

                    // Figure out where the thumbnail and full size versions are, relative to the screen and each other
                    int[] screenLocation = new int[2];
                    et_signup_re_pass.getLocationOnScreen(screenLocation);
                    mLeftDelta = bgLeft_btn - screenLocation[0];
                    mTopDelta = bgTop_btn - screenLocation[1];

                    // Scale factors to make the large version the same size as the thumbnail
                    mWidthScale = (float) bgWidth_btn / et_signup_re_pass.getWidth();
                    mHeightScale = (float) bgHeight_btn / et_signup_re_pass.getHeight();

                    et_signup_re_pass.setPivotX(0);
                    et_signup_re_pass.setPivotY(0);
                    et_signup_re_pass.setScaleX(mWidthScale);
                    et_signup_re_pass.setScaleY(mHeightScale);
                    et_signup_re_pass.setTranslationX(mLeftDelta);
                    et_signup_re_pass.setTranslationY(mTopDelta);

                    // Animate scale and translation to go from bg to full size
                    et_signup_re_pass.animate().setDuration(500).
                            scaleX(1).scaleY(1).
                            translationX(0).translationY(0).
                            setInterpolator(sDecelerator);

                    return true;
                }
            });

        }*/


        //SignUp button
        if (savedInstanceState == null) {

            isComingBackToScreen1 = false;

            ViewTreeObserver observer5 = btn_SignUp.getViewTreeObserver();
            observer5.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {

                @Override
                public boolean onPreDraw() {
                    btn_SignUp.getViewTreeObserver().removeOnPreDrawListener(this);

                    // Figure out where the thumbnail and full size versions are, relative to the screen and each other
                    int[] screenLocation = new int[2];
                    btn_SignUp.getLocationOnScreen(screenLocation);
                    mLeftDelta = bgLeft_btn - screenLocation[0];
                    mTopDelta = bgTop_btn - screenLocation[1];

                    // Scale factors to make the large version the same size as the thumbnail
                    mWidthScale = (float) bgWidth_btn / btn_SignUp.getWidth();
                    mHeightScale = (float) bgHeight_btn / btn_SignUp.getHeight();

                    btn_SignUp.setPivotX(0);
                    btn_SignUp.setPivotY(0);
                    btn_SignUp.setScaleX(mWidthScale);
                    btn_SignUp.setScaleY(mHeightScale);
                    btn_SignUp.setTranslationX(mLeftDelta);
                    btn_SignUp.setTranslationY(mTopDelta);

                    // Animate scale and translation to go from bg to full size
                    btn_SignUp.animate().setDuration(200).
                            scaleX(1).scaleY(1).
                            translationX(0).translationY(0).
                            setInterpolator(sDecelerator).
                            withEndAction(new Runnable() {
                                public void run() {
                                    et_signup_username.animate().translationX(0).setStartDelay(50)
                                            .setDuration(300).setInterpolator(new AccelerateInterpolator()).start();

                                    et_signup_re_pass.animate().translationX(0).setStartDelay(50)
                                            .setDuration(300).setInterpolator(new AccelerateInterpolator()).start();
                                }
                            });
                    return true;
                }
            });
        }

        //endregion

        btn_SignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                if(!et_signup_username.getText().toString().isEmpty() && !et_signup_email.getText().toString().isEmpty() &&
//                        !et_signup_pass.getText().toString().isEmpty() && !et_signup_re_pass.getText().toString().isEmpty() &&
//                        !et_signup_pass.getText().toString().equals(et_signup_re_pass.getText().toString())){
//                    // TODO: ADD the signup process
                    mAuth.createUserWithEmailAndPassword(et_signup_email.getText().toString(), et_signup_pass.getText().toString())
                            .addOnCompleteListener(SignUp.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Sign in success, update UI with the signed-in user's information
                                        final FirebaseUser user = mAuth.getCurrentUser();

                                        if (user != null) {
                                            user.sendEmailVerification()
                                                    .addOnCompleteListener(SignUp.this, new OnCompleteListener() {
                                                        @Override
                                                        public void onComplete(@NonNull Task task) {
                                                            if (task.isSuccessful()) {
                                                                Toast.makeText(SignUp.this,
                                                                        "Verification email sent to " + user.getEmail(),
                                                                        Toast.LENGTH_SHORT).show();
                                                            } else {
                                                                Toast.makeText(SignUp.this,
                                                                        "Failed to send verification email.",
                                                                        Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    });

                                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                                            DatabaseReference mRef =  database.getReference("Users").child(user.getUid()) ;

                                            mRef.child("Name").setValue(et_signup_username.getText().toString());
                                            mRef.child("Status").setValue("NA");
                                            mRef.child("Latitude").setValue("NA");
                                            mRef.child("Longitude").setValue("NA");

                                            startActivity(new Intent(SignUp.this, MainActivity.class));
                                        }

                                    } else {
                                        // If sign in fails, display a message to the user.
                                        Toast.makeText(SignUp.this, task.toString(),
                                                Toast.LENGTH_SHORT).show();

                                    }
                                }
                            });
//                }
//                else {
//                    Toast.makeText(SignUp.this, "From not filled correctly", Toast.LENGTH_SHORT).show();
//                }

            }
        });

        tv_Signup_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    //region OnBackPressed

    @Override
    public void onBackPressed() {

        et_signup_username.animate().translationX(new Utils().screenDimension(SignUp.this)[0]).setStartDelay(50)
                .setDuration(300).setInterpolator(new AccelerateInterpolator()).start();

        et_signup_re_pass.animate().translationX(-new Utils().screenDimension(SignUp.this)[0]).setStartDelay(50)
                .setDuration(300).setInterpolator(new AccelerateInterpolator()).withEndAction(new Runnable() {
            public void run() {

                btn_SignUp.animate().setDuration(150).
                        scaleX(mWidthScale).scaleY(mHeightScale).
                        translationX(mLeftDelta).translationY(mTopDelta).
                        setInterpolator(sDecelerator);

                et_signup_pass.animate().setDuration(150).
                        translationX(mLeftDeltaPass).translationY(mTopDeltaPass).
                        setInterpolator(sDecelerator);

                et_signup_email.animate().setDuration(150).
                        translationX(mLeftDeltaEmail).translationY(mTopDeltaEmail).
                        setInterpolator(sDecelerator).
                        withEndAction(new Runnable() {
                            public void run() {
                                Intent backActivity = new Intent(SignUp.this,
                                        Login.class);

                                backActivity.
                                        putExtra("SignUp", 1);

                                startActivity(backActivity);

                                // Override transitions: we don't want the normal window animation in addition to our custom one
                                overridePendingTransition(0, 0);
                            }
                        });
            }
        });


    }

    //endregion

    //region Private Functions


    //endregion
}
