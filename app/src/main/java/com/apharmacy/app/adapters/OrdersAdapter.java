package com.apharmacy.app.adapters;

import android.graphics.PorterDuff;
import android.support.annotation.ColorInt;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.apharmacy.app.App;
import com.apharmacy.app.R;
import com.apharmacy.app.models.Order;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by arka on 4/30/16.
 */
public class OrdersAdapter extends RecyclerView.Adapter<OrdersAdapter.ViewHolder> {

    public final String TAG = this.getClass().getSimpleName();

    private List<Order> mOrderList;
    private RecyclerView mRecyclerView;
    private OnOrderClickListener mListener;
    private Query mOrdersQuery;
    private ChildEventListener mOrdersListener;

    public OrdersAdapter(RecyclerView recyclerView, OnOrderClickListener listener) {
        mOrderList = new ArrayList<Order>();
        mRecyclerView = recyclerView;
        mListener = listener;

        Firebase ref = App.getFirebase();
        String uid = App.getFirebase().getAuth().getUid();

        mOrdersQuery = ref.child("orders").orderByChild("uid").equalTo(uid);

        mOrdersListener =  mOrdersQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                try {
                    Log.d(TAG, "onChildAdded: key: " + dataSnapshot.getKey());
                    Order order = dataSnapshot.getValue(Order.class);
                    mOrderList.add(0, order);
                    notifyItemInserted(mOrderList.indexOf(order));
                    // mRecyclerView.smoothScrollToPosition(0);
                    //  ((LinearLayoutManager) mRecyclerView.getLayoutManager()).scrollToPositionWithOffset(0, 20);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                try {
                    Log.d(TAG, "onChildchanged: key: " + dataSnapshot.getKey());
                    Order order = dataSnapshot.getValue(Order.class);
                    if (order != null) {

                        int position = mOrderList.indexOf(order);
                        mOrderList.set(position, order);
                        Log.d(TAG, "onChildchanged: position: " + position);
                        Log.d(TAG, "onChildChanged: new shipping charge: " + order.getShippingCharge());
                        notifyItemChanged(position, order);


                        // notifyItemRangeChanged(position, 1, order);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                try {
                    Order order = dataSnapshot.getValue(Order.class);
                    if (order != null) {
                        Log.d(TAG, "onChildRemoved: ID: " + order.getOrderId());
                        Log.d(TAG, "onChildRemoved: position: " + mOrderList.indexOf(order));
                        // the order of the next two lines are important don't change it
                        // otherwise recyclerview will remove different child each time
                        notifyItemRemoved(mOrderList.indexOf(order));
                        mOrderList.remove(order);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                Log.d(TAG, "onChildMoved: key: " + dataSnapshot.getKey());

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.e(TAG, "onCancelled: Error: " + firebaseError.getMessage() );
                Snackbar.make(mRecyclerView, "There is a problem retriving orders", Snackbar.LENGTH_SHORT);
            }
        });

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
       // Log.d(TAG, "onCreateViewHolder");
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.order_list_content, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        try {
            //Log.d(TAG, "onBindViewHolder: " + position);
            Order order = mOrderList.get(position);
            Order.Status status = order.getStatus();
            String orderId = order.getOrderId();
            double price = order.getPrice() + order.getShippingCharge();
            //    holder.mContentView.setText(String.valueOf(mOrderList.get(position).getPrice()));
            //test
//        holder.mContentView.setText(content);
            //  holder.mIdView.setText(String.valueOf(mOrderList.get(position).getShippingCharge()));

       /* holder.mContentView.setText(App.getContext().getResources().getString(R.string.price, 15.0f));

        long epoch = mOrderList.get(position).getCreatedAt() * 1000;
        Date date = new Date(epoch);
        DateFormat dateFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        holder.mIdView.setText(dateFormat.format(epoch));*/

            holder.mOrderIdTextView.setText(orderId);
            holder.mPriceTextView.setText(App.getContext().getResources().getString(R.string.price, price));

            SimpleDateFormat monthFormat = new SimpleDateFormat("MMM", Locale.getDefault());
            holder.mMonthTextView.setText(monthFormat.format(order.getCreatedAtLong()));

            SimpleDateFormat dateFormat = new SimpleDateFormat("d", Locale.getDefault());
            holder.mDateTextView.setText(dateFormat.format(order.getCreatedAtLong()));

            holder.mStatusTextView.setText(status.name());


            holder.mStatusTextView.getBackground().setColorFilter(getColorFromStatus(status), PorterDuff.Mode.SRC_IN);


            fadeOnAck(status, holder.mStatusTextView);

                // on tag image
            setStatus(status, holder.mStatusImageView);

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    mListener.onOrderClick(mOrderList.get(position).getOrderPath());

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return mOrderList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        @BindView(R.id.text_view_order_id)
        public TextView mOrderIdTextView;

        @BindView(R.id.text_view_price)
        public TextView mPriceTextView;

        @BindView(R.id.text_view_date)
        public TextView mDateTextView;

        @BindView(R.id.text_view_month)
        public TextView mMonthTextView;

        @BindView(R.id.image_view_status)
        public ImageView mStatusImageView;

        @BindView(R.id.text_view_status)
        public TextView mStatusTextView;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            ButterKnife.bind(this, view);
            /*mIdView = (TextView) view.findViewById(R.id.id);
            mContentView = (TextView) view.findViewById(R.id.content);*/

        }

        @Override
        public String toString() {
            return super.toString() + " '" + mOrderIdTextView.getText() + "'";
        }
    }



