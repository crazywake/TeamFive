package com.bill.pocket.pocketbill;

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

import java.util.ArrayList;


@SuppressWarnings("deprecation")

public class MainActivity extends ActionBarActivity {

    public enum State {
        MAIN,
        SUB,
        POPUP
    }

    private DAO dataAccessObject;

    public State pre_popup_state = State.MAIN;
    public State cur_state = State.MAIN;

    private PopupWindow popupWindow = null;

    MainActivity this_class;

    ArrayList<Category> main_categories;

    Category current_main_category = null;

    ListView categoryView;
    ArrayAdapter<Category> adapter;

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
        dataAccessObject = DAO.instance(this);


        insertDummyData();
        main_categories = dataAccessObject.getMainData();

        adapter = new ArrayAdapter<>(this_class, android.R.layout.simple_list_item_1, android.R.id.text1, main_categories);

        // Assign adapter to ListView
        categoryView.setAdapter(adapter);

        // ListView Item Click Listener
        categoryView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                // ListView Clicked item value
                Category clickedItem = (Category) categoryView.getItemAtPosition(position);

                ArrayList<Category> new_category_list = clickedItem.getSubcategories();
                if(new_category_list == null) {
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
                    return;
                }
                if(cur_state == State.MAIN) {
                    current_main_category = clickedItem;
                }
                loadAdapter(new_category_list);
                cur_state = State.SUB;
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
                        Category clickedItem = (Category) categoryView.getItemAtPosition(position);
                        Category parent = clickedItem.getParent();
                        if(parent == null) {
                            main_categories.remove(position);
                            //TODO: remove items (children) from clickedItem category, remove clickedItem from database
                            loadAdapter(main_categories);
                            cur_state = State.MAIN;
                        } else {
                            parent.getSubcategories().remove(clickedItem);
                            //TODO: remove items (children) from clickedItem category, remove clickedItem from database
                            loadAdapter(parent.getSubcategories());
                            cur_state = State.SUB;
                        }
                        popupWindow.dismiss();
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
                current_main_category = null;
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

