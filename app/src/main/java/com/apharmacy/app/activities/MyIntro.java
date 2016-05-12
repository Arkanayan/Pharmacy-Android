package com.apharmacy.app.activities;

import android.Manifest;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.apharmacy.app.R;
import com.github.paolorotolo.appintro.AppIntro2;
import com.github.paolorotolo.appintro.AppIntroFragment;

public class MyIntro extends AppIntro2 {


    @Override
    public void init(@Nullable Bundle savedInstanceState) {

        addSlide(AppIntroFragment.newInstance("Introduction", "This is description",
                R.drawable.ic_image,
                R.color.md_amber_200));

        askForPermissions(new String[]{Manifest.permission.CAMERA}, 1); // OR


        addSlide(AppIntroFragment.newInstance("Introduction 2 ", "This is description 2",
                R.drawable.delivery_truck,
                R.color.md_green_200));

        addSlide(AppIntroFragment.newInstance("Introduction 3 ", "This is description 3",
                R.drawable.delivery_truck,
                R.color.md_blue_200));

        showStatusBar(false);

        setSwipeLock(false);

        setCustomTransformer(new ZoomOutPageTransformer());
    }




    @Override
    public void onDonePressed() {
        startActivity(OrderListActivity.getInstance(this));
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
