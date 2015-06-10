package com.bill.pocket.pocketbill;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by Martin on 06.05.2015.
 */
public class CategoryEditor {

    public static enum Type {
        ADD,
        EDIT
    }

    private Type type;
    private Category category;
    private MainActivity activity;
    private ArrayList<Category> main_categories;
    private Category parent_category;
    private Category.Type main_sub_type = Category.Type.SUB;
    private int parent_id = -1;

    public CategoryEditor(Type type, Category category, MainActivity activity,
                          ArrayList<Category> main_categories, Category parent_category) {
        this.type = type;
        this.category = category;
        this.activity = activity;
        this.main_categories = main_categories;
        this.parent_category = parent_category;

         if(main_categories.size() != 0) {
           parent_id = main_categories.get(0).getId();
         }
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

    public PopupWindow display() {

        LayoutInflater layinf = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View addEditPopupView = layinf.inflate(R.layout.add_edit_category, null);
        final PopupWindow popup = new PopupWindow(addEditPopupView, GridLayout.LayoutParams.MATCH_PARENT,
                GridLayout.LayoutParams.WRAP_CONTENT, true);
        popup.showAtLocation(addEditPopupView, Gravity.CENTER, 0, 0);

        final Spinner parentSpinner = (Spinner) addEditPopupView.findViewById(R.id.categoryParentSpinner);

        //if(type == Type.EDIT) main_categories.remove(category);
        ArrayList<Category> copy = (ArrayList<Category>) main_categories.clone();
        copy.remove(this.category);
        ArrayAdapter<Category> adapter = new ArrayAdapter<>(activity.getBaseContext(), android.R.layout.simple_spinner_dropdown_item, copy);

        parentSpinner.setAdapter(adapter);

        Button btnOK = (Button) addEditPopupView.findViewById(R.id.categoryEditorOK);
        Button btnCancel = (Button) addEditPopupView.findViewById(R.id.categoryEditorCancel);

        final EditText catname = (EditText) addEditPopupView.findViewById(R.id.categoryEditName);
        final CheckBox checkbox = ( CheckBox) addEditPopupView.findViewById(R.id.categoryCheckbox);

        parentSpinner.setOnItemSelectedListener(new SpinnerOnItemSelectedListener());

        checkbox.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(main_categories.size() == 0) {
                    checkbox.toggle();
                    return;
                }
                if(checkbox.isChecked()) {
                    parentSpinner.setEnabled(false);
                    main_sub_type = Category.Type.MAIN;
                }
                else {
                    parentSpinner.setEnabled(true);
                    main_sub_type = Category.Type.SUB;
                }
            }
        });

        btnOK.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                // ok button clicked
                    if(parseInput(catname.getText().toString())) {
                        //SET NEW DATA
                        setData(catname.getText().toString(), Category.DEFAULT_COLOR);
                        activity.setCur_state(activity.getPre_popup_state());
                        popup.dismiss();

                    } else {
                        Toast.makeText(activity, "Kenzel Washington requires a valid name sacrifice!", Toast.LENGTH_SHORT).show();
                    }
                }
        });


        btnCancel.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                // kenzel button clicked
                popup.dismiss();
            }
        });

        if(main_categories.size() == 0) {
            checkbox.setEnabled(false);
        }

        if(parent_category == null)
        {
            main_sub_type = Category.Type.MAIN;
            checkbox.setChecked(true);
            parentSpinner.setEnabled(false);
        } else {
            int pos = main_categories.indexOf(parent_category);
            parentSpinner.setSelection(pos);
            parent_id = parent_category.getId();
        }

        if(type == Type.EDIT) {
            catname.setText(category.getName());
        }

        return popup;
    }

    private boolean parseInput(String text) {
        if(text.equals(null) || text.length() == 0 || text.equals("")) {
            return false;
        }
        return true;
    }

    private void setData(String name, String color) {
        if(type == Type.EDIT) {
            Category.Type oldType = category.getType();
            Category.Type newType = main_sub_type;

            if(newType == Category.Type.MAIN) {
                parent_id = -1;
            }
            category.setName(name);
            category.setParent(activity.getCategoryFromID(parent_id));

            category.setType(main_sub_type);

            if(oldType == Category.Type.MAIN && newType == Category.Type.SUB) {
                activity.getDAO().makeMain2Sub(category);
                parent_id = -1;
            }

            activity.getDAO().updateCategory(category);
            activity.updateLists(parent_id);
        }

        if(type == Type.ADD) {
            if(main_sub_type == Category.Type.MAIN) {
                activity.getDAO().insertCategory(new Category(-1, name, Category.ROOT_CATEGORY, null, null, Category.Type.MAIN));
                parent_id = -1;
            }
            if(main_sub_type == Category.Type.SUB) {
                activity.getDAO().insertCategory(new Category(-1, name, parent_category, null, null, Category.Type.SUB));
            }

            activity.updateLists(parent_id);
        }
    }

    private class SpinnerOnItemSelectedListener implements AdapterView.OnItemSelectedListener {
        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
            int parent_category_id = main_categories.get(pos).getId();
            parent_id = parent_category_id;
        }

        public void onNothingSelected(AdapterView parent) {
            // Do nothing.
        }
    }
}
