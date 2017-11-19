package com.ahanapharmacy.app.activities;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ShareCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;

import com.ahanapharmacy.app.R;
import com.ahanapharmacy.app.Utils.Analytics;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

import mehdi.sakout.aboutpage.Element;

public class AboutPage extends AppCompatActivity {

    public static final String CONFIG_PHARMACY_CONTACT = "pharmacy_contact_number";
    public static final String CONFIG_DEVELOPER_CONTACT = "developer_contact";
    public static final String CONFIG_CONTACT_VIA_EMAIL = "contact_via_email";

    FirebaseAnalytics mAnalytics;


    private FirebaseRemoteConfig mRemoteConfig;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mRemoteConfig = FirebaseRemoteConfig.getInstance();
        mRemoteConfig.setDefaults(R.xml.remote_config_defaults);

        mAnalytics = FirebaseAnalytics.getInstance(this);

        mRemoteConfig.fetch(2000)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        mRemoteConfig.activateFetched();
                    }
                });

//        setContentView(R.layout.activity_about_page);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        View aboutPage = new mehdi.sakout.aboutpage.AboutPage(this)
                .isRTL(false)
                .setImage(R.drawable.app_about_logo)
                .setDescription(getString(R.string.pharmacy_address))
                .addGroup("Connect with us")
//                .addEmail(getString(R.string.pharmacy_email_address))
                .addItem(pharmacyContactElement())
                .addGroup("Developed By")
                .addItem(authorElement())
                .addItem(appIconCreditElement())
                .addItem(iconCreditElement())
                .create();

        setContentView(aboutPage);
    }

    Element pharmacyContactElement() {

        String pharmacy_contact_number = mRemoteConfig.getString(CONFIG_PHARMACY_CONTACT);

        Element pharmacyElement = new Element();

        pharmacyElement.setIcon(R.drawable.ic_call);
        pharmacyElement.setTitle("Contact us");

        pharmacyElement.setOnClickListener(v -> {
            String contactNumber = "tel:" + pharmacy_contact_number;

            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse(contactNumber));
            startActivity(intent);
        });
        return pharmacyElement;
    }

    Element authorElement() {

        String developer_contact = mRemoteConfig.getString(CONFIG_DEVELOPER_CONTACT);
        boolean contact_via_email = mRemoteConfig.getBoolean(CONFIG_CONTACT_VIA_EMAIL);

        Element authorElement = new Element();
        authorElement.setIcon(R.drawable.softtware_engineer);
        authorElement.setTitle("Arkanayan Shet");
        authorElement.setValue("Arkanayan Shet");
        authorElement.setGravity(Gravity.LEFT);
        authorElement.setOnClickListener(v -> {

            if (!contact_via_email || developer_contact.equals("")) {

                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(developer_contact));
                startActivity(browserIntent);

            } else {

                String authorEmail = "itsarkanayan@gmail.com";
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:" + authorEmail));

                //new Intent(Intent.ACTION_SEND);
                emailIntent.setType("text/plain");
                emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[] {authorEmail}); // recipients

                startActivity(Intent.createChooser(emailIntent, "Contact me"));
                ShareCompat.IntentBuilder.from(this)
                        .setType("message/rfc822")
                        .addEmailTo(authorEmail)
                        .setChooserTitle("Contact developer")
                        .startChooser();
            }

            Bundle params = new Bundle();
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {

                params.putString(Analytics.Param.USER_ID, user.getUid());
                params.putString(Analytics.Param.USER_NAME, user.getDisplayName());
            }

            mAnalytics.logEvent(Analytics.Event.CONTACT_DEVELOPER, params);
        });

    return authorElement;
    }

    Element appIconCreditElement() {
        Element iconsCreditElement = new Element();
        // TODO: 7/6/16 Change icon to icon provided by dibya
        iconsCreditElement.setIcon(R.drawable.app_logo);
        iconsCreditElement.setTitle("App icon provided by Dibyajyoti Pandey");
        iconsCreditElement.setValue("http://www.zedsofts.net");
        iconsCreditElement.setGravity(Gravity.LEFT);
        iconsCreditElement.setOnClickListener(v -> {
            String flaticonUrl = "http://www.zedsofts.net";

            Intent callingIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(flaticonUrl));
            startActivity(callingIntent);
        });

        return iconsCreditElement;
    }

    Element iconCreditElement() {
        Element iconsCreditElement = new Element();
        iconsCreditElement.setIcon(R.drawable.ic_pill);
        iconsCreditElement.setTitle("Icons provided by flaticon.com");
        iconsCreditElement.setValue("flaticon.com");
        iconsCreditElement.setGravity(Gravity.LEFT);
        iconsCreditElement.setOnClickListener(v -> {
            String flaticonUrl = "http://flaticon.com";

            Intent callingIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(flaticonUrl));
            startActivity(callingIntent);
        });

        return iconsCreditElement;
    }

    public static Intent getInstance(Context context) {
        return new Intent(context, AboutPage.class);
    }
}
