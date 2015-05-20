package com.bill.pocket.pocketbill;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Toast;

import java.util.ArrayList;


@SuppressWarnings("deprecation")

public class MainActivity extends ActionBarActivity {

    public enum State {
        MAIN,
        SUB,
        POPUP
    }

    private MainActivity this_activity = this;

    //Navigation Drawer
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout, mDrawerLayout2;
    private String mActivityTitle = "PocketBill";

    //Database
    private DAO dataAccessObject;

    public State pre_popup_state = State.MAIN;
    public State cur_state = State.MAIN;

    private PopupWindow popupWindow = null;

    private Category current_main_category = null;
    private Category current_sub_category = null;

    private ListView categoryView;
    private ArrayAdapter<Category> adapter;

    ListView mDrawerList;
    ListView mDrawerList2;

    @Override
    protected void onDestroy()
    {
        dataAccessObject.close();
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        String[] mnavDrawerContent;
        this_activity = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        categoryView = (ListView) findViewById(R.id.CategoryView);
        categoryView.setLongClickable(true);
        //fill hashmap with subcategories
        dataAccessObject = DAO.instance(this);
        //insertDummyData();
        main_categories = dataAccessObject.getMainData();

        //Navigation Drawer
        mnavDrawerContent = getResources().getStringArray(R.array.navigationDrawerContent);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        // Set the adapter for the list view
        mDrawerList.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, mnavDrawerContent));
        mDrawerList.setSelector(android.R.color.holo_blue_dark);
        mDrawerList.setOnItemClickListener(new NavigationDrawerListener(this, mDrawerLayout, adapter, categoryView, main_categories));

        mDrawerList2 = (ListView) findViewById(R.id.right_drawer);
        // Set the adapter for the list view
        //mDrawerList2.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, {""}));
        //mDrawerList2.setSelector(android.R.color.holo_blue_dark);
        //mDrawerList2.setOnItemClickListener(new NavigationDrawerListener(this,mDrawerLayout2,adapter,categoryView,main_categories));


        setupDrawer(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        // Define a new Adapter
        // First parameter - Context
        // Second parameter - Layout for the row
        // Third parameter - ID of the TextView to which the data is written
        // Fourth - the Array of data

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, android.R.id.text1, main_categories);
        // Assign adapter to ListView
        categoryView.setAdapter(adapter);

        // ListView Item Click Listener
        categoryView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    final int position, long id) {

                Category clickedItem = (Category) categoryView.getItemAtPosition(position);
                ArrayList<Category> new_category_list = clickedItem.getSubcategories();

                if (cur_state == State.SUB) {
                    System.out.println("in sub");
                    current_sub_category = clickedItem;

                    Intent my_intent = new Intent(getApplicationContext(), AddValueActivity.class);

                    my_intent.putExtra("Main", current_main_category.getId());
                    my_intent.putExtra("Sub", current_sub_category.getId());
                    startActivity(my_intent);
                    //cur_state = State.MAIN;
                }

                if(cur_state == State.MAIN) {
                    System.out.println("back in main");
                    current_main_category = clickedItem;
                    loadAdapter(new_category_list);
                    cur_state = State.SUB;
                }

            } });
                // ListView Clicked item value
