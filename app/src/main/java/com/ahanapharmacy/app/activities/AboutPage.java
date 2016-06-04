package com.ahanapharmacy.app.activities;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ShareCompat;
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
                .setImage(R.drawable.pill_icon)
                .setDescription("Order medicines from anywhere" + getString(R.string.pharmacy_address))
                .addGroup("Connect with us")
//                .addEmail(getString(R.string.pharmacy_email_address))
                .addItem(pharmacyContactElement())
                .addGroup("Developed By")
                .addItem(authorElement())
                .addItem(iconCreditElement())
                .create();

        setContentView(aboutPage);
    }

    Element pharmacyContactElement() {
        Element pharmacyElement = new Element();

        pharmacyElement.setIcon(R.drawable.ic_call);
        pharmacyElement.setTitle("Contact us");

        pharmacyElement.setOnClickListener(v -> {
            String contactNumber = "tel:" + getString(R.string.pharmacy_contact_number);

            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse(contactNumber));
            startActivity(intent);
        });
        return pharmacyElement;
    }

    Element authorElement() {
        Element authorElement = new Element();
        authorElement.setIcon(R.drawable.softtware_engineer);
        authorElement.setTitle("Arkanayan Shet");
        authorElement.setValue("Arkanayan Shet");
        authorElement.setGravity(Gravity.LEFT);
        authorElement.setOnClickListener(v -> {
            String authorEmail = "itsarkanayan@gmail.com";

/*            Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:" + authorEmail));

                    //new Intent(Intent.ACTION_SEND);
            emailIntent.setType("text/plain");
            emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[] {authorEmail}); // recipients

            startActivity(Intent.createChooser(emailIntent, "Contact me"));*/

            ShareCompat.IntentBuilder.from(this)
                    .setType("message/rfc822")
                    .addEmailTo(authorEmail)
                    .setChooserTitle("Contact developer")
                    .startChooser();
        });

    return authorElement;
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
