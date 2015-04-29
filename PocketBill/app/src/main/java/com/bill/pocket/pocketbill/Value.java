package com.bill.pocket.pocketbill;

import java.text.NumberFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Martin on 29.04.2015.
 */
public class Value {
    private int id;
    private long value;
    private Date date;
    private Category parent;

    public Value(int id, long value, int date, Category parent) {
        this.id = id;
        this.value = value;
        this.date = new Date(date);
        this.parent = parent;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getValue() {
        return value;
    }

    public void setValue(long value) {
        this.value = value;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Category getParent() {
        return parent;
    }

    public void setParent(Category parent) {
        this.parent = parent;
    }

    @Override
    public String toString() {
        //SET LOCALE CHOOSER
        NumberFormat numfor = NumberFormat.getCurrencyInstance(Locale.GERMAN);
        String currency = numfor.format(value / 100.0);
        return currency;
    }
}
