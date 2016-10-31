package it.polito.groupslaveapp.data;

import android.support.annotation.NonNull;

import java.util.List;

import it.polito.groupslaveapp.data.source.DataSource;
import it.polito.groupslaveapp.data.source.GroupDataSource;
import it.polito.groupslaveapp.util.OnCompleteListener;

/**
 * Created by giuseppe on 14/10/16.
 */

public class GroupRepository implements DataSource {

    private static GroupRepository INSTANCE = null;
    private GroupDataSource groupDataSource;

    private Group mGroup;

    private GroupRepository(GroupDataSource groupDataSource) {
        this.groupDataSource = groupDataSource;
    }

    public static GroupRepository getInstance(GroupDataSource groupDataSource) {
        if (INSTANCE == null) {
            INSTANCE = new GroupRepository(groupDataSource);
        }
        return INSTANCE;
    }

    @Override
    public void cancelJoin(Group selectedGroup, OnCompleteListener<Group> listener) {
        groupDataSource.cancelJoin(selectedGroup, listener);
    }

    @Override
    public void loadGroup(String groupId, final OnCompleteListener<Group> listener) {
        if (mGroup != null && mGroup.getId().equals(groupId)) {
            listener.onComplete(mGroup, true);
        } else {
            groupDataSource.loadGroup(groupId, new OnCompleteListener<Group>() {
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
    public void stopSearchGroup() {
        groupDataSource.stopSearchGroup();
    }

    @Override
    public void sendVisibleDevices(String groupId, List<Device> devices) {
        groupDataSource.sendVisibleDevices(groupId, devices);
    }

}
