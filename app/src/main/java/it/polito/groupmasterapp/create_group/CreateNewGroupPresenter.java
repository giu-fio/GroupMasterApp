package it.polito.groupmasterapp.create_group;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;

import com.google.common.base.Strings;

import it.polito.groupmasterapp.data.Group;
import it.polito.groupmasterapp.data.source.DevicesDataSource;
import it.polito.groupmasterapp.util.OnCompleteListener;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by giuseppe on 13/10/16.
 */

public class CreateNewGroupPresenter implements CreateNewGroupContract.Presenter {

    public static final String PREFS_NAME = "it.polito.groupmasterapp.APP_PREFERENCES";
    public static final String GROUP_NAME_KEY = "it.polito.groupmasterapp.GROUP_NAME_KEY";
    public static final String DEVICE_NAME_KEY = "it.polito.groupmasterapp.DEVICE_NAME_KEY";


    public static final String GROUP_NAME_DEFAULT = "Group1";
    public static final String DEVICE_NAME_DEFAULT = Build.MODEL;

    private DevicesDataSource mDataSource;
    private CreateNewGroupContract.View mView;
    private Context mContext;

    private String groupName;
    private String deviceName;

    public CreateNewGroupPresenter(CreateNewGroupContract.View view, DevicesDataSource dataSource, Context context) {
        this.mView = view;
        this.mDataSource = dataSource;
        this.mContext = context;
        view.setPresenter(this);
    }

    @Override
    public void groupNameChanged(String name) {
        groupName = name;
        //il bottone è disabilitato se groupName o deviceName sono null o vuoti
        mView.buttonEnabled(!checkNullOrEmptyFields());
    }


    @Override
    public void deviceNameChanged(String name) {
        deviceName = name;
        //il bottone è disabilitato se groupName o deviceName sono null o vuoti
        mView.buttonEnabled(!checkNullOrEmptyFields());
    }

    @Override
    public void createClick() {

        //salvataggio delle modifiche
        mContext.getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit()
                .putString(DEVICE_NAME_KEY, deviceName)
                .putString(GROUP_NAME_KEY, groupName)
                .apply();

        mView.showLoading(true);
        mDataSource.createGroup(deviceName, groupName, new OnCompleteListener<Group>() {
            @Override
            public void onComplete(Group result, boolean success) {
                if (success) {
                    mView.navigateToAddMembers(deviceName, result);
                } else {
                    mView.showCreateErrorMessage();
                }
            }
        });

    }

    @Override
    public void start() {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        groupName = sharedPreferences.getString(GROUP_NAME_KEY, GROUP_NAME_DEFAULT);
        deviceName = sharedPreferences.getString(DEVICE_NAME_KEY, DEVICE_NAME_DEFAULT);
        mView.initGroupName(groupName);
        mView.initDeviceName(deviceName);
        //il bottone è disabilitato se groupName o deviceName sono null o vuoti
        mView.buttonEnabled(!checkNullOrEmptyFields());
    }

    @Override
    public void stop() {

    }

    private boolean checkNullOrEmptyFields() {
        return Strings.isNullOrEmpty(deviceName) || Strings.isNullOrEmpty(groupName);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
    }

    @Override
    public void restoreSavedInstanceState(Bundle savedInstanceState) {
    }
}
