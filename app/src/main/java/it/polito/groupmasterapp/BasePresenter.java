package it.polito.groupmasterapp;

import android.os.Bundle;

/**
 * Created by giuseppe on 12/10/16.
 */

public interface BasePresenter extends LifecycleInterface {

    void onSaveInstanceState(Bundle outState);

    void restoreSavedInstanceState(Bundle savedInstanceState);

}