    private void setStatus(Order.Status status, ImageView imageView) {
        clearAnimation(imageView);
        switch (status) {
            case CONFIRMED:
                imageView.setColorFilter(getColorFromStatus(status),
                        PorterDuff.Mode.SRC_IN);
/*                Animation pulse = AnimationUtils.loadAnimation(App.getContext(), R.anim.pulse);
                imageView.startAnimation(pulse);*/
                break;
            case ACKNOWLEDGED:
                imageView.setColorFilter(ContextCompat.getColor(App.getContext(), R.color.md_amber_500),
                        PorterDuff.Mode.SRC_IN);
                startHangingAnimation(imageView);
                break;
            case CANCELED:
                imageView.setColorFilter(ContextCompat.getColor(App.getContext(), R.color.md_red_500),
                        PorterDuff.Mode.SRC_IN);

                break;
            case COMPLETED:
                imageView.setColorFilter(ContextCompat.getColor(App.getContext(), R.color.md_grey_400),
                        PorterDuff.Mode.SRC_IN);
                break;
        }
    }

    @ColorInt
    private int getColorFromStatus(Order.Status status) {

        switch (status) {
            case CONFIRMED:
                return ContextCompat.getColor(App.getContext(), R.color.md_green_500);
            case ACKNOWLEDGED:
                return ContextCompat.getColor(App.getContext(), R.color.md_amber_500);
            case CANCELED:
                return ContextCompat.getColor(App.getContext(), R.color.md_red_500);
            case COMPLETED:
                return ContextCompat.getColor(App.getContext(), R.color.md_grey_400);
            case OPEN:
                return ContextCompat.getColor(App.getContext(), R.color.colorPrimary);

        }

        return ContextCompat.getColor(App.getContext(), R.color.colorPrimary);
    }

    public interface OnOrderClickListener {

        public void onOrderClick(String orderId);
    }

    private void clearAnimation(View View) {
        View.clearAnimation();
        if (View.getAnimation() != null) {
            View.getAnimation().cancel();
        }

    }
    private void startHangingAnimation(ImageView imageView) {
        // Clear animation before starting another
        clearAnimation(imageView);

        RotateAnimation rotate = new RotateAnimation(-10, 50, Animation.RELATIVE_TO_SELF, 0.2f, Animation.RELATIVE_TO_SELF, 0f);
        rotate.setRepeatCount(Animation.INFINITE);
        rotate.setRepeatMode(Animation.REVERSE);
        rotate.setDuration(1500);
        imageView.setAnimation(rotate);
        rotate.start();

/*        ObjectAnimator rotation = ObjectAnimator.ofFloat(imageView,
                "rotation", -10f, 50f);

        rotation.setRepeatMode(ValueAnimator.REVERSE);
        rotation.setRepeatCount(ValueAnimator.INFINITE);
        ObjectAnimator rotationX = ObjectAnimator.ofFloat(imageView,
                "rotationX", -20f, 5f);

        rotationX.setRepeatMode(ValueAnimator.REVERSE);
        rotationX.setRepeatCount(ValueAnimator.INFINITE);
        imageView.setPivotX(25f);
        imageView.setPivotY(0f);
*//*        Log.d(TAG, "startHangingAnimation: pivotX: " + imageView.getPivotX());
        Log.d(TAG, "startHangingAnimation: pivotY: " + imageView.getPivotY());*//*
        ObjectAnimator pivotX = ObjectAnimator.ofFloat(imageView, "pivotX", imageView.getPivotX());
        pivotX.setRepeatMode(ValueAnimator.REVERSE);
        pivotX.setRepeatCount(ValueAnimator.INFINITE);
        ObjectAnimator pivotY = ObjectAnimator.ofFloat(imageView, "pivotY", imageView.getPivotY());
        pivotY.setRepeatMode(ValueAnimator.REVERSE);
        pivotY.setRepeatCount(ValueAnimator.INFINITE);

        AnimatorSet animation = new AnimatorSet();
        animation.playTogether(rotation,rotationX, pivotX, pivotY);
        animation.setDuration(1500);
        animation.setInterpolator(new AccelerateDecelerateInterpolator());
        animation.start();*/
    }

    private void fadeOnAck(Order.Status status, View view) {

        clearAnimation(view);

        if (status == Order.Status.ACKNOWLEDGED) {

            AlphaAnimation alphaAnimation = new AlphaAnimation(0.3f, 0.9f);
            alphaAnimation.setDuration(700);
            alphaAnimation.setRepeatMode(Animation.REVERSE);
            alphaAnimation.setRepeatCount(Animation.INFINITE);
            view.setAnimation(alphaAnimation);
            alphaAnimation.start();



            // color animation
/*        ObjectAnimator slide = ObjectAnimator.ofObject(view,
                "backgroundColor",
                new ArgbEvaluator(),
                ContextCompat.getColor(App.getContext(), R.color.white),
                ContextCompat.getColor(App.getContext(), R.color.md_deep_orange_50));*/

            /*     ObjectAnimator slide = ObjectAnimator.ofFloat(view,
                    "alpha", 0.3f, 0.9f);
            slide.setInterpolator(new AccelerateDecelerateInterpolator());
            slide.setDuration(700);
            slide.setRepeatCount(ValueAnimator.INFINITE);
            slide.setRepeatMode(ValueAnimator.REVERSE);
            slide.start();*/
        }
    }

    public void cleanUp() {

        mOrdersQuery.removeEventListener(mOrdersListener);
    }


}
