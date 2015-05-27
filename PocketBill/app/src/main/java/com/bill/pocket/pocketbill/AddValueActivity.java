package com.bill.pocket.pocketbill;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;


public class AddValueActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_value);

        Toast.makeText(getApplicationContext(), "AddValue Activity opened", Toast.LENGTH_SHORT).show();
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
        if (id == R.id.addValue) {
            Intent my_intent = getIntent();

            int category_id = my_intent.getIntExtra("parent", -2);
            //TODO: handle -2 error!
            Category parent = new Category(category_id, null, null, null, null, null, null);
            ArrayList<String> tags = my_intent.getStringArrayListExtra("tags");

            //String value = (String) getString(R.id.addValue);
            EditText value = (EditText) findViewById(R.id.editTextAddValue);
            String val = value.getText().toString();
            double endval = Double.parseDouble(val)*100;
            long longvalue = (long) Math.round(endval);

            DAO DataAccessObject = DAO.instance(this);

            Value insert_value = new Value(-1, longvalue, (int) (new Date()).getTime(), parent, tags);
            //TODO: get selected date (not always "today" or default)
            DataAccessObject.insertValue(insert_value);

            super.onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
