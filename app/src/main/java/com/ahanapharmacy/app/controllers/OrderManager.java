package com.ahanapharmacy.app.controllers;

import android.util.Log;

import com.ahanapharmacy.app.App;
import com.ahanapharmacy.app.R;
import com.ahanapharmacy.app.Utils.Constants;
import com.ahanapharmacy.app.Utils.Utils;
import com.ahanapharmacy.app.models.Order;
import com.cloudinary.Cloudinary;
import com.cloudinary.Transformation;
import com.cloudinary.utils.ObjectUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import rx.Observable;
import rx.schedulers.Schedulers;
import timber.log.Timber;

/**
 * Created by arka on 4/30/16.
 */
public class OrderManager {

    /**
     * Creates order
     * @param order
     * @return order id
     */
/*    public static String createOrder(Order order) {
        Firebase ref = App.getFirebase();
        String uid = ref.getAuth().getUid();

        Firebase newOrderRef =  ref.child("orders").push();

        String newOrderKey = newOrderRef.getKey();

        String orderId;
        order.setUid(uid);

        // saves the path, it can be retrived easily like /orders/<order_key>
        order.setOrderPath(newOrderKey);

        // set timestamp
        long timestamp = System.currentTimeMillis() / 1000L;
//        order.setCreatedAt(timestamp);

        newOrderRef.setValue(order);
        newOrderRef.setPriority(0 - timestamp);

        ref.child("order_stats").child("open").child(newOrderKey).setValue(true);
        UserManager.getUserRef().child("orders").child(newOrderKey).setValue(true);

        return order.getOrderId();
    }*/

    /**
     * Creates order reactively
     * @param order
     * @return order key
     */
    public static Observable<String> createOrder(Order order) {

        return Observable.create(subscriber -> {
            FirebaseDatabase ref = FirebaseDatabase.getInstance();
            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

            String uid = firebaseUser.getUid();

            DatabaseReference newOrderRef =  ref.getReference("orders").push();

            String newOrderKey = newOrderRef.getKey();

            order.setUid(uid);

            // saves the path, it can be retrived easily like /orders/<order_key>
            order.setOrderPath(newOrderKey);

            // set timestamp
             long timestamp = System.currentTimeMillis() / 1000L;


            newOrderRef.setValue(order, (databaseError, firebase) -> {
                if (databaseError != null) {
                    Timber.e(databaseError.toException(), "Order create failed, id: %s", order.getOrderId());
                    subscriber.onError(databaseError.toException());

                } else {
                    Timber.i("Order created, id: %s", order.getOrderId());
                    newOrderRef.setPriority(0 - timestamp);
                    subscriber.onNext(firebase.getKey());
                    subscriber.onCompleted();
                }
            });
  /*          UserManager.getUserRef().child("orders").child(newOrderKey).setValue(true, (firebaseError, firebase) -> {
                if (firebaseError != null) {
                    subscriber.onError(firebaseError.toException());

                } else {
                   // ref.child("order_stats").child("open").child(newOrderKey).setValue(true);
                    subscriber.onNext(firebase.getKey());
                    subscriber.onCompleted();
                }
            });*/
        });
    }


    public static void setCompleted(String key) {

        DatabaseReference orderRef = FirebaseDatabase.getInstance().getReference("orders").child(key);

        DatabaseReference orderStatsRef = FirebaseDatabase.getInstance().getReference("order_stats");

        orderStatsRef.child("open").child(key).removeValue();

        orderStatsRef.child("completed").child(key).setValue(true);

        orderRef.child("is_completed").setValue(true);

    }

