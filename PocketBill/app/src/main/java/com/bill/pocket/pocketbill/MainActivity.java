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

import java.lang.reflect.Array;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;


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
        setMain_categories(dataAccessObject.getCategories(Category.ROOT_CATEGORY));

        //Navigation Drawer
        mnavDrawerContent = getResources().getStringArray(R.array.navigationDrawerContent);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        // Set the adapter for the list view
        mDrawerList.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, mnavDrawerContent));
        mDrawerList.setSelector(android.R.color.holo_blue_dark);
        mDrawerList.setOnItemClickListener(new NavigationDrawerListener(this_activity, this, mDrawerLayout, adapter, categoryView, getMain_categories()));

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

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, android.R.id.text1, getMain_categories());
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

                    my_intent.putExtra("cat", current_sub_category.getId());
                    my_intent.putExtra("tags", new ArrayList<String>());
                    //TODO: pass tags to AddValueActivity (change Arraylist above)

                    startActivity(my_intent);
                    //cur_state = State.MAIN;
                }

                if(cur_state == State.MAIN) {
                    System.out.println("back in main");
                    current_main_category = clickedItem;
                    getSupportActionBar().setTitle(current_main_category.getName());
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
                        CategoryEditor catedit = new CategoryEditor(CategoryEditor.Type.EDIT, clickedItem, MainActivity.this, getMain_categories(), clickedItem.getParent());
                        popupWindow = catedit.display();
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
                            getMain_categories().remove(position);
                            dataAccessObject.deleteCategory(clickedItem);

                            loadAdapter(getMain_categories());
                            cur_state = State.MAIN;
                        } else {
                            parent.getSubcategories().remove(clickedItem);
                            dataAccessObject.deleteCategory(clickedItem);

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

                    ArrayList<Value> mnavDrawerContent2 = null;

                    if(cur_state == State.MAIN) {
                        getSupportActionBar().setTitle("All Receipts");
                      //  mnavDrawerContent2 = dataAccessObject.getPayments();
                        mnavDrawerContent2 = getValuesFromCategory(Category.ROOT_CATEGORY);
                    }
                    else {
                        //getSupportActionBar().setTitle(current_main_category.getName());

                        System.out.println("ID of main category :" + current_main_category.getId());
                        mnavDrawerContent2 = getValuesFromCategory(current_main_category);
                    }

                    ArrayList<String> drawerContent = new ArrayList<String>();

                    DateFormat formats = DateFormat.getDateInstance();

                    for(Value value: mnavDrawerContent2)
                    {
                        drawerContent.add(value.getValue() + formats.format(value.getDate()) + value.getTags());
                    }

                    if(drawerContent.size() == 0)
                    {
                       drawerContent.add("No Payments found !");
                    }

                    mDrawerList2.setAdapter(new ArrayAdapter<>(cnt2, android.R.layout.simple_list_item_1, drawerContent));

                }
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                if (current_main_category != null)
                    getSupportActionBar().setTitle(current_main_category.getName());
                else
                getSupportActionBar().setTitle(mActivityTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };

        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    @Override
    public void onBackPressed() {
        System.out.println("BACK PRESSED");
        System.out.println("cur state:" + cur_state);
        System.out.println("pre p state:" + pre_popup_state);
        System.out.println("cur main cat:" + (current_main_category != null ? current_main_category.getId() : "0"));
        System.out.println("cur sub cat:" + (current_sub_category != null ? current_sub_category.getId() : "0"));

        switch(cur_state) {
            case MAIN: {
                moveTaskToBack(true);
                break;
            }
            case SUB: {
                //switch to main
                loadAdapter(getMain_categories());
                current_sub_category = null;
                current_main_category = null;
                cur_state = State.MAIN;
                getSupportActionBar().setTitle(mActivityTitle);
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
            CategoryEditor catedit = new CategoryEditor(CategoryEditor.Type.ADD, null, this, getMain_categories(), current_main_category);
            popupWindow = catedit.display();
            System.out.println("IF ADDEDIT:" + pre_popup_state);
            pre_popup_state = cur_state;
            cur_state = State.POPUP;
        } else if(id == R.id.searchButton) {
            Intent my_intent = new Intent(getApplicationContext(), SearchActivity.class);
            startActivity(my_intent);
        }
        return super.onOptionsItemSelected(item);
    }

    public void insertDummyData() {
        Category dummy1main = new Category(-1, "Gas", Category.ROOT_CATEGORY, null, null, Category.Type.MAIN, Category.DEFAULT_COLOR);
        Category dummy2main = new Category(-1, "Groceries", Category.ROOT_CATEGORY, null, null, Category.Type.MAIN, Category.DEFAULT_COLOR);
        Category dummy3main = new Category(-1, "Shopping", Category.ROOT_CATEGORY, null, null, Category.Type.MAIN, Category.DEFAULT_COLOR);

        dataAccessObject.insertCategory(dummy1main);
        dataAccessObject.insertCategory(dummy2main);
        dataAccessObject.insertCategory(dummy3main);

        dataAccessObject.insertCategory(new Category(-1, "Shell", dummy1main, null, null, Category.Type.SUB, Category.DEFAULT_COLOR));
        Category hans = new Category(-1, "BP", dummy1main, null, null, Category.Type.SUB, Category.DEFAULT_COLOR);
        boolean igzud = dataAccessObject.insertCategory(hans);
        dataAccessObject.insertCategory(new Category(-1, "Jet", dummy1main, null, null, Category.Type.SUB, Category.DEFAULT_COLOR));
        dataAccessObject.insertCategory(new Category(-1, "Spar", dummy2main, null, null, Category.Type.SUB, Category.DEFAULT_COLOR));
        dataAccessObject.insertCategory(new Category(-1, "Billa", dummy2main, null, null, Category.Type.SUB, Category.DEFAULT_COLOR));
        dataAccessObject.insertCategory(new Category(-1, "Merkur", dummy2main, null, null, Category.Type.SUB, Category.DEFAULT_COLOR));
        dataAccessObject.insertCategory(new Category(-1, "New Yorker", dummy3main, null, null, Category.Type.SUB, Category.DEFAULT_COLOR));
        dataAccessObject.insertCategory(new Category(-1, "H&M", dummy3main, null, null, Category.Type.SUB, Category.DEFAULT_COLOR));
        dataAccessObject.insertCategory(new Category(-1, "C&A", dummy3main, null, null, Category.Type.SUB, Category.DEFAULT_COLOR));
    }

    public void loadAdapter(ArrayList<Category> category_list) {
        //reload adapter
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, android.R.id.text1, category_list);
        categoryView.setAdapter(adapter);
    }

    public void updateLists(int parent_id) { //parent id = -1 if no parent (man category view)
        setMain_categories(dataAccessObject.getCategories(Category.ROOT_CATEGORY));

        if(parent_id != -1) {
            Category parent = getCategoryFromID(parent_id);
            if(parent != null) {
                loadAdapter(parent.getSubcategories());
                cur_state = State.SUB;
                System.out.println("pid = -1:" + pre_popup_state);
                pre_popup_state = cur_state;
            }
            else {
                Toast.makeText(this, "Fatal Error is fatal! Database refused to cooperate and was executed! " +
                        "Restart the app or contact support. Good Luck.", Toast.LENGTH_LONG).show();
              System.out.println("Just a test");
              finish();
            }
        } else {
            loadAdapter(getMain_categories());
            cur_state = State.MAIN;
            System.out.println("else:" + pre_popup_state);
            pre_popup_state = cur_state;
        }

    }

    public Category getCategoryFromID(int ID) {
        for(Category cat : getMain_categories()) {
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
        System.out.println("In setter:" + pre_popup_state);
        this.pre_popup_state = pre_popup_state;
    }

    public State getCur_state() {
        return cur_state;
    }

    public void setCur_state(State cur_state) {
        this.cur_state = cur_state;
    }

    public ArrayList<Category> getMain_categories() {
        return CategoryData.getInstance().getMainCategories();
    }

    public void setMain_categories(ArrayList<Category> main_categories) {
        CategoryData.getInstance().setMainCategories(main_categories);
    }

    public Category getCurrent_main_category() {
        return current_main_category;
    }

    public void setCurrent_main_category(Category current_main_category) {
        this.current_main_category = current_main_category;
    }

    public ArrayList<Value> getValuesFromCategory(Category cat) {
        ArrayList<Value> values = new ArrayList<>();
        ArrayList<Category> categories = getMain_categories();
        if(cat != Category.ROOT_CATEGORY) {
            categories = cat.getSubcategories();
        }
        for(Category category : categories) {
            values.addAll(category.getValues());

            for(Category subcat : category.getSubcategories()) {
                values.addAll(subcat.getValues());
            }
        }

        return values;
    }

    public ArrayAdapter<Category> getAdapter() {
        return adapter;
    }

    public void setAdapter(ArrayAdapter<Category> adapter) {
        this.adapter = adapter;
    }
}
