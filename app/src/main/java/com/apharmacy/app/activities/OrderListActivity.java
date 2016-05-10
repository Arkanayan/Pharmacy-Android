package com.apharmacy.app.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.OvershootInterpolator;

import com.apharmacy.app.App;
import com.apharmacy.app.R;
import com.apharmacy.app.adapters.OrdersAdapter;
import butterknife.ButterKnife;
import jp.wasabeef.recyclerview.animators.SlideInRightAnimator;

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
    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;

    private OrdersAdapter mOrdersAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Check if user logged in else go back to login activity
        if (App.getFirebase().getAuth() == null) {
            startActivity(LoginActivity.getInstance(this));
            finish();
            return;
        }

        setContentView(R.layout.activity_order_list);
        ButterKnife.bind(this);

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


        // Set animator
        SlideInRightAnimator slideAnimator = new SlideInRightAnimator(new OvershootInterpolator(0.5f));
        slideAnimator.setAddDuration(300);
        slideAnimator.setRemoveDuration(300);
        slideAnimator.setChangeDuration(300);
        slideAnimator.setMoveDuration(300);

       // recyclerView.setItemAnimator(slideAnimator);
//        SlideInOutRightItemAnimator animator = new SlideInOutRightItemAnimator(recyclerView);
//        animator.setAddDuration(400);
//        animator.setChangeDuration(400);
        recyclerView.setItemAnimator(slideAnimator);
        recyclerView.setAdapter(mOrdersAdapter);

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
}
