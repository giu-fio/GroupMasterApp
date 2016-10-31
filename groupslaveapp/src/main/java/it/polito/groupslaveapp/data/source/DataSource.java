package it.polito.groupslaveapp.data.source;


import android.support.annotation.NonNull;

import java.util.List;

import it.polito.groupslaveapp.LifecycleInterface;
import it.polito.groupslaveapp.data.Device;
import it.polito.groupslaveapp.data.Group;
import it.polito.groupslaveapp.util.OnCompleteListener;

/**
 * Created by giuseppe on 12/10/16.
 */

public interface DataSource extends LifecycleInterface {

    void cancelJoin(Group selectedGroup, OnCompleteListener<Group> listener);

    void loadGroup(String groupId, OnCompleteListener<Group> listener);

    interface FindDevicesCallback {
        void onDeviceFound(Device device);

        void onDeviceLost(Device device);

        void onError(Exception exception);
    }

    interface FindGroupCallback {
        void onGroupFound(Group group);

        void onGroupLost(Group group);

        void onError(Exception exception);

    }

    void saveSlave(String deviceName);

    void searchGroup(String deviceName, @NonNull FindGroupCallback callback);

    void joinGroup(Group group, OnCompleteListener<Group> listener);

    void startDevicesDiscovering(@NonNull FindDevicesCallback callback);

    void stopDevicesDiscovering();

    void stopSearchGroup();

    void sendVisibleDevices(String groupId, List<Device> devices);


}
