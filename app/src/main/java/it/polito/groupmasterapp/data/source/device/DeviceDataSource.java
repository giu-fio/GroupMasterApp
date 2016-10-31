package it.polito.groupmasterapp.data.source.device;

import android.app.Activity;
import android.support.annotation.NonNull;

import it.polito.groupmasterapp.LifecycleInterface;
import it.polito.groupmasterapp.data.source.DevicesDataSource;

/**
 * Created by giuseppe on 13/10/16.
 */

public interface DeviceDataSource extends LifecycleInterface {

    void startDiscovering(@NonNull Activity activity, @NonNull DevicesDataSource.FindDevicesCallback callback);

    void stopDiscovering();


}
