package com.apharmacy.app.activities;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
public class OrderDetailFragment extends Fragment implements ValueEventListener {

    private final String TAG = this.getClass().getSimpleName();



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


    @BindView(R.id.text_view_order_id)
    TextView orderIdTextView;

    @BindView(R.id.image_view_prescripiton)
    ImageView prescriptionImageView;

    @BindView(R.id.button_order_confirm)
    Button confirmButton;

    @BindView(R.id.button_order_cancel)
    Button cancelButton;

    @BindView(R.id.text_view_total_price)
    TextView totalPriceTextView;

    @BindView(R.id.text_view_order_price)
    TextView orderPriceTextView;

    @BindView(R.id.text_view_shipping_charge)
    TextView shippingChargeTextView;

    @BindView(R.id.text_view_seller_note)
    TextView sellerNoteTextView;

    @BindView(R.id.image_view_status)
    ImageView statusImageView;

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


            mOrderRef.addValueEventListener(this);


        }
        return rootView;

    }

    @OnClick(R.id.image_view_prescripiton)
    void onPrescriptionClick(View view) {
        Timber.e("Prescription click");

        startActivity(ImageViewActivity.getInstance(getActivity(),Utils.getImageLowerUrl(mOrder.getPrescriptionUrl())));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mOrderRef.removeEventListener(this);
        compositeSubscription.unsubscribe();

    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        try {
            Order order = dataSnapshot.getValue(Order.class);
            mOrder = order;

            if (order != null) {
                orderIdTextView.setText(order.getOrderId());

                String orderPrice = getResources().getString(R.string.price, order.getPrice());
                String shippingCharge = getResources().getString(R.string.price, order.getShippingCharge());
                String totalPrice = getResources().getString(R.string.price,
                        order.getPrice() + order.getShippingCharge()
                        );

                orderPriceTextView.setText(orderPrice);
                shippingChargeTextView.setText(shippingCharge);
                totalPriceTextView.setText(totalPrice);

                // seller note
                sellerNoteTextView.setText(order.getSellerNote());

                String url = Utils.getThumbUrl(order.getPrescriptionUrl());

                Glide.with(getActivity())
                        .load(url)
                        .placeholder(R.drawable.pill)
                        .into(prescriptionImageView);


                Order.Status status = order.getStatus();

                if (status == Order.Status.OPEN) {
                    enableButton(cancelButton);
                    disableButton(confirmButton);
                } else if (status == Order.Status.ACKNOWLEDGED) {
                    enableButton(confirmButton);
                    enableButton(cancelButton);
                } else {
                    disableButton(confirmButton);
                    disableButton(cancelButton);
                }


                switch (status) {
                    case ACKNOWLEDGED:
                    case OPEN:
                        statusImageView.setImageResource(R.drawable.status_open);
                        break;
                    case COMPLETED:
                        statusImageView.setImageResource(R.drawable.ok_mark);
                        break;
                    case CONFIRMED:
                        statusImageView.setImageResource(R.drawable.delivery_truck);
                        break;
                    case CANCELED:
                        statusImageView.setImageResource(R.drawable.shopping_cart_cancel);
                        break;
                }


            }
        } catch (Exception e) {
            Timber.e(e, "Order decode failed");
            Toast.makeText(getActivity(), "Sorry, Unable to fetch order", Toast.LENGTH_SHORT).show();

        }
    }

    @Override
    public void onCancelled(FirebaseError firebaseError) {
        Timber.e(firebaseError.toException(), "Order fetch error");
        Toast.makeText(getActivity(), "Sorry, Unable to fetch order", Toast.LENGTH_SHORT).show();
    }

    public void deleteOrder() {
        if (mOrder != null) {

            OrderManager.deleteOrder(mOrder).subscribe(aVoid -> {
                Toast.makeText(getActivity(), "Order deleted onnext", Toast.LENGTH_SHORT).show();
                getActivity().finish();
            }, throwable -> {
                Toast.makeText(getActivity(), "Order delete failed", Toast.LENGTH_SHORT).show();
            }, () -> {
                Toast.makeText(getActivity(), "Order deleted", Toast.LENGTH_SHORT).show();
                getActivity().finish();
            });
        }
    }

    @OnClick(R.id.button_order_cancel)
    void onCancel(View v) {

        new AlertDialog.Builder(getActivity())
                .setTitle("Cancel Order")
                .setMessage("Do you want to cancel the order ? It will delete the order permanently.")
                .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                    deleteOrder();
                }).setNegativeButton(android.R.string.no, null).show();
    }

    @OnClick(R.id.button_order_confirm)
     void onConfirm(View v) {
        if (mOrder != null) {

             OrderManager.setOrderStatus(mOrder, Order.Status.CONFIRMED).subscribe(aVoid -> {

             }, throwable -> {
                 Snackbar.make(v, "Sorry, Unable to confirm order", Snackbar.LENGTH_LONG)
                         .setAction("Retry", this::onConfirm)
                         .show();
             }, () -> {
                 Snackbar.make(v, "Order confirmed.", Snackbar.LENGTH_LONG)
                         .show();
             });
        }
    }


    private void disableButton(Button button) {
        button.setClickable(false);
        button.setAlpha(0.5f);
    }

    private void enableButton(Button button) {
        button.setClickable(true);
        button.setAlpha(1f);
    }
}
