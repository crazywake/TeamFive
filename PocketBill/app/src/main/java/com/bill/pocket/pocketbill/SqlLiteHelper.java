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


    private static final String CREATE_MAIN_CAT = "CREATE TABLE " + TBL_MAIN_CAT + " ( " +
            COL_ID_MAIN_CAT + " integer primary key, " +
            COL_NAME_MAIN_CAT + " text not NULL" + " );" ;

    private static final String CREATE_PAYMENT = "CREATE TABLE " + TBL_PAYMENT + " ( " +
            COL_ID_PAYMENT + " integer primary key , " +
            COL_VALUE_PAYMENT + " integer, " +
            COL_DATE_PAYMENT + " integer, " +
            COL_FK_MAIN_CAT_PAYMENT + " integer NOT NULL REFERENCES " + TBL_MAIN_CAT + "(" + COL_ID_MAIN_CAT + ") ON DELETE CASCADE, " +
            COL_FK_SUB_CAT_PAYMENT + " integer REFERENCES "+ TBL_SUB_CAT + "(" + COL_ID_SUB_CAT + ") ON DELETE CASCADE);" ;

    private static final String CREATE_SUB_CAT = "CREATE TABLE " + TBL_SUB_CAT + " ( " +
            COL_ID_SUB_CAT + " integer primary key , " +
            COL_NAME_SUB_CAT + " text NOT NULL, " +
            COL_FK_MAIN_CAT_ID + " integer REFERENCES " + TBL_MAIN_CAT + "(" + COL_ID_MAIN_CAT + ") ON DELETE CASCADE);" ;



    public SqlLiteHelper(Context context)
    {
        super(context, SQL_NAME,null,1);
    }

    public void onOpen(SQLiteDatabase mySQL)
    {
        super.onOpen(mySQL);
        if (!mySQL.isReadOnly()) {
            mySQL.execSQL("PRAGMA foreign_keys=ON;");
        }
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
