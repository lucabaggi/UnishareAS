package it.android.unishare;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

/**
 * Created by luca on 22/06/15.
 */
public class NotificationReceiver extends BroadcastReceiver {

    public static final int NOTIFICATION_ID = 1;
    private NotificationManager mNotificationManager;
    private PendingIntent pendingIntent;

    public NotificationReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(context);
        Bundle extras = intent.getExtras();

        String messageType = gcm.getMessageType(intent);

        if (!extras.isEmpty()) {

            if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {

                String notificationType = intent.getExtras().getString("type");
                if(notificationType != null && !notificationType.equals("")){
                    if(notificationType.equals("book")){
                        Intent myIntent = new Intent(context, MyBooksActivity.class);

                        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
                        // Adds the back stack
                        stackBuilder.addParentStack(MyBooksActivity.class);
                        // Adds the Intent to the top of the stack
                        stackBuilder.addNextIntent(myIntent);
                        // Gets a PendingIntent containing the entire back stack
                        pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
                    }
                    if(notificationType.equals("opinion")){
                        Intent myIntent = new Intent(context, MyCoursesActivity.class);

                        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
                        // Adds the back stack
                        stackBuilder.addParentStack(MyCoursesActivity.class);
                        // Adds the Intent to the top of the stack
                        stackBuilder.addNextIntent(myIntent);
                        // Gets a PendingIntent containing the entire back stack
                        pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
                    }
                }

                // retrieve del campo message della notifica
                String json_info = intent.getExtras().getString("message");
                Log.i("NotificationReceiver", "Info: " + json_info);

                // emette una notifica sul dispositivo
                sendNotification(context, json_info);

            }
        }
    }

    private void sendNotification(Context ctx, String msg) {
        mNotificationManager = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);

        // scelta suoneria per notifica
        Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(ctx)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentTitle("Unishare")
                        .setContentIntent(pendingIntent)
                        .setContentText(msg)
                        .setSound(sound)
                        .setAutoCancel(true);

        // effettua la notifica
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }
}
