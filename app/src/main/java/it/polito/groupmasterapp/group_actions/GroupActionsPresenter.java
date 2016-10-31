package it.polito.groupmasterapp.group_actions;

import android.os.Bundle;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import it.polito.groupmasterapp.events.GroupEvents;
import it.polito.groupmasterapp.data.Device;
import it.polito.groupmasterapp.data.Group;
import it.polito.groupmasterapp.data.Slave;
import it.polito.groupmasterapp.data.source.DevicesDataSource;
import it.polito.groupmasterapp.data.source.GroupService;
import it.polito.groupmasterapp.util.OnCompleteListener;

/**
 * Created by giuseppe on 19/10/16.
 */

public class GroupActionsPresenter implements GroupActionsContract.Presenter {

    private GroupActionsContract.View mView;
    private DevicesDataSource mDataSource;

    private Group mGroup;
    private List<Device> mLostDevices;
    private boolean mStarted;
    private GroupService mService;


    public GroupActionsPresenter(final GroupActionsContract.View mView, DevicesDataSource mDataSource, String groupId) {
        this.mView = mView;
        this.mDataSource = mDataSource;
        this.mStarted = false;
        this.mView.setPresenter(this);
        this.mLostDevices = new LinkedList<>();
        this.mDataSource.loadGroup(groupId, new OnCompleteListener<Group>() {
            @Override
            public void onComplete(Group result, boolean success) {
                if (success) {
                    mGroup = result;
                    mView.showLoading(false);
                    if (!mStarted) start();
                } else mView.showErrorMessage();
            }
        });
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        // TODO: 21/10/16
    }

    @Override
    public void restoreSavedInstanceState(Bundle savedInstanceState) {
        // TODO: 21/10/16
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
        mService.stopListening();
        mView.navigateToHome();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGroupUpdatedEvent(GroupEvents.GroupUpdatedEvent event) {
        if (mService != null) {
            Map<Slave, List<Device>> groupComposition = mService.getGroupComposition();

            for (Slave slave : groupComposition.keySet()) {
                mView.updateSlave(slave, groupComposition.get(slave));
            }
            List<Device> lostDevices = mService.getLostDevices();

            for (Device device : lostDevices) {
                mView.showLostDevice(device);
            }
            mLostDevices.removeAll(lostDevices);
            for (Device device : mLostDevices) {
                mView.showDeviceFound(device);
            }
            mLostDevices.clear();
            mLostDevices.addAll(lostDevices);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onStopGroupEvent(GroupEvents.StopGroupEvent event) {
        mView.navigateToHome();
    }

    public void setService(GroupService service) {
        this.mService = service;
    }
}
