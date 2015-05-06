package com.bill.pocket.pocketbill.uitest;

import android.test.ActivityInstrumentationTestCase2;
import android.view.Display;

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
    public void testActionBar(){
        //mySolo.click
       // mySolo.clickOnActionBarItem(R.id.addCategory);
        //mySolo.sleep(2000);
       //mySolo.clickOnButton("Cancel");

    }
    public void testCategoryListSelect(){
        mySolo.clickOnText("Gas");
        mySolo.clickOnText("Shell");
        mySolo.sleep(2000);
    }

    public void testCategoryDelete(){
        mySolo.clickOnText("Gas");
        mySolo.clickLongOnText("Shell");
        mySolo.clickOnText("Delete");
        mySolo.sleep(2000);
    }

    public void testCategoryBack(){
        mySolo.clickOnText("Gas");
        mySolo.clickLongOnText("Shell");
        mySolo.goBack();
        mySolo.goBack();
        mySolo.sleep(2000);
    }

    public void testAddValueGUI(){
        int editTextID = 0;
        String testText = "12345";
        mySolo.clickOnText("Gas");
        mySolo.clickOnText("Shell");
        mySolo.clickOnEditText(editTextID);
        mySolo.enterText(editTextID,testText);
        assertEquals(mySolo.getEditText(editTextID).getText().toString(),testText);

    }

    public void testNavigationDrawer(){
        mySolo.clickOnText("Gas");
        swipeToRight();
        mySolo.clickOnText("Pocket");
        mySolo.clickOnText("Gas");
    }

    private void swipeToRight() {
        Display display = mySolo.getCurrentActivity().getWindowManager().getDefaultDisplay();
        int width = display.getWidth();
        int height = display.getHeight();
        float xStart = 0 ;
        float xEnd = width / 2;
        mySolo.drag(xStart, xEnd, height / 2, height / 2, 1);
    }

}