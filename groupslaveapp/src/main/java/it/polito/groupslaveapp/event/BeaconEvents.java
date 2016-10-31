package it.polito.groupslaveapp.event;

import org.altbeacon.beacon.Beacon;

import java.util.List;

/**
 * Created by giuseppe on 17/10/16.
 */

public interface BeaconEvents {


    class NewBeaconsFoundEvent {
        private List<Beacon> beacons;

        public NewBeaconsFoundEvent(List<Beacon> beaconList) {
            this.beacons = beaconList;
        }

        public List<Beacon> getBeacons() {
            return beacons;
        }
    }

    class VisibleBeaconsEvent {
        private List<Beacon> beacons;


        public VisibleBeaconsEvent(List<Beacon> beacons) {
            this.beacons = beacons;
        }

        public List<Beacon> getBeacons() {
            return beacons;
        }
    }

    class BeaconsLostEvent {
        private List<Beacon> beacons;

        public BeaconsLostEvent(List<Beacon> beaconList) {
            this.beacons = beaconList;
        }

        public List<Beacon> getBeacons() {
            return beacons;
        }
    }
}
