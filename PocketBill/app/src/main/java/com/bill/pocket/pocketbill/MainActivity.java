package com.bill.pocket.pocketbill;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;


@SuppressWarnings("deprecation")

public class MainActivity extends ActionBarActivity {

    public enum State {
        MAIN,
        SUB,
        POPUP
    }


    public State pre_popup_state = State.MAIN;
    public State cur_state = State.MAIN;

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

                ArrayList<String> sublist = categories_hashmap.get(itemValue);
                if(sublist == null) {
                    Toast.makeText(getApplicationContext(),
                            "No subcategory in " + itemValue, Toast.LENGTH_SHORT)
                            .show();
                    return;
                }
                current_categories = sublist;
                loadAdapter(current_categories);
                cur_state = State.SUB;


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


                    sublist = categories_hashmap.get(itemValue);
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

                pre_popup_state = cur_state;
                cur_state = State.POPUP;

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

                popupWindow.setBackgroundDrawable(new BitmapDrawable());
                popupWindow.setOutsideTouchable(true);

                Button btnEdit = (Button) popupView.findViewById(R.id.edit);
                btnEdit.setOnClickListener(new Button.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // edit button clicked

                        //TODO: EDIT IN DATABASE!!!!
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
                        //TODO: DELETE RECURSIVELY FROM DATABASE!!!!
                    }
                });

                popupWindow.showAsDropDown(view);

                return true;
            }
        });

    }

    @Override
    public void onBackPressed() {
         switch(cur_state) {
            case MAIN: {
                finish();
                break;
            }
            case SUB: {
                //switch to main
                loadAdapter(main_categories);
                cur_state = State.MAIN;
                break;
            }
            case POPUP: {
                //close popup
                popupWindow.dismiss();
                cur_state = pre_popup_state;
                break;
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement.
        if(id == R.id.addCategory)
        {
            AlertDialog.Builder alert = new AlertDialog.Builder(this);

            alert.setTitle("New Category");
            alert.setMessage("Please enter the name of the new Category");

// Set an EditText view to get user input
            final EditText input = new EditText(this);

            alert.setView(input);

            alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {

                    // Do something with value!
                }
            });

            alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    // Canceled.
                }
            });

            alert.show();
        }
        return super.onOptionsItemSelected(item);
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
