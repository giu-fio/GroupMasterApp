package it.polito.groupmasterapp;

import android.app.Activity;

/**
 * Created by giuseppe on 12/10/16.
 */

public interface BaseView<T extends BasePresenter> {
    void setPresenter(T presenter);

    Activity getActivity();

}
