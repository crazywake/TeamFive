package com.bill.pocket.pocketbill;

import java.math.BigInteger;
import java.text.NumberFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by Martin on 29.04.2015.
 */
public class Value {
    private int id;
    private long value;
    private Date date;
    private Category parent;
    private List<String> tags;

    public Value(int id, long value, long date, Category parent, List<String> tags) {
        this.id = id;
        this.value = value;
        this.date = new Date(date);
        this.parent = parent;
        this.tags = tags;
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

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }


    @Override
    public String toString() {
        //SET LOCALE CHOOSER
        NumberFormat numfor = NumberFormat.getCurrencyInstance(Locale.GERMAN);
        String currency = numfor.format(value / 100.0);
        return currency;
    }
}