/*                Category clickedItem = (Category) categoryView.getItemAtPosition(position);

                ArrayList<Category> new_category_list = clickedItem.getSubcategories();
                if(new_category_list == null) {
                    AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);

                    alert.setTitle("Add Value");
                    final EditText input = new EditText(MainActivity.this);
                    input.setInputType(InputType.TYPE_CLASS_NUMBER);
                    alert.setView(input);

                    alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            String value = input.getText().toString();

                            // Do something with value!
                            // TODO: main and sub categories
                            dataAccessObject.insertPayment(Integer.parseInt(value), current_main_category.getId(), 1);
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
*/
            categoryView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener()

            {
                @Override
                public boolean onItemLongClick (AdapterView < ? > parent, View view,
                final int position, long id){

                pre_popup_state = cur_state;
                cur_state = State.POPUP;

                if (popupWindow != null && popupWindow.isShowing()) {
                    popupWindow.dismiss();
                }

                LayoutInflater layoutInflater
                        = (LayoutInflater) getBaseContext()
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
                        popupWindow.dismiss();
                        Category clickedItem = (Category) categoryView.getItemAtPosition(position);
                        CategoryEditor catedit = new CategoryEditor(CategoryEditor.Type.EDIT, clickedItem, MainActivity.this, main_categories, clickedItem.getParent());
                        popupWindow = catedit.display();
                        pre_popup_state = cur_state;
                        cur_state = State.POPUP;
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
                        if (parent == null) {
                            main_categories.remove(position);
                            dataAccessObject.deleteMainCategory(clickedItem.getId());

                            loadAdapter(main_categories);
                            cur_state = State.MAIN;
                        } else {
                            parent.getSubcategories().remove(clickedItem);
                            dataAccessObject.deleteSubCategory(clickedItem.getId());

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
            }

            );

        }
 
        @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    private void setupDrawer(Context cnt) {
        final Context cnt2 = cnt;
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,R.drawable.ic_drawer , R.string.drawer_open, R.string.drawer_close) {

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);

                if (drawerView.getTag().toString().equals("left_drawer"))
                    getSupportActionBar().setTitle("Navigation");

                if (drawerView.getTag().toString().equals("right_drawer")) {
                    getSupportActionBar().setTitle("MAIN_CATEGORY");

                    ArrayList<String> mnavDrawerContent2 = dataAccessObject.getPayments();
                    // TODO: Get Values from DB

                    mDrawerList2.setAdapter(new ArrayAdapter<>(cnt2, android.R.layout.simple_list_item_1, mnavDrawerContent2));

                }
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                getSupportActionBar().setTitle(mActivityTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };

        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    @Override
    public void onBackPressed() {
        System.out.println(cur_state);
        System.out.println(current_main_category.getId());
        System.out.println(current_sub_category != null ? current_sub_category.getId() : "0");

         switch(cur_state) {
            case MAIN: {
                finish();
                break;
            }
            case SUB: {
                //switch to main
                loadAdapter(getMain_categories());
                current_main_category = null;
                current_sub_category = null;
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
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        if(id == R.id.addEditCategory)
        {
            CategoryEditor catedit = new CategoryEditor(CategoryEditor.Type.ADD, null, this, main_categories, current_main_category);
            popupWindow = catedit.display();
            pre_popup_state = cur_state;
            cur_state = State.POPUP;
        }
        return super.onOptionsItemSelected(item);
    }

    public void insertDummyData() {

        int dummy1main = (int) dataAccessObject.insertMainCat("Gas");
        int dummy2main = (int) dataAccessObject.insertMainCat("Groceries");
        int dummy3main = (int) dataAccessObject.insertMainCat("Shopping");

        int dummy1sub = (int) dataAccessObject.insertSubCat("Shell", dummy1main);
        int dummy2sub = (int) dataAccessObject.insertSubCat("BP", dummy1main);
        int dummy3sub = (int) dataAccessObject.insertSubCat("Jet", dummy1main);
        int dummy4sub = (int) dataAccessObject.insertSubCat("Spar", dummy2main);
        int dummy5sub = (int) dataAccessObject.insertSubCat("Billa", dummy2main);
        int dummy6sub = (int) dataAccessObject.insertSubCat("Merkur", dummy2main);
        int dummy7sub = (int) dataAccessObject.insertSubCat("New Yorker", dummy3main);
        int dummy8sub = (int) dataAccessObject.insertSubCat("H&M", dummy3main);
        int dummy9sub = (int) dataAccessObject.insertSubCat("C&A", dummy3main);

        //test comment delete!
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
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, android.R.id.text1, category_list);
        categoryView.setAdapter(adapter);
    }

    public void updateLists(int parent_id) { //parent id = -1 if no parent (man category view)
        main_categories = dataAccessObject.getMainData();

        if(parent_id != -1) {
            Category parent = getCategoryFromID(parent_id);
            if(parent != null) {
                loadAdapter(parent.getSubcategories());
                cur_state = State.SUB;
                pre_popup_state = cur_state;
            }
            else {
                Toast.makeText(this, "Fatal Error is fatal! Database refused to cooperate and was executed! " +
                        "Restart the app or contact support. Good Luck.", Toast.LENGTH_LONG).show();
                finish();
            }

        } else {
            loadAdapter(main_categories);
            cur_state = State.MAIN;
            pre_popup_state = cur_state;
        }

    }

    public Category getCategoryFromID(int ID) {
        for(Category cat : main_categories) {
            if(cat.getId() == ID)
                return cat;
        }

        return null;
    }




    public DAO getDAO() { return dataAccessObject; }

    public State getPre_popup_state() {
        return pre_popup_state;
    }

    public void setPre_popup_state(State pre_popup_state) {
        this.pre_popup_state = pre_popup_state;
    }

    public State getCur_state() {
        return cur_state;
    }

    public void setCur_state(State cur_state) {
        this.cur_state = cur_state;
    }

    public ArrayList<Category> getMain_categories() {
        return main_categories;
    }

    public void setMain_categories(ArrayList<Category> main_categories) {
        this.main_categories = main_categories;
    }

    public Category getCurrent_main_category() {
        return current_main_category;
    }

    public void setCurrent_main_category(Category current_main_category) {
        this.current_main_category = current_main_category;
    }

    public ArrayAdapter<Category> getAdapter() {
        return adapter;
    }

    public void setAdapter(ArrayAdapter<Category> adapter) {
        this.adapter = adapter;
    }
}
