package com.joc_educativ.Database;

public class CategoryModel {

    private int id;
    private String Category;

    public CategoryModel(int id, String category) {
        this.id = id;
        this.Category = category;
    }

    @Override
    public String toString() {
        return "CategoryModel{" +
                "id=" + id +
                ", Category='" + Category + '\'' +
                '}';
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
