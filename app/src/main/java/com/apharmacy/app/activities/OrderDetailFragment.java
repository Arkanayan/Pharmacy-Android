package com.apharmacy.app.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.apharmacy.app.App;
import com.apharmacy.app.R;
import com.apharmacy.app.Utils.Constants;
import com.apharmacy.app.Utils.Utils;
import com.apharmacy.app.controllers.OrderManager;
import com.apharmacy.app.models.Order;
import com.bumptech.glide.Glide;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

/**
 * A fragment representing a single Order detail screen.
 * This fragment is either contained in a {@link OrderListActivity}
 * in two-pane mode (on tablets) or a {@link OrderDetailActivity}
 * on handsets.
 */
public class OrderDetailFragment extends Fragment {

    private final String TAG = this.getClass().getSimpleName();

    @BindView(R.id.order_detail)
    TextView orderTextView;

    Firebase mOrderRef;

    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ORDER_PATH = "order_path";

    /**
     * The dummy content this fragment is presenting.
     */
    private String mOrderPath;

    private CompositeSubscription compositeSubscription;

    @BindView(R.id.image_view_prescripiton)
    ImageView prescriptionImageView;

    ValueEventListener mOrderListener;

    private Order mOrder;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public OrderDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        compositeSubscription = new CompositeSubscription();

        if (getArguments().containsKey(ORDER_PATH)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            mOrderPath = getArguments().getString(ORDER_PATH);

/*            Activity activity = this.getActivity();
            CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
            if (appBarLayout != null) {
                appBarLayout.setTitle(mItem.content);
            }*/
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_order_detail, container, false);
        ButterKnife.bind(this, rootView);

        // Show the dummy content as text in a TextView.
        if (mOrderPath != null) {
            Log.d(TAG, "onCreateView: order path: " + mOrderPath);
            mOrderRef = App.getFirebase().child(Constants.Path.ORDERS).child(mOrderPath);

/*           Subscription fetchOrderSubscription =  OrderManager.fetchOrder(mOrderPath).subscribe(order -> {
                orderTextView.setText(order.getUid());
            }, throwable -> {
               orderTextView.setText(throwable.getLocalizedMessage());
            });

            compositeSubscription.add(fetchOrderSubscription);*/
            mOrderListener = mOrderRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Order order = dataSnapshot.getValue(Order.class);
                    mOrder = order;

                    if (order != null) {
                        orderTextView.setText(order.getNote());

                        String url = Utils.getImageLowerUrl(order.getPrescriptionUrl());

                        Glide.with(getActivity())
                                .load(url)
                                .placeholder(R.drawable.house)
                                .into(prescriptionImageView);
                    }
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {

                }
            });

/*            prescriptionImageView.setOnClickListener(v -> {

                OrderManager.deleteOrder(mOrder).subscribe(aVoid -> {

                }, throwable -> {
                    Toast.makeText(getActivity(), "Order delete failed", Toast.LENGTH_SHORT).show();
                }, () -> {
                    Toast.makeText(getActivity(), "Order deleted", Toast.LENGTH_SHORT).show();
                    getActivity().finish();
                });

*//*                OrderManager.deleteOrderByKey(mOrderPath).subscribe(aVoid -> {}, throwable -> {
                    Toast.makeText(getActivity(), "Order delete failed", Toast.LENGTH_SHORT).show();
                }, () -> {

                    Toast.makeText(getActivity(), "Order deleted", Toast.LENGTH_SHORT).show();
                    getActivity().finish();
                });
            });*//*

            });*/
        }
        return rootView;

    }

    @OnClick(R.id.image_view_prescripiton)
    void onPrescriptionClick(View view) {
        Timber.e("Prescription click");
        OrderManager.deleteOrder(mOrder).subscribe(aVoid -> {

        }, throwable -> {
            Toast.makeText(getActivity(), "Order delete failed", Toast.LENGTH_SHORT).show();
        }, () -> {
            Toast.makeText(getActivity(), "Order deleted", Toast.LENGTH_SHORT).show();
            getActivity().finish();
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mOrderRef.removeEventListener(mOrderListener);
        compositeSubscription.unsubscribe();

    }
}
