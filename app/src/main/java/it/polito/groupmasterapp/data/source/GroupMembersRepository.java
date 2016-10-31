package it.polito.groupmasterapp.data.source;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.List;
import java.util.Map;

import it.polito.groupmasterapp.data.Device;
import it.polito.groupmasterapp.data.Group;
import it.polito.groupmasterapp.data.Slave;
import it.polito.groupmasterapp.data.source.device.DeviceDataSource;
import it.polito.groupmasterapp.data.source.slave.SlaveDataSource;
import it.polito.groupmasterapp.util.OnCompleteListener;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by giuseppe on 13/10/16.
 */

public class GroupMembersRepository implements DevicesDataSource {

    private static GroupMembersRepository INSTANCE = null;
    private static final String TAG = GroupMembersRepository.class.getSimpleName();

    private SlaveDataSource mSlaveDataSource;
    private DeviceDataSource mDeviceDataSource;
    private Context mContext;
    private boolean mScanning;
    private Group mGroup;


    private GroupMembersRepository(@NonNull SlaveDataSource slaveDataSource, @NonNull DeviceDataSource deviceDataSource, @NonNull Context context) {
        mDeviceDataSource = checkNotNull(deviceDataSource);
        mSlaveDataSource = checkNotNull(slaveDataSource);
        mContext = checkNotNull(context);
    }

    public static GroupMembersRepository getInstance(SlaveDataSource slaveDataSource, DeviceDataSource deviceDataSource, Context context) {
        if (INSTANCE == null) {
            INSTANCE = new GroupMembersRepository(slaveDataSource, deviceDataSource, context);
        }
        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }

    @Override
    public void updateGroup(Group group, Map<Slave, List<Device>> groupComposition, OnCompleteListener<Group> callback) {

        mSlaveDataSource.updateGroup(group, groupComposition, callback);

    }

    @Override
    public void updateLostDevice(Group mGroup, Slave slave, Device device, OnCompleteListener<Device> callback) {
        mSlaveDataSource.notifyLostDevice(mGroup, device, slave.getId(), System.currentTimeMillis(), callback);
    }

    @Override
    public void updateFoundDevices(Group mGroup, Slave slave, Device device, OnCompleteListener<Device> callback) {
        mSlaveDataSource.notifyDeviceFound(mGroup, device, slave.getId(), callback);
    }

    @Override
    public void closeGroup(Group group) {
        mSlaveDataSource.closeGroup(group);
    }

    @Override
    public void createGroup(String deviceName, String groupName, OnCompleteListener<Group> onCompleteListener) {
        String id = mSlaveDataSource.createGroup(deviceName, groupName);
        if (id == null) onCompleteListener.onComplete(null, false);
        Group group = new Group(groupName, id);
        onCompleteListener.onComplete(group, true);
    }

    @Override
    public void loadGroup(String groupId, final OnCompleteListener<Group> listener) {
        if (mGroup != null && mGroup.getId().equals(groupId)) {
            listener.onComplete(mGroup, true);
        } else {
            mSlaveDataSource.loadGroup(groupId, new OnCompleteListener<Group>() {
                @Override
                public void onComplete(Group result, boolean success) {
                    if (success) {
                        mGroup = result;
                    }
                    listener.onComplete(result, success);
                }
            });
        }
    }

    @Override
    public void startDevicesDiscovering(Activity activity, @NonNull FindDevicesCallback callback) {
        mDeviceDataSource.startDiscovering(activity, callback);
        mScanning = true;
    }

    @Override
    public void stopDevicesDiscovering() {
        if (mScanning) mDeviceDataSource.stopDiscovering();
        mScanning = false;
    }

    @Override
    public void startSlavesDiscovering(@NonNull FindSlavesCallback callback) {
        mSlaveDataSource.startSlavesDiscovering(callback);
    }

    @Override
    public void stopSlavesDiscovering() {
        mSlaveDataSource.stopSlavesDiscovering();
    }

    @Override
    public void saveGroup(String groupId, List<Slave> slaves, List<Device> devices, OnCompleteListener<Group> onCompleteListener) {
        Group group = new Group(null, groupId);
        group.getSlaves().addAll(slaves);
        group.getDevices().addAll(devices);
        mSlaveDataSource.saveGroup(group, onCompleteListener);
    }


    @Override
    public void start() {
        mSlaveDataSource.start();
    }

    @Override
    public void stop() {
        mSlaveDataSource.stop();
    }


}
