package it.polito.groupmasterapp.group_actions;

import java.util.List;

import it.polito.groupmasterapp.BasePresenter;
import it.polito.groupmasterapp.BaseView;
import it.polito.groupmasterapp.add_members.CreateGroupContract;
import it.polito.groupmasterapp.data.Device;
import it.polito.groupmasterapp.data.Slave;

/**
 * Created by giuseppe on 19/10/16.
 */

public interface GroupActionsContract {

    interface View extends BaseView<GroupActionsContract.Presenter> {

        void showMessage(boolean value);

        void showLoading(boolean loading);

        boolean isActive();

        void showErrorMessage();

        void showLostDevice(Device device);

        void showLostSlave(Slave slave);

        void showDeviceFound(Device device);

        void showSlaveFound(Slave slave);

        void updateSlave(Slave slave, List<Device> devices);

        void updateMaster(List<Device> devices);

        void navigateToHome();
    }

    interface NotificationView extends BaseView<CreateGroupContract.Presenter> {

    }

    interface Presenter extends BasePresenter {

        void stopClick();

    }
}
