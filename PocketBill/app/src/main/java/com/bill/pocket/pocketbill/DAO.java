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

    public ArrayList<Category> getMainData()
    {
        ArrayList<Category> return_value = new ArrayList<>();
        Cursor main_cat_cursor = my_db.query(
                SqlLiteHelper.MAIN_CAT,  // Table to Query
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null, // columns to group by
                null, // columns to filter by row groups
                null
        );

        main_cat_cursor.moveToFirst();
        while(!main_cat_cursor.isAfterLast()) {
            Category newCategory = new Category(main_cat_cursor.getInt(0),main_cat_cursor.getString(1),null,null,null, Category.Type.MAIN);

            Cursor sub_cat_cursor = my_db.query(
                    SqlLiteHelper.SUB_CAT,  // Table to Query
                    null, // leaving "columns" null just returns all the columns.
                    SqlLiteHelper.MAIN_CAT_PAYMENT  + " = ?", // cols for "where" clause
                    new String[] {Integer.toString(newCategory.getId())}, // values for "where" clause
                    null, // columns to group by
                    null, // columns to filter by row groups
                    null
            );
            ArrayList<Category> sub_set = new ArrayList<>();
            ArrayList<Value> value_set_main = new ArrayList<>();
            sub_cat_cursor.moveToFirst();
            while (!sub_cat_cursor.isAfterLast())
            {
                ArrayList<Value> sub_value_set = new ArrayList<>();
                Category newSubCategory = new Category(sub_cat_cursor.getInt(0), sub_cat_cursor.getString(1),newCategory, null,null, Category.Type.SUB);
                sub_set.add(newSubCategory);
                sub_cat_cursor.moveToNext();

                Cursor values_cursor = my_db.query(
                        SqlLiteHelper.PAYMENT,  // Table to Query
                        null, // leaving "columns" null just returns all the columns.
                        SqlLiteHelper.SUB_CAT_PAYMENT + " = ?", // cols for "where" clause
                        new String[]{Integer.toString(newSubCategory.getId())}, // values for "where" clause
                        null, // columns to group by
                        null, // columns to filter by row groups
                        null
                );
                values_cursor.moveToFirst();
                while (!values_cursor.isAfterLast())
                {
                    Value newValue = new Value(values_cursor.getInt(0), values_cursor.getInt(1),values_cursor.getInt(2) * 1000, newCategory);
                    sub_value_set.add(newValue);
                    values_cursor.moveToNext();
                }
                newSubCategory.setValues(sub_value_set);
            }
            Cursor values_cursor = my_db.query(
                    SqlLiteHelper.PAYMENT,  // Table to Query
                    null, // leaving "columns" null just returns all the columns.
                    SqlLiteHelper.MAIN_CAT_PAYMENT + " = ?", // cols for "where" clause
                    new String[]{Integer.toString(newCategory.getId())}, // values for "where" clause
                    null, // columns to group by
                    null, // columns to filter by row groups
                    null
            );
            values_cursor.moveToFirst();
            while (!values_cursor.isAfterLast())
            {
                Value newValue = new Value(values_cursor.getInt(0), values_cursor.getInt(1),values_cursor.getInt(2) * 1000, newCategory);
                value_set_main.add(newValue);
                values_cursor.moveToNext();
            }
            newCategory.setSubcategories(sub_set);
            newCategory.setValues(value_set_main);
            return_value.add(newCategory);
            main_cat_cursor.moveToNext();

        }

        return return_value;
    }

    public boolean deleteMainCat(String name) {
        name = name.toUpperCase();
        return (my_db.delete(SqlLiteHelper.MAIN_CAT, SqlLiteHelper.NAME_MAIN_CAT + " = '" + name + "'", null) > 0);
    }
}
