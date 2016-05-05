package bankura.pharmacy.pharmacyapp.activities;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.client.AuthData;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.util.HashMap;
import java.util.Map;

import bankura.pharmacy.pharmacyapp.App;
import bankura.pharmacy.pharmacyapp.R;
import bankura.pharmacy.pharmacyapp.controllers.UserManager;
import bankura.pharmacy.pharmacyapp.models.Address;
import bankura.pharmacy.pharmacyapp.models.User;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;

/**
 * A placeholder fragment containing a simple view.
 */
public class NewOrderFragment extends BottomSheetDialogFragment {

    private final String TAG = this.getClass().getSimpleName();
    private Firebase mRef = App.getFirebase();

    @BindView(R.id.toolbar)
    Toolbar toolbar;

/*    @BindView(R.id.label_edit_address)
    TextView editAddressTextView;*/

    @BindView(R.id.button_edit_address)
    Button editAddressButton;

    @BindView(R.id.input_name)
    EditText nameEditText;

    @BindView(R.id.input_address_line_1)
    EditText addressLine1EditText;

    public NewOrderFragment() {
    }

    private BottomSheetBehavior.BottomSheetCallback mBottomSheetBehaviorCallback = new BottomSheetBehavior.BottomSheetCallback() {

        @Override
        public void onStateChanged(@NonNull View bottomSheet, int newState) {

            switch (newState) {
                case BottomSheetBehavior.STATE_HIDDEN:
                    dismiss();
                    break;
            }


        }

        @Override
        public void onSlide(@NonNull View bottomSheet, float slideOffset) {
        }
    };


    @Override
    public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        View contentView = View.inflate(getContext()
                , R.layout.fragment_new_order, null);
        ButterKnife.bind(this, contentView);
        dialog.setContentView(contentView);

        setupToolbar();

        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) ((View) contentView.getParent()).getLayoutParams();
        CoordinatorLayout.Behavior behavior = params.getBehavior();
        if (behavior != null && behavior instanceof BottomSheetBehavior) {
            ((BottomSheetBehavior) behavior).setBottomSheetCallback(mBottomSheetBehaviorCallback);
        }

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        AuthData authData = mRef.getAuth();

        Observable<User> fetchUser = UserManager.getUserFromId(authData.getUid());
        Observable<Address> fetchAddress = UserManager.getAddressFromId(authData.getUid());
        Observable.zip(fetchUser, fetchAddress, (user, address) -> {

                    Map<String, Object> map = new HashMap<String, Object>();
                    map.put("user", user);
                    map.put("address", address);
                    return map;
                }
        ).subscribe(userAddressMap -> {
            User user = (User) userAddressMap.get("user");
            Address address = (Address) userAddressMap.get("address");

            nameEditText.setText(user.getFirstName());
            Log.d(TAG, "User name: " + user.getFirstName());
            Log.d(TAG, "User phone: " + user.getPhoneNumber());
            Log.d(TAG, "User time: " + user.getCreatedAt());
            Log.d(TAG, "Address line 1 " + address.getAddressLine1());

        }, throwable -> {
            throwable.printStackTrace();
            Toast.makeText(getActivity(), "Sorry, unable to fetch data", Toast.LENGTH_SHORT).show();
        });

        mRef.child("addresses").child(authData.getUid()).limitToFirst(1).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Address address = dataSnapshot.getValue(Address.class);
                addressLine1EditText.setText(address.getAddressLine1());
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Address address = dataSnapshot.getValue(Address.class);
                addressLine1EditText.setText(address.getAddressLine1());

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

        return super.onCreateView(inflater, container, savedInstanceState);

    }

    private void setupToolbar() {
        if (toolbar == null) {
            return;
        }

/*        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(toolbar);
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        activity.getSupportActionBar().setTitle("New Order");
        activity.getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);
        activity.getSupportActionBar().setHomeButtonEnabled(true);*/

        toolbar.setNavigationIcon(R.drawable.ic_close);
        toolbar.setTitle("New Order");
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    @OnClick(R.id.button_edit_address)
    void onEditAddress() {
        startActivity(EditUserActivity.getInstance(getActivity()));
    }

    @OnClick(R.id.button_scan)
    void onScan() {


    }



    /*    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_new_order, container, false);
    }*/
}
