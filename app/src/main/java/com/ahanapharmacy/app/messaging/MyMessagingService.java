package com.ahanapharmacy.app.messaging;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;

import com.ahanapharmacy.app.R;
import com.ahanapharmacy.app.activities.OrderDetailActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

import timber.log.Timber;

public class MyMessagingService extends FirebaseMessagingService {

    public static final int ORDER_UPDATE_NOTIFICATION_ID = 435345;

    public MyMessagingService() {
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        Timber.d("From %s", remoteMessage.getFrom());
        Timber.d("Notification Message Body: %s", remoteMessage.getNotification().getBody());

        // TODO: 22/5/16 Show notification on Order update

        Map<String, String> data = remoteMessage.getData();
        RemoteMessage.Notification notification = remoteMessage.getNotification();

        if (data.get("type").equals("ORDER_UPDATE")) {
            createOrderUpdateNotification(notification, data);
        }


    }

    private void createOrderUpdateNotification(RemoteMessage.Notification notification, Map<String, String> data) {

        Context context = getBaseContext();
        String orderId = data.get("order_id");
        String type = data.get("type");

        Intent orderDetailIntent = OrderDetailActivity.getInstanceByOrderId(context, orderId);
        int requestID = (int) System.currentTimeMillis(); //unique requestID to differentiate between various notification with same NotifId
        int flags = PendingIntent.FLAG_CANCEL_CURRENT; // cancel old intent and create new one
        PendingIntent pIntent = PendingIntent.getActivity(this, requestID, orderDetailIntent, flags);

        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(notification.getTitle())
                .setContentText(notification.getBody())
                .setSound(alarmSound)
                .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                .setContentIntent(pIntent);

        NotificationManager mNotificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(orderId,ORDER_UPDATE_NOTIFICATION_ID, mBuilder.build());
    }
}
