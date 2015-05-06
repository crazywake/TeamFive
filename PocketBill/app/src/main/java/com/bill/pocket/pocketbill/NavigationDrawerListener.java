package com.bill.pocket.pocketbill;

import android.app.AlertDialog;
import android.content.Context;
import android.support.v4.widget.DrawerLayout;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by Thomas on 06.05.2015.
 */
public class NavigationDrawerListener implements AdapterView.OnItemClickListener {

    private Context mcont;
    private DrawerLayout mlayout;
    private ArrayAdapter madapter;
    ListView mcategoryView;
    ArrayList<Category> mmain_categories;
    /**
     * Callback method to be invoked when an item in this AdapterView has
     * been clicked.
     * <p/>
     * Implementers can call getItemAtPosition(position) if they need
     * to access the data associated with the selected item.
     *
     * @param parent   The AdapterView where the click happened.
     * @param view     The view within the AdapterView that was clicked (this
     *                 will be a view provided by the adapter)
     * @param position The position of the view in the adapter.
     * @param id       The row id of the item that was clicked.
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        mlayout.closeDrawers();
        Toast.makeText(mcont.getApplicationContext(), mcont.getResources().getStringArray(R.array.navigationDrawerContent)[position],Toast.LENGTH_SHORT).show();
        if(position == 0)
        {
            Log.d(mcont.getPackageName(),"mmain_categories Size = "+mmain_categories.size());
            madapter = new ArrayAdapter<>(mcont, android.R.layout.simple_list_item_1, android.R.id.text1, mmain_categories);
            mcategoryView.setAdapter(madapter);
        }else if(mcont.getResources().getStringArray(R.array.navigationDrawerContent)[position].equals("About"))
        {
            AlertDialog.Builder alert = new AlertDialog.Builder(mcont);
            alert.setTitle("PocketBill");
            alert.setMessage(Html.fromHtml(mcont.getResources().getString(R.string.about_text).concat(" <a href=\"http:\\www.google.com\">www.google.com</a>")));
            alert.show();

        }


    }

    public NavigationDrawerListener (Context context, DrawerLayout layout, ArrayAdapter adapter, ListView categoryView, ArrayList<Category> main_categories)
    {
        mcont=context;
        mlayout=layout;
        madapter=adapter;
        mmain_categories=main_categories;
        mcategoryView=categoryView;

    }

    /* Called when drawer is closed */
    public void onDrawerClosed(View view) {
        //Put your code here
    }

    /* Called when a drawer is opened */
    public void onDrawerOpened(View drawerView) {
        //Put your code here
    }

}
