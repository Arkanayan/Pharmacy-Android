package com.ahanapharmacy.app.activities;

import android.Manifest;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.ahanapharmacy.app.R;
import com.ahanapharmacy.app.Utils.Prefs;
import com.github.paolorotolo.appintro.AppIntro2;
import com.github.paolorotolo.appintro.AppIntroFragment;

public class MyIntro extends AppIntro2 {


    @Override
    public void init(@Nullable Bundle savedInstanceState) {
        Prefs.getInstance(this);

        addSlide(AppIntroFragment.newInstance("Order medicines", "From your home",
                R.drawable.house,
                Color.parseColor("#222222")));

       // askForPermissions(new String[]{Manifest.permission.CAMERA}, 1); // OR


        addSlide(AppIntroFragment.newInstance("From your bed ", "You get it. :-)\n\n From Everywhere",
                R.drawable.bed,
                Color.parseColor("#00BCD4")));

        addSlide(AppIntroFragment.newInstance("Just scan and order", "As easy as that  \n\n" +
                "For this, we need your permission to take pictures and storage ",
                R.drawable.photo_camera,
                ContextCompat.getColor(this, R.color.md_teal_500)));

        askForPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 3);


        addSlide(AppIntroFragment.newInstance("We just need", "Your phone number \n\n To verify you \n\n " +
                "P.S. This app is not compatible with your landphone",
                R.drawable.tel,
                Color.parseColor("#5C6BC0")));

        askForPermissions(new String[]{Manifest.permission.READ_PHONE_STATE}, 4);

        addSlide(AppIntroFragment.newInstance("We just need", "Your permission to read the OTP \n\n " +
                "This app works fine even without this permission \n\n ",
                R.drawable.chat,
                ContextCompat.getColor(this, R.color.md_cyan_500)));

        askForPermissions(new String[]{Manifest.permission.RECEIVE_SMS}, 5);

        addSlide(AppIntroFragment.newInstance("We are nearly done", "In the next page click login",
                R.drawable.app_logo,
                ContextCompat.getColor(this, R.color.md_purple_500)));


        showStatusBar(false);

      //  setSwipeLock(true);

//        showSkipButton(false);

        setFlowAnimation();

     //   setNextPageSwipeLock(true);


        //  setCustomTransformer(new ZoomOutPageTransformer());
    }

/*
    @Override
*/
    public void onSkipPressed() {

        Prefs.getInstance().put(Prefs.Key.HAS_INTRO_SHOWN, true);
        startActivity(InitialActivity.getInstance(this));
        finish();
    }


    @Override
    public void onDonePressed() {
        Prefs.getInstance().put(Prefs.Key.HAS_INTRO_SHOWN, true);
        startActivity(InitialActivity.getInstance(this));
        finish();
    }

    @Override
    public void onNextPressed() {

    }

    @Override
    public void onSlideChanged() {

    }

    public class ZoomOutPageTransformer implements ViewPager.PageTransformer {
        private static final float MIN_SCALE = 0.85f;
        private static final float MIN_ALPHA = 0.5f;

        public void transformPage(View view, float position) {
            int pageWidth = view.getWidth();
            int pageHeight = view.getHeight();

            if (position < -1) { // [-Infinity,-1)
                // This page is way off-screen to the left.
                view.setAlpha(0);

            } else if (position <= 1) { // [-1,1]
                // Modify the default slide transition to shrink the page as well
                float scaleFactor = Math.max(MIN_SCALE, 1 - Math.abs(position));
                float vertMargin = pageHeight * (1 - scaleFactor) / 2;
                float horzMargin = pageWidth * (1 - scaleFactor) / 2;
                if (position < 0) {
                    view.setTranslationX(horzMargin - vertMargin / 2);
                } else {
                    view.setTranslationX(-horzMargin + vertMargin / 2);
                }

                // Scale the page down (between MIN_SCALE and 1)
                view.setScaleX(scaleFactor);
                view.setScaleY(scaleFactor);

                // Fade the page relative to its size.
                view.setAlpha(MIN_ALPHA +
                        (scaleFactor - MIN_SCALE) /
                                (1 - MIN_SCALE) * (1 - MIN_ALPHA));

            } else { // (1,+Infinity]
                // This page is way off-screen to the right.
                view.setAlpha(0);
            }
        }
    }


}
