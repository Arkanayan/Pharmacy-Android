package bankura.pharmacy.pharmacyapp.controllers;

import com.firebase.client.Firebase;

import bankura.pharmacy.pharmacyapp.App;
import bankura.pharmacy.pharmacyapp.models.Order;

/**
 * Created by arka on 4/30/16.
 */
public class OrderManager {

    public static String createOrder(Order order) {
        Firebase ref = App.getFirebase();

        Firebase newOrderRef =  ref.child("orders").push();
        newOrderRef.setValue(order);
        String newOrderKey = newOrderRef.getKey();

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
}
