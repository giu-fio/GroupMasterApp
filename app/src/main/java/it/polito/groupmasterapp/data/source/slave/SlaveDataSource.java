package it.polito.groupmasterapp.data.source.slave;

import android.support.annotation.NonNull;

import java.util.List;
import java.util.Map;

import it.polito.groupmasterapp.LifecycleInterface;
import it.polito.groupmasterapp.data.Device;
import it.polito.groupmasterapp.data.Group;
import it.polito.groupmasterapp.data.Slave;
import it.polito.groupmasterapp.data.source.DevicesDataSource;
import it.polito.groupmasterapp.util.OnCompleteListener;

/**
 * Created by giuseppe on 13/10/16.
 */

public interface SlaveDataSource extends LifecycleInterface {

    String createGroup(String deviceName, String groupName);

    void saveGroup(Group group, OnCompleteListener<Group> onCompleteListener);

    void startSlavesDiscovering(@NonNull DevicesDataSource.FindSlavesCallback callback);

    void stopSlavesDiscovering();

    void updateGroup(Group group, Map<Slave, List<Device>> groupCompositionMap, OnCompleteListener<Group> callback);

    void closeGroup(Group group);

    void notifyLostDevice(Group group, Device device, String lastSlaveId, long lastTimestamp, OnCompleteListener<Device> callback);

    void notifyDeviceFound(Group group, Device device, String slaveId, OnCompleteListener<Device> callback);

    void startSlaveMessagesListening(@NonNull Group group, @NonNull DevicesDataSource.SlaveMessageCallback callback);

    void stopSlaveMessagesListening();

    void startBackgroundSlaveListening();

    void stopBackgroundSlaveListening();

    void loadGroup(String groupId, OnCompleteListener<Group> onCompleteListener);

    void sendVisibleDevices(String groupId, List<Device> devices);


}
