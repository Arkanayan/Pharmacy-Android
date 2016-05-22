package com.ahanapharmacy.app.messaging;

import com.ahanapharmacy.app.Utils.Constants;
import com.ahanapharmacy.app.Utils.Prefs;
import com.ahanapharmacy.app.controllers.UserManager;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;
import java.util.Map;

import timber.log.Timber;

public class MyInstanceIdService extends FirebaseInstanceIdService {
    public MyInstanceIdService() {
    }

    @Override
    public void onTokenRefresh() {

        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Timber.d("FCM token: %s", refreshedToken);

        // subscribe to global topic
        FirebaseMessaging.getInstance().subscribeToTopic("global");

        // send gcm token to database
        Prefs.getInstance(this).put(Prefs.Key.FCM_REG_ID, refreshedToken);


        if (UserManager.getUserRef() != null) {

            Map<String, Object> gcmTokenMap = new HashMap<>();

            gcmTokenMap.put(Constants.User.FCM_REG_ID, refreshedToken);

            UserManager.updateUser(gcmTokenMap)
                    .subscribe(aVoid -> {

                            }, Throwable::printStackTrace,
                            () -> {
                                Timber.d("Token sent to server successfully.");
                            });
        }

    }
}
