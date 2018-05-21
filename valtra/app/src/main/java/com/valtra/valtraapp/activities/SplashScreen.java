package com.valtra.valtraapp.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.valtra.valtraapp.R;
import com.valtra.valtraapp.utils.TypeWriter;
import com.valtra.valtraapp.utils.Utils;

import io.codetail.animation.SupportAnimator;
import io.codetail.animation.ViewAnimationUtils;

public class SplashScreen extends AppCompatActivity {

    //region Variables

    ImageView logo_ishi;
    TextView tv_ishi;

    private static final int STARTUP_DELAY = 200;
    private static final int ANIM_ITEM_DURATION = 500;
    private static final int ITEM_DELAY = 300;
    private static int SPLASH_TIME_OUT = 500;

    private FrameLayout reveal_layout;
    private boolean hidden = true;

    TextView tv_getStarted, tv;//,tv_one;
    ImageView img_myChar, img_myChar_poor;
    TypeWriter writer;//,writer_one;


    //Variables for Activity Transition
    ImageView img_to_animate_to_next_screen;
    private static final String TAG = "activity_splash_screen";
    static float sAnimatorScale = 1;

    private Boolean animationAlreadyPlayed = false;

    public static Boolean isComingBack = false;
    private Boolean toggle = false;

    //endregion


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //region Making Activity Full Screen

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //endregion

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        //region initialisations

        logo_ishi = (ImageView) findViewById(R.id.logo_ishi);
        tv_ishi = (TextView) findViewById(R.id.tv_ishi);

        reveal_layout = (FrameLayout) findViewById(R.id.reveal_layout);

        img_myChar = (ImageView) findViewById(R.id.img_myChar);
        img_myChar_poor = (ImageView) findViewById(R.id.img_myChar_poor);

        tv_getStarted = (TextView) findViewById(R.id.tv_getStarted);
        tv = (TextView) findViewById(R.id.tv);
        //tv_one = (TextView) findViewById(R.id.tv_one);
        writer = (TypeWriter) findViewById(R.id.tv_chatText);
        //writer_one = (TypeWriter) findViewById(R.id.tv_chatText_one);

        img_to_animate_to_next_screen = (ImageView) findViewById(R.id.img_to_animate_to_next_screen);

        //endregion

        //region Setting TypeFace of Views

        tv_getStarted.setTypeface(new Utils(this).headingTypeFace());
        writer.setTypeface(new Utils(this).headingTypeFace());
        // writer_one.setTypeface(new Utils(this).headingTypeFace());

        //endregion



        //region Click Events

        findViewById(R.id.tv_getStarted).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                isComingBack = false;
                logo_ishi.setVisibility(View.INVISIBLE);
                tv_ishi.setVisibility(View.INVISIBLE);

                findViewById(R.id.activity_splash_screen).setBackgroundColor(getResources().getColor(R.color.mainColor));


