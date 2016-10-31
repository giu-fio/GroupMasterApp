package it.polito.groupslaveapp.data.source;


import android.support.annotation.NonNull;

import java.util.List;

import it.polito.groupslaveapp.data.Device;
import it.polito.groupslaveapp.data.Group;
import it.polito.groupslaveapp.util.OnCompleteListener;

/**
 * Created by giuseppe on 12/10/16.
 */

public interface DataSource {

    void cancelJoin(Group selectedGroup, OnCompleteListener<Group> listener);

    void loadGroup(String groupId, OnCompleteListener<Group> listener);

    void saveSlave(String deviceName);

    void searchGroup(String deviceName, @NonNull FindGroupCallback callback);

    void joinGroup(Group group, OnCompleteListener<Group> listener);

    void stopSearchGroup();

    void sendVisibleDevices(String groupId, List<Device> devices);

    interface FindGroupCallback {
        void onGroupFound(Group group);

        void onGroupLost(Group group);

        void onError(Exception exception);
    }

}
