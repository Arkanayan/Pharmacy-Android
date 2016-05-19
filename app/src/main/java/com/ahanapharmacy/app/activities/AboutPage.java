package com.ahanapharmacy.app.activities;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;

import com.ahanapharmacy.app.R;

import mehdi.sakout.aboutpage.Element;

public class AboutPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_about_page);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        View aboutPage = new mehdi.sakout.aboutpage.AboutPage(this)
                .isRTL(false)
                .setImage(R.drawable.pill)
                .setDescription("Order medicines from anywhere")
                .addGroup("Created By")
                .addItem(authorElement())
                .addGroup("Connect with us")
                .addEmail("itsarkanayan@gmail.com")
                .addWebsite("http://arkanayan.me")
                .addGitHub("Arkanayan")
                .create();

        setContentView(aboutPage);
    }

    Element authorElement() {
        Element authorElement = new Element();
        authorElement.setIcon(R.drawable.softtware_engineer);
        authorElement.setTitle("Arkanayan Shet");
        authorElement.setValue("Arkanayan Shet");
        authorElement.setGravity(Gravity.LEFT);
        authorElement.setOnClickListener(v -> {
            String contactNo = getString(R.string.author_contact_no);
            String number = "tel:" + contactNo;

            Intent callingIntent = new Intent(Intent.ACTION_DIAL, Uri.parse(number));
            startActivity(callingIntent);
        });

    return authorElement;
    }

    public static Intent getInstance(Context context) {
        return new Intent(context, AboutPage.class);
    }
}
