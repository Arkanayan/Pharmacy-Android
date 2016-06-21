package com.ahanapharmacy.app.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.ahanapharmacy.app.R;
import com.ahanapharmacy.app.Utils.Analytics;
import com.ahanapharmacy.app.Utils.Constants;
import com.ahanapharmacy.app.Utils.Prefs;
import com.ahanapharmacy.app.Utils.Utils;
import com.ahanapharmacy.app.controllers.UserManager;
import com.ahanapharmacy.app.messaging.MyInstanceIdService;
import com.ahanapharmacy.app.models.Address;
import com.ahanapharmacy.app.models.User;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.mobsandgeeks.saripaar.annotation.Optional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

public class EditUserActivity extends AppCompatActivity implements Validator.ValidationListener {

    private final String TAG = this.getClass().getSimpleName();

    CompositeSubscription compositeSubscription;

    private FirebaseAuth mAuth;
    private FirebaseUser mFirebaseUser;
    private FirebaseAnalytics mAnalytics;

    @NotEmpty
    @BindView(R.id.input_first_name)
    TextInputEditText firstNameEditText;

    @NotEmpty
    @BindView(R.id.input_last_name) TextInputEditText lastNameEditText;

    @NotEmpty
    @BindView(R.id.input_phone_number)
    TextInputEditText phoneNumberEditText;

    @Optional
    @BindView(R.id.input_email)
    TextInputEditText emailEditText;

    @NotEmpty
    @BindView(R.id.input_address_line_1)
    TextInputEditText addressLine1EditText;

    @BindView(R.id.input_address_line_2)
    TextInputEditText addressLine2EditText;

    @BindView(R.id.input_address_landmark)
    TextInputEditText addressLandmarkEditText;

    @NotEmpty
    @BindView(R.id.input_address_pin)
    TextInputEditText addressPinEditText;

    @BindView(R.id.fab_button_save)
    FloatingActionButton fabSaveButton;


    Validator validator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        compositeSubscription = new CompositeSubscription();

        mAuth = FirebaseAuth.getInstance();

        mFirebaseUser = mAuth.getCurrentUser();

        mAnalytics = FirebaseAnalytics.getInstance(this);

        // Check if user logged in else go back to login activity
        if (mFirebaseUser == null) {
            startActivity(LoginActivity.getInstance(EditUserActivity.this));
            finish();
            return;
        }


        setContentView(R.layout.activity_edit_user);
        ButterKnife.setDebug(true);

        ButterKnife.bind(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        validator = new Validator(this);
        validator.setValidationListener(this);


        Observable<User> fetchUser = UserManager.getUserFromId(mFirebaseUser.getUid());
        Observable<Address> fetchAddress = UserManager.getAddressFromId(mFirebaseUser.getUid());

/*        fetchUser.subscribe(user -> {
            Log.d(TAG, "User name: " + user.getFirstName());
            Log.d(TAG, "User phone: " + user.getPhoneNumber());
            Log.d(TAG, "User time: " + user.getCreatedAt());

        });*/

       Subscription fetchUserSubscription = Observable.zip(fetchUser, fetchAddress, (user, address) -> {

            Map<String, Object> map = new HashMap<String, Object>();
            map.put("user", user);
            map.put("address", address);
            return map;
            }
        ).subscribe(userAddressMap -> {
            User user = (User) userAddressMap.get("user");
            Address address = (Address) userAddressMap.get("address");

            // Add delay to show nice edit text animation
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    populateUserFields(user);
                    populateAddressFields(address);
                }
            }, 300);

