package com.apharmacy.app.activities;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.apharmacy.app.App;
import com.apharmacy.app.R;
import com.apharmacy.app.models.Address;
import com.apharmacy.app.models.User;
import com.digits.sdk.android.AuthCallback;
import com.digits.sdk.android.DigitsAuthButton;
import com.digits.sdk.android.DigitsException;
import com.digits.sdk.android.DigitsOAuthSigning;
import com.digits.sdk.android.DigitsSession;
import com.firebase.client.AuthData;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterAuthToken;
import com.twitter.sdk.android.core.TwitterCore;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginActivity extends AppCompatActivity {

    private final String TAG = this.getClass().getSimpleName();

    @BindView(R.id.button_auth)
    DigitsAuthButton authButton;


    @BindView(R.id.app_icon)
    ImageView appIconImageView;

    private ObjectAnimator mAnimator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        mAnimator = new ObjectAnimator();

/*
        logoutButton = (Button) findViewById(R.id.button_logout);
        logoutButton.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {
                Log.d(TAG, "Logout clicked");
                if (Digits.getSessionManager() != null) {
                    Digits.getSessionManager().clearActiveSession();
                    Log.d(TAG, "Logged out ");
                }
            }
        });
*/

       // final DigitsAuthButton authButton = (DigitsAuthButton) findViewById(R.id.button_auth);
        authButton.setAuthTheme(R.style.AppTheme);
        authButton.setText("Login");
        authButton.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary));


        authButton.setCallback(new AuthCallback() {
            @Override
            public void success(DigitsSession session, String phoneNumber) {
               // Log.d(TAG, "success: ph no: " + phoneNumber);
              //  Log.d(TAG, "Auth token" + session.getAuthToken());
                TwitterAuthConfig authConfig = TwitterCore.getInstance().getAuthConfig();

                DigitsOAuthSigning oAuthSigning = new DigitsOAuthSigning(authConfig,
                        (TwitterAuthToken) session.getAuthToken());
                Map<String, String> authHeaders = oAuthSigning.getOAuthEchoHeadersForVerifyCredentials();

                doLogin(authHeaders);

              //  Toast.makeText(LoginActivity.this, "Phone no: " + session.getPhoneNumber(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void failure(DigitsException error) {
                showLoginError();
            }
        });
    }

    private void showLoggingIn() {
        showIconAnimation();

        authButton.setClickable(false);
        authButton.setText("Logging in...");

        Snackbar.make(authButton, "We are logging you in. Please wait a moment...", Snackbar.LENGTH_INDEFINITE).show();

    }

    private void showLoginError() {

      //  stopIconAnimation();
        runOnUiThread(() -> {
            authButton.setClickable(true);

            authButton.setText("Login");

            Snackbar.make(authButton, "Sorry, We are unable to log you in.", Snackbar.LENGTH_INDEFINITE)
                    .setAction("Retry", v -> {
                        authButton.performClick();
                    })
                    .show();
        });

    }

    private void doLogin(Map<String, String> authHeaders) {

        showLoggingIn();

        OkHttpClient client = new OkHttpClient();

        Log.d(TAG, "*****Headers*****");
        FormEncodingBuilder builder = new FormEncodingBuilder();
        for (Map.Entry<String, String> entry : authHeaders.entrySet()) {
            Log.d(TAG, "Key: " + entry.getKey() + " Value: " + entry.getValue());
            builder.add(entry.getKey(), entry.getValue());

        }
        client.setConnectTimeout(10, TimeUnit.SECONDS);
        RequestBody formBody = builder.build();

        Request request = new Request.Builder()
                .url(getString(R.string.server_url) + "/getToken")
                .post(formBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                showLoginError();
                e.printStackTrace();
            }

            @Override
            public void onResponse(Response response) throws IOException {
                String token = response.body().string();
                Log.d(TAG, "onResponse: Token: " + token);
                firebaseLogin(token);

            }
        });


    }

    private void firebaseLogin(String token) {

        final Firebase ref = App.getFirebase();

        ref.authWithCustomToken(token, new Firebase.AuthResultHandler() {
            @Override
            public void onAuthenticated(AuthData authData) {
                firebaseLoginOrRegister(authData);
            }

            @Override
            public void onAuthenticationError(FirebaseError firebaseError) {
                showLoginError();
            }
        });
    }

    private void firebaseLoginOrRegister(final AuthData authData) {
        final Firebase ref = App.getFirebase();
        ref.child("users").child(authData.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // check if user exists
                if (dataSnapshot.exists()) {
                    try {
                        User user = dataSnapshot.getValue(User.class);
                        Toast.makeText(LoginActivity.this, "Welcome " + user.getFirstName(),
                                Toast.LENGTH_SHORT).show();
                        //todo start activity user edit or order details
                        startActivity(EditUserActivity.getInstance(LoginActivity.this));
                        finish();
                    } catch (Exception e) {
                        e.printStackTrace();
                        startActivity(EditUserActivity.getInstance(LoginActivity.this));
                        finish();
                    }
                } else {
/*                    final Map<String, String> map = new HashMap<>();
                    map.put("uid", authData.getUid());
                    map.put("phone_number", authData.getAuth().get("phone_number").toString());
                    map.put("created_at", Long.toString(timestamp));
                    */
                    // store created_at as unix timestamp
                    User user = new User();
                    user.setUid(authData.getUid());
                    user.setPhoneNumber((String) authData.getAuth().get("phone_number"));
                    Firebase userRef = ref.child("users").child(authData.getUid());
                    userRef.setValue(user);

                    // address test start
                    Firebase addressRef = App.getFirebase().child("addresses");
                    Address address = new Address();
                    address.setAddressLine1("");
                    address.setAddressLine2("");
                    address.setLandmark("");
                    addressRef.child(authData.getUid()).push().setValue(address);
                    //address test finish


                    Log.d(TAG, "New firebase user id: " + authData.getUid());
                    Toast.makeText(LoginActivity.this, "Welcome", Toast.LENGTH_SHORT).show();
                    //todo start activity where user can enter his info
                    startActivity(EditUserActivity.getInstance(LoginActivity.this));
                    finish();
                    //finish
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Toast.makeText(LoginActivity.this, "Unable to fetch data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showIconAnimation() {
/*        mAnimator = ObjectAnimator.ofFloat(
                appIconImageView,
                "rotation",
                0f,
                360f
        );
        mAnimator.setDuration(1000);
        mAnimator.setRepeatCount(ValueAnimator.INFINITE);
        mAnimator.setRepeatMode(ValueAnimator.RESTART);

        new Handler().post(() -> {
            mAnimator.start();
        });*/
    }

    private void stopIconAnimation() {
   /*     Looper.prepare();
        new Handler().post(() -> {
                Looper.myLooper().loop();
                mAnimator.end();
            });
        Looper.myLooper().quit();*/
    }

    @Override
    protected void onDestroy() {
        stopIconAnimation();
        super.onDestroy();
    }

    /*    public void logout() {
        Log.d(TAG, "Logout clicked");
        if (Digits.getSessionManager() != null) {
            Digits.getSessionManager().clearActiveSession();
            Log.d(TAG, "Logged out ");
        }
        App.getFirebase().unauth();
    }*/

/*    // for testing
    @OnClick(R.id.button_edit_user)
    public void lauchEdit(View view) {
        Log.d(TAG, "lauchEdit: clicked");
        startActivity(EditUserActivity.getInstance(this));
    }*/

/*    @OnClick(R.id.button_edit_address)
    public void editAddress(View view) {
        Log.d(TAG, "editAddress");
        Address address = new Address();
        address.setAddressLine1("another new line 1");
        address.setAddressLine2("anther new line 2");
        address.setLandmark("This is a landmark");

        UserManager.updateAddress(address);

    }*/

    public static Intent getInstance(Context context) {

        return new Intent(context, LoginActivity.class);
    }


}
