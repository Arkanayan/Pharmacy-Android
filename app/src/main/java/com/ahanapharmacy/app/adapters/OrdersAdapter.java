package com.ahanapharmacy.app.adapters;

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

import com.ahanapharmacy.app.App;
import com.ahanapharmacy.app.R;
import com.ahanapharmacy.app.Utils.Constants;
import com.ahanapharmacy.app.models.Order;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

import static com.ahanapharmacy.app.models.Order.Status.ACKNOWLEDGED;
import static com.ahanapharmacy.app.models.Order.Status.CANCELED;
import static com.ahanapharmacy.app.models.Order.Status.COMPLETED;
import static com.ahanapharmacy.app.models.Order.Status.CONFIRMED;
import static com.ahanapharmacy.app.models.Order.Status.PENDING;

/**
 * Created by arka on 4/30/16.
 */
public class OrdersAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

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

//        Firebase ref = App.getFirebase();
//        String uid = App.getFirebase().getAuth().getUid();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference ordersRef = firebaseDatabase.getReference(Constants.Path.ORDERS);
        FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();

        mOrdersQuery = ordersRef.orderByChild("uid").equalTo(mUser.getUid());

        mOrdersListener =  mOrdersQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                try {

                    Log.d(TAG, "onChildAdded: key: " + dataSnapshot.getKey());
                    Order order = dataSnapshot.getValue(Order.class);
                    int prevSize = mOrderList.size();
                    mOrderList.add(0, order);

                    // for removing empty view
                    // if there was no data on the list, empty view is shown
                    // if data added then remove the empty view and add the new data at its position(0)
                    if (prevSize == 0 ) {
                        notifyItemRemoved(0);
                    }
                    notifyItemInserted(0);

                     mRecyclerView.smoothScrollToPosition(0);
                   //   ((LinearLayoutManager) mRecyclerView.getLayoutManager()).scrollToPositionWithOffset(0, 20);
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
                        Timber.d("Item changed at position: %d",position);
                        mOrderList.set(position, order);

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
                        int position = mOrderList.indexOf(order);
                        Timber.d("Child remoed key: %s", dataSnapshot.getKey());
                        Timber.d("onChildRemoved: position: %d", position);
                        // the order of the next two lines are important don't change it
                        // otherwise recyclerview will remove different child each time
                       // notifyItemRemoved(mOrderList.indexOf(order));
                        notifyItemRangeRemoved(position, 1);
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
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "onCancelled: Error: " + databaseError.getMessage() );
                Snackbar.make(mRecyclerView, "There is a problem retriving orders", Snackbar.LENGTH_SHORT);
            }
        });

    }

    @Override
    public int getItemViewType(int position) {
        if (mOrderList.size() == 0) {
            return R.layout.order_list_empty_view;
        } else {

            return R.layout.order_list_content;
        }


    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
       // Log.d(TAG, "onCreateViewHolder");
        View view = LayoutInflater.from(parent.getContext())
                .inflate(viewType, parent, false);

        if (viewType == R.layout.order_list_empty_view) {
            return new EmptyViewHolder(view);
        } else {
            return new ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        // if layout is not empty view then only populate views

        if (holder.getItemViewType() == R.layout.order_list_content) {
            try {
                ViewHolder viewHolder = (ViewHolder) holder;
                //Log.d(TAG, "onBindViewHolder: " + position);
                Order order = mOrderList.get(holder.getAdapterPosition());
                @Order.Status String status = order.getStatus();
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

                viewHolder.mOrderIdTextView.setText(orderId);
                viewHolder.mPriceTextView.setText(App.getContext().getResources().getString(R.string.price, price));
                // holder.mPriceTextView.setText(String.valueOf(holder.getAdapterPosition()));

                SimpleDateFormat monthFormat = new SimpleDateFormat("MMM", Locale.getDefault());
                viewHolder.mMonthTextView.setText(monthFormat.format(order.getCreatedAtLong()));

                SimpleDateFormat dateFormat = new SimpleDateFormat("d", Locale.getDefault());
                viewHolder.mDateTextView.setText(dateFormat.format(order.getCreatedAtLong()));

                viewHolder.mStatusTextView.setText(status);


                viewHolder.mStatusTextView.getBackground().setColorFilter(getColorFromStatus(status), PorterDuff.Mode.SRC_IN);
                // Timber.d("Status: %s , Color: %d", status.name(), getColorFromStatus(status));

                fadeOnAck(status, viewHolder.mStatusTextView);

                // on tag image
                setStatus(status, viewHolder.mStatusImageView);

                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (holder.getAdapterPosition() != RecyclerView.NO_POSITION) {
                            mListener.onOrderClick(mOrderList.get(holder.getAdapterPosition()).getOrderPath());

                        }

                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            // Empty view bind goes here
        }

    }

    @Override
    public int getItemCount() {
        return mOrderList.size() > 0 ? mOrderList.size() : 1;
     //  return mOrderList.size();
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

    public class EmptyViewHolder extends RecyclerView.ViewHolder {

        public EmptyViewHolder(View itemView) {
            super(itemView);
        }

        @Override
        public String toString() {
            return "size: " + mOrderList.size();
        }
    }

    private void setStatus(@Order.Status String status, ImageView imageView) {
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
            case PENDING:
                imageView.setColorFilter(ContextCompat.getColor(App.getContext(), R.color.md_cyan_500),
                        PorterDuff.Mode.SRC_IN);
                break;
        }
    }

    @ColorInt
    private int getColorFromStatus(@Order.Status String status) {

        switch (status) {
            case CONFIRMED:
                return ContextCompat.getColor(App.getContext(), R.color.md_green_500);
            case ACKNOWLEDGED:
                return ContextCompat.getColor(App.getContext(), R.color.md_amber_500);
            case CANCELED:
                return ContextCompat.getColor(App.getContext(), R.color.md_red_500);
            case COMPLETED:
                return ContextCompat.getColor(App.getContext(), R.color.md_grey_400);
            case PENDING:
                return ContextCompat.getColor(App.getContext(), R.color.md_cyan_500);

        }

        return ContextCompat.getColor(App.getContext(), R.color.md_cyan_500);
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

    }

    private void fadeOnAck(@Order.Status String status, View view) {

        clearAnimation(view);

        if (status.equals(ACKNOWLEDGED)) {

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
