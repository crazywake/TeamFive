package com.bill.pocket.pocketbill;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.GridLayout;
import android.widget.PopupWindow;
import android.widget.Spinner;

import java.util.ArrayList;

/**
 * Created by Martin on 06.05.2015.
 */
public class CategoryEditor {

    public static enum Type {
        ADD,
        EDIT,
    }

    private Type type;
    private Category category;
    private MainActivity activity;
    private ArrayList<Category> main_categories;
    private Category parent_category;

    public CategoryEditor(Type type, Category category, MainActivity activity,
                          ArrayList<Category> main_categories, Category parent_category) {
        this.type = type;
        this.category = category;
        this.activity = activity;
        this.main_categories = main_categories;
        this.parent_category = parent_category;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public void display() {

        LayoutInflater layinf = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View addEditPopup = layinf.inflate(R.layout.add_edit_category, null);
        PopupWindow popup = new PopupWindow(addEditPopup, GridLayout.LayoutParams.MATCH_PARENT,
                GridLayout.LayoutParams.WRAP_CONTENT, true);
        popup.setAnimationStyle(android.R.style.Animation_Dialog);
        popup.showAtLocation(addEditPopup, Gravity.CENTER, 0, 0);

        Spinner parentSpinner = (Spinner) addEditPopup.findViewById(R.id.categoryParentSpinner);
        ArrayAdapter<Category> adapter = new ArrayAdapter<>(activity.getBaseContext(), android.R.layout.simple_spinner_dropdown_item, main_categories);
        parentSpinner.setAdapter(adapter);



    }
}
