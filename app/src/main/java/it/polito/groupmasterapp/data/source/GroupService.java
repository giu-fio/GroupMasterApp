package it.polito.groupmasterapp.data.source;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.google.common.base.Preconditions;

import org.altbeacon.beacon.Beacon;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import it.polito.groupmasterapp.BeaconEvents;
import it.polito.groupmasterapp.Configuration;
import it.polito.groupmasterapp.GroupEvents;
import it.polito.groupmasterapp.Injection;
import it.polito.groupmasterapp.MyNotificationManager;
import it.polito.groupmasterapp.SlaveEvents;
import it.polito.groupmasterapp.add_members.EddystoneService;
import it.polito.groupmasterapp.data.Device;
import it.polito.groupmasterapp.data.Group;
import it.polito.groupmasterapp.data.Slave;
import it.polito.groupmasterapp.util.OnCompleteListener;

public class GroupService extends Service {

    public static final String GROUP_ID_ARG = "it.polito.groupmasterapp.GROUP_ID_ARG";
    public static final String STOP_SERVICE_ARG = "it.polito.groupmasterapp.STOP_SERVICE_ARG";

    private static final String TAG = GroupService.class.getSimpleName();
    private static final long UPDATE_PERIOD = Configuration.UPDATE_GROUP_PERIOD;

    private final IBinder mBinder = new GroupBinder();
    private DevicesDataSource mDataSource;

    private boolean mStarted;
    private Group mGroup;

    private Map<Slave, List<Device>> mSlaveDevicesMap;
    private List<Device> mMasterDevices;
    private List<Device> mLostDevices;
    private List<Device> mFoundDevices;

    private Map<Device, Slave> mLastDeviceSlaveMap;
    private double[][] mMatrix;


    private Handler mHandler = new Handler();

    private Runnable mPeriodicTask = new Runnable() {
        @Override
        public void run() {
            updateMatrix(mGroup.getMaster(), mMasterDevices);
            updateGroup();
            sendUpdate();
            mHandler.postDelayed(this, UPDATE_PERIOD);
        }
    };

    public GroupService() {
    }

    @Override
    public void onCreate() {
        mSlaveDevicesMap = new HashMap<>();
        mLostDevices = new LinkedList<>();
        mFoundDevices = new LinkedList<>();
        mMasterDevices = new LinkedList<>();
        mLastDeviceSlaveMap = new HashMap<>();
        mDataSource = Injection.provideGroupRepository(getApplicationContext());
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        mHandler.removeCallbacks(mPeriodicTask);
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        init(intent);
        return START_REDELIVER_INTENT;
    }

