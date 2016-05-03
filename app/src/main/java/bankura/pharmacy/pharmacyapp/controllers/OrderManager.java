package bankura.pharmacy.pharmacyapp.controllers;

import android.util.Log;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.UUID;

import bankura.pharmacy.pharmacyapp.App;
import bankura.pharmacy.pharmacyapp.Utils.Constants;
import bankura.pharmacy.pharmacyapp.models.Order;
import rx.Observable;

/**
 * Created by arka on 4/30/16.
 */
public class OrderManager {

    public static String createOrder(Order order) {
        Firebase ref = App.getFirebase();
        String uid = ref.getAuth().getUid();

        Firebase newOrderRef =  ref.child("orders").push();

        String newOrderKey = newOrderRef.getKey();

        String orderId;

        orderId = "OD" + UUID.randomUUID().toString().substring(0,8).toUpperCase();

        order.setOrderId(orderId);
        newOrderRef.setValue(order);
        newOrderRef.setPriority(0 - order.getCreatedAt());


        ref.child("order_stats").child("open").child(newOrderKey).setValue(true);
        UserManager.getUserRef().child("orders").child(newOrderKey).setValue(true);

        return newOrderKey;
    }

    public static void setCompleted(String key) {

        Firebase orderRef = App.getFirebase().child("orders").child(key);

        Firebase orderStatsRef = App.getFirebase().child("order_stats");

        orderStatsRef.child("open").child(key).removeValue();

        orderStatsRef.child("completed").child(key).setValue(true);

        orderRef.child("is_completed").setValue(true);

    }

    public static Observable<Order> fetchOrder(String orderId) {
        return Observable.create(subscriber -> {
            App.getFirebase().child(Constants.Path.ORDERS).orderByChild(Constants.Order.ORDER_ID)
                    .equalTo(orderId).limitToFirst(1).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Log.d("fetchOrder", "Key: " + dataSnapshot.getValue().toString());
                            try {
                                // get the first child of the list
                                Order order = dataSnapshot.getChildren().iterator().next().getValue(Order.class);
                                if (order != null) {
                                //    Log.d("fetchOrder", "Order uid: " + order.getUid());
                                    subscriber.onNext(order);
                                    subscriber.onCompleted();
                                } else {
                                    subscriber.onError(new Throwable("There is a problem retriving the order"));
                                }
                            } catch (Exception e) {
                                subscriber.onError(e);
                            }
                        }

                        @Override
                        public void onCancelled(FirebaseError firebaseError) {
                            subscriber.onError(firebaseError.toException());
                        }
                    });
        });
    }
}
