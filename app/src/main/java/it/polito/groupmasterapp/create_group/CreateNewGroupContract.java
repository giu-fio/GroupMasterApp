package it.polito.groupmasterapp.create_group;

import it.polito.groupmasterapp.BasePresenter;
import it.polito.groupmasterapp.BaseView;
import it.polito.groupmasterapp.data.Group;

/**
 * Created by giuseppe on 13/10/16.
 */

public interface CreateNewGroupContract {


    interface View extends BaseView<CreateNewGroupContract.Presenter> {

        void buttonEnabled(boolean enabled);

        void initDeviceName(String name);

        void initGroupName(String groupName);

        void setPresenter(CreateNewGroupContract.Presenter presenter);

        void navigateToAddMembers(String deviceName, Group group);

        void showLoading(boolean loading);

        void showCreateErrorMessage();
    }

    interface Presenter extends BasePresenter {

        void groupNameChanged(String name);

        void deviceNameChanged(String name);

        void createClick();
    }


}
