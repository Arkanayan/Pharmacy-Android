package bankura.pharmacy.pharmacyapp.activities;

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

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.mobsandgeeks.saripaar.annotation.Optional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bankura.pharmacy.pharmacyapp.App;
import bankura.pharmacy.pharmacyapp.R;
import bankura.pharmacy.pharmacyapp.Utils.Constants;
import bankura.pharmacy.pharmacyapp.Utils.Utils;
import bankura.pharmacy.pharmacyapp.controllers.UserManager;
import bankura.pharmacy.pharmacyapp.models.Address;
import bankura.pharmacy.pharmacyapp.models.User;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;

public class EditUserActivity extends AppCompatActivity implements Validator.ValidationListener {

    public final String TAG = this.getClass().getSimpleName();

    Firebase mRef = App.getFirebase();

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

    @NotEmpty
    @BindView(R.id.input_address_line_2)
    TextInputEditText addressLine2EditText;

    @NotEmpty
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

        // Check if user logged in else go back to login activity
        if (mRef.getAuth() == null) {
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

        AuthData authData = mRef.getAuth();

        Observable<User> fetchUser = UserManager.getUserFromId(authData.getUid());
        Observable<Address> fetchAddress = UserManager.getAddressFromId(authData.getUid());

/*        fetchUser.subscribe(user -> {
            Log.d(TAG, "User name: " + user.getFirstName());
            Log.d(TAG, "User phone: " + user.getPhoneNumber());
            Log.d(TAG, "User time: " + user.getCreatedAt());

        });*/

        Observable.zip(fetchUser, fetchAddress, (user, address) -> {

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
    public void onValidationSucceeded() {
        Map<String, Object> userMap = new HashMap<String, Object>();

        userMap.put(Constants.User.FIRST_NAME, firstNameEditText.getText().toString().trim());
        userMap.put(Constants.User.LAST_NAME, lastNameEditText.getText().toString().trim());
        userMap.put(Constants.User.EMAIL_ADDRESS, emailEditText.getText().toString().trim());

        UserManager.updateUser(userMap);

        Address address = new Address();
        address.setAddressLine1(addressLine1EditText.getText().toString().trim());
        address.setAddressLine2(addressLine2EditText.getText().toString().trim());
        address.setLandmark(addressLandmarkEditText.getText().toString().trim());
        address.setPin(Integer.valueOf(addressPinEditText.getText().toString().trim()));

        UserManager.updateAddress(address).subscribe(aVoid -> {
            // it doesn't return anything so no op in onNext()
        }, throwable -> {
            showSnackBarOnErrorWithRetry("Sorry, Address couldn't be updated");
        }, () -> {
            Log.d(TAG, "Address updated");
            Toast.makeText(this, "Info updated", Toast.LENGTH_SHORT).show();
        });

        Log.d(TAG, "onValidationSucceeded: ");
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
}
