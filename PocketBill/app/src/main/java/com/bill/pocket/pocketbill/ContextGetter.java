package com.bill.pocket.pocketbill;


import android.app.Application;
import android.content.Context;

//Debugging Class! Provides the Application Context for Database tests
//This Class should not be used except in JUnit Tests!!!!!

public class ContextGetter extends Application
{
    private static Context sContext;

    @Override
    public void onCreate()
    {
        super.onCreate();
        sContext = getApplicationContext();
    }
    public static Context getContext()
    {
        return sContext;
    }
}