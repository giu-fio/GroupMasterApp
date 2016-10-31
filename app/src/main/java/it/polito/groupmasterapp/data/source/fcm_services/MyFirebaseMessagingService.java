package it.polito.groupmasterapp.data.source.fcm_services;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import it.polito.groupmasterapp.SlaveEvents;
import it.polito.groupmasterapp.data.Device;
import it.polito.groupmasterapp.data.Slave;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMessagingServ";

    public MyFirebaseMessagingService() {
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        final Map<String, String> dataMap = new HashMap<>(remoteMessage.getData());
        String type = dataMap.remove("type");

        Log.d(TAG, "onMessageReceived() called with: dataMap = [" + dataMap + "]");

        if (type.equals("update")) {

            long timestamp = Long.parseLong(dataMap.remove("timestamp"));
            String id = dataMap.remove("slave_id");
            List<Device> devices = new LinkedList<>();
            for (String uid : dataMap.keySet()) {
                Device d = new Device(uid);
                d.setDistance(Double.parseDouble(dataMap.get(uid)));
                devices.add(d);
            }
            SlaveEvents.SlaveMessageEvent event = new SlaveEvents.SlaveMessageEvent(new Slave(id), devices, timestamp);
            EventBus.getDefault().post(event);
        }
    }

    @Override
    public void onDeletedMessages() {
        super.onDeletedMessages();
        Log.d(TAG, "onDeletedMessages");
    }
}
