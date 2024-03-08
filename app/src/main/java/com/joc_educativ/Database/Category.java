package com.joc_educativ.Database;

public class Category {

    private String key;
    private int id;
    private String Category;

    public Category(int id, String category) {
        this.id = id;
        this.Category = category;
    }

    public Category() {
    }

    @Override
    public String toString() {
        return "CategoryModel{" +
                "id=" + id +
                ", Category='" + Category + '\'' +
                '}';
    }

    public String getKey(){ return key;}

    public void setKey(String key) {
        this.key = key;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCategory() {
        return Category;
    }

    public void setCategory(String category) {
        Category = category;
    }
}
