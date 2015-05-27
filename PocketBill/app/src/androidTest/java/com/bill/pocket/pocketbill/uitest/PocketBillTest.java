package com.bill.pocket.pocketbill.uitest;

import android.test.ActivityInstrumentationTestCase2;
import android.widget.ListView;

import com.bill.pocket.pocketbill.MainActivity;
import com.bill.pocket.pocketbill.R;
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
        mySolo.enterText(editTextID, testText);
        assertEquals(mySolo.getEditText(editTextID).getText().toString(), testText);
    }

    public void testDisplayValues(){
        mySolo.clickOnText("Gas");

        int screenWidth = getActivity().getWindowManager().getDefaultDisplay().getWidth();
        int screenHeight = getActivity().getWindowManager().getDefaultDisplay().getHeight();
        int fromX, toX, fromY, toY = 0;
        fromX = screenWidth -5;
        toX = 0;
        fromY = (screenHeight/2);
        toY = (screenHeight/2);

        mySolo.sleep(2000);
        
        mySolo.drag(fromX, toX, fromY, toY, 50);

        mySolo.sleep(2000);
    }

    public void testMainCategoryDelete() {
        ListView myList = (ListView) mySolo.getView(R.id.CategoryView);
        int count = myList.getAdapter().getCount();
        if (count < 1) return;

        mySolo.clickLongInList(0);
        mySolo.clickOnText("Delete");
        mySolo.sleep(2000);

        assertEquals(myList.getAdapter().getCount() + 1, count);
    }

    public void testSubCategoryDelete() {
        ListView myList = (ListView) mySolo.getView(R.id.CategoryView);
        if (myList.getAdapter().getCount() < 1) return;

        mySolo.clickInList(0);
        mySolo.sleep(2000);
        int count = myList.getAdapter().getCount();
        if (count < 1) return;
        mySolo.clickLongInList(0);

        mySolo.clickOnText("Delete");
        mySolo.sleep(2000);

        assertEquals(myList.getAdapter().getCount() + 1, count);
    }

<<<<<<< HEAD
    public void testSearchButton() {
        mySolo.clickOnActionBarItem(R.id.searchButton);
        mySolo.sleep(5999);
    }
=======
    public void testAddTags()
    {
        ListView myList = (ListView) mySolo.getView(R.id.CategoryView);
        if (myList.getAdapter().getCount() < 1) return;

        mySolo.clickInList(0);
        mySolo.sleep(500);

        int count = myList.getAdapter().getCount();
        if (count < 1) return;
        mySolo.clickInList(0);

        mySolo.enterText(0, "Family");

        assertEquals(true,true);
    }

>>>>>>> [FNWR] Added Tags in the Activity
}