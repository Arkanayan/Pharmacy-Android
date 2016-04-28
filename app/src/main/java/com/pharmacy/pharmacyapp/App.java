package com.pharmacy.pharmacyapp;

import android.app.Application;
import android.content.Context;

import com.crashlytics.android.Crashlytics;
import com.digits.sdk.android.Digits;
import com.firebase.client.Firebase;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterCore;

import io.fabric.sdk.android.Fabric;

/**
 * Created by arka on 4/28/16.
 */
public class App extends Application {


    private static Firebase mFirebase;
    private static Context mContext;


    @Override
    public void onCreate() {
        super.onCreate();

        // Initialize Firebase
        Firebase.setAndroidContext(this);
        // Firebase.getDefaultConfig().setPersistenceEnabled(true);
        mFirebase = new Firebase(getString(R.string.firebase_url));

        mContext = this;

        // Fabric initialization
        final String TWITTER_KEY = getString(R.string.twitter_key);
        final String TWITTER_SECRET = getString(R.string.twitter_secret);

        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new TwitterCore(authConfig), new Digits());
        Fabric.with(this, new Crashlytics());


    }

    public static Firebase getFirebase() {
        return mFirebase;
    }

    public static Context getContext(){
        return mContext;
    }
}
