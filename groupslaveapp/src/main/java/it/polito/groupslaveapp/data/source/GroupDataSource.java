package it.polito.groupslaveapp.data.source;

import java.util.List;

import it.polito.groupslaveapp.LifecycleInterface;
import it.polito.groupslaveapp.data.Device;
import it.polito.groupslaveapp.data.Group;
import it.polito.groupslaveapp.util.OnCompleteListener;


/**
 * Created by giuseppe on 13/10/16.
 */

public interface GroupDataSource extends LifecycleInterface {
    String saveDevice(String deviceName);

    void searchGroup(DataSource.FindGroupCallback callback);

    void stopSearchGroup();

    void joinGroup(Group group, OnCompleteListener<Group> listener);

    void cancelJoin(Group selectedGroup, OnCompleteListener<Group> listener);

    void loadGroup(String groupId, OnCompleteListener<Group> listener);

    void sendVisibleDevices(String groupId, List<Device> devices);

}
