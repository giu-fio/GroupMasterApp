package it.polito.groupslaveapp.join_group;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;

import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.List;

import it.polito.groupslaveapp.data.Group;
import it.polito.groupslaveapp.data.source.DataSource;
import it.polito.groupslaveapp.util.OnCompleteListener;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by giuseppe on 14/10/16.
 */

public class JoinGroupPresenter implements JoinGroupContract.Presenter {

    private static final long SCAN_PERIOD = 10000;
    private Handler mHandler = new Handler();

    private DataSource mDataSource;
    private JoinGroupContract.View mView;

    private List<Group> discoveredGroups;
    private Group selectedGroup;

    public JoinGroupPresenter(DataSource devicesDataSource, JoinGroupContract.View view, Context context) {
        this.mView = view;
        this.mDataSource = devicesDataSource;
        mView.setPresenter(this);
        discoveredGroups = new ArrayList<>();
    }

    @Override
    public void discoverGroups() {

        discoveredGroups.clear();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mView.showLoadingGroups(false);
                mView.joinButtonEnabled(!discoveredGroups.isEmpty());
                mDataSource.stopSearchGroup();
            }
        }, SCAN_PERIOD);

        mView.showMessage(false);
        mView.removeAllGroups();
        mView.showLoadingGroups(true);
        mView.joinButtonEnabled(false);
        mDataSource.searchGroup(null, new DataSource.FindGroupCallback() {
                    @Override
                    public void onGroupFound(Group group) {
                        discoveredGroups.add(group);
                        mView.showGroups(Lists.newArrayList(group));
                    }

                    @Override
                    public void onGroupLost(Group group) {
                        discoveredGroups.remove(group);
                        mView.removeGroup(group);
                    }

                    @Override
                    public void onError(Exception exception) {
                        mView.showErrorMessage();

                    }
                }
        );

    }

    @Override
    public void selectGroup(Group group) {
        selectedGroup = checkNotNull(group);
        mView.joinButtonEnabled(true);
    }

    @Override
    public void joinClick() {
        checkNotNull(selectedGroup);
        // mView.showLoadingGroups(true);
        mView.showDialog(true);
        mDataSource.joinGroup(selectedGroup, new OnCompleteListener<Group>() {
            @Override
            public void onComplete(Group result, boolean success) {
                if (success) {
                    // mView.showDialog(false);
                    mView.navigateToGroupAction(selectedGroup.getId());
                }
            }
        });
    }

    @Override
    public void cancelJoin() {
        mDataSource.cancelJoin(selectedGroup, new OnCompleteListener<Group>() {
            @Override
            public void onComplete(Group result, boolean success) {
                // TODO: 15/10/16
                discoverGroups();
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

    }

    @Override
    public void restoreSavedInstanceState(Bundle savedInstanceState) {

    }

    @Override
    public void start() {
        mView.joinButtonEnabled(false);
    }

    @Override
    public void stop() {

    }
}
