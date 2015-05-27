package com.bill.pocket.pocketbill;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Map;


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
            /*if (!my_dao.open())
            {
               return null;
            }*/
        }
        return my_dao;
    }

    public boolean open()
    {
        //my_db = my_helper.getWritableDatabase();

        return false;
    }

    public void close()
    {
        my_helper.close();
        //my_db.close();
    }

    public ArrayList<Category> getCategories(Category parent)
    {
        int rootId = -1;
        if(parent != Category.ROOT_CATEGORY)
            rootId = parent.getId();

        ArrayList<Category> categories = new ArrayList<Category>();
        ArrayList<Map<String, String>> resultset = my_helper.query("SELECT * FROM " + SqlLiteHelper.CATEGORY_TABLE +
            " WHERE parentId = " + rootId);

        for(Map<String, String> map : resultset) {
            int id = Integer.parseInt(map.get("id"));
            String name = map.get("name");
            Category.Type type = Category.Type.MAIN;

            if(rootId != -1)
                type = Category.Type.SUB;

            Category category = new Category(id, name, parent, null, null, type, Category.DEFAULT_COLOR);

            category.setSubcategories(getCategories(category));
            category.setValues(getValues(category));

            categories.add(category);
        }

        return categories;
    }

    public ArrayList<Value> getValues(Category category)
    {
        ArrayList<Value> values = new ArrayList<Value>();
        ArrayList<Map<String, String>> resultset = my_helper.query("SELECT * FROM " + SqlLiteHelper.VALUE_TABLE +
                " WHERE catId = " + category.getId());

        for(Map<String, String> map : resultset) {
            int id = Integer.parseInt(map.get("id"));
            int val = Integer.parseInt(map.get("value"));
            int date = Integer.parseInt(map.get("date"));
            Value value = new Value(id, val, date, category, new ArrayList<String>());

            ArrayList<Map<String, String>> tagSet = my_helper.query("SELECT * FROM " + SqlLiteHelper.TAG_VALUE_TABLE +
                    " AS tv JOIN " + SqlLiteHelper.TAG_TABLE + " AS t ON tv.tagId = t.id WHERE tv.valId = " + id);

            for(Map<String, String> tagmap : tagSet) {
                value.getTags().add(tagmap.get("name"));
            }
            values.add(value);
        }

        return values;
    }

    public ArrayList<Value> getFilteredValues(ArrayList<Integer> mainCategories, ArrayList<Integer> subCategories) {
        ArrayList<Map<String, String>> resultset = my_helper.query(my_helper.filterValues(mainCategories, subCategories));
        ArrayList<Value> values = new ArrayList<Value>();

        for(Map<String, String> map : resultset) {
            int id = Integer.parseInt(map.get("id"));
            int val = Integer.parseInt(map.get("value"));
            int date = Integer.parseInt(map.get("date"));
            int catId = Integer.parseInt(map.get("catId"));

            Value value = new Value(id, val, date, my_helper.getCategoryById(catId), new ArrayList<String>());

            ArrayList<Map<String, String>> tagSet = my_helper.query("SELECT * FROM " + SqlLiteHelper.TAG_VALUE_TABLE +
                    " AS tv JOIN " + SqlLiteHelper.TAG_TABLE + " AS t ON tv.tagId = t.id WHERE tv.valId = " + id);

            for(Map<String, String> tagmap : tagSet) {
                value.getTags().add(tagmap.get("name"));
            }
            values.add(value);
        }

        return values;
    }

    public void makeMain2Sub(Category category)
    {
        for(Category sub : category.getSubcategories())
        {
            my_helper.exec(my_helper.main2SubSQL(category, sub));
            my_helper.exec(my_helper.deleteCategorySQL(sub));
        }
    }

    public boolean insertCategory(Category category) {
        /*my_helper.exec(my_helper.insertCategorySQL(category));

        ArrayList<Map<String, String>> t = my_helper.query("SELECT id FROM " + my_helper.CATEGORY_TABLE + " WHERE parentId = " + category.getParentId() + " AND name = '"
            + category.getName() + "';");
        //Log.w("möp = ", möp);

        return true;*/
        int id = my_helper.insert(my_helper.CATEGORY_TABLE, my_helper.insertCategorySQL(category));
        if(id == 0)
            return false;
        category.setId(id);
        return true;
    }

    public boolean insertValue(Value value) {
            value.setId(my_helper.insert(my_helper.VALUE_TABLE, my_helper.insertValueSQL(value)));
            for(String tag : value.getTags()) {
                insertTagValue(value, tag);
            }
            return true;
    }

    public int insertTag(String tag) {
        try {
            return my_helper.insert(my_helper.TAG_TABLE, my_helper.insertTagSQL(tag));
        }  catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public boolean insertTagValue(Value value, String tag) {
        try {
            my_helper.exec(my_helper.insertTagValueSQL(value, tag));
            return true;
        }  catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteCategory(Category category) {
        try {
            my_helper.exec(my_helper.deleteCategorySQL(category));
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteValue(Value value) {
        try {
            my_helper.exec(my_helper.deleteValueSQL(value));

            for(String tag : value.getTags()) {
                if(my_helper.query("SELECT * FROM " + my_helper.TAG_VALUE_TABLE
                        + " WHERE tagId = (SELECT id FROM " + my_helper.TAG_TABLE + " WHERE name = '" + tag + "')").size() == 0) {
                    my_helper.exec(my_helper.deleteTagSQL(tag));
                }
            }

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteTag(String tag) {
        try {
            my_helper.exec(my_helper.deleteTagSQL(tag));
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteTagValue(Value val, String tag) {
        try {
            my_helper.exec(my_helper.deleteTagValueSQL(val, tag));
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateCategory(Category cat) {
        try {
            my_helper.exec(my_helper.updateCategorySQL(cat));
            return true;
        }  catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateValue(Value val) {
        try {
            my_helper.exec(my_helper.updateValueSQL(val));
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public ArrayList<Tag> getAllTags() {
        return my_helper.getAllTags();
    }
}
