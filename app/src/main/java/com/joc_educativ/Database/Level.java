package com.joc_educativ.Database;

public class Level {
    private String key;
    private int id;
    private int categoryId;
    private int level;
    private int mapXSize;
    private int mapYSize;
    private String[][] map;
    private String codeElement;

    private int starStage2;
    private int starStage3;
    private int userStar;

    public Level(int id, int categoryId, int level, int mapXSize, int mapYSize, String[][] map, String codeElement,int starStage2, int starStage3) {
        this.id = id;
        this.categoryId = categoryId;
        this.level = level;
        this.mapXSize = mapXSize;
        this.mapYSize = mapYSize;
        this.map = map;
        this.codeElement = codeElement;
        this.starStage2 = starStage2;
        this.starStage3 = starStage3;
    }

    public Level(int id,int categoryId, int userStar){
        this.id = id;
        this.categoryId = categoryId;
        this.userStar = userStar;
    }

    public Level() {
    }

    @Override
    public String toString() {
        return "LevelModel{" +
                "id=" + id +
                ", categoryId=" + categoryId +
                ", level=" + level +
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

    public String[][] getMap() {
        return map;
    }

    public void setMap(String[][] map) {
        this.map = map;
    }

    public String getCodeElement() {
        return codeElement;
    }

    public void setCodeElement(String codeElement) {
        this.codeElement = codeElement;
    }

    public int getStarStage2() {
        return starStage2;
    }

    public void setStarStage2(int starStage2) {
        this.starStage2 = starStage2;
    }

    public int getStarStage3() {
        return starStage3;
    }

    public void setStarStage3(int starStage3) {
        this.starStage3 = starStage3;
    }

    public int getUserStar() {
        return userStar;
    }

    public void setUserStar(int userStar) {
        this.userStar = userStar;
    }
}
