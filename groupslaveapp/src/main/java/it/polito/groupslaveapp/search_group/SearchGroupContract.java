package it.polito.groupslaveapp.search_group;

import it.polito.groupslaveapp.BasePresenter;
import it.polito.groupslaveapp.BaseView;

/**
 * Created by giuseppe on 14/10/16.
 */

public interface SearchGroupContract {

    interface View extends BaseView<Presenter> {
        void buttonEnabled(boolean enabled);

        void initDeviceName(String name);

        void setPresenter(SearchGroupContract.Presenter presenter);

        void navigateToJoinGroup(String deviceName);

        void showLoading(boolean loading);

        void showCreateErrorMessage();

    }

    interface Presenter extends BasePresenter {

        void deviceNameChanged(String name);

        void searchClick();
    }
}
