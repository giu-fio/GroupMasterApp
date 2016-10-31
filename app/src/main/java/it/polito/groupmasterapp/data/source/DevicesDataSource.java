package it.polito.groupmasterapp.data.source;

import android.app.Activity;
import android.support.annotation.NonNull;

import java.util.List;
import java.util.Map;

import it.polito.groupmasterapp.LifecycleInterface;
import it.polito.groupmasterapp.data.Device;
import it.polito.groupmasterapp.data.Group;
import it.polito.groupmasterapp.data.Slave;
import it.polito.groupmasterapp.util.OnCompleteListener;

/**
 * Created by giuseppe on 12/10/16.
 */

public interface DevicesDataSource extends LifecycleInterface {



    interface FindDevicesCallback {
        void onDeviceFound(Device device);

        void onDeviceLost(Device device);

        void onError(Exception exception);
    }

    interface FindSlavesCallback {
        void onSlaveFound(Slave slave);

        void onSlaveLost(Slave slave);

        void onError(Exception exception);
    }

    interface SlaveMessageCallback {
        void onSlaveMessage(Slave slave, List<Device> devices, long timestamp);

        void onError(Exception exception);
    }

    interface GroupCallback {
        void onGroupChanged(Slave slave, List<Device> devices, long timestamp);

        void onLostDevice(Device device, Slave lastSlave, long timestamp);

        void onDeviceFound(Device device, Slave lastSlave, long timestamp);

        void onError(Exception exception);
    }

    void createGroup(String deviceName, String groupName, OnCompleteListener<Group> onCompleteListener);

    void loadGroup(String groupId, OnCompleteListener<Group> onCompleteListener);

    void startDevicesDiscovering(Activity activity, @NonNull FindDevicesCallback callback);

    void stopDevicesDiscovering();

    void startSlavesDiscovering(@NonNull FindSlavesCallback callback);

    void stopSlavesDiscovering();

    void saveGroup(String groupId, List<Slave> slaves, List<Device> devices, OnCompleteListener<Group> onCompleteListener);

    void updateGroup(Group group, Map<Slave, List<Device>> groupComposition, OnCompleteListener<Group> callback) ;

    void updateLostDevice(Group mGroup, Slave slave, Device device, OnCompleteListener<Device> callback);
    void updateFoundDevices(Group mGroup, Slave slave, Device device, OnCompleteListener<Device> onCompleteListener);

    void closeGroup(Group group);
}
