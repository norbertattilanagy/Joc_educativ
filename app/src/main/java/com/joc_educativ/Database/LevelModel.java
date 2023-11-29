package com.joc_educativ.Database;

public class LevelModel {
    private int id;
    private int categoryId;
    private int level;

    public LevelModel(int id, int categoryId, int level) {
        this.id = id;
        this.categoryId = categoryId;
        this.level = level;
    }

    @Override
    public String toString() {
        return "LevelModel{" +
                "id=" + id +
                ", categoryId=" + categoryId +
                ", level=" + level +
                '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }
}