    public void insertDummyData() {

        int dummy1main = (int) dataAccessObject.insertMainCat("Gas");
        int dummy2main = (int) dataAccessObject.insertMainCat("Groceries");
        int dummy3main = (int) dataAccessObject.insertMainCat("Shopping");

        /*int dummy1sub = (int) */dataAccessObject.insertSubCat("Shell", dummy1main);
        /*int dummy2sub = (int) */dataAccessObject.insertSubCat("BP", dummy1main);
        /*int dummy3sub = (int) */dataAccessObject.insertSubCat("Jet", dummy1main);
        /*int dummy4sub = (int) */dataAccessObject.insertSubCat("Spar", dummy2main);
        /*int dummy5sub = (int) */dataAccessObject.insertSubCat("Billa", dummy2main);
        /*int dummy6sub = (int) */dataAccessObject.insertSubCat("Merkur", dummy2main);
        /*int dummy7sub = (int) */dataAccessObject.insertSubCat("New Yorker", dummy3main);
        /*int dummy8sub = (int) */dataAccessObject.insertSubCat("H&M", dummy3main);
        /*int dummy9sub = (int) */dataAccessObject.insertSubCat("C&A", dummy3main);

        /*

        ArrayList<Category> subcat1list = new ArrayList<>();
        ArrayList<Category> subcat2list = new ArrayList<>();
        ArrayList<Category> subcat3list = new ArrayList<>();

        //---------- main category dummies ----------
        Category maincat1 = new Category(0, "Gas", null, subcat1list, new ArrayList<Value>(), Category.Type.MAIN);
        Category maincat2 = new Category(1, "Groceries", null, subcat2list, new ArrayList<Value>(), Category.Type.MAIN);
        Category maincat3 = new Category(2, "Shopping", null, subcat3list, new ArrayList<Value>(), Category.Type.MAIN);
        maincatlist.addAll(Arrays.asList(maincat1, maincat2, maincat3));

        //---------- subcat 1 category dummies + value arrays ----------
        ArrayList<Value> val11 = new ArrayList<>();
        ArrayList<Value> val12 = new ArrayList<>();
        ArrayList<Value> val13 = new ArrayList<>();
        Category subcat11 = new Category(3, "Shell", maincat1, null, val11, Category.Type.SUB);
        Category subcat12 = new Category(4, "BP", maincat1, null, val12, Category.Type.SUB);
        Category subcat13 = new Category(5, "Jet", maincat1, null, val13, Category.Type.SUB);
        subcat1list.addAll(Arrays.asList(subcat11, subcat12, subcat13));

        //---------- subcat 2 category dummies + value arrays ----------
        ArrayList<Value> val21 = new ArrayList<>();
        ArrayList<Value> val22 = new ArrayList<>();
        ArrayList<Value> val23 = new ArrayList<>();
        Category subcat21 = new Category(6, "Spar", maincat2, null, val21, Category.Type.SUB);
        Category subcat22 = new Category(7, "Billa", maincat2, null, val22, Category.Type.SUB);
        Category subcat23 = new Category(8, "Merkur", maincat2, null, val23, Category.Type.SUB);
        subcat2list.addAll(Arrays.asList(subcat21, subcat22, subcat23));

        //---------- subcat 3 category dummies + value arrays ----------
        ArrayList<Value> val31 = new ArrayList<>();
        ArrayList<Value> val32 = new ArrayList<>();
        ArrayList<Value> val33 = new ArrayList<>();
        Category subcat31 = new Category(9, "New Yorker", maincat3, null, val31, Category.Type.SUB);
        Category subcat32 = new Category(10, "H&M", maincat3, null, val32, Category.Type.SUB);
        Category subcat33 = new Category(11, "C&A", maincat3, null, val33, Category.Type.SUB);
        subcat3list.addAll(Arrays.asList(subcat31, subcat32, subcat33));

        //---------- dummy values ----------
        val11.addAll(Arrays.asList(new Value(0, 100, 10000000, subcat11),
                                   new Value(1, 200, 11000000, subcat11),
                                   new Value(2, 300, 11100000, subcat11)));
        val12.addAll(Arrays.asList(new Value(3, 400, 11110000, subcat12),
                                   new Value(4, 500, 11111000, subcat12),
                                   new Value(5, 600, 11111100, subcat12)));
        val13.addAll(Arrays.asList(new Value(6, 700, 11111110, subcat13),
                                   new Value(7, 800, 11111111, subcat13),
                                   new Value(8, 900, 20000000, subcat13)));

        val21.addAll(Arrays.asList(new Value(9, 1000, 22000000, subcat21),
                                   new Value(10, 1100, 22200000, subcat21),
                                   new Value(11, 1200, 22220000, subcat21)));
        val22.addAll(Arrays.asList(new Value(12, 1300, 22222000, subcat22),
                                   new Value(13, 1400, 22222200, subcat22),
                                   new Value(14, 1500, 22222220, subcat22)));
        val23.addAll(Arrays.asList(new Value(15, 1600, 22222222, subcat23),
                                   new Value(16, 1700, 30000000, subcat23),
                                   new Value(17, 1800, 33000000, subcat23)));

        val31.addAll(Arrays.asList(new Value(18, 1900, 33300000, subcat31),
                                   new Value(19, 2000, 33330000, subcat31),
                                   new Value(20, 2100, 33333000, subcat31)));
        val32.addAll(Arrays.asList(new Value(21, 2200, 33333300, subcat32),
                                   new Value(22, 2300, 33333330, subcat32),
                                   new Value(23, 2400, 33333333, subcat32)));
        val33.addAll(Arrays.asList(new Value(24, 2500, 40000000, subcat33),
                                   new Value(25, 2600, 44000000, subcat33),
                                   new Value(26, 2700, 44400000, subcat33)));

        return maincatlist;*/
    }

    public void loadAdapter(ArrayList<Category> category_list) {
        //reload adapter
        adapter = new ArrayAdapter<>(this_class, android.R.layout.simple_list_item_1, android.R.id.text1, category_list);
        categoryView.setAdapter(adapter);
    }
}
