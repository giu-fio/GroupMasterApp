package it.polito.groupmasterapp.test;

import android.app.Activity;
import android.support.annotation.NonNull;

import it.polito.groupmasterapp.data.source.DevicesDataSource;
import it.polito.groupmasterapp.data.source.device.DeviceDataSource;

/**
 * Created by giuseppe on 13/10/16.
 */

public class DeviceDataSourceStub implements DeviceDataSource {
    @Override
    public void start() {

    }

    @Override
    public void stop() {

    }

    @Override
    public void startDiscovering(@NonNull Activity activity, @NonNull DevicesDataSource.FindDevicesCallback callback) {

    }

    @Override
    public void stopDiscovering() {

    }
}
