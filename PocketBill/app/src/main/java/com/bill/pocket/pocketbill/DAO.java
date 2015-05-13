package com.bill.pocket.pocketbill;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


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



    public ArrayList<Category> getMainData()
    {
        ArrayList<Category> return_value = new ArrayList<>();
        Cursor main_cat_cursor = my_db.query(
                SqlLiteHelper.TBL_MAIN_CAT,  // Table to Query
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null, // columns to group by
                null, // columns to filter by row groups
                null
        );

        main_cat_cursor.moveToFirst();
        while(!main_cat_cursor.isAfterLast()) {
            Category newCategory = new Category(main_cat_cursor.getInt(0),main_cat_cursor.getString(1),null,null,null, Type.MAIN);

            Cursor sub_cat_cursor = my_db.query(
                    SqlLiteHelper.TBL_SUB_CAT,  // Table to Query
                    null, // leaving "columns" null just returns all the columns.
                    SqlLiteHelper.COL_FK_MAIN_CAT_PAYMENT + " = ?", // cols for "where" clause
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
                        SqlLiteHelper.TBL_PAYMENT,  // Table to Query
                        null, // leaving "columns" null just returns all the columns.
                        SqlLiteHelper.COL_FK_SUB_CAT_PAYMENT + " = ?", // cols for "where" clause
                        new String[]{Integer.toString(newSubCategory.getId())}, // values for "where" clause
                        null, // columns to group by
                        null, // columns to filter by row groups
                        null
                );
                values_cursor.moveToFirst();
                while (!values_cursor.isAfterLast())
                {
                    Value newValue = new Value(values_cursor.getInt(0), values_cursor.getInt(1),values_cursor.getInt(2), newCategory);
                    sub_value_set.add(newValue);
                    values_cursor.moveToNext();
                }
                newSubCategory.setValues(sub_value_set);
            }
            Cursor values_cursor = my_db.query(
                    SqlLiteHelper.TBL_PAYMENT,  // Table to Query
                    null, // leaving "columns" null just returns all the columns.
                    SqlLiteHelper.COL_FK_MAIN_CAT_PAYMENT + " = ?", // cols for "where" clause
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

    public ArrayList<String> getSubCats()
    {
        ArrayList<String> sub_cats = new ArrayList<String>();

        Cursor get_subs = my_db.query(
                SqlLiteHelper.TBL_SUB_CAT,  // Table to Query
                new String[] { SqlLiteHelper.COL_NAME_SUB_CAT}, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null, // columns to group by
                null, // columns to filter by row groups
                null
        );


        get_subs.moveToFirst();

        while (!get_subs.isAfterLast())
        {
            sub_cats.add(get_subs.getString(0));
            get_subs.moveToNext();
        }


        return sub_cats;
    }

    public ArrayList<String> getPayments()
    {
        ArrayList<String> payments = new ArrayList<String>();

        Cursor get_payments = my_db.query(
                SqlLiteHelper.TBL_PAYMENT,  // Table to Query
                new String[] { SqlLiteHelper.COL_VALUE_PAYMENT }, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                SqlLiteHelper.COL_DATE_PAYMENT, // columns to group by
                null, // columns to filter by row groups
                null
        );


        get_payments.moveToFirst();

        while (!get_payments.isAfterLast())
        {
            DateFormat formats = DateFormat.getDateInstance();
            long time = Long.parseLong(get_payments.getString(1));
            Date my_date = new Date(time);

            payments.add(formats.format(my_date)  + "                   " + get_payments.getString(0) + " Euro");
            get_payments.moveToNext();
        }

        if(payments.size() == 0)
        {
            payments.add("No Payment found");
        }

        return payments;
    }

    public long insertMainCat(String newCat)
    {
        Cursor checkNameCursor = my_db.query(
                SqlLiteHelper.TBL_MAIN_CAT,  // Table to Query
                new String[] { SqlLiteHelper.COL_NAME_MAIN_CAT}, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null, // columns to group by
                null, // columns to filter by row groups
                null
        );
        checkNameCursor.moveToFirst();
        while (!checkNameCursor.isAfterLast())
        {
            checkNameCursor.moveToNext();
        }

        ContentValues new_cont = new ContentValues();
        new_cont.put(SqlLiteHelper.COL_NAME_MAIN_CAT,newCat);

        return my_db.insert(SqlLiteHelper.TBL_MAIN_CAT,null,new_cont);
    }

    public long insertSubCat(String newSubCat,int MainCatId)
    {
        Cursor checkNameCursor = my_db.query(
                SqlLiteHelper.TBL_SUB_CAT,  // Table to Query
                new String[] { SqlLiteHelper.COL_NAME_SUB_CAT}, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null, // columns to group by
                null, // columns to filter by row groups
                null
        );

        checkNameCursor.moveToFirst();
        while(!checkNameCursor.isAfterLast())
        {
            checkNameCursor.moveToNext();
        }
        ContentValues new_name = new  ContentValues();
        new_name.put(SqlLiteHelper.COL_NAME_SUB_CAT,newSubCat);
        new_name.put(SqlLiteHelper.COL_FK_MAIN_CAT_ID,MainCatId);

        return my_db.insert(SqlLiteHelper.TBL_SUB_CAT,null,new_name);
    }

    public boolean insertPayment(int new_value, int main_cat, int sub_cat)
    {
        return insertPayment(new_value, main_cat,sub_cat, new Date());
    }

    public boolean insertPayment(int new_value, int main_cat, int sub_cat, Date currentDate)
    {
        long unixTime = currentDate.getTime();

        ContentValues new_cont = new ContentValues();
        new_cont.put(SqlLiteHelper.COL_VALUE_PAYMENT,new_value);
        new_cont.put(SqlLiteHelper.COL_DATE_PAYMENT,unixTime);
        new_cont.put(SqlLiteHelper.COL_FK_MAIN_CAT_PAYMENT,main_cat);
        new_cont.put(SqlLiteHelper.COL_FK_SUB_CAT_PAYMENT,sub_cat);

        my_db.insert(SqlLiteHelper.TBL_PAYMENT,null,new_cont);
        return true;
    }

    public void deleteMainCategory(int main_cat_id) {
        String query = String.format(
            "delete from " +
            SqlLiteHelper.TBL_MAIN_CAT + " where " +
            SqlLiteHelper.COL_ID_MAIN_CAT + " = '%d'",
                main_cat_id);

        my_db.execSQL(query);
    }

    public void deleteSubCategory(int sub_cat_id) {
        String query = String.format(
                "delete from " +
                SqlLiteHelper.TBL_SUB_CAT + " where " +
                SqlLiteHelper.COL_ID_SUB_CAT + " = '%d'",
                sub_cat_id);

        my_db.execSQL(query);

    }

    public boolean updatePayment(Integer new_value,Integer id)
    {
        ContentValues new_cont = new ContentValues();
        new_cont.put(SqlLiteHelper.COL_VALUE_PAYMENT, new_value);

        return (my_db.update(SqlLiteHelper.TBL_PAYMENT, new_cont, "id =" + id, null) > 0);
    }

    public boolean updateTime(Date new_value,Integer id)
    {
        long unixTime = new_value.getTime();

        ContentValues new_cont = new ContentValues();
        new_cont.put(SqlLiteHelper.COL_DATE_PAYMENT, unixTime);

        return (my_db.update(SqlLiteHelper.TBL_PAYMENT, new_cont, "id =" + id, null) > 0);
    }

    public boolean updateMainCategory(String new_value, Integer main_cat_id)
    {
        ContentValues new_cont = new ContentValues();
        new_cont.put(SqlLiteHelper.COL_NAME_MAIN_CAT, new_value);

        return (my_db.update(SqlLiteHelper.TBL_MAIN_CAT, new_cont, "id =" + main_cat_id, null) > 0);
    }

    public boolean updateSubCategory(String new_value, Integer sub_cat_id)
    {
        ContentValues new_cont = new ContentValues();
        new_cont.put(SqlLiteHelper.COL_NAME_SUB_CAT, new_value);

        return (my_db.update(SqlLiteHelper.TBL_SUB_CAT, new_cont, "id =" + sub_cat_id, null) > 0);
    }
}
