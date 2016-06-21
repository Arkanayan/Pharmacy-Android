package com.ahanapharmacy.app.activities;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ahanapharmacy.app.R;
import com.ahanapharmacy.app.Utils.Analytics;
import com.ahanapharmacy.app.Utils.Constants;
import com.ahanapharmacy.app.Utils.Utils;
import com.ahanapharmacy.app.controllers.OrderManager;
import com.ahanapharmacy.app.models.Order;
import com.bumptech.glide.Glide;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

import static com.ahanapharmacy.app.models.Order.Status.ACKNOWLEDGED;
import static com.ahanapharmacy.app.models.Order.Status.CANCELED;
import static com.ahanapharmacy.app.models.Order.Status.COMPLETED;
import static com.ahanapharmacy.app.models.Order.Status.CONFIRMED;
import static com.ahanapharmacy.app.models.Order.Status.PENDING;

/**
 * A fragment representing a single Order detail screen.
 * This fragment is either contained in a {@link OrderListActivity}
 * in two-pane mode (on tablets) or a {@link OrderDetailActivity}
 * on handsets.
 */
public class OrderDetailFragment extends Fragment implements ValueEventListener {

    private final String TAG = this.getClass().getSimpleName();


    private Query mOrderRef;

    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ORDER_PATH = "order_path";
    public static final String ORDER_ID = "order_id";
    /**
     * The dummy content this fragment is presenting.
     */
    private String mOrderPath;
    private String mOrderId;

    private CompositeSubscription compositeSubscription;
    private FirebaseAnalytics mAnalytics;

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

        mAnalytics = FirebaseAnalytics.getInstance(getContext());

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
        } else if (getArguments().containsKey(ORDER_ID)) {
            mOrderId = getArguments().getString(ORDER_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_order_detail, container, false);
        ButterKnife.bind(this, rootView);

        // Show the dummy content as text in a TextView.
        if (mOrderPath != null) {
            Timber.d("onCreateView: order path: %s", mOrderPath);
          //  mOrderRef = App.getFirebase().child(Constants.Path.ORDERS).child(mOrderPath);
            mOrderRef = FirebaseDatabase.getInstance()
                    .getReference(Constants.Path.ORDERS)
                    .child(mOrderPath);

        /*    mOrderRef =  FirebaseDatabase.getInstance()
                    .getReference(Constants.Path.ORDERS)
                    .orderByChild(Constants.Order.ORDER_ID)
                    .equalTo(mOrderPath);*/


            mOrderRef.addValueEventListener(this);


        } else if (mOrderId != null) {
            Timber.d("onCreateView: order path: %s", mOrderId);
            //  mOrderRef = App.getFirebase().child(Constants.Path.ORDERS).child(mOrderPath);
            mOrderRef = FirebaseDatabase.getInstance()
                    .getReference(Constants.Path.ORDERS)
                    .orderByChild(Constants.Order.ORDER_ID)
                    .equalTo(mOrderId);

        /*    mOrderRef =  FirebaseDatabase.getInstance()
                    .getReference(Constants.Path.ORDERS)
                    .orderByChild(Constants.Order.ORDER_ID)
                    .equalTo(mOrderPath);*/


            mOrderRef.addValueEventListener(this);
        }
        return rootView;

    }

    @OnClick(R.id.image_view_prescripiton)
    void onPrescriptionClick(View view) {
        Timber.e("Prescription click");
        if (!mOrder.getPrescriptionUrl().isEmpty())
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

            Bundle params = new Bundle();
            Order order;
            if (mOrderId != null) {
                order = dataSnapshot.getChildren().iterator().next().getValue(Order.class);

            } else {
                order = dataSnapshot.getValue(Order.class);
            }
            mOrder = order;



            if (order != null) {

                // analytics
                params.putString(FirebaseAnalytics.Param.ITEM_ID, mOrder.getUid());
                params.putString(FirebaseAnalytics.Param.CONTENT_TYPE, Analytics.Param.ORDER_TYPE);
                params.putString(FirebaseAnalytics.Param.VALUE, mOrder.getOrderId());
                params.putString(Analytics.Param.ORDER_STATUS, mOrder.getStatus());
                params.putString(FirebaseAnalytics.Param.PRICE, mOrder.getPrice().toString());
                params.putString(FirebaseAnalytics.Param.SHIPPING, mOrder.getShippingCharge().toString());
                mAnalytics.logEvent(FirebaseAnalytics.Event.VIEW_ITEM, params);


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
                        .placeholder(ContextCompat.getDrawable(getContext(), R.drawable.ic_pill))
                        .into(prescriptionImageView);


                @Order.Status String status = order.getStatus();

                switch (status) {
                    case Order.Status.PENDING:
                        enableButton(cancelButton);
                        disableButton(confirmButton);
                        break;
                    case Order.Status.ACKNOWLEDGED:
                        enableButton(confirmButton);
                        enableButton(cancelButton);
                        break;
                    default:
                        disableButton(confirmButton);
                        disableButton(cancelButton);
                        break;
                }


                switch (status) {
                    case ACKNOWLEDGED:
                    case PENDING:
//                        statusImageView.setImageResource(R.drawable.status_pending);
                        statusImageView.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.status_pending));
                        break;
                    case COMPLETED:
//                        statusImageView.setImageResource(R.drawable.ok_mark);
                        statusImageView.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ok_mark));
                        break;
                    case CONFIRMED:
