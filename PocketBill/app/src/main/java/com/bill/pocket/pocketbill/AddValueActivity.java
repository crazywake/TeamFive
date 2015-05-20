package com.bill.pocket.pocketbill;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import static java.lang.Math.*;


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
            Intent tmp = getIntent();

            int main_id = tmp.getIntExtra("Main", 0);
            int sub_id = tmp.getIntExtra("Sub", 0);

            //String value = (String) getString(R.id.addValue);
            EditText value = (EditText) findViewById(R.id.editTextAddValue);
            String val = value.getText().toString();
            double endval = Double.parseDouble(val)*100;
            int intval = (int) Math.round(endval);

            DAO DataAccessObject = DAO.instance(this);

            DataAccessObject.insertPayment(intval, main_id, sub_id);

            super.onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
