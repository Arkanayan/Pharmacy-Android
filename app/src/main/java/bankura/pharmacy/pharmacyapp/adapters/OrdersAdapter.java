package bankura.pharmacy.pharmacyapp.adapters;

import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.util.ArrayList;
import java.util.List;

import bankura.pharmacy.pharmacyapp.App;
import bankura.pharmacy.pharmacyapp.R;
import bankura.pharmacy.pharmacyapp.models.Order;

/**
 * Created by arka on 4/30/16.
 */
public class OrdersAdapter extends RecyclerView.Adapter<OrdersAdapter.ViewHolder> {

    public final String TAG = this.getClass().getSimpleName();

    private List<Order> mOrderList;
    private RecyclerView mRecyclerView;
    private OnOrderClickListener mListener;

    public OrdersAdapter(RecyclerView recyclerView, OnOrderClickListener listener) {
        mOrderList = new ArrayList<Order>();
        mRecyclerView = recyclerView;
        mListener = listener;

        Firebase ref = App.getFirebase();
        String uid = App.getFirebase().getAuth().getUid();

        ref.child("orders").orderByChild("uid").equalTo(uid).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Log.d(TAG, "onChildAdded: key: " + dataSnapshot.getKey());
                Order order = dataSnapshot.getValue(Order.class);
                mOrderList.add(0, order);
                notifyItemInserted(mOrderList.indexOf(order));
              // mRecyclerView.smoothScrollToPosition(0);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
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

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Order order = dataSnapshot.getValue(Order.class);
                if (order != null) {
                    Log.d(TAG, "onChildRemoved: ID: " + order.getOrderId());
                    Log.d(TAG, "onChildRemoved: position: " + mOrderList.indexOf(order));
                    // the order of the next two lines are important don't change it
                    // otherwise recyclerview will remove different child each time
                    notifyItemRemoved(mOrderList.indexOf(order));
                    mOrderList.remove(order);
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
        //Log.d(TAG, "onBindViewHolder: " + position);
        String status = mOrderList.get(position).getStatus().name();
        String content = mOrderList.get(position).getOrderId() + " " + status;
     //    holder.mContentView.setText(String.valueOf(mOrderList.get(position).getPrice()));
        //test
        holder.mContentView.setText(content);
        holder.mIdView.setText(String.valueOf(mOrderList.get(position).getShippingCharge()));

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mListener.onOrderClick(mOrderList.get(position).getOrderId());

            }
        });

    }

    @Override
    public int getItemCount() {
        return mOrderList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mIdView;
        public final TextView mContentView;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = (TextView) view.findViewById(R.id.id);
            mContentView = (TextView) view.findViewById(R.id.content);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }

    public interface OnOrderClickListener {

        public void onOrderClick(String orderId);
    }
}
