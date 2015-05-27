package com.bill.pocket.pocketbill;

import java.util.ArrayList;

/**
 * Created by Thomas on 20.05.2015.
 */
public class CategoryData {

    private static CategoryData categoryData;
    private ArrayList<Category> main_categories;

    public static CategoryData getInstance() {
        if (categoryData == null)
            categoryData = new CategoryData();
        return categoryData;
    }

    private CategoryData() {
        this.main_categories = new ArrayList<>();
    }

    public ArrayList<Category> getMainCategories() {
        return main_categories;
    }

    public void setMainCategories(ArrayList<Category> main_categories) {
        this.main_categories = main_categories;
    }
}
