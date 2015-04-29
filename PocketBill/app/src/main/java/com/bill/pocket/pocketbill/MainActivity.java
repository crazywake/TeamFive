package com.bill.pocket.pocketbill;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.List;


public class MainActivity extends ActionBarActivity {

    MainActivity this_class;
    boolean isSubCategory = false;

    String[] main_categories = new String[] {"Gas","Groceries","Shopping"};

    HashMap<String, String[]> categories_hashmap = new HashMap<String, String[]>();

    ListView categoryView;
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this_class = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        categoryView = (ListView) findViewById(R.id.CategoryView);

        // Define a new Adapter
        // First parameter - Context
        // Second parameter - Layout for the row
        // Third parameter - ID of the TextView to which the data is written
        // Fourth - the Array of data

        //fill hashmap with subcategories
        createSubcategories();

        adapter = new ArrayAdapter<String>(this_class, android.R.layout.simple_list_item_1, android.R.id.text1, main_categories);
        //subadapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, subcategories);

        // Assign adapter to ListView
        categoryView.setAdapter(adapter);

        // ListView Item Click Listener
        categoryView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                int itemPosition = position;

                // ListView Clicked item value
                String itemValue = (String) categoryView.getItemAtPosition(position);


                if(isSubCategory)
                {
                    AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);

                    alert.setTitle("Add Value");
                    final EditText input = new EditText(MainActivity.this);
                    input.setInputType(InputType.TYPE_CLASS_NUMBER);
                    alert.setView(input);

                    alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            String value = input.getText().toString();
                            // Do something with value!
                        }
                    });

                    alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            // Canceled.
                        }
                    });

                    alert.show();
                }else{
                    String[] sublist = categories_hashmap.get(itemValue);
                    isSubCategory=true;
                    if(sublist == null) {
                        Toast.makeText(getApplicationContext(),
                                "No subcategory in " + itemValue, Toast.LENGTH_SHORT)
                                .show();
                        return;
                    }

                    //reload adapter
                    adapter = new ArrayAdapter<String>(this_class, android.R.layout.simple_list_item_1, android.R.id.text1, sublist);

                    categoryView.setAdapter(adapter);
                }
            }

        });


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement.
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void createSubcategories() {
        String[] subtest1 = new String[] {"Shell","BP","Jet","OMV","Turmöl","Roth","Grünburg"};
        String[] subtest2 = new String[] {"Spar","Billa","Merkur","Hofer","Lidl"};
        String[] subtest3 = new String[] {"New Yorker","H&M","C&A"};

        categories_hashmap.put("Gas", subtest1);
        categories_hashmap.put("Groceries", subtest2);
        categories_hashmap.put("Shopping", subtest3);
    }

    private class StableArrayAdapter extends ArrayAdapter<String> {

        HashMap<String, Integer> mIdMap = new HashMap<>();

        public StableArrayAdapter(Context context, int textViewResourceId,
                                  List<String> objects) {
            super(context, textViewResourceId, objects);
            for (int i = 0; i < objects.size(); ++i) {
                mIdMap.put(objects.get(i), i);
            }
        }

        @Override
        public long getItemId(int position) {
            String item = getItem(position);
            return mIdMap.get(item);
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

    }


}
