package it.polito.groupslaveapp.join_group;

import java.util.List;

import it.polito.groupslaveapp.BasePresenter;
import it.polito.groupslaveapp.BaseView;
import it.polito.groupslaveapp.data.Group;

/**
 * Created by giuseppe on 14/10/16.
 */

public interface JoinGroupContract {

    interface View extends BaseView<Presenter> {

        void showMessage(boolean value);

        void showLoadingGroups(boolean loading);

        void showNoGroups();

        void showGroups(List<Group> groups);

        void removeAllGroups();

        void removeGroup(Group group);

        void showErrorMessage();

        void joinButtonEnabled(boolean enabled);

        void showDialog(boolean show);

        void navigateToGroupAction(String groupId);

    }

    interface Presenter extends BasePresenter {

        void discoverGroups();

        void selectGroup(Group group);

        void joinClick();

        void cancelJoin();
    }
}
