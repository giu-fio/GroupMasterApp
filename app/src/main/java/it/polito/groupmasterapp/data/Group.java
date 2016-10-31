package it.polito.groupmasterapp.data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by giuseppe on 13/10/16.
 */

public class Group {

    private String name;
    private String id;
    private Slave master;
    private List<Slave> slaveList;
    private List<Device> deviceList;


    public Group(String name, String id) {
        this.name = name;
        this.id = id;
        this.slaveList = new ArrayList<>();
        this.deviceList = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Slave getMaster() {
        return master;
    }

    public void setMaster(Slave master) {
        this.master = master;
    }

    public void addDevice(Device device) {
        if (!deviceList.contains(device)) deviceList.add(device);
    }

    public void addSlave(Slave slave) {
        if (!slaveList.contains(slave)) slaveList.add(slave);
    }

    public void removeDevice(Device device) {
        deviceList.remove(device);
    }

    public void removeSlave(Slave slave) {
        slaveList.remove(slave);
    }

    public List<Slave> getSlaves() {
        return slaveList;
    }

    public List<Device> getDevices() {
        return deviceList;
    }
}

