package com.ahanapharmacy.app.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import com.ahanapharmacy.app.BuildConfig;
import com.ahanapharmacy.app.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import butterknife.BindView;
import butterknife.ButterKnife;
import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

public class ImageViewActivity extends Activity {

    public static final String RX_URI = BuildConfig.APPLICATION_ID + "rx_uri";

    @BindView(R.id.rx_image_viewer)
    PhotoView rxImageViewer;

    @BindView(R.id.imageviewer_layout)
    RelativeLayout imageViewerLayout;

    PhotoViewAttacher mAttacher;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_view);
        ButterKnife.bind(this);

        if (getIntent().hasExtra(RX_URI)) {
            String uri = getIntent().getStringExtra(RX_URI);
            //ssImageView.setImage(ImageSource.uri(uri));
            mAttacher = new PhotoViewAttacher(rxImageViewer);
            mAttacher.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {
                @Override
                public void onPhotoTap(View view, float v, float v1) {
                    finish();
                    return;
                }

                @Override
                public void onOutsidePhotoTap() {
                    finish();
                    return;
                }
            });
            Glide.with(this)
                    .load(uri)
                    .listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            mAttacher.update();
                            return false;
                        }
                    })
                    .fitCenter()
                    .into(rxImageViewer);
        }
    }


    @Override
    public void onBackPressed() {
      //  super.onBackPressed();
        finish();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0,0);
    }

    public static Intent getInstance(Context context, String url) {
        Intent intent = new  Intent(context, ImageViewActivity.class);
        intent.putExtra(RX_URI, url);

        return intent;
    }
}
