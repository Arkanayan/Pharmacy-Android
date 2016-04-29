package bankura.pharmacy.pharmacyapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import com.crashlytics.android.Crashlytics;
import com.digits.sdk.android.Digits;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterCore;
import io.fabric.sdk.android.Fabric;

public class InitialActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // throw new RuntimeException("This is a new runtime exception");

       // if (App.getFirebase().getAuth() == null) {

            startActivity(LoginActivity.getInstance(this));
      //  }
    }
}
