package it.polito.groupmasterapp.util;

/**
 * Created by giuseppe on 13/10/16.
 */

public interface OnCompleteListener<T> {
    public void onComplete(T result, boolean success);
}
