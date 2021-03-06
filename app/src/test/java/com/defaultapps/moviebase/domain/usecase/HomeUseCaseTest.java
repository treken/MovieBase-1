package com.defaultapps.moviebase.domain.usecase;

import com.defaultapps.moviebase.data.models.responses.movies.MoviesResponse;
import com.defaultapps.moviebase.domain.repository.ApiRepository;
import com.defaultapps.moviebase.utils.AppConstants;
import com.defaultapps.moviebase.utils.rx.RxBus;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import io.github.benas.randombeans.api.EnhancedRandom;
import io.reactivex.Single;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.TestScheduler;
import io.reactivex.subjects.BehaviorSubject;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


public class HomeUseCaseTest {

    @Mock
    private ApiRepository apiRepository;

    @Mock
    private RxBus rxBus;

    private HomeUseCase homeUseCase;
    private TestScheduler testScheduler;

    private List<MoviesResponse> actualResponse;
    private Throwable expectedThrowable;

    private static final int RESPONSE_LIST_SIZE = 4;

    @Before
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);
        testScheduler = new TestScheduler();
        homeUseCase = new HomeUseCaseImpl(apiRepository, rxBus);
    }

    @Test
    public void fetchHomePageDataSuccess() throws Exception {
        List<MoviesResponse> response =
                EnhancedRandom.randomListOf(RESPONSE_LIST_SIZE, MoviesResponse.class);

        Single<List<MoviesResponse>> single = Single.just(response);

        when(apiRepository.requestHomeData())
                .thenReturn(single);

        homeUseCase.requestHomeData(true).subscribe(moviesResponses -> actualResponse = moviesResponses);

        testScheduler.triggerActions();

        assertNotNull(actualResponse);
        assertEquals(response, actualResponse);
    }

    @Test
    public void fetchHomePageDataFailure() throws Exception {
        Exception exception = new Exception("Network error.");
        Single<List<MoviesResponse>> single = Single.error(exception);

        when(apiRepository.requestHomeData())
                .thenReturn(single);

        homeUseCase.requestHomeData(true).subscribe(moviesResponses -> {}, err -> expectedThrowable = err);

        testScheduler.triggerActions();
        assertNotNull(expectedThrowable);
        assertEquals(exception, expectedThrowable);
    }

    @Test
    public void shouldReturnInstantCache() throws Exception {
        Field cache = HomeUseCaseImpl.class.getDeclaredField("cache");
        Field behaviorSubject = HomeUseCaseImpl.class.getDeclaredField("moviesBehaviorSubject");
        cache.setAccessible(true);
        behaviorSubject.setAccessible(true);

        BehaviorSubject<List<MoviesResponse>> fakeReplaySubject = BehaviorSubject.create();
        List<MoviesResponse> fakeCache = new ArrayList<>();
        cache.set(homeUseCase, fakeCache);
        behaviorSubject.set(homeUseCase, fakeReplaySubject);

        setupEmptyNetworkCalls();

        homeUseCase.requestHomeData(false);

        verify(rxBus).publish(AppConstants.HOME_INSTANT_CACHE, fakeCache);
    }

    @Test
    public void shouldDisposeOnForceCall() throws Exception {
        Field disposable = HomeUseCaseImpl.class.getDeclaredField("moviesDisposable");
        disposable.setAccessible(true);

        final Disposable mockDisposable = mock(Disposable.class);
        disposable.set(homeUseCase, mockDisposable);

        setupEmptyNetworkCalls();

        homeUseCase.requestHomeData(true);

        verify(mockDisposable).dispose();
    }

    @Test
    public void shouldReturnInstantCacheOnError() throws Exception {
        Field cache = HomeUseCaseImpl.class.getDeclaredField("cache");
        Field behaviorSubject = HomeUseCaseImpl.class.getDeclaredField("moviesBehaviorSubject");
        cache.setAccessible(true);
        behaviorSubject.setAccessible(true);

        final List<MoviesResponse> fakeCache = new ArrayList<>();
        final BehaviorSubject<List<MoviesResponse>> fakeReplaySubject = BehaviorSubject.create();
        fakeReplaySubject.onError(new Throwable("Error"));

        cache.set(homeUseCase, fakeCache);
        behaviorSubject.set(homeUseCase, fakeReplaySubject);

        setupEmptyNetworkCalls();

        homeUseCase.requestHomeData(false)
                .test()
                .assertComplete();

        verify(rxBus).publish(AppConstants.HOME_INSTANT_CACHE, fakeCache);
    }

    @Test
    public void shouldDisposeOnEmptyCacheAndError() throws Exception {
        Field cache = HomeUseCaseImpl.class.getDeclaredField("cache");
        Field behaviorSubject = HomeUseCaseImpl.class.getDeclaredField("moviesBehaviorSubject");
        Field movieDisposableField = HomeUseCaseImpl.class.getDeclaredField("moviesDisposable");
        cache.setAccessible(true);
        behaviorSubject.setAccessible(true);
        movieDisposableField.setAccessible(true);

        final Disposable movieDisposable = mock(Disposable.class);
        final BehaviorSubject<List<MoviesResponse>> fakeReplaySubject = BehaviorSubject.create();
        fakeReplaySubject.onError(new Throwable("Error"));

        cache.set(homeUseCase, null);
        behaviorSubject.set(homeUseCase, fakeReplaySubject);
        movieDisposableField.set(homeUseCase, movieDisposable);

        setupEmptyNetworkCalls();

        homeUseCase.requestHomeData(false)
                .test()
                .assertError(Throwable.class);

        verify(movieDisposable).dispose();
    }

    private void setupEmptyNetworkCalls() {
        when(apiRepository.requestHomeData())
                .thenReturn(Single.just(new ArrayList<>()));
    }
}
