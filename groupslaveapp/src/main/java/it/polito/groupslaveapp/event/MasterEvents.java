package it.polito.groupslaveapp.event;

import java.util.List;

import it.polito.groupslaveapp.data.Device;
import it.polito.groupslaveapp.data.Group;
import it.polito.groupslaveapp.data.Slave;

/**
 * Created by giuseppe on 25/10/16.
 */

public interface MasterEvents {

    class GroupUpdateEvent {

        private Slave slave;
        private List<Device> devices;
        private long timestamp;

        public GroupUpdateEvent(Slave slave, List<Device> devices, long timestamp) {
            this.slave = slave;
            this.devices = devices;
            this.timestamp = timestamp;
        }

        public Slave getSlave() {
            return slave;
        }

        public List<Device> getDevices() {
            return devices;
        }


        public long getTimestamp() {
            return timestamp;
        }
    }

    class LostDeviceEvent {

        private Device device;
        private Slave lastSlave;
        private long lastTimestamp;

        public LostDeviceEvent(Device device, Slave lastSlave, long lastTimestamp) {
            this.device = device;
            this.lastSlave = lastSlave;
            this.lastTimestamp = lastTimestamp;
        }

        public Device getDevice() {
            return device;
        }

        public Slave getLastSlave() {
            return lastSlave;
        }

        public long getLastTimestamp() {
            return lastTimestamp;
        }
    }

    class DeviceFoundEvent {
        private Device device;
        private Slave slave;
        private long timestamp;


        public DeviceFoundEvent(Device device, Slave slave, long timestamp) {
            this.device = device;
            this.slave = slave;
            this.timestamp = timestamp;
        }

        public Device getDevice() {
            return device;
        }

        public Slave getSlave() {
            return slave;
        }

        public long getTimestamp() {
            return timestamp;
        }

    }

    class CloseGroupEvent {
        private Group group;

        public CloseGroupEvent(Group group) {
            this.group = group;
        }

        public Group getGroup() {
            return group;
        }
    }

    class LostSlaveEvent {
        private Slave slave;
        private List<Device> devices;
        private long timestamp;

        public LostSlaveEvent(Slave slave, List<Device> devices, long timestamp) {
            this.slave = slave;
            this.devices = devices;
            this.timestamp = timestamp;
        }

        public Slave getSlave() {
            return slave;
        }

        public long getTimestamp() {
            return timestamp;
        }

        public List<Device> getDevices() {
            return devices;
        }
    }

    class SlaveFoundEvent {
        private Slave slave;
        private List<Device> devices;
        private long timestamp;

        public SlaveFoundEvent(Slave slave, List<Device> devices, long timestamp) {
            this.slave = slave;
            this.devices = devices;
            this.timestamp = timestamp;
        }

        public Slave getSlave() {
            return slave;
        }

        public long getTimestamp() {
            return timestamp;
        }

        public List<Device> getDevices() {
            return devices;
        }
    }


}
