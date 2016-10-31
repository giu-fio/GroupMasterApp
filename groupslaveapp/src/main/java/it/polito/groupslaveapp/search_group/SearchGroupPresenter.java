package it.polito.groupslaveapp.search_group;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;

import com.google.common.base.Strings;

import it.polito.groupslaveapp.data.source.DataSource;

import static android.content.Context.MODE_PRIVATE;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by giuseppe on 14/10/16.
 */

public class SearchGroupPresenter implements SearchGroupContract.Presenter {

    public static final String PREFS_NAME = "it.polito.groupmasterapp.APP_PREFERENCES";
    public static final String DEVICE_NAME_KEY = "it.polito.groupmasterapp.DEVICE_NAME_KEY";

    public static final String DEVICE_NAME_DEFAULT = Build.MODEL;

    private DataSource mDataSource;
    private SearchGroupContract.View mView;
    private Context mContext;
    private String deviceName;

    public SearchGroupPresenter(DataSource mDataSource, SearchGroupContract.View mView, Context mContext) {
        this.mDataSource = mDataSource;
        this.mView = mView;
        this.mContext = mContext;
        mView.setPresenter(this);
    }

    @Override
    public void deviceNameChanged(String name) {
        deviceName = name;
        mView.buttonEnabled(!Strings.isNullOrEmpty(deviceName));
    }

    @Override
    public void searchClick() {
        checkNotNull(deviceName);
        mDataSource.saveSlave(deviceName);

        //salvataggio delle modifiche
        mContext.getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit()
                .putString(DEVICE_NAME_KEY, deviceName)
                .apply();

        mView.showLoading(true);

        mView.navigateToJoinGroup(deviceName);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

    }

    @Override
    public void restoreSavedInstanceState(Bundle savedInstanceState) {

    }

    @Override
    public void start() {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        deviceName = sharedPreferences.getString(DEVICE_NAME_KEY, DEVICE_NAME_DEFAULT);
        mView.initDeviceName(deviceName);
        mView.buttonEnabled(!Strings.isNullOrEmpty(deviceName));
    }

    @Override
    public void stop() {

    }
}
