package it.polito.groupslaveapp.data.source.fcm_services;

import android.util.Log;

import com.google.common.base.Preconditions;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import it.polito.groupslaveapp.data.Device;
import it.polito.groupslaveapp.data.Group;
import it.polito.groupslaveapp.data.Slave;
import it.polito.groupslaveapp.event.MasterEvents;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMessagingServ";

    public MyFirebaseMessagingService() {
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        final Map<String, String> dataMap = new HashMap<>(remoteMessage.getData());
        Log.d(TAG, "onMessageReceived: " + dataMap);

        String type = Preconditions.checkNotNull(dataMap.remove("type"));
        long timestamp;
        String deviceUid, slaveId;
        double distance;
        switch (type) {
            case "group_update":
                Log.d(TAG, "group_update: ");
                timestamp = Long.parseLong(dataMap.remove("timestamp"));
                String group = dataMap.remove("group");
                Slave slave = new Slave(dataMap.remove("slave"));
                List<Device> devices = new LinkedList<>();
                for (String devUid : dataMap.keySet()) {
                    Device device = new Device(devUid);
                    device.setDistance(Double.parseDouble(dataMap.get(devUid)));
                    devices.add(device);
                }
                EventBus.getDefault().post(new MasterEvents.GroupUpdateEvent(slave, devices, timestamp));
                break;
            case "lost_device":
                Log.d(TAG, "lost_device: ");
                timestamp = Long.parseLong(dataMap.remove("last_timestamp"));
                deviceUid = dataMap.remove("device");
                slaveId = dataMap.remove("last_slave");
                distance = Double.parseDouble(dataMap.remove("last_distance"));
                Device device = new Device(deviceUid);
                device.setDistance(distance);
                EventBus.getDefault().post(new MasterEvents.LostDeviceEvent(device, new Slave(slaveId), timestamp));

                break;
            case "device_found":
                Log.d(TAG, "device_found: ");
                timestamp = Long.parseLong(dataMap.remove("timestamp"));
                deviceUid = dataMap.remove("device");
                slaveId = dataMap.remove("slave");
                EventBus.getDefault().post(new MasterEvents.DeviceFoundEvent(new Device(deviceUid), new Slave(slaveId), timestamp));

                break;
            case "lost_slave":
                Log.d(TAG, "lost_slave: ");
                // TODO: 25/10/16  
                break;
            case "slave_found":
                Log.d(TAG, "slave_found: ");
                // TODO: 25/10/16
                break;
            case "close_group":
                Log.d(TAG, "close_group: ");
                EventBus.getDefault().post(new MasterEvents.CloseGroupEvent(new Group(null, dataMap.get("group"))));
                break;
            default:
                Log.d(TAG, "default: ");
                break;
        }
    }

    @Override
    public void onDeletedMessages() {
        super.onDeletedMessages();
        Log.d(TAG, "onDeletedMessages");
    }
}