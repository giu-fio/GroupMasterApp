package it.polito.groupslaveapp.group_action;

import java.util.List;

import it.polito.groupslaveapp.BasePresenter;
import it.polito.groupslaveapp.BaseView;
import it.polito.groupslaveapp.data.Device;
import it.polito.groupslaveapp.data.Slave;

/**
 * Created by giuseppe on 25/10/16.
 */

public interface GroupActionSlaveContract {

    interface View extends BaseView<Presenter> {
        void showMessage(boolean value);

        void showLoading(boolean loading);

        void showErrorMessage();

        void showLostDevice(Device device);

        void updateLostDevices(List<Device> devices);

        void showDeviceFound(Device device);

        void updateSlave(Slave slave, List<Device> devices);

        void navigateToHome();

        void setTitle(String name);
    }

    interface Presenter extends BasePresenter {
        void stopClick();
    }
}
