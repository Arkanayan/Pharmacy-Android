package com.ahanapharmacy.app.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.ahanapharmacy.app.R;
import com.ahanapharmacy.app.Utils.Prefs;

public class NewOrderActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_order);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

/*        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        */
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public static Intent getInstance(Context context) {
        return new Intent(context, NewOrderActivity.class);
    }

    @Override
    public void onBackPressed() {

        if (Prefs.getInstance(this).getBoolean(Prefs.Key.IS_FIRST_TIME, true)) {
            // Go to list page if user pressed back without order for the first time
            Prefs.getInstance().put(Prefs.Key.IS_FIRST_TIME, false);

            startActivity(OrderListActivity.getInstance(this));
            finish();
        }
        super.onBackPressed();
    }
}
