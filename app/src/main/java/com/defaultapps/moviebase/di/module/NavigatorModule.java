package com.defaultapps.moviebase.di.module;

import com.defaultapps.moviebase.ui.login.LoginContract;
import com.defaultapps.moviebase.ui.login.LoginNavigatorImpl;
import com.defaultapps.moviebase.ui.movie.MovieContract;
import com.defaultapps.moviebase.ui.movie.MovieNavigatorImpl;
import com.defaultapps.moviebase.ui.user.UserContract;
import com.defaultapps.moviebase.ui.user.UserNavigatorImpl;

import dagger.Binds;
import dagger.Module;


@Module
@SuppressWarnings("unused")
public abstract class NavigatorModule {

    @Binds
    abstract LoginContract.LoginNavigator bindLoginNavigator(LoginNavigatorImpl loginNavigator);

    @Binds
    abstract MovieContract.MovieNavigator bindMovieNavigator(MovieNavigatorImpl movieNavigator);

    @Binds
    abstract UserContract.UserNavigator bindUserNavigator(UserNavigatorImpl userNavigator);
}
