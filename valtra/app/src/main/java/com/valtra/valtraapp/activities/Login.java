package com.valtra.valtraapp.activities;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.valtra.valtraapp.R;
import com.valtra.valtraapp.utils.Utils;

import io.codetail.widget.RevealFrameLayout;

public class Login extends AppCompatActivity {

    //region Variables

    RelativeLayout rl_login;
    LinearLayout ll_login_bg,ll_login_data;

    RevealFrameLayout reveal_layout;

    ImageView img_phone, img_email;

    View animatedView, animatedViewToSignUp;

    private Button btn_login;
    private EditText et_login_email, et_login_password;
    private TextView tv_login_title, tv_login_signup, tv_login_with;


    private static final String TAG = "activity_login_screen";

    //Variables for Activity Transition
    private static final TimeInterpolator sDecelerator = new DecelerateInterpolator();
    private static final TimeInterpolator sAccelerator = new AccelerateInterpolator();
    private static final String TAG_NAME = "activity_splash_screen";
    private static final int ANIM_DURATION = 500;
    int mLeftDelta;
    int mTopDelta;
    float mWidthScale;
    float mHeightScale;
    private int mOriginalOrientation;
    ColorDrawable mBackground;

    int bgTop = 0;
    int bgLeft = 0;
    int bgWidth = 0;
    int bgHeight = 0;

    public static Boolean isComingBackToScreen1 = false;

    private FirebaseAuth mAuth;

    //endregion


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //region Making Activity Full Screen

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //endregion

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        //region Initializations

        rl_login = (RelativeLayout) findViewById(R.id.rl_login);
        ll_login_bg = (LinearLayout) findViewById(R.id.ll_login_bg);
        ll_login_data = (LinearLayout) findViewById(R.id.ll_login_data);

        tv_login_title = (TextView) findViewById(R.id.tv_login_title);
        tv_login_signup = (TextView) findViewById(R.id.tv_login_signup);
        tv_login_with= (TextView) findViewById(R.id.tv_login_with);
        btn_login= (Button) findViewById(R.id.btn_login);

        et_login_email = (EditText) findViewById(R.id.et_login_email);
        et_login_password = (EditText) findViewById(R.id.et_login_password);

        animatedView = findViewById(R.id.animatedView);
        animatedViewToSignUp = findViewById(R.id.animatedViewToSignUp);

        img_email = (ImageView) findViewById(R.id.img_email);
        img_phone = (ImageView) findViewById(R.id.img_phone);

        reveal_layout = (RevealFrameLayout) findViewById(R.id.reveal_layout);

        //endregion

        //region Setting Font Type Face

        tv_login_title.setTypeface(new Utils(this).headingTypeFace());
        tv_login_with.setTypeface(new Utils(this).headingTypeFace());
        btn_login.setTypeface(new Utils(this).headingTypeFace());

        //endregion

        //region Getting The Intent Bundle

        Bundle bundle = getIntent().getExtras();

        if (bundle.getInt("SignUp") != 1) {

            bgTop = bundle.getInt(TAG_NAME + ".top");
            bgLeft = bundle.getInt(TAG_NAME + ".left");
            bgWidth = bundle.getInt(TAG_NAME + ".width");
            bgHeight = bundle.getInt(TAG_NAME + ".height");
            mOriginalOrientation = bundle.getInt(TAG_NAME + ".orientation");


            mBackground = new ColorDrawable(Color.WHITE);
            rl_login.setBackground(mBackground);
        }
        //endregion

        //region Setting our view Equal to bg coming from previous Activity

