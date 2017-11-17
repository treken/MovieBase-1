package com.defaultapps.moviebase.ui.base;


public interface MvpPresenter<V extends MvpView> {
    void onAttach(V view);
    void onDetach();
    void disposeUseCaseCalls();
}
