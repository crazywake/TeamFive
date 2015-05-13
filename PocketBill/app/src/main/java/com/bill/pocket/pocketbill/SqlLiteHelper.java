package com.bill.pocket.pocketbill;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SqlLiteHelper extends SQLiteOpenHelper {

    private static final String SQL_NAME = "Pocket.db";
    public static final String TBL_MAIN_CAT = "TBL_MAIN_CAT";
    public static final String COL_ID_MAIN_CAT = "ID_MAIN";
    public static final String COL_NAME_MAIN_CAT = "NAME";
    public static final String TBL_PAYMENT = "TBL_PAYMENT";
    public static final String COL_ID_PAYMENT = "COL_ID_PAYMENT";
    public static final String COL_VALUE_PAYMENT = "VALUE";
    public static final String COL_DATE_PAYMENT = "DATE";
    public static final String COL_FK_MAIN_CAT_PAYMENT = "COL_FK_MAIN_CAT_ID";
    public static final String COL_FK_SUB_CAT_PAYMENT = "SUB_CAT_ID";
    public static final String COL_NAME_SUB_CAT = "NAME";
    public static final String COL_ID_SUB_CAT = "ID_SUB";
    public static final String TBL_SUB_CAT = "TBL_SUB_CAT";
    public static final String COL_FK_MAIN_CAT_ID = "COL_FK_MAIN_CAT_ID";


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
        assert(false);
        mySQL.execSQL("DROP TABLE IF EXISTS ");
        onCreate(mySQL);
    }

}
