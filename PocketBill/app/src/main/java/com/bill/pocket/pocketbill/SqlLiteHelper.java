package com.bill.pocket.pocketbill;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SqlLiteHelper extends SQLiteOpenHelper {

    private static final String SQL_NAME = "Pocket.db";
    public static final String MAIN_CAT = "MAIN_CAT";
    public static final String ID_MAIN_CAT = "ID";
    public static final String NAME_MAIN_CAT = "NAME";
    public static final String PAYMENT = "PAYMENT";
    public static final String ID_PAYMENT = "ID";
    public static final String VALUE_PAYMENT = "VALUE";
    public static final String DATE_PAYMENT = "DATE";
    public static final String MAIN_CAT_PAYMENT = "MAIN_CAT_ID";
    public static final String SUB_CAT_PAYMENT = "SUB_CAT_ID";
    public static final String NAME_SUB_CAT = "NAME";
    public static final String ID_SUB_CAT = "ID";
    public static final String SUB_CAT = "SUB_CAT";
    public static final String MAIN_CAT_ID = "MAIN_CAT_ID";


    private static final String CREATE_MAIN_CAT = "CREATE TABLE " + MAIN_CAT + " ( " +
            ID_MAIN_CAT + " integer primary key, " +
            NAME_MAIN_CAT + " text not NULL" + " );" ;

    private static final String CREATE_PAYMENT = "CREATE TABLE " + PAYMENT + " ( " +
    ID_PAYMENT + " integer primary key , " +
    VALUE_PAYMENT + " integer, " +
    DATE_PAYMENT + " integer, " +
    MAIN_CAT_PAYMENT + " integer, " +
    SUB_CAT_PAYMENT + " integer " + ");" ;

    private static final String CREATE_SUB_CAT = "CREATE TABLE " + SUB_CAT + " ( " +
            ID_SUB_CAT + " integer primary key , " +
            NAME_SUB_CAT + " text not NULL, " +
            MAIN_CAT_ID + " integer " + ");" ;



    public SqlLiteHelper(Context context)
    {
        super(context, SQL_NAME,null,1);
    }

    @Override
    public void onCreate(SQLiteDatabase mySQL)
    {
        mySQL.execSQL(CREATE_MAIN_CAT);
        mySQL.execSQL(CREATE_SUB_CAT);
        mySQL.execSQL(CREATE_PAYMENT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase mySQL, int oldversion, int newversion)
    {
        assert (false); //Should not happen at this time! This method is invoke if we Update the app!
        mySQL.execSQL("DROP TABLE IF EXISTS ");
        onCreate(mySQL);
    }

}
