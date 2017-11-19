package com.ahanapharmacy.app;

import android.test.ApplicationTestCase;
import android.test.suitebuilder.annotation.MediumTest;
import android.test.suitebuilder.annotation.SmallTest;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<App> {

    public final String TAG = this.getClass().getSimpleName();

    public ApplicationTest() {
        super(App.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @MediumTest
    public void testCreate() {
        createApplication();
    }

    @SmallTest
    public void testPrintUrl() {
        createApplication();
      String key = App.getFirebase().child("/users").getKey();
        System.out.println("Key: " + key);

    }
}