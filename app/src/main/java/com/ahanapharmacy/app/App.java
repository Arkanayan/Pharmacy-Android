package com.ahanapharmacy.app;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.ahanapharmacy.app.analytics.CrashlyticsTree;
import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.core.CrashlyticsCore;
import com.digits.sdk.android.Digits;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterCore;

import io.fabric.sdk.android.Fabric;
import timber.log.Timber;

/**
 * Created by arka on 4/28/16.
 */
public class App extends Application {


    private final String TAG = this.getClass().getSimpleName();
    private static FirebaseDatabase mFirebase;
    private static Context mContext;


    @Override
    public void onCreate() {
        super.onCreate();

        final String TWITTER_KEY = getString(R.string.twitter_key);
        final String TWITTER_SECRET = getString(R.string.twitter_secret);

        final TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        // Initialize Firebase

        mFirebase = FirebaseDatabase.getInstance();

        mFirebase.setPersistenceEnabled(true);

        mContext = this;

        // Fabric initialization
        Crashlytics crashlyticsKit = new Crashlytics.Builder()
                .core(new CrashlyticsCore.Builder().disabled(BuildConfig.DEBUG).build())
                .build();

        Fabric.with(this, new TwitterCore(authConfig), new Digits(), crashlyticsKit);

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }

        Timber.plant(new CrashlyticsTree());

    }

    //Logs out user and redirects to login activity
    public static void logout() {
        String TAG = BuildConfig.APPLICATION_ID + ".App";
        if (!BuildConfig.DEBUG) {
            if (Digits.getSessionManager() != null) {
                Digits.getSessionManager().clearActiveSession();
                Log.d(TAG, "Logged out from digits");
            }
        }
        FirebaseAuth.getInstance().signOut();
    }

    public static Context getContext(){
        return mContext;
    }
}
