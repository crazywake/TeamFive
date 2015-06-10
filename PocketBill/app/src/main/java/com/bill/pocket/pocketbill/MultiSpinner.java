package com.bill.pocket.pocketbill;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.AttributeSet;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

public class MultiSpinner extends Spinner implements
        DialogInterface.OnMultiChoiceClickListener, DialogInterface.OnCancelListener {

    private List<String> items;
    private List<Integer> itemIds;
    private boolean[] selected;
    private String defaultText;
    private MultiSpinnerListener listener;
    private SpinnerType type;

    public MultiSpinner(Context context) {
        super(context);
    }

    public MultiSpinner(Context arg0, AttributeSet arg1) {
        super(arg0, arg1);
    }

    public MultiSpinner(Context arg0, AttributeSet arg1, int arg2) {
        super(arg0, arg1, arg2);
    }

    @Override
    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
        selected[which] = isChecked;
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        // refresh text on spinner

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_item,
                new String[] { getSpinnerText() });
        setAdapter(adapter);

        ArrayList<Integer> selItemIds = this.getSelectedIds();
        listener.onItemsSelected(selItemIds, type);
    }

    @Override
    public boolean performClick() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMultiChoiceItems(
                items.toArray(new CharSequence[items.size()]), selected, this);
        builder.setPositiveButton(android.R.string.ok,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        builder.setOnCancelListener(this);
        builder.show();
        return true;
    }

    public void setItems(List<String> items, List<Integer> itemIds, String allText, SpinnerType type,
                         MultiSpinnerListener listener) {

        ArrayList<Integer> preselectedIds = null;
        if (selected != null)
            preselectedIds = this.getSelectedIds();

        this.items = items;
        this.defaultText = allText;
        this.listener = listener;
        this.type = type;
        this.itemIds = itemIds;

        // all unselected by default
        selected = new boolean[items.size()];
        //for (int i = 0; i < selected.length; i++)
        //    selected[i] = false;

        if (preselectedIds != null) {
            int i = 0;
            for (Integer id : this.itemIds) {
                if (preselectedIds.contains(id))
                    selected[i] = true;

                ++i;
            }
        }

        // all text on the spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_item, new String[] { getSpinnerText() });
        setAdapter(adapter);
    }

    public interface MultiSpinnerListener {
        public void onItemsSelected(ArrayList<Integer> itemIds, SpinnerType type);
    }

    public enum SpinnerType {
        MAIN_CATEGORY,
        SUB_CATEGORY,
        TAGS,
        INITIAL
    }

    private ArrayList<Integer> getSelectedIds() {
        ArrayList<Integer> selItemIds = new ArrayList<>();
        int i = 0;
        for (boolean b : selected) {
            if (b) selItemIds.add(itemIds.get(i));
            ++i;
        }
        return selItemIds;
    }

    private String getSpinnerText() {
        StringBuilder spinnerBuffer = new StringBuilder();
        boolean allUnselected = true;
        for (int i = 0; i < items.size(); i++) {
            if (selected[i]) {
                spinnerBuffer.append(items.get(i));
                spinnerBuffer.append(", ");
                allUnselected = false;
            }
        }

        String spinnerText;
        if (allUnselected) {
            spinnerText = defaultText;
        } else {
            spinnerText = spinnerBuffer.toString();
            if (spinnerText.length() > 2)
                spinnerText = spinnerText.substring(0, spinnerText.length() - 2);
        }

        return spinnerText;
    }
}
