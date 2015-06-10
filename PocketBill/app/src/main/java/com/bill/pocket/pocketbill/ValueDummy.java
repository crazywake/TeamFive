package com.bill.pocket.pocketbill;

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