                reveal_layout.animate().setDuration(300).alpha(0).setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);

                        // Interesting data to pass across are the View size/location, the
                        // orientation (to avoid returning back to an obsolete configuration if
                        // the device rotates again in the meantime)

                        int[] screenLocation = new int[2];
                        reveal_layout.getLocationOnScreen(screenLocation);
                        Intent subActivity = new Intent(SplashScreen.this,
                                Login.class);
                        int orientation = getResources().getConfiguration().orientation;
                        subActivity.
                                putExtra(TAG + ".orientation", orientation).
                                putExtra(TAG + ".left", screenLocation[0]).
                                putExtra(TAG + ".top", screenLocation[1]).
                                putExtra(TAG + ".width", reveal_layout.getWidth()).
                                putExtra(TAG + ".height", reveal_layout.getHeight());
                        startActivity(subActivity);

                        // Override transitions: we don't want the normal window animation in addition to our custom one
                        overridePendingTransition(0, 0);
                    }
                });
            }
        });

        findViewById(R.id.tv_getStarted).setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                if (toggle) {
                    img_myChar.setVisibility(View.GONE);
                    img_myChar_poor.setVisibility(View.VISIBLE);
                    toggle = !toggle;
                }else
                {
                    img_myChar_poor.setVisibility(View.GONE);
                    img_myChar.setVisibility(View.VISIBLE);
                    toggle = !toggle;
                }
                return true;
            }
        });

        //endregion
    }
    //region onWindowFocusChanged

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {


        //region Chained Animation delay Handlers

        if (!animationAlreadyPlayed && !isComingBack) {

            animationAlreadyPlayed = true;

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {

                    animateLogo();

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            //  animateLogoText();

                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    animateCircleReveal();

                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            animateCharacterChat();
                                        }
                                    }, 900);
                                }
                            }, 2000);
                        }
                    }, 10);

                }
            }, SPLASH_TIME_OUT);
        }
        //endregion

        if (isComingBack) {
            reveal_layout.setAlpha(1);
        }

        super.onWindowFocusChanged(hasFocus);
    }

    //endregion

    //region Private Functions

    private void animateCircleReveal() {

        int[] screenDimensions = new Utils().screenDimension(SplashScreen.this);
        int width = screenDimensions[0];
        int height = screenDimensions[1];

        int cy = height / 2;
        int cx = width / 2;
        int radius = Math.max(height, width);

        //Below Android LOLIPOP Version
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            SupportAnimator animator =
                    ViewAnimationUtils.createCircularReveal(reveal_layout, cx, cy, 0, radius);
            animator.setInterpolator(new AccelerateDecelerateInterpolator());
            animator.setDuration(1000);

            SupportAnimator animator_reverse = animator.reverse();

            if (hidden) {
                reveal_layout.setVisibility(View.VISIBLE);
                animator.start();
                hidden = false;
            } else {
                animator_reverse.addListener(new SupportAnimator.AnimatorListener() {
                    @Override
                    public void onAnimationStart() {

                    }

                    @Override
                    public void onAnimationEnd() {
                        reveal_layout.setVisibility(View.INVISIBLE);
                        hidden = true;
                    }

                    @Override
                    public void onAnimationCancel() {

                    }

                    @Override
                    public void onAnimationRepeat() {

                    }
                });
                animator_reverse.start();
            }
        }
        // Android LOLIPOP And ABOVE Version
        else {
            if (hidden) {
                Animator anim = android.view.ViewAnimationUtils.createCircularReveal(reveal_layout, cx, cy, 0, radius);
                reveal_layout.setVisibility(View.VISIBLE);
                anim.setDuration(1000);
                anim.start();
                hidden = false;
            } else {
                Animator anim = android.view.ViewAnimationUtils.createCircularReveal(reveal_layout, cx, cy, radius, 0);
                anim.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        reveal_layout.setVisibility(View.INVISIBLE);
                        hidden = true;
                    }
                });
                anim.setDuration(1000);
                anim.start();
            }
        }
    }

    private void animateLogo() {
        ViewCompat.animate(logo_ishi).setStartDelay(STARTUP_DELAY)
                .setDuration(300).scaleY(1).scaleX(1).start();
    }

    private void animateLogoText() {
        tv_ishi.setVisibility(View.INVISIBLE);
        tv_ishi.setTypeface(new Utils(SplashScreen.this).headingTypeFace());

        ViewCompat.animate(tv_ishi)
                .translationY(80)
                .setStartDelay(0)
                .setDuration(ANIM_ITEM_DURATION).setInterpolator(
                new DecelerateInterpolator(1.2f)).start();
    }

    private void animateCharacterChat() {
        //region Animation Delay Handlers

        ViewCompat.animate(tv).setStartDelay(0)
                .setDuration(200).scaleY(1).scaleX(1).start();

        // ViewCompat.animate(tv_one).setStartDelay(0)
        //       .setDuration(200).scaleY(1).scaleX(1).start();

        new Handler().postDelayed(new Runnable() {

            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */


            @Override
            public void run() {
                // This method will be executed once the timer is over

                writer.setCharacterDelay(50);
//                writer.animateText("Hi i will keep track");
                writer.animateText("Hello Farmer!!");

            }
        }, 450);

//        new Handler().postDelayed(new Runnable() {
//
//            /*
//             * Showing splash screen with a timer. This will be useful when you
//             * want to show case your app logo / company
//             */
//
//
//            @Override
//            public void run() {
//                // This method will be executed once the timer is over
//
//                writer_one.setCharacterDelay(50);
//                writer_one.animateText("of expenses for you");
//
//            }
//        }, 1000);

        //endregion
    }
    //endregion
}
