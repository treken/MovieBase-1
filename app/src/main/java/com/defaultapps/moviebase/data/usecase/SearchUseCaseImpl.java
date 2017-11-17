package com.defaultapps.moviebase.data.usecase;

import com.defaultapps.moviebase.BuildConfig;
import com.defaultapps.moviebase.data.SchedulerProvider;
import com.defaultapps.moviebase.data.base.BaseUseCase;
import com.defaultapps.moviebase.data.local.AppPreferencesManager;
import com.defaultapps.moviebase.data.models.responses.movies.MoviesResponse;
import com.defaultapps.moviebase.data.network.NetworkService;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.disposables.Disposable;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.PublishSubject;

@Singleton
public class SearchUseCaseImpl extends BaseUseCase implements SearchUseCase {

    private final NetworkService networkService;
    private final AppPreferencesManager preferencesManager;
    private final SchedulerProvider schedulerProvider;

    private Disposable disposable;
    private Disposable paginationDisposable;
    private BehaviorSubject<MoviesResponse> behaviorSubject;

    @Inject
    SearchUseCaseImpl(NetworkService networkService,
                             SchedulerProvider schedulerProvider,
                             AppPreferencesManager preferencesManager) {
        this.networkService = networkService;
        this.schedulerProvider = schedulerProvider;
        this.preferencesManager = preferencesManager;
    }

    @Override
    public Observable<MoviesResponse> requestSearchResults(String query, boolean force) {
        if (force && disposable != null) disposable.dispose();
        if (disposable == null || disposable.isDisposed()) {
            behaviorSubject = BehaviorSubject.create();

            disposable = network(query, 1)
                    .compose(schedulerProvider.applyIoSchedulers())
                    .subscribe(behaviorSubject::onNext, behaviorSubject::onError);
            getCompositeDisposable().add(disposable);
        }
        return behaviorSubject;
    }

    @Override
    public Observable<MoviesResponse> requestMoreSearchResults(String query) {
        if (paginationDisposable != null && !paginationDisposable.isDisposed()) {
            paginationDisposable.dispose();
        }
        MoviesResponse previousResult = behaviorSubject.getValue();
        PublishSubject<MoviesResponse> paginationResult = PublishSubject.create();
        paginationDisposable = network(query, previousResult.getPage() + 1)
                .map(moviesResponse -> {
                    previousResult.getResults().addAll(moviesResponse.getResults());
                    previousResult.setPage(moviesResponse.getPage());
                    return previousResult;
                })
                .compose(schedulerProvider.applyIoSchedulers())
                .subscribe(
                        response -> {
                            paginationResult.onNext(response);
                            behaviorSubject = BehaviorSubject.create();
                            behaviorSubject.onNext(response);
                        },
                        paginationResult::onError
                );
        getCompositeDisposable().add(paginationDisposable);
        return paginationResult;
    }

    private Single<MoviesResponse> network(String query, int page) {
        final String API_KEY = BuildConfig.MDB_API_KEY;
        return networkService.getNetworkCall()
                .getSearchQuery(API_KEY, "en-Us", query, page, preferencesManager.getAdultStatus());
    }
}
