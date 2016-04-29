package bankura.pharmacy.pharmacyapp.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.digits.sdk.android.AuthCallback;
import com.digits.sdk.android.Digits;
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
import java.util.HashMap;
import java.util.Map;

import bankura.pharmacy.pharmacyapp.App;
import bankura.pharmacy.pharmacyapp.R;
import butterknife.OnClick;

public class LoginActivity extends AppCompatActivity {

    private final String TAG = this.getClass().getSimpleName();

   // @BindView(R.id.button_auth)
    DigitsAuthButton authButton;

   // @BindView(R.id.button_logout)
    Button logoutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
       // ButterKnife.bind(this);

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

        final DigitsAuthButton authButton = (DigitsAuthButton) findViewById(R.id.button_auth);
        authButton.setText("Login");

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

                Toast.makeText(LoginActivity.this, "Phone no: " + session.getPhoneNumber(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void failure(DigitsException error) {

            }
        });
    }


    private void doLogin(Map<String, String> authHeaders) {

        OkHttpClient client = new OkHttpClient();

        Log.d(TAG, "*****Headers*****");
        FormEncodingBuilder builder = new FormEncodingBuilder();
        for (Map.Entry<String, String> entry : authHeaders.entrySet()) {
            Log.d(TAG, "Key: " + entry.getKey() + " Value: " + entry.getValue());
            builder.add(entry.getKey(), entry.getValue());

        }
        RequestBody formBody = builder.build();

        Request request = new Request.Builder()
                .url(getString(R.string.server_url) + "/getToken")
                .post(formBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
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

            }
        });
    }

    private void firebaseLoginOrRegister(final AuthData authData) {
        final Firebase ref = App.getFirebase();
        ref.child("users").child(authData.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    Toast.makeText(LoginActivity.this, "Firebase no: " + dataSnapshot.child("phone_number").getValue(),
                            Toast.LENGTH_SHORT).show();
                    //todo start activity user edit or order details
                    startActivity(EditUserActivity.getInstance(LoginActivity.this));
                    // finish();
                } else {
                    final Map<String, String> map = new HashMap<>();
                    map.put("uid", authData.getUid());
                    map.put("phone_number", authData.getAuth().get("phone_number").toString());
                    map.put("created_at", Long.toString(System.currentTimeMillis() / 1000L));
                    Firebase userRef = ref.child("users").child(authData.getUid());
                    userRef.setValue(map);
                    long unixTime = System.currentTimeMillis() / 1000L;
                    Log.d(TAG, "New firebase user id: " + authData.getUid());
                    Toast.makeText(LoginActivity.this, "Welcome", Toast.LENGTH_SHORT).show();
                    //todo start activity where user can enter his info
                    startActivity(EditUserActivity.getInstance(LoginActivity.this));
                    //finish
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    @OnClick(R.id.button_logout)
    public void logout(View view) {
        Log.d(TAG, "Logout clicked");
        if (Digits.getSessionManager() != null) {
            Digits.getSessionManager().clearActiveSession();
            Log.d(TAG, "Logged out ");
        }
    }




    public static Intent getInstance(Context context) {
        return new Intent(context, LoginActivity.class);
    }


}
