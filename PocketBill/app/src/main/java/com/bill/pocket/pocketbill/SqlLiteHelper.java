package com.bill.pocket.pocketbill;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SqlLiteHelper extends SQLiteOpenHelper {

    private SQLiteDatabase db;

    private static final String SQL_NAME = "Pocket.db";
    public static final String CATEGORY_TABLE = "categories";
    public static final String VALUE_TABLE = "vals";
    public static final String TAG_TABLE = "tags";
    public static final String TAG_VALUE_TABLE = "tags_values";

    private static final String CREATE_CAT_TABLE = "CREATE TABLE " + CATEGORY_TABLE + " (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "parentId INTEGER," +
            "name TEXT NOT NULL," +
            "color TEXT NOT NULL," +
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
        try {
            return (int) db.insertOrThrow(table, null, vals);
        } finally {
            return 0;
        }
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
        vals.put("color", cat.getColor());
        return vals;/*
        String ins = "INSERT INTO " + CATEGORY_TABLE + " (parentId, name, color) VALUES (" + cat.getParentId()
                + ", '" + cat.getName() + "', '" + cat.getColor() + "');";
        Log.w("query: ", ins);
        return ins;*/
    }

    public ContentValues insertValueSQL(Value val) {
        ContentValues vals = new ContentValues();
        vals.put("value", val.getValue());
        vals.put("date", val.getDate().getTime()/1000);
        vals.put("catId", val.getParent().getId());
        return vals;
        /* return "INSERT INTO " + VALUE_TABLE + " (value, date, catId) VALUES (" + val.getValue()
                + ", " + val.getDate().getTime()/1000 + ", " + val.getParent().getId() + ");";*/
    }

    public ContentValues insertTagSQL(String name) {
        ContentValues vals = new ContentValues();
        vals.put("name", name);
        return vals;
        /*return "INSERT INTO " + TAG_TABLE + " (name) VALUES ('" + name + "');";*/
    }

    public String insertTagValueSQL(Value val, String name) {
        return "INSERT INTO " + TAG_VALUE_TABLE + " (valId, tagId) VALUES ("+ val.getId()
                + ", (SELECT id FROM " + TAG_TABLE + " WHERE name = '" + name + "');";
    }

    public String deleteCategorySQL(Category cat) {
        return "DELETE " + CATEGORY_TABLE + " WHERE id = " + cat.getId();
    }

    public String deleteValueSQL(Value val) {
        return "DELETE " + TAG_VALUE_TABLE + " WHERE valId = " + val.getId() + ";"
                + "DELETE " + VALUE_TABLE + " WHERE id = " + val.getId() + ";";
    }

    public String deleteTagSQL(String name) {
        return "DELETE " + TAG_VALUE_TABLE + " WHERE tagId = "
                + "(SELECT id FROM " + TAG_TABLE + " WHERE name = '" + name + "');"
                + "DELETE " + TAG_TABLE + " WHERE name = '" + name + "';";
    }

    public String deleteTagValueSQL(Value val, String name) {
        return "DELETE " + TAG_VALUE_TABLE + " WHERE tagId = "
                + "(SELECT id FROM " + TAG_TABLE + " WHERE name = '" + name + "') AND valId = "
                + val.getId() + ";";
    }

    public String updateCategorySQL(Category cat) {
        return "UPDATE " + CATEGORY_TABLE + " SET name = '" + cat.getName() + "', parentId = "
                + cat.getParentId() + ", color = '" + cat.getColor() + "' WHERE id = " + cat.getId();
    }

    public String updateValueSQL(Value val) {
        return "UPDATE " + VALUE_TABLE + " SET value = " + val.getValue() + ", date = "
                + val.getDate().getTime()/1000 + ", catId = " + val.getParent().getId()
                + " WHERE id = " + val.getId();
    }

    public String selectAllValuesSQL() {
        return "SELECT * FROM " + VALUE_TABLE;
    }
}
