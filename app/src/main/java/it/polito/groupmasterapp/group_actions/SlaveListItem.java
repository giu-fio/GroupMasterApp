package it.polito.groupmasterapp.group_actions;

import com.bignerdranch.expandablerecyclerview.Model.ParentListItem;

import java.util.List;

import it.polito.groupmasterapp.data.Device;
import it.polito.groupmasterapp.data.Slave;

/**
 * Created by giuseppe on 19/10/16.
 */

public class SlaveListItem implements ParentListItem {

    private Slave slave;
    private List<Device> deviceList;

    public SlaveListItem(Slave slave, List<Device> deviceList) {
        this.slave = slave;
        this.deviceList = deviceList;
    }

    @Override
    public List<?> getChildItemList() {
        return deviceList;
    }

    @Override
    public boolean isInitiallyExpanded() {
        return false;
    }

    public Slave getSlave() {
        return slave;
    }

    public void setSlave(Slave slave) {
        this.slave = slave;
    }

    public List<Device> getDeviceList() {
        return deviceList;
    }

    public void setDeviceList(List<Device> deviceList) {
        this.deviceList = deviceList;
    }
}
