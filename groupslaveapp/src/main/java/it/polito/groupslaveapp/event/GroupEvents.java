package it.polito.groupslaveapp.event;

import it.polito.groupslaveapp.data.Device;

/**
 * Created by giuseppe on 23/10/16.
 */

public interface GroupEvents {

    class GroupUpdatedEvent {
    }

    class CloseGroupEvent {
    }

    class LostDeviceEvent {
        private Device device;

        public LostDeviceEvent(Device device) {
            this.device = device;
        }

        public Device getDevice() {
            return device;
        }
    }

    class DeviceFoundEvent {
        private Device device;

        public DeviceFoundEvent(Device device) {
            this.device = device;
        }

        public Device getDevice() {
            return device;
        }
    }


}
