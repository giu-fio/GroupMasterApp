package it.polito.groupmasterapp.events;

import java.util.List;

import it.polito.groupmasterapp.data.Device;
import it.polito.groupmasterapp.data.Slave;

/**
 * Created by giuseppe on 22/10/16.
 */

public interface SlaveEvents {

    class SlaveMessageEvent {
        private Slave slave;
        private List<Device> devices;
        private long timestamp;

        public SlaveMessageEvent(Slave slave, List<Device> devices, long timestamp) {
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
}
