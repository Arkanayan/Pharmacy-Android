package bankura.pharmacy.pharmacyapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.fabric.sdk.android.Fabric;

public class LoginActivity extends AppCompatActivity {

    private final String TAG = this.getClass().getSimpleName();

   // @BindView(R.id.button_auth)
    DigitsAuthButton authButton;

    @BindView(R.id.button_logout)
    Button logoutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
      //  ButterKnife.bind(this);


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
                } else {
                    final Map<String, String> map = new HashMap<>();
                    map.put("phone_number", authData.getAuth().get("phone_number").toString());
                    ref.child("users").child(authData.getUid()).setValue(map);
                    Log.d(TAG, "New firebase user id: " + authData.getUid());
                    Toast.makeText(LoginActivity.this, "Welcome", Toast.LENGTH_SHORT).show();
                    //todo start activity where user can enter his info
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    @OnClick(R.id.button_logout)
    void logout() {
        if (Digits.getSessionManager() != null)
            Digits.getSessionManager().clearActiveSession();
    }




    public static Intent getInstance(Context context) {
        return new Intent(context, LoginActivity.class);
    }


}
