package it.polito.groupslaveapp.data;

import android.support.annotation.NonNull;

import java.util.List;

import it.polito.groupslaveapp.data.source.DataSource;
import it.polito.groupslaveapp.data.source.DeviceDataSource;
import it.polito.groupslaveapp.data.source.GroupDataSource;
import it.polito.groupslaveapp.util.OnCompleteListener;

/**
 * Created by giuseppe on 14/10/16.
 */

public class GroupRepository implements DataSource {

    private static GroupRepository INSTANCE = null;
    private GroupDataSource groupDataSource;
    private DeviceDataSource deviceDataSource;

    private GroupRepository(GroupDataSource groupDataSource, DeviceDataSource deviceDataSource) {
        this.groupDataSource = groupDataSource;
        this.deviceDataSource = deviceDataSource;
    }

    public static GroupRepository getInstance(GroupDataSource groupDataSource, DeviceDataSource deviceDataSource) {
        if (INSTANCE == null) {
            INSTANCE = new GroupRepository(groupDataSource, deviceDataSource);
        }
        return INSTANCE;
    }


    @Override
    public void cancelJoin(Group selectedGroup, OnCompleteListener<Group> listener) {
        groupDataSource.cancelJoin(selectedGroup, listener);
    }

    @Override
    public void loadGroup(String groupId, OnCompleteListener<Group> listener) {
        groupDataSource.loadGroup(groupId, listener);
    }

    @Override
    public void saveSlave(String deviceName) {
        groupDataSource.saveDevice(deviceName);
    }

    @Override
    public void searchGroup(String deviceName, @NonNull FindGroupCallback callback) {
        groupDataSource.searchGroup(callback);
    }

    @Override
    public void joinGroup(Group group, OnCompleteListener<Group> listener) {
        groupDataSource.joinGroup(group, listener);
    }

    @Override
    public void startDevicesDiscovering(@NonNull FindDevicesCallback callback) {

    }

    @Override
    public void stopDevicesDiscovering() {

    }

    @Override
    public void stopSearchGroup() {
        groupDataSource.stopSearchGroup();
    }

    @Override
    public void sendVisibleDevices(String groupId, List<Device> devices) {
        groupDataSource.sendVisibleDevices(groupId, devices);
    }

    @Override
    public void start() {

    }

    @Override
    public void stop() {

    }
}
