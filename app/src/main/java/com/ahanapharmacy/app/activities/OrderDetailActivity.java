package com.ahanapharmacy.app.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.ahanapharmacy.app.R;

/**
 * An activity representing a single Order detail screen. This
 * activity is only used narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link OrderListActivity}.
 */
public class OrderDetailActivity extends AppCompatActivity {

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);


        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // savedInstanceState is non-null when there is fragment state
        // saved from previous configurations of this activity
        // (e.g. when rotating the screen from portrait to landscape).
        // In this case, the fragment will automatically be re-added
        // to its container so we don't need to manually add it.
        // For more information, see the Fragments API guide at:
        //
        // http://developer.android.com/guide/components/fragments.html
        //
        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
            Bundle arguments = new Bundle();

            // If order id is provided
            if (getIntent().hasExtra(OrderDetailFragment.ORDER_ID)) {
                arguments.putString(OrderDetailFragment.ORDER_ID,
                        getIntent().getStringExtra(OrderDetailFragment.ORDER_ID));
            } else if (getIntent().hasExtra(OrderDetailFragment.ORDER_PATH)) {
                // if order path is provided
                arguments.putString(OrderDetailFragment.ORDER_PATH,
                        getIntent().getStringExtra(OrderDetailFragment.ORDER_PATH));
            }
            OrderDetailFragment fragment = new OrderDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.order_detail_container, fragment)
                    .commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. Use NavUtils to allow users
            // to navigate up one level in the application structure. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //
            NavUtils.navigateUpTo(this, new Intent(this, OrderListActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static Intent getInstance(Context context, String orderPath) {

        Intent detailOrder = new Intent(context, OrderDetailActivity.class);
        detailOrder.putExtra(OrderDetailFragment.ORDER_PATH, orderPath);
        return detailOrder;
    }

    public static Intent getInstanceByOrderId(Context context, String orderId) {

        Intent detailOrder = new Intent(context, OrderDetailActivity.class);
        detailOrder.putExtra(OrderDetailFragment.ORDER_ID, orderId);
        return detailOrder;
    }
}
