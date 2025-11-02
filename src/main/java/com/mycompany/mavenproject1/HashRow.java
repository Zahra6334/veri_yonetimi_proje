package com.mycompany.mavenproject1;

public class HashRow {
    private int index;
    private String key;
    private String value;

    public HashRow(int index, String key, String value) {
        this.index = index;
        this.key = key;
        this.value = value;
    }

    public int getIndex() { return index; }
    public String getKey() { return key; }
    public String getValue() { return value; }

    public void setIndex(int index) { this.index = index; }
    public void setKey(String key) { this.key = key; }
    public void setValue(String value) { this.value = value; }
}
