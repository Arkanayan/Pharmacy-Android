package bankura.pharmacy.pharmacyapp.activities;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.dd.processbutton.iml.ActionProcessButton;
import com.firebase.client.AuthData;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import bankura.pharmacy.pharmacyapp.App;
import bankura.pharmacy.pharmacyapp.R;
import bankura.pharmacy.pharmacyapp.Utils.Constants;
import bankura.pharmacy.pharmacyapp.controllers.OrderManager;
import bankura.pharmacy.pharmacyapp.controllers.UserManager;
import bankura.pharmacy.pharmacyapp.models.Address;
import bankura.pharmacy.pharmacyapp.models.Order;
import bankura.pharmacy.pharmacyapp.models.User;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * A placeholder fragment containing a simple view.
 */
public class NewOrderFragment extends BottomSheetDialogFragment {

    public static final String KEY_PRESCRIPTION_URI = "prescription_uri";
    private final String TAG = this.getClass().getSimpleName();
    private Firebase mRef = App.getFirebase();

    private CompositeSubscription mCompositeSubscription;

    private File mPrescriptionFile;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.textview_name)
    TextView nameTextView;

    @BindView(R.id.textview_address_line_1)
    TextView addressLine1TextView;

    @BindView(R.id.textview_address_line_2)
    TextView addressLine2TextView;

    @BindView(R.id.textview_landmark)
    TextView landmarkTextView;

    @BindView(R.id.textview_pin)
    TextView pinTextView;

    @BindView(R.id.button_edit_address)
    TextView editAddressButton;


    @BindView(R.id.button_scan)
    Button mScanButton;

    @BindView(R.id.imageview_prescription)
    ImageView prescriptionImageview;

    @BindView(R.id.input_note)
    EditText noteEditText;

    @BindView(R.id.button_submit_order)
    ActionProcessButton mSubmitButton;

    String mRxPath = "";

    public NewOrderFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Check if user logged in else go back to login activity
        if (App.getFirebase().getAuth() == null) {
            startActivity(LoginActivity.getInstance(getActivity()));
            dismiss();
            return;
        }
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

        mCompositeSubscription = new CompositeSubscription();

        disableSubmitButton();

        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) ((View) contentView.getParent()).getLayoutParams();
        CoordinatorLayout.Behavior behavior = params.getBehavior();
        if (behavior != null && behavior instanceof BottomSheetBehavior) {
            ((BottomSheetBehavior) behavior).setBottomSheetCallback(mBottomSheetBehaviorCallback);
            // ((BottomSheetBehavior) behavior).setPeekHeight(600);
            ((BottomSheetBehavior) behavior).setState(BottomSheetBehavior.STATE_EXPANDED);
        }

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        AuthData authData = mRef.getAuth();

        if (savedInstanceState != null) {
            if (savedInstanceState.getString(KEY_PRESCRIPTION_URI) != null) {
                String prescriptionUri = savedInstanceState.getString(KEY_PRESCRIPTION_URI);
                setmPrescriptionFile(new File(prescriptionUri));
            }
        }
       /* Observable<User> fetchUser = UserManager.getUserFromId(authData.getUid());
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
*/

        String uid = authData.getUid();

       mRef.child(Constants.Path.USERS).child(uid).addValueEventListener(new ValueEventListener() {
           @Override
           public void onDataChange(DataSnapshot dataSnapshot) {
               User user = dataSnapshot.getValue(User.class);
               if (user != null) {
                   populateUser(user);
               }
           }

           @Override
           public void onCancelled(FirebaseError firebaseError) {

           }
       });

        mRef.child(Constants.Path.ADDRESSES).child(uid).limitToFirst(1).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                dataSnapshot = dataSnapshot.getChildren().iterator().next();
                Address address = dataSnapshot.getValue(Address.class);
                if (address != null) {
                    populateAddress(address);
                }
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

    private void disableSubmitButton() {

        mSubmitButton.setEnabled(false);
        mSubmitButton.setClickable(false);
        mSubmitButton.setAlpha(0.5f);
    }
    private void enableSubmitButton() {

        mSubmitButton.setAlpha(1f);
        mSubmitButton.setClickable(true);
        mSubmitButton.setEnabled(true);
    }

    @OnClick(R.id.button_edit_address)
    void onEditAddress() {
        startActivity(EditUserActivity.getInstance(getActivity()));
    }

    @OnClick(R.id.button_scan)
    void onScan() {
        Log.d(TAG, "onScan: clicked");

        EasyImage.configuration(getContext())
                .setCopyExistingPicturesToPublicLocation(true)
                .setImagesFolderName("prescriptions");

        EasyImage.openChooserWithGallery(this, "Scan Prescription", 2);
    }

    @OnClick(R.id.imageview_prescription)
    void onPrescriptionClick() {

        if (mPrescriptionFile != null) {
            String uri = mPrescriptionFile.getPath();
            startActivity(ImageViewActivity.getInstance(getActivity(), uri));
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        EasyImage.handleActivityResult(requestCode, resultCode, data, getActivity(), new DefaultCallback() {
            @Override
            public void onImagePicked(File file, EasyImage.ImageSource imageSource, int i) {
                Log.d(TAG, "onImagePicked: file Absolute path: " + file.getAbsolutePath());
                Log.d(TAG, "onImagePicked: file  path: " + file.getPath());

                setmPrescriptionFile(file);
                attachImage(file);

               /* OrderManager.uploadImage(file)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(url -> {
                                Log.d(TAG, "onImagePicked: cloud url: " + url);
                                mRxPath = url;

                            }, throwable -> {
                                Snackbar.make(toolbar, "There is a problem creating order", Snackbar.LENGTH_SHORT).show();
                            });*/

            }

            @Override
            public void onImagePickerError(Exception e, EasyImage.ImageSource source, int type) {
               // super.onImagePickerError(e, source, type);
            }

            @Override
            public void onCanceled(EasyImage.ImageSource source, int type) {
                // delete captured but not used image
                if (source == EasyImage.ImageSource.CAMERA) {
                    File photoFile = EasyImage.lastlyTakenButCanceledPhoto(getContext());
                    if (photoFile != null) photoFile.delete();
                }
            }
        });
    }

    private void setmPrescriptionFile(File file) {
        mPrescriptionFile = file;
        attachImage(file);
        enableSubmitButton();
    }

    private void attachImage(File file) {
        Glide.with(this)
                .load(file)
                .fitCenter()
                .listener(new RequestListener<File, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, File model, Target<GlideDrawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, File model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        return false;
                    }
                })
                .into(prescriptionImageview);

    }

    @OnClick(R.id.button_submit_order)
    void submitOrder() {


        if (mPrescriptionFile != null) {
            // enable indeterminate mode
            mSubmitButton.setMode(ActionProcessButton.Mode.ENDLESS);
            mSubmitButton.setProgress(2);

         Observable<String> imageuploadObservable = OrderManager.uploadImage(mPrescriptionFile)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread());

            Observable<String> addressKeyObservable = UserManager.getAddressKey();


            Subscription orderSubscription = Observable.zip(imageuploadObservable, addressKeyObservable, (publicId, addressKey) -> {

                Map<String, String> map = new HashMap<String, String>();
                map.put("public_id", publicId);
                map.put("address_key", addressKey);

                return map;
            }).subscribe(map -> {
                String imageId = map.get("public_id");

                Log.d(TAG, "submitOrder: ImageUrl: " + imageId);

                String addressKey = map.get("address_key");

                Order order = new Order();
                order.setPrescriptionUrl(imageId);
                order.setAddress(addressKey);
                order.setNote(noteEditText.getText().toString());

                String orderId = OrderManager.createOrder(order);

                showSnackbar("Order created " + orderId);

            }, throwable -> {
                        showSnackbar(throwable.getMessage());
                        mSubmitButton.setProgress(-1);
                    }, () -> {
                        mSubmitButton.setProgress(100);
                        mSubmitButton.setClickable(false);
                    });

            mCompositeSubscription.add(orderSubscription);

        } else {
            showSnackbar("Please attach a prescription");
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mCompositeSubscription.unsubscribe();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mPrescriptionFile != null) {
            outState.putString(KEY_PRESCRIPTION_URI, mPrescriptionFile.getPath());
        }
    }

    private void showSnackbar(String message) {

        Snackbar.make(editAddressButton, message , Snackbar.LENGTH_LONG).show();

    }

    private void populateUser(User user) {

        nameTextView.setText(user.getFirstName() + " " + user.getLastName());
    }

    private void populateAddress(Address address) {

        addressLine1TextView.setText(address.getAddressLine1());
        addressLine2TextView.setText(address.getAddressLine2());
        landmarkTextView.setText(address.getLandmark());
        pinTextView.setText(String.valueOf(address.getPin()));
    }
    /*    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_new_order, container, false);
    }*/
}
