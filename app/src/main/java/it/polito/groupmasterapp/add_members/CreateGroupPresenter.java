package it.polito.groupmasterapp.add_members;


import android.os.Bundle;
import android.os.Handler;

import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.List;

import it.polito.groupmasterapp.data.Device;
import it.polito.groupmasterapp.data.Group;
import it.polito.groupmasterapp.data.Slave;
import it.polito.groupmasterapp.data.source.DevicesDataSource;
import it.polito.groupmasterapp.util.OnCompleteListener;

/**
 * Created by giuseppe on 12/10/16.
 */

public class CreateGroupPresenter implements CreateGroupContract.Presenter {

    private static final long SCAN_PERIOD = 10000;
    private static final String GROUP_NAME_ARG = "it.polito.groupmasterapp.GROUP_NAME_ARG";
    private static final String DEVICE_NAME_ARG = "it.polito.groupmasterapp.DEVICE_NAME_ARG";
    private static final String DEVICE_IDS_ARG = "it.polito.groupmasterapp.DEVICE_IDS_ARG";
    private static final String SLAVE_IDS_ARG = "it.polito.groupmasterapp.SLAVE_IDS_ARG";


    private DevicesDataSource mDataSource;
    private CreateGroupContract.View mView;

    private Handler mHandler = new Handler();


    private Group mGroup;
    private List<Slave> mDiscoveredSlaveList;
    private List<Slave> mGroupSlaveList;
    private List<Device> mDiscoveredDeviceList;
    private List<Device> mGroupDeviceList;
    private String mMasterName;


    public CreateGroupPresenter(CreateGroupContract.View view, DevicesDataSource dataSource) {
        this.mView = view;
        this.mDataSource = dataSource;
        mDiscoveredSlaveList = new ArrayList<>();
        mGroupSlaveList = new ArrayList<>();
        mDiscoveredDeviceList = new ArrayList<>();
        mGroupDeviceList = new ArrayList<>();
        view.setPresenter(this);
    }

    @Override
    public void discoverDevices() {

        mDiscoveredDeviceList.clear();
        mView.removeAllDevices();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mDataSource.stopDevicesDiscovering();
                mView.showLoadingDevices(false);
                mView.createButtonEnabled(!mGroupDeviceList.isEmpty());

            }
        }, SCAN_PERIOD);

        this.mDataSource.startDevicesDiscovering(mView.getActivity(),
                new DevicesDataSource.FindDevicesCallback() {
                    @Override
                    public void onDeviceFound(Device device) {
                        if (!mDiscoveredDeviceList.contains(device)) {
                            mDiscoveredDeviceList.add(device);
                            mView.showDevices(Lists.newArrayList(device));
                        }
                    }

                    @Override
                    public void onDeviceLost(Device device) {
                        if (mDiscoveredDeviceList.contains(device)) {
                            mDiscoveredDeviceList.remove(device);
                            mView.removeDevice(device);
                        }
                    }

                    @Override
                    public void onError(Exception exception) {
                        mView.showErrorMessage();
                    }
                }
        );
    }


    @Override
    public void discoverSlaves() {
        mView.showMessage(false);
        mDiscoveredSlaveList.clear();
        mView.removeAllSlaves();
        mView.discoverButtonEnabled(false);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mDataSource.stopSlavesDiscovering();
                mView.showLoadingSlaves(false);
                mView.discoverButtonEnabled(true);


            }
        }, SCAN_PERIOD);

        mView.showLoadingSlaves(true);
        mDataSource.startSlavesDiscovering(new DevicesDataSource.FindSlavesCallback() {
            @Override
            public void onSlaveFound(Slave slave) {
                mDiscoveredSlaveList.add(slave);
                mView.showSlaves(Lists.newArrayList(slave));
            }

            @Override
            public void onSlaveLost(Slave slave) {
                mDiscoveredSlaveList.remove(slave);
                mView.removeSlave(slave);
            }

            @Override
            public void onError(Exception exception) {
                mView.showErrorMessage();
            }
        });
    }

    @Override
    public void addDevice(Device device) {
        mGroupDeviceList.add(device);
        mView.createButtonEnabled(true);
    }

    @Override
    public void addSlave(Slave slave) {
        mGroupSlaveList.add(slave);
    }

    @Override
    public void removeDevice(Device device) {
        mGroupDeviceList.remove(device);
        if (mGroupDeviceList.isEmpty()) mView.createButtonEnabled(false);
    }

    @Override
    public void removeSlave(Slave slave) {
        mGroupSlaveList.remove(slave);
    }

    @Override
    public void setGroup(Group group) {
        mGroup = group;
    }

    @Override
    public void setDeviceName(String deviceName) {
        mMasterName = deviceName;
    }


    @Override
    public void startGroup() {
        mDataSource.saveGroup(mGroup.getId(), mGroupSlaveList, mGroupDeviceList, new OnCompleteListener<Group>() {
            @Override
            public void onComplete(Group result, boolean success) {
                mView.navigateToGroupAction(result.getId());
            }
        });
    }


    @Override
    public void start() {
        mDataSource.start();
        mView.createButtonEnabled(false);
        mView.showMessage(true);
    }

    @Override
    public void stop() {
        mDataSource.stopDevicesDiscovering();
        mDataSource.stopSlavesDiscovering();
        mDataSource.stop();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // TODO: 14/10/16
    }

    @Override
    public void restoreSavedInstanceState(Bundle savedInstanceState) {
// TODO: 14/10/16
    }
}
