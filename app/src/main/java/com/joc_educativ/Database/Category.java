package com.joc_educativ.Database;

public class Category {

    private String key;
    private int id;
    private String Category;
    private int unlockedLevel;

    private String roCategory;
    private String enCategory;

    public Category(int id, String category, int unlockedLevel) {
        this.id = id;
        Category = category;
        this.unlockedLevel = unlockedLevel;
    }

    public Category(int id, String category, int unlockedLevel, String roCategory, String enCategory) {
        this.id = id;
        Category = category;
        this.unlockedLevel = unlockedLevel;
        this.roCategory = roCategory;
        this.enCategory = enCategory;
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

    public int getUnlockedLevel() {
        return unlockedLevel;
    }

    public void setUnlockedLevel(int unlockedLevel) {
        this.unlockedLevel = unlockedLevel;
    }

    public String getRoCategory() {
        return roCategory;
    }

    public void setRoCategory(String roCategory) {
        this.roCategory = roCategory;
    }

    public String getEnCategory() {
        return enCategory;
    }

    public void setEnCategory(String enCategory) {
        this.enCategory = enCategory;
    }
}
