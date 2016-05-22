package com.ahanapharmacy.app.messaging;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import timber.log.Timber;

public class MyMessagingService extends FirebaseMessagingService {
    public MyMessagingService() {
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        Timber.d("From %s", remoteMessage.getFrom());
        Timber.d("Notification Message Body: %s", remoteMessage.getNotification().getBody());

        // TODO: 22/5/16 Show notification on Order update
    }
}
