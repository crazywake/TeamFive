package com.bill.pocket.pocketbill.uitest;

import android.test.ActivityInstrumentationTestCase2;

import com.bill.pocket.pocketbill.MainActivity;
import com.robotium.solo.Solo;

public class PocketBillTest extends ActivityInstrumentationTestCase2<MainActivity> {

    private Solo mySolo;

    public PocketBillTest(){
        super(MainActivity.class);
    }

    public void setUp() throws Exception {
        super.setUp();
        mySolo = new Solo ( getInstrumentation (), getActivity ());
    }

    public void tearDown() throws Exception {

    }

    public void testCategoryList(){
        mySolo.clickOnText("Gas");
        mySolo.clickOnText("Shell");
    }
}