package com.example.debtbuddies;

import com.example.debtbuddies.LoginScreenActivity;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;


import androidx.test.filters.LargeTest;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;
//import androidx.test.rule.ActivityTestRule;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.core.StringEndsWith.endsWith;


import android.support.test.rule.ActivityTestRule;
import android.util.Log;
@RunWith(AndroidJUnit4ClassRunner.class)
@LargeTest   // large execution time

public class MenuTest {
    private static final int SIMULATED_DELAY_MS = 500;

    @Rule   // needed to launch the activity
    public ActivityTestRule<Menu> activityRule = new ActivityTestRule<>(Menu.class);

    @Test
    public void Menu() {
        onView(withId(R.id.b_main_menu)).perform(click());
    }


}