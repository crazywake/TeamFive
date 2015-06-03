package com.bill.pocket.pocketbill;

import android.content.Intent;

import android.graphics.Color;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import static java.lang.Math.*;

import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;


public class AddValueActivity extends ActionBarActivity {

    AutoCompleteTextView last_text = null;
    ArrayAdapter<String> my_adapter = null;
    ArrayList<String> my_tags = null;
    HashMap<Integer, String> tags = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_value);
        DAO dao = DAO.instance(this);

        Toast.makeText(getApplicationContext(), "AddValue Activity opened", Toast.LENGTH_SHORT).show();

        final AutoCompleteTextView my_base_text_view = (AutoCompleteTextView) findViewById(R.id.autocomplete_text);
        last_text = my_base_text_view;
        my_adapter = new ArrayAdapter<String>(this,android.R.layout.simple_dropdown_item_1line, dao.getAllTagsArray());
        my_tags = new ArrayList<String>();
        tags = new HashMap<>();
        my_base_text_view.setAdapter(my_adapter);
        my_base_text_view.setHintTextColor(Color.LTGRAY);

        Button btn = (Button) findViewById(R.id.ok_button);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent my_intent = getIntent();

                int category_id = my_intent.getIntExtra("cat", -2);

                //TODO: handle -2 error!
                Category parent = new Category(category_id, null, null, null, null, null, null);
                //ArrayList<String> tags = my_intent.getStringArrayListExtra("tags");

                //String value = (String) getString(R.id.addValue);
                EditText value = (EditText) findViewById(R.id.editTextAddValue);

                if(value.getText().toString().equals(null) || value.getText().toString().equals("") ||
                        value.getText().toString().length() == 0) {
                    Toast.makeText(getApplicationContext(), "No value added", Toast.LENGTH_SHORT).show();
                    return;
                }

                String val = value.getText().toString();
                double endval = Double.parseDouble(val)*100;
                long longvalue = (long) Math.round(endval);

                DAO DataAccessObject = DAO.instance(AddValueActivity.this);

                ArrayList<String> tagList = new ArrayList<>();
                for(String tag : tags.values())
                {
                    tagList.add(tag);
                }
                Value insert_value = new Value(-1, longvalue, new Date().getTime(), parent, tagList);
                //TODO: get selected date (not always "today" or default)
                DataAccessObject.insertValue(insert_value);

                AddValueActivity.super.onBackPressed();
            }
        });
        
         my_base_text_view.setOnEditorActionListener(new TextView.OnEditorActionListener() {
           @Override
           public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

               if (actionId == EditorInfo.IME_ACTION_DONE) {
                   if (v.length() == 0) return false;
                   int id = v.getId();
                   String tag = v.getEditableText().toString();

                   if(!tags.containsKey(id) && tags.size() < 2) {
                       AutoCompleteTextView next_view = new AutoCompleteTextView(getApplicationContext());
                       next_view.setOnEditorActionListener(this);
                       RelativeLayout.LayoutParams myLayout = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                               ViewGroup.LayoutParams.WRAP_CONTENT);

                       RelativeLayout myRelativeLayout = (RelativeLayout) findViewById(R.id.layout_add_value);
                       myLayout.addRule(RelativeLayout.BELOW, last_text.getId());

                       next_view.setBackground(getResources().getDrawable(R.drawable.abc_edit_text_material));
                       next_view.setHint("Type Tag");
                       next_view.setVisibility(View.VISIBLE);
                       next_view.setImeOptions(EditorInfo.IME_ACTION_DONE);
                       next_view.setLayoutParams(myLayout);
                       next_view.setTextColor(Color.BLACK);
                       next_view.setHintTextColor(Color.LTGRAY);
                       next_view.setInputType(InputType.TYPE_TEXT_FLAG_AUTO_COMPLETE);
                       int next_id = View.generateViewId();
                       next_view.setId(next_id);
                       next_view.setAdapter(my_adapter);
                       myRelativeLayout.addView(next_view);
                       last_text = next_view;
                   }
                   tags.put(id, tag);
               }
               return false;
               }
           });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }
}
