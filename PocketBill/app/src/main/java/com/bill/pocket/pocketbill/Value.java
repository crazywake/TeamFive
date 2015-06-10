package com.bill.pocket.pocketbill;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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

    public Category getParent() {
        return parent;
    }

    public List<String> getTags() {
        return tags;
    }


    @Override
    public String toString() {
        NumberFormat numfor = NumberFormat.getCurrencyInstance(Locale.GERMANY);
        return DateFormat.getDateInstance().format(this.date) + "    " + numfor.format(value / 100.0);
    }
}
