package bankura.pharmacy.pharmacyapp.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class InitialActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // throw new RuntimeException("This is a new runtime exception");

       // if (App.getFirebase().getAuth() == null) {

            startActivity(LoginActivity.getInstance(this));
            finish();
      //  }
    }
}
