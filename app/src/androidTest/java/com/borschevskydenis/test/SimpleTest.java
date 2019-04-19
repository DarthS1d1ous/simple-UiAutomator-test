package com.borschevskydenis.test;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Looper;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Collection;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SdkSuppress;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.lifecycle.ActivityLifecycleMonitor;
import androidx.test.runner.lifecycle.ActivityLifecycleMonitorRegistry;
import androidx.test.runner.lifecycle.Stage;
import androidx.test.uiautomator.By;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.Until;

import static androidx.test.core.app.ApplicationProvider.getApplicationContext;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;

import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;

@RunWith(AndroidJUnit4.class)
@SdkSuppress(minSdkVersion = 18)
public class SimpleTest {

    private static final String BASIC_SAMPLE_PACKAGE
            = "com.borschevskydenis.test";

    private static final int LAUNCH_TIMEOUT = 5000;

    private static final String CORRECT_STRING_LOGIN = "test";
    private static final String CORRECT_STRING_PASSWORD = "test";
    private static final String INCORRECT_STRING_LOGIN = "qwer";
    private static final String INCORRECT_STRING_PASSWORD = "qwer";

    private static final String NAME_MAIN_ACTIVITY = "MainActivity";
    private static final String NAME_SECOND_ACTIVITY = "SecondActivity";

    private UiDevice mDevice;


//    @Rule
//    public ActivityTestRule<MainActivity> activityActivityTestRule = new ActivityTestRule<>(MainActivity.class);


    @Before
    public void startMainActivityFromHomeScreen() {

        mDevice = UiDevice.getInstance(getInstrumentation());

        mDevice.pressHome();

        final String launcherPackage = getLauncherPackageName();
        assertThat(launcherPackage, notNullValue());
        mDevice.wait(Until.hasObject(By.pkg(launcherPackage).depth(0)), LAUNCH_TIMEOUT);

        Context context = getApplicationContext();
        final Intent intent = context.getPackageManager()
                .getLaunchIntentForPackage(BASIC_SAMPLE_PACKAGE);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);

        mDevice.wait(Until.hasObject(By.pkg(BASIC_SAMPLE_PACKAGE).depth(0)), LAUNCH_TIMEOUT);

    }

    @Test
    public void checkPreconditions() {
        assertThat(mDevice, notNullValue());
    }

    @Test
    public void checkTrueLogIn() {
        mDevice.findObject(By.res(BASIC_SAMPLE_PACKAGE, "etLogin"))
                .setText(CORRECT_STRING_LOGIN);
        mDevice.findObject(By.res(BASIC_SAMPLE_PACKAGE, "etPassword")).
                setText(CORRECT_STRING_PASSWORD);
        mDevice.findObject(By.res(BASIC_SAMPLE_PACKAGE, "btnSubmit")).
                click();
        mDevice.wait(Until.hasObject(By.pkg(BASIC_SAMPLE_PACKAGE).depth(0)), LAUNCH_TIMEOUT);
        Activity current = getCurrentActivity();
        assertThat(current.getLocalClassName(), is(equalTo(NAME_SECOND_ACTIVITY)));
    }

    @Test
    public void checkFalseLogIn() {
        mDevice.findObject(By.res(BASIC_SAMPLE_PACKAGE, "etLogin"))
                .setText(INCORRECT_STRING_LOGIN);
        mDevice.findObject(By.res(BASIC_SAMPLE_PACKAGE, "etPassword")).
                setText(INCORRECT_STRING_PASSWORD);
        mDevice.findObject(By.res(BASIC_SAMPLE_PACKAGE, "btnSubmit")).
                click();
        mDevice.wait(Until.hasObject(By.pkg(BASIC_SAMPLE_PACKAGE).depth(0)), LAUNCH_TIMEOUT);
        Activity current = getCurrentActivity();
        assertThat(current.getLocalClassName(), is(equalTo(NAME_MAIN_ACTIVITY)));
    }

    @Test
    public void checkTrueLogInAndReturnToMainActivity() {
        mDevice.findObject(By.res(BASIC_SAMPLE_PACKAGE, "etLogin"))
                .setText(CORRECT_STRING_LOGIN);
        mDevice.findObject(By.res(BASIC_SAMPLE_PACKAGE, "etPassword")).
                setText(CORRECT_STRING_PASSWORD);
        mDevice.findObject(By.res(BASIC_SAMPLE_PACKAGE, "btnSubmit")).
                click();
        mDevice.wait(Until.hasObject(By.pkg(BASIC_SAMPLE_PACKAGE).depth(0)), LAUNCH_TIMEOUT);
        mDevice.findObject(By.res(BASIC_SAMPLE_PACKAGE, "btnBack")).
                click();
        mDevice.wait(Until.hasObject(By.pkg(BASIC_SAMPLE_PACKAGE).depth(0)), LAUNCH_TIMEOUT);
        Activity current = getCurrentActivity();
        assertThat(current.getLocalClassName(), is(equalTo(NAME_MAIN_ACTIVITY)));
    }


    private String getLauncherPackageName() {

        final Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);

        PackageManager pm = getApplicationContext().getPackageManager();
        ResolveInfo resolveInfo = pm.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY);
        return resolveInfo.activityInfo.packageName;
    }

    public static Activity getCurrentActivity() {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            return getCurrentActivityOnMainThread();
        } else {
            final Activity[] topActivity = new Activity[1];
            getInstrumentation().runOnMainSync(new Runnable() {
                @Override
                public void run() {
                    topActivity[0] = getCurrentActivityOnMainThread();
                }
            });
            return topActivity[0];
        }
    }

    private static Activity getCurrentActivityOnMainThread() {
        ActivityLifecycleMonitor registry = ActivityLifecycleMonitorRegistry.getInstance();
        Collection<Activity> activities = registry.getActivitiesInStage(Stage.RESUMED);
        return activities.iterator().hasNext() ? activities.iterator().next() : null;
    }
}
