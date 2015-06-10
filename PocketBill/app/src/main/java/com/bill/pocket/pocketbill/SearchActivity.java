package com.bill.pocket.pocketbill;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.bill.pocket.pocketbill.MultiSpinner.MultiSpinnerListener;

import java.util.ArrayList;


public class SearchActivity extends ActionBarActivity implements MultiSpinnerListener {

    private ArrayList<Integer> mainCatFilter = new ArrayList<>();
    private ArrayList<Integer> subCatFilter = new ArrayList<>();
    private ArrayList<Integer> tagsFilter = new ArrayList<>();
    private MultiSpinner subCatSpinner = null;
    private ListView valuesView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        valuesView = (ListView) findViewById(R.id.search_value_view);

        MultiSpinner mainCatSpinner = (MultiSpinner) findViewById(R.id.search_maincat_spinner);
        subCatSpinner = (MultiSpinner) findViewById(R.id.search_subcat_spinner);
        MultiSpinner tagSpinner = (MultiSpinner) findViewById(R.id.search_tag_spinner);

        ArrayList<String> mainCats = new ArrayList<>();
        ArrayList<Integer> mainCatIds = new ArrayList<>();
        ArrayList<String> subCats = new ArrayList<>();
        ArrayList<Integer> subCatIds = new ArrayList<>();
        ArrayList<String> tags = new ArrayList<>();
        ArrayList<Integer> tagIds = new ArrayList<>();

        for(Category c : CategoryData.getInstance().getMainCategories()) {
            mainCats.add(c.getName());
            mainCatIds.add(c.getId());
        }

        for (Tag t : DAO.instance(this).getAllTags()) {
            tags.add(t.getName());
            tagIds.add(t.getId());
        }

        mainCatSpinner.setItems(mainCats, mainCatIds, "Main categories", MultiSpinner.SpinnerType.MAIN_CATEGORY, this);
        subCatSpinner.setItems(subCats, subCatIds, "Sub categories", MultiSpinner.SpinnerType.SUB_CATEGORY, this);
        subCatSpinner.setEnabled(false);
        tagSpinner.setItems(tags, tagIds, "Tags", MultiSpinner.SpinnerType.TAGS, this);

        this.updateFilter(MultiSpinner.SpinnerType.INITIAL, null);
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
                    if (filterIds.contains(Integer.valueOf(c.getId()))) {
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
                break;
            case INITIAL:
                mainCatFilter = new ArrayList<>();
                subCatFilter = new ArrayList<>();
                tagsFilter = new ArrayList<>();
        }

        final ArrayList<Value> filteredVals = DAO.instance(this).getFilteredValues(mainCatFilter, subCatFilter, tagsFilter);

        if (filteredVals.size() < 1) {
            filteredVals.add(new ValueDummy("No entries found!"));
        }

        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_2, android.R.id.text1, filteredVals) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView text1 = (TextView) view.findViewById(android.R.id.text1);
                TextView text2 = (TextView) view.findViewById(android.R.id.text2);

                Value val = filteredVals.get(position);
                if (val != null) {
                    text1.setText(filteredVals.get(position).toString());
                    if (val.getParent() != null)
                        text2.setText(filteredVals.get(position).getParent().getName());
                }
                return view;
            }
        };
        valuesView.setAdapter(adapter);
    }
}
