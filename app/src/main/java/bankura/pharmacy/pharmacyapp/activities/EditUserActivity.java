package bankura.pharmacy.pharmacyapp.activities;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.firebase.client.AuthData;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.firebase.client.realtime.util.StringListReader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import bankura.pharmacy.pharmacyapp.App;
import bankura.pharmacy.pharmacyapp.R;
import bankura.pharmacy.pharmacyapp.Utils.Utils;
import bankura.pharmacy.pharmacyapp.controllers.UserManager;
import bankura.pharmacy.pharmacyapp.models.Address;
import bankura.pharmacy.pharmacyapp.models.User;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.Subscription;

public class EditUserActivity extends AppCompatActivity {

    public final String TAG = this.getClass().getSimpleName();

    Firebase mRef = App.getFirebase();

    @BindView(R.id.input_first_name)
    TextInputEditText firstNameEditText;

    @BindView(R.id.input_last_name) TextInputEditText lastNameEditText;

    @BindView(R.id.input_phone_number)
    TextInputEditText phoneNumberEditText;

    @BindView(R.id.input_address_line_1)
    TextInputEditText addressLine1EditText;

    @BindView(R.id.input_address_line_2)
    TextInputEditText addressLine2EditText;

    @BindView(R.id.input_address_landmark)
    TextInputEditText addressLandmarkEditText;

    @BindView(R.id.input_address_pin)
    TextInputEditText addressPinEditText;

    @BindView(R.id.fab_button_save)
    FloatingActionButton fabSaveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user);
        ButterKnife.bind(this);
        ButterKnife.setDebug(true);

        if (mRef.getAuth() == null) {
            startActivity(LoginActivity.getInstance(EditUserActivity.this));
            finish();
            return;
        }

        phoneNumberEditText = (TextInputEditText) findViewById(R.id.input_phone_number);

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

            populateUserFields(user);
            populateAddressFields(address);
            Log.d(TAG, "User name: " + user.getFirstName());
            Log.d(TAG, "User phone: " + user.getPhoneNumber());
            Log.d(TAG, "User time: " + user.getCreatedAt());
            Log.d(TAG, "Address line 1 " + address.getAddressLine1());

        }, throwable -> {
            throwable.printStackTrace();
            Toast.makeText(EditUserActivity.this, "There is some error on processing", Toast.LENGTH_SHORT).show();
        });



    }

    // populate user related views with data from user
    private void populateUserFields(User user) {
        firstNameEditText.setText(Utils.getStringOrEmpty(user.getFirstName()));
        lastNameEditText.setText(Utils.getStringOrEmpty(user.getLastName()));
        phoneNumberEditText.setText(user.getPhoneNumber());
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
        Snackbar.make(fabSaveButton, "Save", Snackbar.LENGTH_SHORT).show();

    }

    public static Intent getInstance(Context context) {
        return new Intent(context, EditUserActivity.class);
    }
}
