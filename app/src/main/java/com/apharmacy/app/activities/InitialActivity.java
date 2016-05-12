package com.apharmacy.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.apharmacy.app.App;
import com.apharmacy.app.Utils.Prefs;

public class InitialActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // throw new RuntimeException("This is a new runtime exception");
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
        } else {
            startActivity(OrderListActivity.getInstance(this));
            finish();
        }
    }
}
