package com.defaultapps.moviebase.ui.discover;


import com.defaultapps.moviebase.di.scope.PerFragment;
import com.defaultapps.moviebase.domain.usecase.DiscoverUseCase;
import com.defaultapps.moviebase.ui.base.BasePresenter;

import javax.inject.Inject;

import timber.log.Timber;


@PerFragment
public class DiscoverPresenterImpl extends BasePresenter<DiscoverContract.DiscoverView> implements DiscoverContract.DiscoverPresenter {

    private final DiscoverUseCase discoverUseCase;

    @Inject
    DiscoverPresenterImpl(DiscoverUseCase discoverUseCase) {
        super(discoverUseCase);
        this.discoverUseCase = discoverUseCase;
    }

    @Override
    public void requestData() {
        getCompositeDisposable().add(
                discoverUseCase.provideGenresList()
                .subscribe(
                        genres -> getView().showData(genres),
                        Timber::e
                )
        );
    }
}
