package com.joc_educativ.Database;

public class FirebaseLevel {
    private int id;
    private int categoryId;
    private int level;
    private int mapXSize;
    private int mapYSize;
    private String map;

    public FirebaseLevel(int id, int categoryId, int level, int mapXSize, int mapYSize, String map) {
        this.id = id;
        this.categoryId = categoryId;
        this.level = level;
        this.mapXSize = mapXSize;
        this.mapYSize = mapYSize;
        this.map = map;
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

    public int getMapXSize() {
        return mapXSize;
    }

    public void setMapXSize(int mapXSize) {
        this.mapXSize = mapXSize;
    }

    public int getMapYSize() {
        return mapYSize;
    }

    public void setMapYSize(int mapYSize) {
        this.mapYSize = mapYSize;
    }

    public String getMap() {
        return map;
    }

    public void setMap(String map) {
        this.map = map;
    }
}
