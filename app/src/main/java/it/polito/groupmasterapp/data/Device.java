package it.polito.groupmasterapp.data;

/**
 * Created by giuseppe on 12/10/16.
 */

public class Device {

    private String uid;
    private String address;
    private double distance;
    private float battery;
    private long timestamp;

    public Device(String uid, String address) {
        this.uid = uid;
        this.address = address;
    }

    public Device(String uid) {
        this.uid = uid;
    }

    public Device(Device device) {
        this.uid = device.getUid();
        this.address = device.getAddress();
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public float getBattery() {
        return battery;
    }

    public void setBattery(float battery) {
        this.battery = battery;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Device device = (Device) o;

        return uid != null ? uid.equals(device.uid) : device.uid == null;

    }

    @Override
    public int hashCode() {
        return uid != null ? uid.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "Device{" +
                "uid='" + uid + '\'' +
                '}';
    }
}
