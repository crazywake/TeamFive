package com.bill.pocket.pocketbill.test;

import com.bill.pocket.pocketbill.ContextGetter;
import com.bill.pocket.pocketbill.DAO;
import junit.framework.TestCase;

import java.util.ArrayList;

public class DatabaseTest extends TestCase {

    DAO myDAO = null;

    public void testSelectMainCat()
    {
        myDAO = DAO.instance(ContextGetter.getContext());
        myDAO.insertMainCat("Hofer");
        myDAO.insertMainCat("ShEll");
        myDAO.insertMainCat("biLla");
        ArrayList<String> return_values = myDAO.getMainCats();

        boolean asserting = false;
        if (return_values.contains("HOFER") && return_values.contains("SHELL") && return_values.contains("BILLA"))
            asserting = true;

        assertEquals (true, asserting);
        asserting = false;

        myDAO.deleteMainCat("Hofer");
        myDAO.deleteMainCat("Billa");
        myDAO.deleteMainCat("Shell");
        return_values = myDAO.getMainCats();

        if (return_values.size() == 0) asserting = true;

        assertEquals(true,asserting);
    }
}
