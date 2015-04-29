package com.bill.pocket.pocketbill;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;


public class DAO {

    private static DAO my_dao = null;

    private static SqlLiteHelper  my_helper;
    private SQLiteDatabase my_db;

    private DAO()
    {}

    public static DAO instance(Context context)
    {
        if (my_dao == null)
        {
            my_dao = new DAO();
            my_helper = new SqlLiteHelper(context);
            if (!my_dao.open())
            {
               return null;
            }
        }
        return my_dao;
    }

    public boolean open()
    {
        my_db = my_helper.getWritableDatabase();
        return  my_db != null;
    }

    public void close()
    {
        my_db.close();
    }

    public boolean insertMainCat(String newCat)
    {
        newCat = newCat.toUpperCase();
        Cursor checkNameCursor = my_db.query(
            SqlLiteHelper.MAIN_CAT,  // Table to Query
            new String[] { SqlLiteHelper.NAME_MAIN_CAT }, // leaving "columns" null just returns all the columns.
            null, // cols for "where" clause
            null, // values for "where" clause
            null, // columns to group by
            null, // columns to filter by row groups
            null
        );
        checkNameCursor.moveToFirst();
        while (!checkNameCursor.isAfterLast())
        {
            if (checkNameCursor.getString(0).equals(newCat))
            {
                return false;
            }
            checkNameCursor.moveToNext();
        }

        ContentValues new_cont = new ContentValues();
        new_cont.put(SqlLiteHelper.NAME_MAIN_CAT,newCat);

        my_db.insert(SqlLiteHelper.MAIN_CAT,null,new_cont);
        return true;
    }

    public ArrayList<String> getMainCats()
    {
        ArrayList<String> return_value = new ArrayList<>();
        Cursor ret_cursor = my_db.query(
                SqlLiteHelper.MAIN_CAT,  // Table to Query
                new String[] { SqlLiteHelper.NAME_MAIN_CAT }, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null, // columns to group by
                null, // columns to filter by row groups
                null
        );

        ret_cursor.moveToFirst();
        while(!ret_cursor.isAfterLast()) {
            return_value.add(ret_cursor.getString(0));
            ret_cursor.moveToNext();
        }
        return return_value;
    }

    public boolean deleteMainCat(String name) {
        name = name.toUpperCase();
        return (my_db.delete(SqlLiteHelper.MAIN_CAT, SqlLiteHelper.NAME_MAIN_CAT + " = '" + name + "'", null) > 0);
    }
}
