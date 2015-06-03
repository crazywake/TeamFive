package com.bill.pocket.pocketbill;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.bill.pocket.pocketbill.MultiSpinner.MultiSpinnerListener;

import java.util.ArrayList;


public class SearchActivity extends ActionBarActivity implements MultiSpinnerListener {

    private ArrayList<Integer> mainCatFilter = new ArrayList<>();
    private ArrayList<Integer> subCatFilter = new ArrayList<>();
    private ArrayList<Integer> tagsFilter = new ArrayList<>();
    private MultiSpinner subCatSpinner = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        MultiSpinner mainCatSpinner = (MultiSpinner) findViewById(R.id.search_maincat_spinner);
        subCatSpinner = (MultiSpinner) findViewById(R.id.search_subcat_spinner);
        MultiSpinner tagSpinner = (MultiSpinner) findViewById(R.id.search_tag_spinner);

        ArrayList<String> mainCats = new ArrayList<>();
        ArrayList<Integer> mainCatIds = new ArrayList<>();
        ArrayList<String> subCats = new ArrayList<>();
        ArrayList<Integer> subCatIds = new ArrayList<>();
        ArrayList<Value> values = new ArrayList<>();
        ArrayList<String> tags = new ArrayList<>();
        ArrayList<Integer> tagIds = new ArrayList<>();

        for(Category c : CategoryData.getInstance().getMainCategories()) {
            mainCats.add(c.getName());
            mainCatIds.add(c.getId());
            values.addAll(DAO.instance(this).getValues(c));
        }

        for (Tag t : DAO.instance(this).getAllTags()) {
            tags.add(t.getName());
            tagIds.add(t.getId());
        }

        mainCatSpinner.setItems(mainCats, mainCatIds, "Main categories", MultiSpinner.SpinnerType.MAIN_CATEGORY, this);
        subCatSpinner.setItems(subCats, subCatIds, "Sub categories", MultiSpinner.SpinnerType.SUB_CATEGORY, this);
        subCatSpinner.setEnabled(false);
        tagSpinner.setItems(tags, tagIds, "Tags", MultiSpinner.SpinnerType.TAGS, this);

        final ListView valuesView = (ListView) findViewById(R.id.search_value_view);


        ArrayAdapter adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, android.R.id.text1, values);
        valuesView.setAdapter(adapter);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search, menu);
        return true;
    }



    @Override
    public void onItemsSelected(ArrayList<Integer> itemIds, MultiSpinner.SpinnerType type) {
        Log.w("changed spinner", type.name());
        for (Integer i : itemIds) {
            Log.w("selected id", i + "");
        }
        updateFilter(type, itemIds);
    }

    private void updateFilter(MultiSpinner.SpinnerType type, ArrayList<Integer> filterIds) {
        switch (type) {
            case MAIN_CATEGORY:
                this.mainCatFilter = filterIds;

                ArrayList<String> subCats = new ArrayList<>();
                ArrayList<Integer> subCatIds = new ArrayList<>();

                for(Category c : CategoryData.getInstance().getMainCategories()) {
                    if (filterIds.contains(new Integer(c.getId()))) {
                        for (Category subCat : c.getSubcategories()) {
                            subCats.add(subCat.getName());
                            subCatIds.add(subCat.getId());
                        }
                    }
                }

                subCatSpinner.setItems(subCats, subCatIds, "Sub categories", MultiSpinner.SpinnerType.SUB_CATEGORY, this);
                subCatSpinner.setEnabled(subCatIds.size() > 0);

                break;
            case SUB_CATEGORY:
                this.subCatFilter = filterIds;
                break;
            case TAGS:
                this.tagsFilter = filterIds;
        }

        ArrayList<Value> filteredVals = DAO.instance(this).getFilteredValues(mainCatFilter, subCatFilter, tagsFilter);
        final ListView valuesView = (ListView) findViewById(R.id.search_value_view);

        if (filteredVals.size() < 1) {
            filteredVals.add(new ValueDummy("No entries found! - Refine your search, bastard."));
        }

        ArrayAdapter adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, android.R.id.text1, filteredVals);
        valuesView.setAdapter(adapter);
    }
}
