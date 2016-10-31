package it.polito.groupmasterapp.util;

import android.text.Editable;
import android.text.TextWatcher;

/**
 * Created by giuseppe on 13/10/16.
 */

public abstract class AbstractTextWatcher implements TextWatcher {
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}