//                        statusImageView.setImageResource(R.drawable.delivery_truck);
                        statusImageView.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.delivery_truck));
                        break;
                    case CANCELED:
//                        statusImageView.setImageResource(R.drawable.shopping_cart_cancel);
                        statusImageView.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.shopping_cart_cancel));
                        break;
                }


            }
        } catch (Exception e) {
            Timber.e(e, "Order decode failed");
            Toast.makeText(getActivity(), "Sorry, Unable to fetch order", Toast.LENGTH_SHORT).show();

        }
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {
        Timber.e(databaseError.toException(), "Order fetch error");
        Toast.makeText(getActivity(), "Sorry, Unable to fetch order", Toast.LENGTH_SHORT).show();
    }

    public void deleteOrder() {
        if (mOrder != null) {
            // analytics
            Bundle params = new Bundle();
            params.putString(FirebaseAnalytics.Param.ITEM_ID, mOrder.getOrderId());
            params.putString(Analytics.Param.ORDER_STATUS, mOrder.getStatus());
            params.putString(FirebaseAnalytics.Param.PRICE, mOrder.getPrice().toString());
            params.putString(FirebaseAnalytics.Param.SHIPPING, mOrder.getShippingCharge().toString());
            params.putString(FirebaseAnalytics.Param.VALUE, mOrder.getOrderId());


            ProgressDialog orderCancelIndicator = new ProgressDialog(getContext());
            orderCancelIndicator.setTitle("Deleting Order...");
            orderCancelIndicator.setMessage("Please wait a moment...");
            orderCancelIndicator.setIndeterminate(true);
            orderCancelIndicator.setCancelable(false);
            orderCancelIndicator.show();

            OrderManager.deleteOrder(mOrder).subscribe(aVoid -> {
                Toast.makeText(getActivity(), "Order deleted onnext", Toast.LENGTH_SHORT).show();
                getActivity().finish();
            }, throwable -> {
                orderCancelIndicator.dismiss();
                Toast.makeText(getActivity(), "Order delete failed", Toast.LENGTH_SHORT).show();
            }, () -> {

                // log event
                mAnalytics.logEvent(Analytics.Event.ORDER_CANCEL, params);

                orderCancelIndicator.dismiss();
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

             OrderManager.setOrderStatus(mOrder, CONFIRMED).subscribe(aVoid -> {

             }, throwable -> {
                 Snackbar.make(v, "Sorry, Unable to confirm order", Snackbar.LENGTH_LONG)
                         .setAction("Retry", this::onConfirm)
                         .show();
             }, () -> {
                 Snackbar.make(v, "Order confirmed.", Snackbar.LENGTH_LONG)
                         .show();

                 // analytics
                 Bundle params = new Bundle();
                 params.putString(FirebaseAnalytics.Param.ITEM_ID, mOrder.getOrderId());
                 params.putString(Analytics.Param.ORDER_STATUS, mOrder.getStatus());
                 params.putString(FirebaseAnalytics.Param.PRICE, mOrder.getPrice().toString());
                 params.putString(FirebaseAnalytics.Param.SHIPPING, mOrder.getShippingCharge().toString());
                 params.putString(FirebaseAnalytics.Param.VALUE, mOrder.getOrderId());


                 mAnalytics.logEvent(Analytics.Event.ORDER_CONFIRM, params);
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
