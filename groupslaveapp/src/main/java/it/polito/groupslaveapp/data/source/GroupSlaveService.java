package it.polito.groupslaveapp.data.source;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import org.altbeacon.beacon.Beacon;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import it.polito.groupslaveapp.Injection;
import it.polito.groupslaveapp.MyNotificationManager;
import it.polito.groupslaveapp.data.Device;
import it.polito.groupslaveapp.data.Slave;
import it.polito.groupslaveapp.event.BeaconEvents;
import it.polito.groupslaveapp.event.GroupEvents;
import it.polito.groupslaveapp.event.MasterEvents;

public class GroupSlaveService extends Service {

    private static final String TAG = GroupSlaveService.class.getSimpleName();
    public static final String GROUP_ID_ARG = "it.polito.groupslaveapp.data.source.GROUP_ID_ARG";
    public static final String STOP_SERVICE_ARG = "it.polito.groupslaveapp.data.source.STOP_SERVICE_ARG";

    private final IBinder mBinder = new GroupBinder();
    private DataSource mDataSource;

    private boolean mStarted;
    private Map<Slave, List<Device>> mSlaveDevicesMap;
    private List<Device> mLostDevices;
    private List<Device> mDevices;
    private String mGroupId;
    private MyNotificationManager mNotificationManager;


    public GroupSlaveService() {
    }

    @Override
    public void onCreate() {
        mDataSource = Injection.provideGroupRepository(getApplicationContext());
        mSlaveDevicesMap = new HashMap<>();
        mLostDevices = new LinkedList<>();
        mDevices = new ArrayList<>();
        mNotificationManager = MyNotificationManager.getInstance(getApplicationContext());
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        MyNotificationManager.destroyInstance();
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        init(intent);
        return START_REDELIVER_INTENT;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        if (!mStarted) init(intent);
        return mBinder;
    }

    public Map<Slave, List<Device>> getGroupComposition() {
        return mSlaveDevicesMap;
    }

    public List<Device> getLostDevices() {
        return mLostDevices;
    }

    public List<Device> getVisibleDevices() {
        return mDevices;
    }

    public void stopListening() {
        Log.d(TAG, "stopListening: ");
        mNotificationManager.cancelNotification();
        stopScanDeviceService();
        stopSelf();
    }

    @Subscribe
    public void onVisibleBeaconsEvent(BeaconEvents.VisibleBeaconsEvent event) {

        mDevices.clear();
        for (Beacon beacon : event.getBeacons()) {
            Device device = new Device(beacon.getId2().toHexString(), beacon.getBluetoothAddress());
            device.setDistance(beacon.getDistance());
            mDevices.add(device);
        }
        mDataSource.sendVisibleDevices(mGroupId, mDevices);
    }

   /* @Subscribe
    public void onBeaconsLostEvent(BeaconEvents.BeaconsLostEvent event) {
        Log.d(TAG, "onBeaconsLostEvent: ");
        for (Beacon beacon : event.getBeacons()) {
            Device device = new Device(beacon.getId2().toHexString(), beacon.getBluetoothAddress());
            mDevices.remove(device);
        }
    }*/

    @Subscribe
    public void onGroupUpdateEvent(MasterEvents.GroupUpdateEvent event) {
        Log.d(TAG, "onGroupUpdateEvent: " + event.getSlave().getId() + " " + event.getDevices());
        mSlaveDevicesMap.put(event.getSlave(), event.getDevices());
        mNotificationManager.updateNotification(null, mSlaveDevicesMap, mLostDevices);
        EventBus.getDefault().post(new GroupEvents.GroupUpdatedEvent());
    }

    @Subscribe
    public void onLostDeviceEvent(MasterEvents.LostDeviceEvent event) {
        if (!mLostDevices.contains(event.getDevice())) {
            mLostDevices.add(event.getDevice());
            EventBus.getDefault().post(new GroupEvents.LostDeviceEvent(event.getDevice()));
        }
    }

    @Subscribe
    public void onDeviceFoundEvent(MasterEvents.DeviceFoundEvent event) {
        mLostDevices.remove(event.getDevice());
        EventBus.getDefault().post(new GroupEvents.DeviceFoundEvent(event.getDevice()));
    }

    @Subscribe
    public void onCloseGroupEvent(MasterEvents.CloseGroupEvent event) {
        if (event.getGroup().getId().equals(mGroupId)) {
            stopListening();
            EventBus.getDefault().post(new GroupEvents.CloseGroupEvent());
        }
    }

    private void init(Intent intent) {
        boolean close = intent.getBooleanExtra(STOP_SERVICE_ARG, false);
        if (close) {

        } else if (!mStarted) {
            mGroupId = intent.getStringExtra(GROUP_ID_ARG);
            startListening();
            startScanDeviceService();
            mStarted = true;
        }
    }

    private void startScanDeviceService() {
        Log.d(TAG, "startScanDeviceService: ");
        EddystoneService.startScanAction(this);
    }

    private void stopScanDeviceService() {
        Log.d(TAG, "stopScanDeviceService: ");
        EddystoneService.stopScanAction(this);
    }

    private void startListening() {
        Log.d(TAG, "startListening: ");
        EventBus.getDefault().register(this);
    }

    public class GroupBinder extends Binder {
        public GroupSlaveService getService() {
            // Return this instance of LocalService so clients can call public methods
            return GroupSlaveService.this;
        }
    }

}
