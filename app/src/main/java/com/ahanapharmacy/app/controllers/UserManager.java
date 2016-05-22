package com.ahanapharmacy.app.controllers;

import android.util.Log;

import com.ahanapharmacy.app.App;
import com.ahanapharmacy.app.Utils.Constants;
import com.ahanapharmacy.app.models.Address;
import com.ahanapharmacy.app.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

import rx.Observable;
import timber.log.Timber;

/**
 * Created by arka on 4/29/16.
 */
public class UserManager {


    // returns observable<user> from userid
    public static Observable<User> getUserFromId(String id) {

        return Observable.create(subscriber -> {
            FirebaseDatabase.getInstance().getReference("users").child(id).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    User user = dataSnapshot.getValue(User.class);
                    subscriber.onNext(user);
                    subscriber.onCompleted();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                    subscriber.onError(new Throwable(databaseError.getMessage()));
                }


            });
        });

    }

    public static Observable<User> getUser() {

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        return getUserFromId(uid);
    }

    public static Observable<Address> getAddressFromId(String id) {

        return Observable.create(subscriber -> {
            FirebaseDatabase.getInstance().getReference("addresses").child(id).limitToFirst(1).addListenerForSingleValueEvent(new ValueEventListener() {
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
                public void onCancelled(DatabaseError databaseError) {
                    subscriber.onError(new Throwable(databaseError.getMessage()));
                }
            });
        });
    }

    public static Observable<Void> updateAddress(Address address) {

        return Observable.create(subscriber -> {
            String TAG = App.getContext().getClass().getSimpleName();

            String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

            DatabaseReference addressRef = FirebaseDatabase.getInstance().getReference("addresses").child(uid);

            addressRef.limitToFirst(1).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // Getting the dataSnapshot and key of the first address
                    // because we are only storing one address now
                    DataSnapshot addressSnapshot = dataSnapshot.getChildren().iterator().next();

                    String addressKey = addressSnapshot.getKey();

                    Log.d(TAG, "Address key: " + addressKey);

                    // Overwrite address by new address
                    addressRef.child(addressKey).setValue(address, (databaseError, firebase) -> {
                        if (databaseError != null) {
                            Timber.e(databaseError.toException(), "Address not updated");
                            subscriber.onError(new Throwable("Sorry, Address couldn't be updated"));
                        } else {

                            subscriber.onCompleted();
                        }
                    });

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Timber.e(databaseError.toException(), "Address not updated");
                    subscriber.onError(new Throwable("Sorry, Address couldn't be updated"));
                }
            });
        });

    }

    public static Observable<String> getAddressKey() {
        return Observable.create(subscriber -> {
            String TAG = App.getContext().getClass().getSimpleName();

            String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

            DatabaseReference addressRef = FirebaseDatabase.getInstance().getReference(Constants.Path.ADDRESSES).child(uid);

            addressRef.limitToFirst(1).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // Getting the dataSnapshot and key of the first address
                    // because we are only storing one address now
                    DataSnapshot addressSnapshot = dataSnapshot.getChildren().iterator().next();

                    String addressKey = addressSnapshot.getKey();

                    Log.d(TAG, "Address key: " + addressKey);

                    subscriber.onNext(addressKey);
                    subscriber.onCompleted();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    subscriber.onError(databaseError.toException());
                }
            });

        });
    }

/*    public static void updateUser(Map<String, Object> userMap) {

        String uid = App.getFirebase().getAuth().getUid();

        Firebase userRef = App.getFirebase().child("users").child(uid);

        userRef.updateChildren(userMap);

    }*/

    /**
     * Update user reactively
     *
     * @return void
     * @throws  Throwable()
     * @erorMessage "User couldn't be upddated"
     */
    public static Observable<Void> updateUser(Map<String, Object> userMap) {

        String uid = getUserRef().getUid();

        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference(Constants.Path.USERS).child(uid);
        return Observable.create(subscriber -> {
            userRef.updateChildren(userMap, (firebaseError, firebase) -> {
                if (firebaseError != null) {
                    subscriber.onError(new Throwable("Sorry, User couldn't be upddated"));
                } else {
                  subscriber.onCompleted();
                }
            });
        });
    }

    public static FirebaseUser getUserRef() {
        return FirebaseAuth.getInstance().getCurrentUser();
    }



}
