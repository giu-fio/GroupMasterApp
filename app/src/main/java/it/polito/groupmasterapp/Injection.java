package it.polito.groupmasterapp;

import android.content.Context;
import android.support.annotation.NonNull;

import com.google.common.base.Preconditions;

import it.polito.groupmasterapp.data.source.GroupMembersRepository;
import it.polito.groupmasterapp.data.source.device.EddystoneDeviceRepository;
import it.polito.groupmasterapp.data.source.slave.FirebaseSlaveDataSource;


/**
 * Created by giuseppe on 12/10/16.
 */

public class Injection {
    public static GroupMembersRepository provideGroupRepository(@NonNull Context context) {
        Preconditions.checkNotNull(context);
        return GroupMembersRepository.getInstance(new FirebaseSlaveDataSource(), new EddystoneDeviceRepository(), context);
    }

}