            Log.d(TAG, "User name: " + user.getFirstName());
            Log.d(TAG, "User phone: " + user.getPhoneNumber());
            Log.d(TAG, "User time: " + user.getCreatedAt());
            Log.d(TAG, "Address line 1 " + address.getAddressLine1());

        }, throwable -> {
            throwable.printStackTrace();
            Toast.makeText(EditUserActivity.this, "There is some error on processing", Toast.LENGTH_SHORT).show();
        });

        compositeSubscription.add(fetchUserSubscription);


        if (checkPlayServices()) {
            // Start IntentService to  register this application with GCM
            Intent intent = new Intent(this, MyInstanceIdService.class);
            startService(intent);
        }


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);

    }

    // populate user related views with data from user
    private void populateUserFields(User user) {
        firstNameEditText.setText(Utils.getStringOrEmpty(user.getFirstName()));
        lastNameEditText.setText(Utils.getStringOrEmpty(user.getLastName()));
        phoneNumberEditText.setText(user.getPhoneNumber());
        emailEditText.setText(Utils.getStringOrEmpty(user.getEmailAddress()));
    }

    // populate address related views
    private void populateAddressFields(Address address) {

        addressLine1EditText.setText(address.getAddressLine1());
        addressLine2EditText.setText(address.getAddressLine2());
        addressLandmarkEditText.setText(address.getLandmark());
        addressPinEditText.setText(String.valueOf(address.getPin() == null ? "" : address.getPin()));
    }

    @OnClick(R.id.fab_button_save)
    void onFabClick() {
        //Snackbar.make(fabSaveButton, "Save", Snackbar.LENGTH_SHORT).show();
        validator.setViewValidatedAction(action);
        validator.validate();

    }

    Validator.ViewValidatedAction action = new Validator.ViewValidatedAction() {
        @Override
        public void onAllRulesPassed(View view) {
            if (view instanceof TextInputEditText) {
                ((TextInputLayout) view.getParent()).setErrorEnabled(false);
            }
        }
    };

    public static Intent getInstance(Context context) {
        return new Intent(context, EditUserActivity.class);
    }

    @Override
    protected void onDestroy() {
        if (compositeSubscription != null) {

            compositeSubscription.unsubscribe();
        }
        super.onDestroy();

    }

    @Override
    public void onValidationSucceeded() {

        Snackbar.make(firstNameEditText, "Saving...", Snackbar.LENGTH_INDEFINITE).show();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        Map<String, Object> userMap = new HashMap<String, Object>();

        String userEmail = emailEditText.getText().toString().trim();

        userMap.put(Constants.User.FIRST_NAME, firstNameEditText.getText().toString().trim());
        userMap.put(Constants.User.LAST_NAME, lastNameEditText.getText().toString().trim());
        userMap.put(Constants.User.EMAIL_ADDRESS, userEmail);

        if (!userEmail.equals("") && user != null) {
            user.updateEmail(userEmail);
        }

        String fcmToken = Prefs.getInstance(this).getString(Prefs.Key.FCM_REG_ID);
        if (fcmToken != null) {
            userMap.put(Constants.User.FCM_REG_ID, fcmToken);
        }

        Observable<Void> userUpdateObserver = UserManager.updateUser(userMap);

        Address address = new Address();
        address.setAddressLine1(addressLine1EditText.getText().toString().trim());
        address.setAddressLine2(addressLine2EditText.getText().toString().trim());
        address.setLandmark(addressLandmarkEditText.getText().toString().trim());
        address.setPin(Integer.valueOf(addressPinEditText.getText().toString().trim()));

        Observable<Void> addressUpdateObserver = UserManager.updateAddress(address);


        // user details update subscription
        Subscription infoUpdateSubscription = Observable.zip(userUpdateObserver, addressUpdateObserver, (t1, t2) -> {
            // return null because "Void" observable
            return null;
        }).subscribe(r -> {
            // do nothing on onNext
        }, infoUpdateThrowable -> {
            showSnackBarOnErrorWithRetry(infoUpdateThrowable.getMessage());

        }, () -> {
            // Set has details preferences to true
            Prefs.getInstance().put(Prefs.Key.IS_USER_DETAILS_PRESENT, true);
            // Update success and completed
            Timber.i("User info updated");
            Toast.makeText(this, "Info updated", Toast.LENGTH_SHORT).show();

            //analytics
            Bundle params = new Bundle();
            params.putString(Analytics.Param.USER_NAME, (String) userMap.get(Constants.User.FIRST_NAME) + userMap.get(Constants.User.LAST_NAME));
            params.putString(Analytics.Param.USER_ID, user != null ? user.getUid() : null);
            params.putBoolean(Analytics.Param.USER_EMAIL_PROVIDED, userEmail.isEmpty());
            params.putString(FirebaseAnalytics.Param.VALUE, user.getUid());

            mAnalytics.logEvent(Analytics.Event.EDIT_USER, params);

            mAnalytics.setUserProperty(Analytics.Param.USER_PIN_CODE, address.getPin().toString());
            mAnalytics.setUserProperty(Analytics.Param.USER_NAME, (String) userMap.get(Constants.User.FIRST_NAME) + userMap.get(Constants.User.LAST_NAME));

            Prefs.getInstance(this);
            // Finish edit user activity after some delay
            new Handler().postDelayed(() -> {
                if (Prefs.getInstance().getBoolean(Prefs.Key.IS_FIRST_TIME, true)) {
                    startActivity(NewOrderActivity.getInstance(this));
                    finish();
                } else {
                    finish();

                }

            }, 300);

        });

        compositeSubscription.add(infoUpdateSubscription);

    }



    private void showSnackBarOnErrorWithRetry(String message) {
        Snackbar.make(firstNameEditText, message , Snackbar.LENGTH_LONG)
                .setAction("Retry", v -> {
                    validator.validate();
                })
                .show();
    }

    @Override
    public void onValidationFailed(List<ValidationError> errors) {
        for (ValidationError error : errors) {
            View view = error.getView();
            String message = error.getCollatedErrorMessage(this);

            // Display error messages ;)
            if (view instanceof TextInputEditText) {
                ((TextInputLayout) view.getParent()).setError(message);
                view.requestFocus();
            } else {
                Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            }
        }
    }

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {
        final int PLAY_SERVICES_RESOLUTION_REQUEST = 9002;

        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Timber.i("Play services: This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

}