        // Only run the animation if we're coming from the parent activity, not if
        // we're recreated automatically by the window manager (e.g., device rotation)
        if (savedInstanceState == null && bundle.getInt("SignUp") != 1) {

            isComingBackToScreen1 = false;

            ViewTreeObserver observer1 = animatedView.getViewTreeObserver();
            observer1.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {

                @Override
                public boolean onPreDraw() {
                    animatedView.getViewTreeObserver().removeOnPreDrawListener(this);

                    // Figure out where the thumbnail and full size versions are, relative to the screen and each other
                    int[] screenLocation = new int[2];
                    animatedView.getLocationOnScreen(screenLocation);
                    mLeftDelta = bgLeft - screenLocation[0];
                    mTopDelta = bgTop - screenLocation[1];

                    // Scale factors to make the large version the same size as the thumbnail
                    mWidthScale = (float) bgWidth / animatedView.getWidth();
                    mHeightScale = (float) bgHeight / animatedView.getHeight();

                    runEnterAnimation();

                    return true;
                }
            });

        }

        //endregion

        //region Click Events

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SignIn(et_login_email.getText().toString(),et_login_password.getText().toString());
            }
        });

        tv_login_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int[] screenLocation_email = new int[2];
                et_login_email.getLocationOnScreen(screenLocation_email);
                Intent subActivity = new Intent(Login.this,
                        SignUp.class);
                int orientation = getResources().getConfiguration().orientation;
                subActivity.
                        putExtra(TAG + ".orientation", orientation).
                        putExtra(TAG + ".left_email", screenLocation_email[0]).
                        putExtra(TAG + ".top_email", screenLocation_email[1]).
                        putExtra(TAG + ".width_email", et_login_email.getWidth()).
                        putExtra(TAG + ".height_email", et_login_email.getHeight());


                int[] screenLocation_pass = new int[2];
                et_login_password.getLocationOnScreen(screenLocation_pass);

                subActivity.
                        putExtra(TAG + ".left_pass", screenLocation_pass[0]).
                        putExtra(TAG + ".top_pass", screenLocation_pass[1]).
                        putExtra(TAG + ".width_pass", et_login_password.getWidth()).
                        putExtra(TAG + ".height_pass", et_login_password.getHeight());


                int[] screenLocation_login = new int[2];
                btn_login.getLocationOnScreen(screenLocation_login);

                subActivity.
                        putExtra(TAG + ".left_login", screenLocation_login[0]).
                        putExtra(TAG + ".top_login", screenLocation_login[1]).
                        putExtra(TAG + ".width_login", btn_login.getWidth()).
                        putExtra(TAG + ".height_login", btn_login.getHeight());


                startActivity(subActivity);

                // Override transitions: we don't want the normal window animation in addition to our custom one
                overridePendingTransition(0, 0);
            }
        });

        img_email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                reveal_layout.setVisibility(View.VISIBLE);

                // get the center for the clipping circle
                int cx = (img_email.getLeft() + img_email.getRight()) / 2;
                int cy = (img_email.getBottom());

                // get the final radius for the clipping circle
                int dx = Math.max(cx, img_email.getWidth() - cx);
                int dy = Math.max(cy, img_email.getHeight() - cy);
                float finalRadius = (float) Math.hypot(dx, dy);

                // Android native animator
                Animator animator =
                        ViewAnimationUtils.createCircularReveal(ll_login_data, cx, cy, 0, finalRadius*1.5f);
                animator.setInterpolator(new AccelerateDecelerateInterpolator());
                animator.setDuration(1000);
                animator.start();
            }
        });

        //endregion
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            Intent intent = new Intent(Login.this, MainActivity.class);
            startActivity(intent);
        }
//        updateUI(currentUser);
    }


    //region Private Functions

    private void runEnterAnimation() {
        final long duration = (long) (ANIM_DURATION);

        // Set starting values for properties we're going to animate. These
        // values scale and position the full size version down to the thumbnail
        // size/location, from which we'll animate it back up

        animatedView.setPivotX(0);
        animatedView.setPivotY(0);
        animatedView.setScaleX(mWidthScale);
        animatedView.setScaleY(mHeightScale);
        animatedView.setTranslationX(mLeftDelta);
        animatedView.setTranslationY(mTopDelta);

        // Animate scale and translation to go from bg to full size
        animatedView.animate().setDuration(500).
                scaleX(1).scaleY(1).
                translationX(0).translationY(0).
                setInterpolator(sDecelerator).
                withEndAction(new Runnable() {
                    public void run() {

                        animatedView.setVisibility(View.GONE);

                        ll_login_bg.animate()
                                .alpha(1.0f)
                                .setDuration(300).start();


                    }
                });

        // Fade in the black background
        ObjectAnimator bgAnim = ObjectAnimator.ofInt(mBackground, "alpha", 0, 255);
        bgAnim.setDuration(200);
        bgAnim.start();
    }

    private void SignIn(String email, String password) {

    mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        FirebaseUser user = mAuth.getCurrentUser();
                        if(user != null){
                            Intent intent = new Intent(Login.this, MainActivity.class);
                            startActivity(intent);
                        }
//                        updateUI(user);
                    } else {
                        // If sign in fails, display a message to the user.
                        Toast.makeText(Login.this, "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                    }

                }
            });

    }

    //endregion
}
