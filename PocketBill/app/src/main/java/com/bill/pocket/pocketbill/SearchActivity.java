package com.bill.pocket.pocketbill;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.bill.pocket.pocketbill.MultiSpinner.MultiSpinnerListener;

import java.util.ArrayList;
import java.util.Date;


public class SearchActivity extends ActionBarActivity implements MultiSpinnerListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        MultiSpinner mainCatSpinner = (MultiSpinner) findViewById(R.id.search_maincat_spinner);
        MultiSpinner subCatSpinner = (MultiSpinner) findViewById(R.id.search_subcat_spinner);

        ArrayList<String> mainCats = new ArrayList<>();
        ArrayList<String> subCats = new ArrayList<>();

        for(Category c : CategoryData.getInstance().getMainCategories()) {
            mainCats.add(c.getName());

            for (Category sub : c.getSubcategories()) {
                subCats.add(sub.getName());
            }
        }

        mainCatSpinner.setItems(mainCats, "Main categories", this);
        subCatSpinner.setItems(subCats, "Sub categories", this);

        final ListView valuesView = (ListView) findViewById(R.id.search_value_view);
        ArrayList<Value> values = new ArrayList<>();

        // TODO insert real values from database
        for (int i = 0; i < 10; i++) {
            values.add(new Value(1, 123, (int) new Date().getTime(), CategoryData.getInstance().getMainCategories().get(0)));
            values.add(new Value(2, 456, (int) new Date().getTime(), CategoryData.getInstance().getMainCategories().get(1)));
            values.add(new Value(3, 789, (int) new Date().getTime(), CategoryData.getInstance().getMainCategories().get(2)));
        }


        ArrayAdapter adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, android.R.id.text1, values);
        valuesView.setAdapter(adapter);


        valuesView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    final int position, long id) {

                Value clickedItem = (Value) valuesView.getItemAtPosition(position);
                // TODO show Value
                //Intent my_intent = new Intent(getApplicationContext(), AddValueActivity.class);
                //startActivity(my_intent);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search, menu);
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

    @Override
    public void onItemsSelected(boolean[] selected) {

    }
}
