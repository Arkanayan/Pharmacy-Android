package bankura.pharmacy.pharmacyapp.activities;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import bankura.pharmacy.pharmacyapp.App;
import bankura.pharmacy.pharmacyapp.R;
import bankura.pharmacy.pharmacyapp.models.User;
import butterknife.BindView;
import butterknife.ButterKnife;

public class EditUserActivity extends AppCompatActivity {

    public final String TAG = this.getClass().getSimpleName();

    Firebase mRef = App.getFirebase();

    @BindView(R.id.input_first_name)
    TextInputEditText firstNameEditText;

    @BindView(R.id.input_last_name) TextInputEditText lastNameEditText;

    @BindView(R.id.input_phone_number)
    TextInputEditText phoneNumberEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user);
        ButterKnife.bind(this);
        ButterKnife.setDebug(true);

        if (mRef.getAuth() == null) {
            startActivity(LoginActivity.getInstance(this));
            finish();
        }

        phoneNumberEditText = (TextInputEditText) findViewById(R.id.input_phone_number);

        mRef.child("users").child(mRef.getAuth().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                Log.d(TAG, "User name: " + user.getFirstName());
                Log.d(TAG, "User phone: " + user.getPhoneNumber());
                Log.d(TAG, "User time: " + user.getCreatedAt());
                phoneNumberEditText.setText(user.getPhoneNumber());
                firstNameEditText.setText((user.getFirstName() == null ? "" : user.getFirstName()));
                lastNameEditText.setText((user.getLastName() == null ? "" : user.getLastName()));


            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });


    }


    public static Intent getInstance(Context context) {
        return new Intent(context, EditUserActivity.class);
    }
}
