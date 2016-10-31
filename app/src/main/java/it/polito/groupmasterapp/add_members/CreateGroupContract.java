package it.polito.groupmasterapp.add_members;

/**
 * Created by giuseppe on 12/10/16.
 */

import java.util.List;

import it.polito.groupmasterapp.BasePresenter;
import it.polito.groupmasterapp.BaseView;
import it.polito.groupmasterapp.data.Device;
import it.polito.groupmasterapp.data.Group;
import it.polito.groupmasterapp.data.Slave;

/**
 * Specifica il contratto tra View e Presenter.
 */
public interface CreateGroupContract {

    interface View extends BaseView<Presenter> {

        void showMessage(boolean value);

        void showLoadingSlaves(boolean loading);

        void showLoadingDevices(boolean loading);

        void showNoSlave();

        void showNoDevices();

        void showDevices(List<Device> devices);

        void showSlaves(List<Slave> slaves);

        boolean isActive();

        void removeAllSlaves();

        void removeSlave(Slave slave);

        void removeAllDevices();

        void removeDevice(Device device);

        void showErrorMessage();

        void createButtonEnabled(boolean enabled);

        void navigateToGroupAction(String groupId);

        void discoverButtonEnabled(boolean b);
    }

    interface Presenter extends BasePresenter {

        void discoverDevices();

        void discoverSlaves();

        void addDevice(Device device);

        void addSlave(Slave slave);

        void removeDevice(Device device);

        void removeSlave(Slave slave);

        void setGroup(Group group);

        void setDeviceName(String deviceName);

        void startGroup();
    }

}
