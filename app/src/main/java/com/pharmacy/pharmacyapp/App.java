package com.pharmacy.pharmacyapp;

import android.app.Application;
import android.content.Context;

import com.firebase.client.Firebase;

/**
 * Created by arka on 4/28/16.
 */
public class App extends Application {

    private static Firebase mFirebase;
    private static Context mContext;


    @Override
    public void onCreate() {
        super.onCreate();

        Firebase.setAndroidContext(this);

        mFirebase = new Firebase(getString(R.string.firebase_url));

        mContext = this;
    }

    public static Firebase getFirebase() {
        return mFirebase;
    }

    public static Context getContext(){
        return mContext;
    }
}
