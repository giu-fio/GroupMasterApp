package it.polito.groupslaveapp;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import com.google.common.base.Preconditions;

import java.util.List;
import java.util.Map;

import it.polito.groupslaveapp.data.Device;
import it.polito.groupslaveapp.data.Group;
import it.polito.groupslaveapp.data.Slave;
import it.polito.groupslaveapp.data.source.GroupSlaveService;
import it.polito.groupslaveapp.group_action.GroupActionSlaveActivity;


/**
 * Created by giuseppe on 28/10/16.
 */

public class MyNotificationManager {
    private Context mContext;

    private static MyNotificationManager INSTANCE = null;
    private static final int NOTIFICATION_ID = 765;

    private MyNotificationManager(Context context) {
        this.mContext = context;
    }

    public static MyNotificationManager getInstance(Context context) {
        Preconditions.checkNotNull(context);
        if (INSTANCE == null) INSTANCE = new MyNotificationManager(context);
        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }

    public void updateNotification(Group group, Map<Slave, List<Device>> groupComposition, List<Device> lostDevices) {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(mContext)
                        .setSmallIcon(R.drawable.ic_info_outline_green_500_24dp)
                        .setContentTitle("Gruppo attivo")
                        .setContentText(String.format("%d dispositivi smarriti", lostDevices.size()))
                        .setAutoCancel(false)
                        .setOngoing(true);

        if (lostDevices.isEmpty()) {
            mBuilder.setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.ic_info_outline_green_500_24dp));
        } else {
            mBuilder.setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.ic_info_outline_red_500_24dp));
            mBuilder.setVibrate(new long[]{1000, 0, 0, 0, 0});
            mBuilder.setLights(Color.RED, 3000, 3000);
        }

        //Set the close service action
        Intent stopIntent = new Intent(mContext, GroupSlaveService.class);
        stopIntent.putExtra(GroupSlaveService.STOP_SERVICE_ARG, true);
        PendingIntent stopPendingIntent = PendingIntent.getService(mContext, 0, stopIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.addAction(R.drawable.ic_close_black_24dp, "CHIUDI", stopPendingIntent);


        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
        inboxStyle.setBigContentTitle("Composizione del gruppo:");
        for (Slave slave : groupComposition.keySet()) {
            inboxStyle.addLine(String.format("%s\t vede %d dispositivi", slave.getName(), groupComposition.get(slave).size()));
        }
        mBuilder.setStyle(inboxStyle);


        Intent resultIntent = new Intent(mContext, GroupActionSlaveActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(mContext);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }

    public void cancelNotification() {
        NotificationManager mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancel(NOTIFICATION_ID);
    }
}