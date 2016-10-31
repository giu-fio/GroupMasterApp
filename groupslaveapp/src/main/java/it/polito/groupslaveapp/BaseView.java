package it.polito.groupslaveapp;

/**
 * Created by giuseppe on 12/10/16.
 */

public interface BaseView<T extends BasePresenter> {
    void setPresenter(T presenter);
}
