package edu.nctu.wirelab.sensinggo.BroadcastReceiver;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class CancelNotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(final Context context, final Intent intent) {
        final int notifyID = intent.getIntExtra("cancel_notify_id", 0);
        final NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE); // Get the notification service of the system
        notificationManager.cancel(notifyID);
    }
}
