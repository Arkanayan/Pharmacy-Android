package bankura.pharmacy.pharmacyapp.controllers;

import android.util.Log;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.Map;

import bankura.pharmacy.pharmacyapp.App;
import bankura.pharmacy.pharmacyapp.models.Address;
import bankura.pharmacy.pharmacyapp.models.User;
import rx.Observable;

/**
 * Created by arka on 4/29/16.
 */
public class UserManager {


    // returns observable<user> from userid
    public static Observable<User> getUserFromId(String id) {

        return Observable.create(subscriber -> {
            App.getFirebase().child("users").child(id).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    User user = dataSnapshot.getValue(User.class);
                    subscriber.onNext(user);
                    subscriber.onCompleted();
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {

                    subscriber.onError(new Throwable(firebaseError.getMessage()));
                }


            });
        });

    }

    public static Observable<Address> getAddressFromId(String id) {

        return Observable.create(subscriber -> {
            App.getFirebase().child("addresses").child(id).limitToFirst(1).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // getting the first child from the addresses
                    for (DataSnapshot snapshot :
                            dataSnapshot.getChildren()) {

                        Address address = snapshot.getValue(Address.class);
                        subscriber.onNext(address);
                        subscriber.onCompleted();
                        // break free after one iteration
                        break;
                    }
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {
                    subscriber.onError(new Throwable(firebaseError.getMessage()));
                }
            });
        });
    }

    public static Observable<Void> updateAddress(Address address) {

        return Observable.create(subscriber -> {
            String TAG = App.getContext().getClass().getSimpleName();

            String uid = App.getFirebase().getAuth().getUid();

            Firebase addressRef = App.getFirebase().child("addresses").child(uid);

            addressRef.limitToFirst(1).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // Getting the dataSnapshot and key of the first address
                    // because we are only storing one address now
                    DataSnapshot addressSnapshot = dataSnapshot.getChildren().iterator().next();

                    String addressKey = addressSnapshot.getKey();

                    Log.d(TAG, "Address key: " + addressKey);

                    // Overwrite address by new address
                    addressRef.child(addressKey).setValue(address);
                    subscriber.onCompleted();

                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {
                    subscriber.onError(firebaseError.toException());
                }
            });
        });

    }

    public static void updateUser(Map<String, Object> userMap) {

        String uid = App.getFirebase().getAuth().getUid();

        Firebase userRef = App.getFirebase().child("users").child(uid);

        userRef.updateChildren(userMap);

    }

    public static Firebase getUserRef() {
        String uid = App.getFirebase().getAuth().getUid();
        return App.getFirebase().child("users").child(uid);
    }



}
