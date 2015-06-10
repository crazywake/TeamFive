package com.bill.pocket.pocketbill;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SqlLiteHelper extends SQLiteOpenHelper {

    public SQLiteDatabase db;

    private static final String SQL_NAME = "Pocket.db";
    public static final String CATEGORY_TABLE = "categories";
    public static final String VALUE_TABLE = "vals";
    public static final String TAG_TABLE = "tags";
    public static final String TAG_VALUE_TABLE = "tags_values";

    private static final String CREATE_CAT_TABLE = "CREATE TABLE " + CATEGORY_TABLE + " (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "parentId INTEGER," +
            "name TEXT NOT NULL," +
            "UNIQUE (parentId, name)" +
            ");";

    private static final String CREATE_VAL_TABLE = "CREATE TABLE " + VALUE_TABLE + " (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "value INTEGER NOT NULL," +
            "date INTEGER NOT NULL," +
            "catId INTEGER NOT NULL" +
            ");";

    private static final String CREATE_TAG_TABLE = "CREATE TABLE " + TAG_TABLE + " (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "name TEXT NOT NULL UNIQUE" +
            ");";

    private static final String CREATE_TAG_VAL_TABLE = "CREATE TABLE " + TAG_VALUE_TABLE + " (" +
            "valId INTEGER," +
            "tagId INTEGER," +
            "PRIMARY KEY(valId, tagId)" +
            ");";

    public SqlLiteHelper(Context context)
    {
        super(context, SQL_NAME,null,1);
        if(db == null)
        {
            db = getWritableDatabase();

            boolean firstTime = false;
            try {
                Cursor c = db.rawQuery("SELECT * FROM " + CATEGORY_TABLE, null);
                firstTime = c.getCount() < 0;
            }
            catch(Exception e) {
                firstTime = true;
            }

            if(firstTime)
            {
                db.execSQL(CREATE_CAT_TABLE);
                db.execSQL(CREATE_VAL_TABLE);
                db.execSQL(CREATE_TAG_TABLE);
                db.execSQL(CREATE_TAG_VAL_TABLE);
            }
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        this.db = db;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldversion, int newversion)
    {
        assert(false);
        db.execSQL("DROP TABLE IF EXISTS ");
        onCreate(db);
    }

    public void close() {
        db.close();
    }

    public ArrayList<Map<String, String>> query(String cmd) {
        ArrayList<Map<String, String>> resultset = new ArrayList<Map<String, String>>();
        Cursor cursor;
        try {
            cursor = db.rawQuery(cmd, null);
            while(cursor.moveToNext()) {
                Map<String, String> map = new HashMap<String, String>();
                for (int i = 0; i < cursor.getColumnCount(); i++) {
                    if(cursor.isNull(i))
                        map.put(cursor.getColumnName(i), null);
                    map.put(cursor.getColumnName(i), cursor.getString(i));
                }
                resultset.add(map);
            }
            cursor.close();
        } catch(Exception e)
        {
            Log.w("Exception: ", e.getMessage());
        }
        return resultset;
    }

    public boolean exec(String cmd)
    {
        try {
            db.execSQL(cmd);
            return true;
        } finally {
            return false;
        }
    }

    public int insert(String table, ContentValues vals)
    {
        int result = 0;
        try {
            result = (int) db.insertOrThrow(table, null, vals);
        } catch(Exception e) {
            Log.w("Exception: ", e.getMessage());
        }
        return result;
    }

    public String main2SubSQL(Category main, Category sub)
    {
        return "UPDATE " + VALUE_TABLE + " SET catId = " + main.getId() + " WHERE catId = "
                + sub.getId();
    }

    public ContentValues insertCategorySQL(Category cat) {
        ContentValues vals = new ContentValues();
        vals.put("name", cat.getName());
        vals.put("parentId", cat.getParentId());
        return vals;/*
        String ins = "INSERT INTO " + CATEGORY_TABLE + " (parentId, name, color) VALUES (" + cat.getParentId()
                + ", '" + cat.getName() + "', '" + cat.getColor() + "');";
        Log.w("query: ", ins);
        return ins;*/
    }

    public ContentValues insertValueSQL(Value val) {
        ContentValues vals = new ContentValues();
        vals.put("value", val.getValue());
        vals.put("date", val.getDate().getTime());
        vals.put("catId", val.getParent().getId());

        /*String ins =  "INSERT INTO " + VALUE_TABLE + " (value, date, catId) VALUES (" + val.getValue()
                + ", " + val.getDate().getTime()/1000 + ", " + val.getParent().getId() + ");";

        Log.w("", ins);*/

        return vals;
    }

    public ContentValues insertTagSQL(String name) {
        ContentValues vals = new ContentValues();
        vals.put("name", name);
        return vals;
        /*return "INSERT INTO " + TAG_TABLE + " (name) VALUES ('" + name + "');";*/
    }

    public String insertTagValueSQL(Value val, String name) {
        return "INSERT INTO " + TAG_VALUE_TABLE + " (valId, tagId) VALUES ("+ val.getId()
                + ", (SELECT id FROM " + TAG_TABLE + " WHERE name = '" + name + "'));";
    }

    public String deleteCategorySQL(Category cat) {
        return "DELETE FROM " + CATEGORY_TABLE + " WHERE id = " + cat.getId();
    }

    public String deleteValueSQL(Value val) {
        return "DELETE FROM " + TAG_VALUE_TABLE + " WHERE valId = " + val.getId() + ";"
                + "DELETE FROM " + VALUE_TABLE + " WHERE id = " + val.getId() + ";";
    }

    public String deleteTagSQL(String name) {
        return "DELETE FROM " + TAG_VALUE_TABLE + " WHERE tagId = "
                + "(SELECT id FROM " + TAG_TABLE + " WHERE name = '" + name + "');"
                + "DELETE FROM " + TAG_TABLE + " WHERE name = '" + name + "';";
    }

    public String deleteTagValueSQL(Value val, String name) {
        return "DELETE FROM " + TAG_VALUE_TABLE + " WHERE tagId = "
                + "(SELECT id FROM " + TAG_TABLE + " WHERE name = '" + name + "') AND valId = "
                + val.getId() + ";";
    }

    public String updateCategorySQL(Category cat) {
        return "UPDATE " + CATEGORY_TABLE + " SET name = '" + cat.getName() + "', parentId = "
                + cat.getParentId() + " WHERE id = " + cat.getId();
    }

    public String updateValueSQL(Value val) {
        return "UPDATE " + VALUE_TABLE + " SET value = " + val.getValue() + ", date = "
                + val.getDate().getTime() + ", catId = " + val.getParent().getId()
                + " WHERE id = " + val.getId();
    }

    public String selectAllValuesSQL() {
        return "SELECT * FROM " + VALUE_TABLE;
    }

    public String filterValues(ArrayList<Integer> mainCategories,
                               ArrayList<Integer> subCategories,
                               ArrayList<Integer> tagIds) {

        String query = "select * from " + VALUE_TABLE;
        String tagIdsInString = "";

        List<Integer> categories = new ArrayList<>();

        Category tmp = null;
        for (Integer subCat : subCategories) {
            tmp = getCategoryById(subCat);
            if (mainCategories.contains(tmp.getParentId()))
                mainCategories.remove(new Integer(tmp.getParentId()));
        }

        for (Integer mainCat : mainCategories) {
            ArrayList<Integer> newSubCategories = getAllSubCategoryIdsFromMainCategoryId(mainCat);
            categories.addAll(newSubCategories);
        }

        categories.addAll(subCategories);


        if (tagIds.size() > 0) {
            query += " v join " + TAG_VALUE_TABLE + " t on (v.id = t.valId)";
            for (Integer id : tagIds) {
                tagIdsInString += id + ",";
            }
            tagIdsInString = tagIdsInString.substring(0, tagIdsInString.length() - 1);
        }

        if (categories.size() > 0) {
            query += " where catId in (";
            for (Integer id : categories) {
                query += id + ",";
            }
            query = query.substring(0, query.length() - 1) + ")";

            if (tagIds.size() > 0) {
                query += " and t.tagId in (" + tagIdsInString + ")";
            }

        } else if (tagIds.size() > 0) {
            query += " where t.tagId in (" + tagIdsInString + ")";
        }

        Log.w("", query);
        return query;
    }

    public Category getCategoryById(int catId) {
        Cursor c = db.rawQuery("select * from " + CATEGORY_TABLE + " where id = " + catId, null);
        if (c.getCount() < 1) return null;

        c.moveToFirst();

        Category cat = new Category(catId,
                c.getString(c.getColumnIndex("name")),
                new Category(c.getColumnIndex("parentId"), "", null, null, null, null),
                null,
                null,
                null);

        c.close();

        return cat;
    }

    public ArrayList<Integer> getAllSubCategoryIdsFromMainCategoryId(int mainCatId) {
        ArrayList<Integer> categories = new ArrayList<>();
        Cursor c = db.rawQuery("select * from " + CATEGORY_TABLE
                + " c where c.parentId = " + mainCatId, null);

        if (c.getCount() < 1) return categories;

        Category cat = null;
        while (c.moveToNext()) {
            categories.add(c.getInt(c.getColumnIndex("id")));
        }

        c.close();

        return categories;
    }

    public ArrayList<Tag> getAllTags() {
        ArrayList<Tag> tags = new ArrayList<>();
        Cursor c = db.rawQuery("select * from " + TAG_TABLE, null);
        if (c.getCount() < 1) return tags;

        Tag t = null;
        while (c.moveToNext()) {
            t = new Tag();
            t.setId(c.getInt(c.getColumnIndex("id")));
            t.setName(c.getString(c.getColumnIndex("name")));
            tags.add(t);
        }

        c.close();

        return tags;
    }
}
