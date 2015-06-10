package com.bill.pocket.pocketbill;

import java.util.ArrayList;

/**
 * Created by Martin on 29.04.2015.
 */
public class Category {

    public static Category ROOT_CATEGORY = null;
    public static String DEFAULT_COLOR = "#A35F7A";

    public enum Type {
        MAIN,
        SUB,
    }

    private int id;
    private String name;
    private Category parent;
    private ArrayList<Category> subcategories;
    private ArrayList<Value> values;
    private Type type;

    public Category() {
    }

    // main category: db_id, name, null, new ArrayList<Category>(), new ArrayList<Value>(), Type.MAIN
    // sub category: db_id, name, parent, null, new ArrayList<Value>(), Type.SUB
    public Category(int id, String name, Category parent, ArrayList<Category> subcategories, ArrayList<Value> values, Type type) {
        this.id = id;
        this.name = name;
        this.parent = parent;
        this.subcategories = subcategories;
        if(subcategories == null) this.subcategories = new ArrayList<>();
        this.values = values;
        if(values == null) this.values = new ArrayList<>();
        this.type = type;
    }

    public int getParentId()
    {
        if(parent != null)
            return parent.getId();
        return -1;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Category getParent() {
        return parent;
    }

    public void setParent(Category parent) {
        this.parent = parent;
    }

    public ArrayList<Category> getSubcategories() {
        return subcategories;
    }

    public void setSubcategories(ArrayList<Category> subcategories) {
        this.subcategories = subcategories;
    }

    public ArrayList<Value> getValues() {
        return values;
    }

    public void setValues(ArrayList<Value> values) {
        this.values = values;
    }

    public void addValues(Value value) {
        values.add(value);
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return name;
    }
}
