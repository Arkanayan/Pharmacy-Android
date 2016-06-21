package com.ahanapharmacy.app.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.webkit.WebView;

import com.ahanapharmacy.app.App;
import com.ahanapharmacy.app.R;
import com.ahanapharmacy.app.Utils.Analytics;
import com.ahanapharmacy.app.adapters.OrdersAdapter;
import com.ahanapharmacy.app.messaging.MyInstanceIdService;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

import butterknife.ButterKnife;
import jp.wasabeef.recyclerview.animators.SlideInRightAnimator;
import timber.log.Timber;

/**
 * An activity representing a list of Orders. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link OrderDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class OrderListActivity extends AppCompatActivity implements OrdersAdapter.OnOrderClickListener{

    public final String TAG = this.getClass().getSimpleName();
    private FirebaseRemoteConfig mRemoteConfig;
    private FirebaseAnalytics mAnalytics;

    private AlertDialog mAlertDialog;
    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;

    private OrdersAdapter mOrdersAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mRemoteConfig = FirebaseRemoteConfig.getInstance();
        mAnalytics = FirebaseAnalytics.getInstance(this);

        mRemoteConfig.setDefaults(R.xml.remote_config_defaults);

        mRemoteConfig.fetch(2000)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        mRemoteConfig.activateFetched();
                    }
                });

        // Check if user logged in else go back to login activity
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            startActivity(LoginActivity.getInstance(this));
            finish();
            return;
        }

        setContentView(R.layout.activity_order_list);
        ButterKnife.bind(this);

        if (checkPlayServices()) {
            // Start IntentService to  register this application with GCM
            Intent intent = new Intent(this, MyInstanceIdService.class);
            startService(intent);
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(NewOrderActivity.getInstance(OrderListActivity.this));

               /* BottomSheetDialogFragment bottomSheetDialogFragment = new NewOrderFragment();
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    Fade explode = new Fade();
                    explode.setDuration(1000);
                    bottomSheetDialogFragment.setEnterTransition(explode);
                }

                bottomSheetDialogFragment.show(getSupportFragmentManager(), bottomSheetDialogFragment.getTag());*/

            }
        });

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.order_list);
        assert recyclerView != null;
       // setupRecyclerView((RecyclerView) recyclerView);
        mOrdersAdapter = new OrdersAdapter(recyclerView, this);

        // Set item animator
        // Set animator
        SlideInRightAnimator slideAnimator = new SlideInRightAnimator(new OvershootInterpolator(0.5f));
        slideAnimator.setAddDuration(300);
        slideAnimator.setRemoveDuration(300);
        slideAnimator.setChangeDuration(300);
        slideAnimator.setMoveDuration(300);
        recyclerView.setItemAnimator(slideAnimator);

        recyclerView.setAdapter(mOrdersAdapter);

/*        if (mOrdersAdapter.getItemCount() < 1) {
            emptyTextView.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            emptyTextView.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }*/

        if (findViewById(R.id.order_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }
    }

    @Override
    public void onOrderClick(String orderPath) {
        if (mTwoPane) {
            Bundle arguments = new Bundle();
            arguments.putString(OrderDetailFragment.ORDER_PATH, orderPath);
            OrderDetailFragment fragment = new OrderDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.order_detail_container, fragment)
                    .commit();
        } else {

            startActivity(OrderDetailActivity.getInstance(this, orderPath));

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_order_list, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_settings:
                startActivity(EditUserActivity.getInstance(this));
                break;
            case R.id.action_logout:
                App.logout();
                startActivity(LoginActivity.getInstance(this));
                Bundle params = new Bundle();
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {

                    params.putString(Analytics.Param.USER_ID, user.getUid());
                    params.putString(Analytics.Param.USER_NAME, user.getDisplayName());
                    params.putString(FirebaseAnalytics.Param.VALUE, user.getUid());

                }
                mAnalytics.logEvent(Analytics.Event.LOGOUT, params);
                finish();
                return false;
            case R.id.action_about:
                startActivity(AboutPage.getInstance(this));
                break;
            case R.id.action_licenses:
                displayLicensesAlertDialog();
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    public static Intent getInstance(Context context) {
        return new Intent(context, OrderListActivity.class);
    }

    @Override
    protected void onDestroy() {
        // Clean up child listener
        mOrdersAdapter.cleanUp();
        super.onDestroy();
    }

    // Displays licenses
    private void displayLicensesAlertDialog() {
        WebView view = (WebView) LayoutInflater.from(this).inflate(R.layout.dialog_licenses, null);
        view.loadUrl("file:///android_asset/open_source_licenses.html");
        mAlertDialog = new AlertDialog.Builder(this, R.style.Theme_AppCompat_Light_Dialog_Alert)
                .setTitle(getString(R.string.action_licenses))
                .setView(view)
                .setPositiveButton(android.R.string.ok, null)
                .show();
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

