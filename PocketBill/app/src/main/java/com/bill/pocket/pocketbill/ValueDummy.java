package com.bill.pocket.pocketbill;

/**
 * Created by Thomas on 03.06.2015.
 */
public class ValueDummy extends Value {
    private String text;

    public ValueDummy(String text) {
        super(-1, -1, -1, null, null);
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return this.getText();
    }
}
