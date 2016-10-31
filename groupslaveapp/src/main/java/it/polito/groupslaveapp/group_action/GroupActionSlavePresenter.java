package it.polito.groupslaveapp.group_action;

import android.os.Bundle;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import it.polito.groupslaveapp.data.Device;
import it.polito.groupslaveapp.data.Group;
import it.polito.groupslaveapp.data.Slave;
import it.polito.groupslaveapp.data.source.DataSource;
import it.polito.groupslaveapp.data.source.GroupSlaveService;
import it.polito.groupslaveapp.event.GroupEvents;
import it.polito.groupslaveapp.util.OnCompleteListener;

/**
 * Created by giuseppe on 25/10/16.
 */

public class GroupActionSlavePresenter implements GroupActionSlaveContract.Presenter {

    private static final String TAG = GroupActionSlavePresenter.class.getSimpleName();
    private GroupSlaveService mService;
    private GroupActionSlaveContract.View mView;
    private DataSource mDataSource;

    private Group mGroup;
    private boolean mStarted;
    private List<Device> mLostDevices;


    public GroupActionSlavePresenter(GroupActionSlaveContract.View view, DataSource dataSource, String groupId) {
        this.mView = view;
        this.mDataSource = dataSource;
        this.mStarted = false;
        this.mLostDevices = new ArrayList<>();
        this.mView.setPresenter(this);
        this.mDataSource.loadGroup(groupId, new OnCompleteListener<Group>() {
            @Override
            public void onComplete(Group result, boolean success) {
                if (success) {
                    mGroup = result;
                    mView.setTitle(mGroup.getName());
                    mView.showLoading(false);
                    if (!mStarted) start();
                    else {
                        mView.updateSlave(mGroup.getMaster(), mGroup.getDevices());
                    }
                } else mView.showErrorMessage();
            }
        });
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        // TODO: 25/10/16  
    }

    @Override
    public void restoreSavedInstanceState(Bundle savedInstanceState) {
        // TODO: 25/10/16
    }

    @Override
    public void start() {
        mStarted = true;
        EventBus.getDefault().register(this);
        if (mGroup == null) {
            mView.showLoading(true);
        } else {
            mView.updateSlave(mGroup.getMaster(), mGroup.getDevices());
        }
    }

    @Override
    public void stop() {
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void stopClick() {
        // TODO: 25/10/16
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGroupUpdatedEvent(GroupEvents.GroupUpdatedEvent event) {

        if (mService != null) {
            Map<Slave, List<Device>> groupComposition = mService.getGroupComposition();

            for (Slave slave : groupComposition.keySet()) {
                mView.updateSlave(slave, groupComposition.get(slave));
            }
            List<Device> lostDevices = mService.getLostDevices();
            mView.updateLostDevices(lostDevices);

            mLostDevices.clear();
            mLostDevices.addAll(lostDevices);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onStopGroupEvent(GroupEvents.CloseGroupEvent event) {
        mView.navigateToHome();
    }


    public void setService(GroupSlaveService service) {
        this.mService = service;
    }
}
