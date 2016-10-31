package it.polito.groupmasterapp.data.source.device;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.common.base.Preconditions;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.Identifier;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import it.polito.groupmasterapp.events.BeaconEvents;
import it.polito.groupmasterapp.add_members.EddystoneService;
import it.polito.groupmasterapp.data.Device;
import it.polito.groupmasterapp.data.source.DevicesDataSource;

/**
 * Created by giuseppe on 16/10/16.
 */

public class EddystoneDeviceRepository implements DeviceDataSource {
    private static final String TAG = "EddystoneDevRepository";

    private Activity mActivity;
    private DevicesDataSource.FindDevicesCallback mCallback;

    @Override
    public void start() {

    }

    @Override
    public void stop() {

    }

    @Override
    public void startDiscovering(@NonNull Activity activity, @NonNull DevicesDataSource.FindDevicesCallback callback) {
        Log.d(TAG, "startDiscovering: ");
        this.mActivity = Preconditions.checkNotNull(activity);
        this.mCallback = Preconditions.checkNotNull(callback);
        EddystoneService.startScanAction(mActivity);
        EventBus.getDefault().register(this);
    }

    @Override
    public void stopDiscovering() {

        Log.d(TAG, "stopDiscovering: ");
        EddystoneService.stopScanAction(mActivity);
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBeaconsFoundEvent(BeaconEvents.NewBeaconsFoundEvent event) {
        Log.d(TAG, "onBeaconsFoundEvent: ");
        for (Beacon beacon : event.getBeacons()) {
            //  Identifier namespaceId = beacon.getId1();
            Identifier instanceId = beacon.getId2();

            Device device = new Device(instanceId.toHexString(), beacon.getBluetoothAddress());
            device.setDistance(beacon.getDistance());

            // Do we have telemetry data?
            if (beacon.getExtraDataFields().size() > 0) {
                // long telemetryVersion = beacon.getExtraDataFields().get(0);
                long batteryMilliVolts = beacon.getExtraDataFields().get(1);
                // long pduCount = beacon.getExtraDataFields().get(3);
                //long uptime = beacon.getExtraDataFields().get(4);
                device.setBattery(batteryMilliVolts);
            }

            mCallback.onDeviceFound(device);

        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBeaconsLostEvent(BeaconEvents.BeaconsLostEvent event) {
        Log.d(TAG, "onBeaconsLostEvent: ");
        for (Beacon beacon : event.getBeacons()) {
            //  Identifier namespaceId = beacon.getId1();
            Identifier instanceId = beacon.getId2();

            Device device = new Device(instanceId.toHexString(), beacon.getBluetoothAddress());
            device.setDistance(beacon.getDistance());

            // Do we have telemetry data?
            if (beacon.getExtraDataFields().size() > 0) {
                // long telemetryVersion = beacon.getExtraDataFields().get(0);
                long batteryMilliVolts = beacon.getExtraDataFields().get(1);
                // long pduCount = beacon.getExtraDataFields().get(3);
                //long uptime = beacon.getExtraDataFields().get(4);
                device.setBattery(batteryMilliVolts);
            }
            mCallback.onDeviceLost(device);

        }
    }


}
