package com.bill.pocket.pocketbill;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;


public class MainActivity extends ActionBarActivity {

    public enum State {
        MAIN,
        SUB
    }

    PopupWindow popupWindow = null;

    MainActivity this_class;
    State state = State.MAIN;

    ArrayList<String> main_categories = new ArrayList<>(Arrays.asList("Gas","Groceries","Shopping"));
    ArrayList<String> current_categories = main_categories;

    HashMap<String, ArrayList<String>> categories_hashmap = new HashMap<>();

    ListView categoryView;
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        this_class = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        categoryView = (ListView) findViewById(R.id.CategoryView);
        categoryView.setLongClickable(true);

        // Define a new Adapter
        // First parameter - Context
        // Second parameter - Layout for the row
        // Third parameter - ID of the TextView to which the data is written
        // Fourth - the Array of data

        //fill hashmap with subcategories
        createSubcategories();

        adapter = new ArrayAdapter<>(this_class, android.R.layout.simple_list_item_1, android.R.id.text1, main_categories);

        // Assign adapter to ListView
        categoryView.setAdapter(adapter);

        // ListView Item Click Listener
        categoryView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                // ListView Clicked item value
                String itemValue = (String) categoryView.getItemAtPosition(position);



                if(state == State.SUB)
                {
                    AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);

                    alert.setTitle("Add Value");
                    final EditText input = new EditText(MainActivity.this);
                    input.setInputType(InputType.TYPE_CLASS_NUMBER);
                    alert.setView(input);

                    alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            //String value = input.getText().toString();

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


                    ArrayList<String> sublist = categories_hashmap.get(itemValue);
                    if(sublist == null)
                        return;

                    current_categories = sublist;
                    loadAdapter(current_categories);

                    //reload adapter
                    adapter = new ArrayAdapter<>(this_class, android.R.layout.simple_list_item_1, android.R.id.text1, sublist);

                    categoryView.setAdapter(adapter);
                    state = State.SUB;
                }
            }

        });

        categoryView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view,
                                        final int position, long id) {

                if(popupWindow != null && popupWindow.isShowing()) {
                    popupWindow.dismiss();
                }

                LayoutInflater layoutInflater
                        = (LayoutInflater)getBaseContext()
                        .getSystemService(LAYOUT_INFLATER_SERVICE);

                View popupView = layoutInflater.inflate(R.layout.category_popup, null);
                popupWindow = new PopupWindow(
                        popupView,
                        LayoutParams.WRAP_CONTENT,
                        LayoutParams.WRAP_CONTENT);

                Button btnEdit = (Button) popupView.findViewById(R.id.edit);
                btnEdit.setOnClickListener(new Button.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // edit button clicked

                    }
                });

                Button btnDelete = (Button) popupView.findViewById(R.id.delete);
                btnDelete.setOnClickListener(new Button.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // delete
                        current_categories.remove(position);
                        loadAdapter(current_categories);
                        popupWindow.dismiss();
                    }
                });

                popupWindow.showAsDropDown(view);

                return true;
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
        return id == R.id.action_settings || super.onOptionsItemSelected(item);

    }

    public void createSubcategories() {
        ArrayList<String> subtest1 = new ArrayList<> (Arrays.asList("Shell","BP","Jet","OMV","Turmöl","Roth","Grünburg"));
        ArrayList<String> subtest2 = new ArrayList<> (Arrays.asList("Spar","Billa","Merkur","Hofer","Lidl"));
        ArrayList<String> subtest3 = new ArrayList<> (Arrays.asList("New Yorker","H&M","C&A"));

        categories_hashmap.put("Gas", subtest1);
        categories_hashmap.put("Groceries", subtest2);
        categories_hashmap.put("Shopping", subtest3);
    }

    public void loadAdapter(ArrayList<String> category_list) {
        //reload adapter
        adapter = new ArrayAdapter<>(this_class, android.R.layout.simple_list_item_1, android.R.id.text1, category_list);
        categoryView.setAdapter(adapter);
    }
}