    /**
     * Fetch order by order id
     * @param orderId
     * @return Order
     */
    public static Observable<Order> fetchOrder(String orderId) {
        return Observable.create(subscriber -> {
            FirebaseDatabase.getInstance().getReference(Constants.Path.ORDERS).orderByChild(Constants.Order.ORDER_ID)
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
                        public void onCancelled(DatabaseError databaseError) {
                            subscriber.onError(databaseError.toException());
                        }
                    });
        });
    }


    /**
     * Uploads image to cloudinary
     * @param file to upload
     * @return public_id of the uploaded image
     */
    public static Observable<String> uploadImage(File file, String orderId) {
        return Observable.create(subscriber -> {


            Cloudinary cloudinary = new Cloudinary(App.getContext().getResources().getString(R.string.cloudinary_url));
/*            String fileName = file.getName();
            int pos = fileName.lastIndexOf(".");
            if (pos > 0) {
                fileName = fileName.substring(0, pos);
            }*/
            String folderName = "ahanaPharmacy/";

            String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

            Map context = new HashMap();
            context.put("uid", uid);
            context.put("order_id", orderId);
            context.put("status", "PENDING");

            String tags = "prescription, " + uid + "," + orderId + "," + "PENDING";

            Map options = ObjectUtils.asMap(
              "tags", tags,
            "context", context,
            "folder", folderName,
                    // store image as reduced quality 60%
                    "transformation", new Transformation().quality(60)

            );

            try {
              Map map =  cloudinary.uploader().upload(file, options );
                String publicId = map.get("public_id").toString();
                Log.d("uploadImage", "public_id: " + publicId);
               subscriber.onNext(publicId);
                subscriber.onCompleted();

            } catch (IOException e) {
                Timber.e(e, "Image Upload failed");
                e.printStackTrace();
                subscriber.onError(new Throwable("Error uploading prescription. Make sure you're connected to internet"));
            }

        });
    }


    /**
     * Deletes image on the given id
     * @implNote Run it on other than MainThread
     * @param publicId
     * @return void
     */
    public static Observable<Void> deleteImage(String publicId) {
        return Observable.create(subscriber -> {
            try {
                Cloudinary cloudinary = Utils.getCloudinary();
                cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
                Timber.i("Image deleted, id: %s", publicId);
                subscriber.onCompleted();
            } catch (IOException e) {
                Timber.e(e, "Image delete error, id: %s", publicId);
                e.printStackTrace();
                subscriber.onError(e);
            }
        });
    }

    /**
     * Fetch order by order address key
     * @param key
     * @return Order
     */
    public static Observable<Order> fetchOrderByKey(String key) {
        return Observable.create(subscriber -> {
            FirebaseDatabase.getInstance().getReference(Constants.Path.ORDERS).child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    try {
                        Order order = dataSnapshot.getValue(Order.class);
                        subscriber.onNext(order);
                        subscriber.onCompleted();
                    } catch (Exception e) {
                        Timber.e(e, "Order retrival by key failed, key: %s", key);
                        subscriber.onError(e);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Timber.e(databaseError.toException(), "Order retrival by key failed, key: %s", key);
                    subscriber.onError(databaseError.toException());
                }
            });

        });
    }



    /**
     * Deletes order with Image
     *
     * @return void
     */
    public static Observable<Void> deleteOrder(Order order) {


        Observable<Void> isConnectedToInternet = Utils.isNetworkAvailable();


        // deltes order image

        // Deletes order without image
        Observable<Void> deleteOrderObservable = Observable.create(subscriber -> {

            FirebaseDatabase.getInstance().getReference(Constants.Path.ORDERS).child(order.getOrderPath()).removeValue((databaseError, firebase) -> {
                if (databaseError != null) {
                    // on order delete failed
                    Timber.e(databaseError.toException(), "Order delete failed on id ", order.getOrderId());
                    subscriber.onError(databaseError.toException());
                } else {
                    Timber.i("Order deleted, key: %s", order.getOrderPath());
                    subscriber.onCompleted();
                }

            });

        });

        // If there is no prescription provided, then delete only the order from database
        if (order.getPrescriptionUrl().isEmpty()) {

            return isConnectedToInternet.concatWith(deleteOrderObservable);
        }

        Observable<Void> deleteImageObservable = deleteImage(order.getPrescriptionUrl())
                .subscribeOn(Schedulers.io());

       /* return Observable.zip(
                deleteImageObservable,
                deleteOrderObservable, (aVoid, aVoid2) -> {
                    return aVoid;
                }
        );*/

//        return deleteImageObservable.concatWith(deleteOrderObservable);
        return Observable.concat(isConnectedToInternet, deleteImageObservable, deleteOrderObservable);

      //  return deleteImageObservable.zipWith(deleteOrderObservable, (aVoid, aVoid2) -> aVoid);
//        return deleteImageObservable.mergeWith(deleteOrderObservable);
    }

    /**
     * Deletes Order by order key
     *
     * @return void
     */
    public static Observable<Void> deleteOrderByKey(String key) {

        return fetchOrderByKey(key).concatMap(order -> {
           return deleteImage(order.getPrescriptionUrl()).subscribeOn(Schedulers.io());
        }).concatMap(aVoid -> {
            return Observable.create(subscriber -> {

                     FirebaseDatabase.getInstance().getReference(Constants.Path.ORDERS).child(key).removeValue((databaseError, firebase) -> {
                         if (databaseError != null) {
                             // on order delete failed
                             Timber.e(databaseError.toException(), "Order delete failed on key %s", key);
                             subscriber.onError(databaseError.toException());
                         } else {
                             Timber.i("Order deleted, key: %s", key);
                             subscriber.onCompleted();
                         }
                     });
                 }
            );
        });
    }

    /**
     * Confirms the order
     * @return void
     */

    public static Observable<Void> setOrderStatus(Order order, @Order.Status String status) {

        return Observable.create(subscriber -> {
           FirebaseDatabase.getInstance().getReference(Constants.Path.ORDERS).child(order.getOrderPath())
                   .child(Constants.Order.STATUS).setValue(status, (databaseError, firebase) -> {

               if (databaseError != null) {
                   Timber.e(databaseError.toException(), "Order status changed to %s failed", status);
                   subscriber.onError(databaseError.toException());
               } else {
                   subscriber.onCompleted();

               }
           });
        });
    }
}
