package com.apharmacy.app.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.apharmacy.app.App;
import com.apharmacy.app.Utils.Prefs;

public class InitialActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // throw new RuntimeException("This is a new runtime exception");

        // Show intro if it has not been shown
        Prefs.getInstance(this);
        if (!Prefs.getInstance().getBoolean(Prefs.Key.HAS_INTRO_SHOWN, false)) {
            Intent intro = new Intent(this, MyIntro.class);
            startActivity(intro);
            finish();
            return;
        }



        if (App.getFirebase().getAuth() == null) {

            startActivity(LoginActivity.getInstance(this));
            finish();
            return;
        } else if (!Prefs.getInstance().getBoolean(Prefs.Key.IS_USER_DETAILS_PRESENT, false)) {
            Toast.makeText(InitialActivity.this, "Please enter your details.", Toast.LENGTH_SHORT).show();
            startActivity(EditUserActivity.getInstance(this));
            finish();
            return;
        }


            startActivity(OrderListActivity.getInstance(this));
            finish();

    }

    public static Intent getInstance(Context context) {
        return new Intent(context, InitialActivity.class);
    }
}