    private void init(Intent intent) {

        boolean stop = intent.getBooleanExtra(STOP_SERVICE_ARG, false);
        if (stop) {
            stopListening();
            return;
        }

        if (!mStarted) {
            String groupId = intent.getStringExtra(GROUP_ID_ARG);
            mDataSource.loadGroup(groupId, new OnCompleteListener<Group>() {
                @Override
                public void onComplete(Group result, boolean success) {
                    if (success) {
                        mGroup = result;
                        mMatrix = new double[mGroup.getSlaves().size()][mGroup.getDevices().size()];
                        for (int i = 0; i < mMatrix.length; i++) {
                            Arrays.fill(mMatrix[i], -1);
                        }

                        for (Slave slave : result.getSlaves()) {
                            mSlaveDevicesMap.put(slave, new LinkedList<Device>());
                        }

                        for (Device device : mGroup.getDevices()) {
                            mLastDeviceSlaveMap.put(device, result.getMaster());
                        }

                        startScanDeviceService();
                        startListening();
                    } else ;                // TODO: 23/10/16


                }
            });
            mStarted = true;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        init(intent);
        return mBinder;
    }

    @Subscribe
    public void onVisibleBeaconsEvent(BeaconEvents.VisibleBeaconsEvent event) {
        // Log.d(TAG, "Beacon visible " + event.getBeacons());
        mMasterDevices.clear();
        for (Beacon beacon : event.getBeacons()) {
            Device device = new Device(beacon.getId2().toHexString(), beacon.getBluetoothAddress());
            device.setDistance(beacon.getDistance());
            mMasterDevices.add(device);
        }
    }

    @Subscribe
    public void onSlaveMessageEvent(SlaveEvents.SlaveMessageEvent event) {
        Log.d(TAG, "onSlaveMessageEvent: i");
        Log.d(TAG, String.format("Slave %s devices %d ", event.getSlave().getId(), event.getDevices().size()));
        updateMatrix(event.getSlave(), event.getDevices());
    }

    public Map<Slave, List<Device>> getGroupComposition() {
        return mSlaveDevicesMap;
    }

    public List<Device> getLostDevices() {
        return mLostDevices;
    }

    public void stopListening() {
        Log.d(TAG, "stopListening: ");
        if (mGroup != null) mDataSource.closeGroup(mGroup);
        MyNotificationManager.getInstance(getApplicationContext()).cancelNotification();
        stopScanDeviceService();
        EventBus.getDefault().post(new GroupEvents.StopGroupEvent());
        stopSelf();
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
        mHandler.postDelayed(mPeriodicTask, UPDATE_PERIOD);
    }

    private void sendUpdate() {
        Log.d(TAG, "sendUpdate: " + mSlaveDevicesMap);
        mDataSource.updateGroup(mGroup, mSlaveDevicesMap, new OnCompleteListener<Group>() {
            @Override
            public void onComplete(Group result, boolean success) {

            }
        });

        for (final Device device : mLostDevices) {
            Slave slave = mLastDeviceSlaveMap.get(device);
            mDataSource.updateLostDevice(mGroup, slave, device, new OnCompleteListener<Device>() {
                @Override
                public void onComplete(Device result, boolean success) {
                }
            });
        }

        for (final Device device : mFoundDevices) {
            Slave slave = mLastDeviceSlaveMap.get(device);
            mDataSource.updateFoundDevices(mGroup, slave, device, new OnCompleteListener<Device>() {
                @Override
                public void onComplete(Device result, boolean success) {
                }
            });
        }




    }

    private void updateMatrix(Slave slave, List<Device> devices) {

        int slaveIndex = mGroup.getSlaves().indexOf(slave);
        Arrays.fill(mMatrix[slaveIndex], -1.0);
        for (Device device : devices) {
            int deviceIndex = mGroup.getDevices().indexOf(device);
            if (deviceIndex >= 0 && deviceIndex < mMatrix[slaveIndex].length) {
                mMatrix[slaveIndex][deviceIndex] = device.getDistance();
            }
        }
        Log.d(TAG, "updateMatrix: \n" + slave.getId() + "\n" + devices + "\n" + matrixToString(mMatrix));
    }

    private void updateGroup() {
        Log.d(TAG, "Group updating... ");
        for (Slave slave : mSlaveDevicesMap.keySet()) {
            mSlaveDevicesMap.get(slave).clear();
        }
        mFoundDevices.clear();
        mFoundDevices.addAll(mLostDevices);
        mLostDevices.clear();

        for (int j = 0; j < mMatrix[0].length; j++) {
            Device d = mGroup.getDevices().get(j);
            double min = Double.MAX_VALUE;
            int minIndex = -1;
            for (int i = 0; i < mMatrix.length; i++) {
                double distance = mMatrix[i][j];
                if (distance >= 0.0 && distance < min) {
                    min = distance;
                    minIndex = i;
                }
            }
            Device device = new Device(d);
            device.setDistance(min);

            if (minIndex >= 0) {
                Slave slave = mGroup.getSlaves().get(minIndex);
                mSlaveDevicesMap.get(slave).add(device);
                mLastDeviceSlaveMap.put(device, slave);
            } else {
                mLostDevices.add(device);
            }
            mFoundDevices.removeAll(mLostDevices);

            MyNotificationManager.getInstance(getApplicationContext()).updateNotification(mGroup, mSlaveDevicesMap, mLostDevices);
        }

        Log.d(TAG, "Group composition\n" + groupToString(mSlaveDevicesMap));
        Log.d(TAG, "Lost devices\n" + mLostDevices);

        EventBus.getDefault().post(new GroupEvents.GroupUpdatedEvent());
    }

    private String groupToString(Map<Slave, List<Device>> map) {
        StringBuilder sb = new StringBuilder();
        for (Slave slave : map.keySet()) {
            sb.append("Slave ").append(slave.getId()).append("\n");
            for (Device device : map.get(slave)) {
                sb.append("\tDevice ").append(device.getUid()).append(" ").append(device.getDistance()).append("\n");
            }
        }
        return sb.toString();

    }

    private String matrixToString(double[][] matrix) {
        Preconditions.checkNotNull(matrix);
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < matrix.length; i++) {
            stringBuilder.append('[').append('\t');
            for (int j = 0; j < matrix[0].length; j++) {
                stringBuilder.append(matrix[i][j]).append('\t');

            }
            stringBuilder.append(']').append('\n');
        }

        return stringBuilder.toString();
    }

    public class GroupBinder extends Binder {
        public GroupService getService() {
            // Return this instance of LocalService so clients can call public methods
            return GroupService.this;
        }
    }
}
