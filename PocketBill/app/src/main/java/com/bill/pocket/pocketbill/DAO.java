package com.bill.pocket.pocketbill;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
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

    public boolean deleteValue(Value value) {
        return (my_db.delete(SqlLiteHelper.PAYMENT, SqlLiteHelper.ID_PAYMENT + " = " + value.getId(), null) > 0);
    }

    public boolean deleteCategory(Category category, boolean recursive) {
        if(recursive) {
            //delete values
            ArrayList<Value> values = new ArrayList<>();
            for(Category cat : category.getSubcategories()) {
                values.addAll(cat.getValues());
            }
            for(Value val : values) {
                deleteValue(val);
            }

            //delete subcategories
            for(Category cat : category.getSubcategories()) {
                deleteCategory(cat, false);
            }
        }

        for(Value val : category.getValues()) {
            deleteValue(val);
        }

        if(category.getType() == Category.Type.SUB) {
            return (my_db.delete(SqlLiteHelper.SUB_CAT, SqlLiteHelper.ID_SUB_CAT + " = " + category.getId(), null) > 0);
        }
        else {
            return (my_db.delete(SqlLiteHelper.MAIN_CAT, SqlLiteHelper.ID_MAIN_CAT + " = " + category.getId(), null) > 0);
        }
    }

    public boolean deleteSubCat(String name){ //depricated!

        return (my_db.delete(SqlLiteHelper.SUB_CAT, SqlLiteHelper.NAME_SUB_CAT + " = '" + name + "'", null) > 0);
    }

    public ArrayList<String> getSubCats()
    {
        ArrayList<String> sub_cats = new ArrayList<String>();

        Cursor get_subs = my_db.query(
                SqlLiteHelper.SUB_CAT,  // Table to Query
                new String[] { SqlLiteHelper.NAME_SUB_CAT }, // leaving "columns" null just returns all the columns.
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
                SqlLiteHelper.PAYMENT,  // Table to Query
                new String[] { SqlLiteHelper.VALUE_PAYMENT }, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null, // columns to group by
                null, // columns to filter by row groups
                null
        );


        get_payments.moveToFirst();

        while (!get_payments.isAfterLast())
        {
            payments.add(get_payments.getString(0));
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
            checkNameCursor.moveToNext();
        }

        ContentValues new_cont = new ContentValues();
        new_cont.put(SqlLiteHelper.NAME_MAIN_CAT,newCat);

        return my_db.insert(SqlLiteHelper.MAIN_CAT,null,new_cont);
    }

    public long insertSubCat(String newSubCat,Integer MainCatId)
    {
        Cursor checkNameCursor = my_db.query(
                SqlLiteHelper.SUB_CAT,  // Table to Query
                new String[] { SqlLiteHelper.NAME_SUB_CAT }, // leaving "columns" null just returns all the columns.
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
        new_name.put(SqlLiteHelper.NAME_SUB_CAT,newSubCat);
        new_name.put(SqlLiteHelper.MAIN_CAT_ID,MainCatId);

        return my_db.insert(SqlLiteHelper.SUB_CAT,null,new_name);
    }

    public boolean insertPayment(Integer new_value, Integer main_cat,Integer sub_cat)
    {
        long unixTime = System.currentTimeMillis();

        ContentValues new_cont = new ContentValues();
        new_cont.put(SqlLiteHelper.VALUE_PAYMENT,new_value);
        new_cont.put(SqlLiteHelper.DATE_PAYMENT,unixTime);
        new_cont.put(SqlLiteHelper.MAIN_CAT_PAYMENT,main_cat);
        new_cont.put(SqlLiteHelper.SUB_CAT_PAYMENT,sub_cat);

        my_db.insert(SqlLiteHelper.PAYMENT,null,new_cont);
        return true;
    }

    public boolean updatePayment(Integer new_value, Integer id)
    {
        ContentValues new_cont = new ContentValues();
        new_cont.put(SqlLiteHelper.VALUE_PAYMENT, new_value);

        return (my_db.update(SqlLiteHelper.PAYMENT, new_cont, "id =" + id, null) > 0);
    }

    public boolean changePaymentCategory(Integer main_cat, Integer sub_cat, Integer id)
    {
        ContentValues new_cont = new ContentValues();
        new_cont.put(SqlLiteHelper.MAIN_CAT_PAYMENT, main_cat);
        new_cont.put(SqlLiteHelper.SUB_CAT_PAYMENT, sub_cat);

        return (my_db.update(SqlLiteHelper.PAYMENT, new_cont, "id =" + id, null) > 0);
    }



    public boolean updateTime(Date new_value,Integer id)
    {
        long unixTime = new_value.getTime();

        ContentValues new_cont = new ContentValues();
        new_cont.put(SqlLiteHelper.DATE_PAYMENT, unixTime);

        return (my_db.update(SqlLiteHelper.PAYMENT, new_cont, "id =" + id, null) > 0);
    }

    public void updateMainCategory(Category old_category, Category new_category)
    {
        if(old_category.getType() == new_category.getType()) {
            //category stays the same type

            String new_value = new_category.getName();
            Integer main_cat_id = new_category.getId();

            ContentValues new_cont = new ContentValues();
            new_cont.put(SqlLiteHelper.NAME_MAIN_CAT, new_value);

            my_db.update(SqlLiteHelper.MAIN_CAT, new_cont, "id = " + main_cat_id, null);
        } else {
            //main category changed to sub_category

            //step 1: get all values in main category and all subcategories:
            ArrayList<Value> values = new ArrayList<>();
            values.addAll(old_category.getValues());
            for(Category cat : old_category.getSubcategories()) {
                values.addAll(cat.getValues());
            }
            //step 2: create new subcategory
            int new_id = (int) insertSubCat(new_category.getName(), new_category.getParent().getId());

            //step 3: change value categories
            for(Value val : values) {
                changePaymentCategory(null, new_id, val.getId());
            }

            //step 4: delete subcategories non-recursively (keep values)
            for(Category subcat : old_category.getSubcategories()) {
                deleteCategory(subcat, false);
            }

            //step 5: delete main category non-recursively (because of reasons)
            deleteCategory(old_category, false);
        }
    }

    public void updateSubCategory(Category old_category, Category new_category)
    {
        if(old_category.getType() == new_category.getType()) {
            //category stays the same type
            String new_value = new_category.getName();
            Integer sub_cat_id = new_category.getId();

            ContentValues new_cont = new ContentValues();
            new_cont.put(SqlLiteHelper.NAME_SUB_CAT, new_value);
            new_cont.put(SqlLiteHelper.MAIN_CAT_ID, new_category.getParent().getId());

            my_db.update(SqlLiteHelper.SUB_CAT, new_cont, "id = " + sub_cat_id, null);
        } else {

            //sub category changed to main_category

            //step 1: get all values in sub category:
            ArrayList<Value> values = new ArrayList<>();
            values.addAll(old_category.getValues());

            //step 2: create new main category
            int new_id = (int) insertMainCat(new_category.getName());

            //step 3: change value categories
            for(Value val : values) {
                changePaymentCategory(new_id, null, val.getId());
            }

            //step 4: delete sub category non-recursively (because of reasons)
            deleteCategory(old_category, false);
        }
    }

    public boolean updateSubCategory(String new_value, Integer sub_cat_id)
    {
        ContentValues new_cont = new ContentValues();
        new_cont.put(SqlLiteHelper.NAME_SUB_CAT, new_value);

        return (my_db.update(SqlLiteHelper.SUB_CAT, new_cont, "id =" + sub_cat_id, null) > 0);
    }
}
