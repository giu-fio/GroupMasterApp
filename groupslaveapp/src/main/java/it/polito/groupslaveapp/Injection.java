package it.polito.groupslaveapp;

import android.content.Context;
import android.support.annotation.NonNull;

import com.google.common.base.Preconditions;

import it.polito.groupslaveapp.data.GroupRepository;
import it.polito.groupslaveapp.data.source.FirebaseGroupDataSource;

/**
 * Created by giuseppe on 12/10/16.
 */

public class Injection {
    public static GroupRepository provideGroupRepository(@NonNull Context context) {
        Preconditions.checkNotNull(context);
        return GroupRepository.getInstance(new FirebaseGroupDataSource());
    }
}
