package com.bill.pocket.pocketbill;

import android.graphics.Color;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;


public class AddValueActivity extends ActionBarActivity {

    AutoCompleteTextView last_text = null;
    ArrayAdapter<String> my_adapter = null;
    ArrayList<String> my_tags = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_value);


        final AutoCompleteTextView my_base_text_view = (AutoCompleteTextView) findViewById(R.id.autocomplete_text);
        last_text = my_base_text_view;
        my_adapter = new ArrayAdapter<String>(this,android.R.layout.simple_dropdown_item_1line,new String[] {"Fun", "Family", "Uni"} );
        my_tags = new ArrayList<String>();
        my_base_text_view.setAdapter(my_adapter);
        my_base_text_view.setHintTextColor(Color.LTGRAY);

         my_base_text_view.setOnEditorActionListener(new TextView.OnEditorActionListener() {
           @Override
           public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

               if (actionId == EditorInfo.IME_ACTION_DONE) {
                   if (v.length() == 0) return false;
                   String new_string = v.getEditableText().toString();
                   for (String current_string : my_tags)
                   {
                       if (current_string.equals(new_string)) return false;
                   }
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
                   my_tags.add(new_string);
                   last_text = next_view;
               }
               return false;
               }
           });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_value, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
