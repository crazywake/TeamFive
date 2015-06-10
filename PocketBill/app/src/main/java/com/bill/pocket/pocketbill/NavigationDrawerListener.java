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

import java.util.ArrayList;

public class NavigationDrawerListener implements AdapterView.OnItemClickListener {

    private MainActivity mact;
    private Context mcont;
    private DrawerLayout mlayout;
    private ArrayAdapter madapter;
    ListView mcategoryView;
    ArrayList<Category> mmain_categories;

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        mlayout.closeDrawers();

        if(position == 0)
        {
            mact.updateLists(-1);
            mact.getSupportActionBar().setTitle("Pocket Bill");
            Log.d(mcont.getPackageName(),"mmain_categories Size = "+mmain_categories.size());
            madapter = new ArrayAdapter<>(mcont, android.R.layout.simple_list_item_1, android.R.id.text1, mmain_categories);
            mcategoryView.setAdapter(madapter);

            mact.setCur_state(MainActivity.State.MAIN);
        }
        else if (position == 1)
        {
            ArrayList<Value> test = mact.getDAO().getAllValuesSorted();
            ArrayAdapter test2 = new ArrayAdapter<>(mcont, android.R.layout.simple_list_item_1, android.R.id.text1, test);
            mcategoryView.setAdapter(test2);
            mact.getSupportActionBar().setTitle("History");
            mact.setCur_state(MainActivity.State.HISTORY);
        }
        else if(mcont.getResources().getStringArray(R.array.navigationDrawerContent)[position].equals("About"))
        {
            AlertDialog.Builder alert = new AlertDialog.Builder(mcont);
            alert.setTitle("PocketBill");
            alert.setMessage(Html.fromHtml(mcont.getResources().getString(R.string.about_text).concat(" <a href=\"http:\\www.google.com\">www.google.com</a>")));
            alert.show();

        }


    }

    public NavigationDrawerListener (MainActivity main, Context context, DrawerLayout layout, ArrayAdapter adapter, ListView categoryView, ArrayList<Category> main_categories)
    {
        mact = main;
        mcont=context;
        mlayout=layout;
        madapter=adapter;
        mmain_categories=main_categories;
        mcategoryView=categoryView;

    }
}
