package com.defaultapps.moviebase.utils;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.SingleTransformer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Created on 6/22/2017.
 */

public class ResponseOrError<T> {
    @Nullable
    private final T data;
    @Nullable
    private final String message;

    private ResponseOrError(@Nullable T data, @Nullable String message) {
        this.data = data;
        this.message = message;
    }

    public static <T> ResponseOrError<T> fromError(@NonNull String message) {
        return new ResponseOrError(null, message);
    }

    public static <T> ResponseOrError<T> fromData(@NonNull T data) {
        return new ResponseOrError(data, null);
    }

    @NonNull
    public static <T> ObservableTransformer<T, ResponseOrError<T>> toResponseOrErrorObservable() {
        return ResponseOrError::toResponseOrErrorObservable;
    }

    @NonNull
    private static <T> Observable<ResponseOrError<T>> toResponseOrErrorObservable(@NonNull Observable<T> observable) {
        return observable
                .map(ResponseOrError::fromData)
                .onErrorResumeNext(new Function<Throwable, ObservableSource<? extends ResponseOrError<T>>>() {
                    @Override
                    public ObservableSource<? extends ResponseOrError<T>> apply(@io.reactivex.annotations.NonNull Throwable throwable) throws Exception {
                        return Observable.just(ResponseOrError.fromError(throwable.getMessage()));
                    }
                });
    }

    public boolean isData() {
        return this.data != null;
    }

    public boolean isError() {
        return !this.isData();
    }
}