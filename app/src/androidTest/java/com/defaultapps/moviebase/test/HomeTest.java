package com.defaultapps.moviebase.test;

import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.defaultapps.moviebase.screen.HomeScreen;
import com.defaultapps.moviebase.ui.main.MainActivity;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withId;


@RunWith(AndroidJUnit4.class)
@LargeTest
public class HomeTest {

    @Rule
    public ActivityTestRule testRule = new ActivityTestRule<>(MainActivity.class);

    private HomeScreen homeScreen;

    @Before
    public void setup() {
        homeScreen = new HomeScreen();
    }

//    @Test
//    public void shouldDisplayToolbarText()  {
//        homeScreen.hasToolbarTitle("Home");
//    }
//
//    @Test
//    public void shouldHaveNowPlayingItems() {
//        homeScreen.hasNowPlayingItems();
//    }
//
//    @Test
//    public void shouldHaveUpcomingItems() {
//        homeScreen.hasUpcomingItems();
//    }
    @Test
    public void clickRecycler() {
        onView(withId(com.defaultapps.moviebase.R.id.upcomingRecycler)).perform(RecyclerViewActions.actionOnItemAtPosition(6, click()));

    }
}
