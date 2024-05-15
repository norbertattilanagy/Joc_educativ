package com.joc_educativ.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    public DatabaseHelper(@Nullable Context context) {
        super(context, "educative_game.db", null, 2);//create db
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CategoryTable = "CREATE TABLE Category (id INTEGER PRIMARY KEY AUTOINCREMENT, Category TEXT, UnlockedLevel INTEGER)";
        String LevelTable = "CREATE TABLE Level (id INTEGER PRIMARY KEY AUTOINCREMENT, Category INTEGER, Level INTEGER, MapXSize INTEGER, MapYSize INTEGER, Map TEXT, CodeElement TEXT)";
        String appData = "CREATE TABLE AppData (id INTEGER PRIMARY KEY AUTOINCREMENT, DBVersion FLOAT,AppVersion FLOAT)";

        //create table in db
        db.execSQL(CategoryTable);
        db.execSQL(LevelTable);
        db.execSQL(appData);

        //insert category type
        String insertCategory = "INSERT INTO Category (Category,UnlockedLevel) VALUES ('cars',1),('people',1),('algorithm',1)";
        db.execSQL(insertCategory);

        //insert level type
        String insertLevel = "INSERT INTO Level (Category,Level,MapXSize,MapYSize,Map, CodeElement) VALUES " +
                "(1,1,6,4,'RRTTTTRTTTTTCXXXXHTTTTTT','right;')" +
                ",(1,2,6,4,'RXXXXTCXTTXTRRRTXHRRRRRR','right;up;down;')" +
                ",(1,3,6,4,'RXXXTTRXTXTTCXRXXHRRRRRR','right;up;down;')" +
                ",(1,4,8,5,'TTTTTTTTRXXXXTTTCXTRXRTHRRRTXXXXRRTTTTTT','right;up;down;')" +
                ",(1,5,8,5,'TRRRRRTTRRXXXXTTCXXTTXXHRRRTTTTTRRTTTTTT','right;up;down;')" +
                ",(1,6,8,5,'RRRTTTTTRRTTTTTTCXXTTXXHRRXXRXTTTRRXXXTT','right;up;down;')" +
                ",(1,7,8,5,'TTTXXXXHTTTXRRRRTTTXXRRRTTTTXRRRCXXXXRRR','right;left;up;')" +
                ",(1,8,8,5,'TTTTTTRRTTTTRRRRCXXXXXXHTTTTTRRRTTTTRRRR','right;repeat;')" +
                ",(1,9,8,5,'RRTXXXXHRRTXTTTTRRTXXXXTRRTTTTXTCXXXXXXT','right;left;up;repeat;')" +
                ",(1,10,10,6,'RRTTXXXXXHRRTTXTTTRRRRTTXTTTRRRRTTXTTTRRRRTTXTTTRRCXXXXTTTRR','right;up;repeat;')" +
                //",(1,11,8,5,'XXXXXXXXXXXXXXXXCXXXXXXHXXXXXXXXXXXXXXXX','right;left;up;down;jump;repeat;if;')" +

                ",(2,1,6,4,'RRTTTTRTTTTTPXLXXHTTTTTT','right;jump;repeat;if;')";/* +
                ",(2,7,,,)" +
                ",(1,10,,,)";*/
        db.execSQL(insertLevel);

        String insertAppData = "INSERT INTO AppData (DBVersion,AppVersion) VALUES (0.1,0.1)";
        db.execSQL(insertAppData);

        System.out.println("CREATE DB");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        //delete table
        db.execSQL("DROP TABLE IF EXISTS Category");
        db.execSQL("DROP TABLE IF EXISTS Level");

        System.out.println("UPDATE");
        //create table again
        onCreate(db);
    }

    public Float getDbVersion(){
        float version = 0;
        String query = "SELECT DBVersion FROM AppData";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {//put all level in level list
            version = cursor.getFloat(0);
        }
        return version;
    }

    public void updateDbVersion(long dbVersion){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("dbVersion", dbVersion);
        db.update("AppData", values, null, null);
        db.close();
    }

    public void clearTable(String tableName){
        SQLiteDatabase db = this.getReadableDatabase();
        db.delete(tableName, null, null);
        db.close();
    }

    public List<Category> selectAllCategory() {

        List<Category> allCategory = new ArrayList<>();
        String getCategory = "SELECT * FROM Category";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(getCategory, null);

        if (cursor.moveToFirst()) {//put all category in category list
            do {
                int id = cursor.getInt(0);
                String category = cursor.getString(1);
                int unlockedLevel = cursor.getInt(2);
                allCategory.add(new Category(id, category,unlockedLevel));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return allCategory;
    }

    public Category selectCategoryById(int categoryId){
        Category category = null;
        String getLevel = "SELECT * FROM Category WHERE id = " + categoryId;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(getLevel, null);

        if (cursor.moveToFirst()) {
            int id = cursor.getInt(0);
            String cat = cursor.getString(1);
            int unlockedLevel = cursor.getInt(2);
            category = new Category(id,cat,unlockedLevel);
        }
        cursor.close();
        db.close();
        return category;
    }

    public boolean addCategory(Category category) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        if (category.getId()>0)
            contentValues.put("Id",category.getId());

        contentValues.put("Category", category.getCategory());
        long result = db.insert("Category", null, contentValues);

        if (result == -1)
            return false;
        return true;
    }

    public int selectUnlockedLevel(int categoryId){
        int unlockedLevel=1;
        String getLevel = "SELECT UnlockedLevel FROM Category WHERE id = " + categoryId;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(getLevel, null);

        if (cursor.moveToFirst()) {
            unlockedLevel = cursor.getInt(0);
        }
        cursor.close();
        db.close();
        return unlockedLevel;
    }

    public void updateUnlockedLevel(int categoryId, int unlockedLevel){//save next unlocked level
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("UnlockedLevel", unlockedLevel);
        db.update("Category", values, "id = ?", new String[]{String.valueOf(categoryId)});
        db.close();
    }

    public List<Level> selectAllLevelByCategory(int categoryId) {

        List<Level> allLevelByCategory = new ArrayList<>();
        String getLevel = "SELECT * FROM Level WHERE Category = " + categoryId;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(getLevel, null);

        if (cursor.moveToFirst()) {//put all level in level list
            do {
                int id = cursor.getInt(0);
                int category = cursor.getInt(1);
                int level = cursor.getInt(2);
                int mapXSize = cursor.getInt(3);//8
                int mapYSize = cursor.getInt(4);//5
                String mapText = cursor.getString(5);
                String codeElement = cursor.getString(6);

                String[][] map = new String[mapYSize][mapXSize];
                int index = 0;
                for (int i = 0; i < mapYSize; i++) {
                    for (int j = 0; j < mapXSize; j++) {
                        map[i][j] = String.valueOf(mapText.charAt(0));//select first character
                        mapText = mapText.substring(1);//delete first character
                    }
                }
                allLevelByCategory.add(new Level(id, category, level, mapXSize, mapYSize, map, codeElement));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return allLevelByCategory;
    }

    public Level selectLevelById(int levelId) {

        Level levelModel = null;
        String getLevel = "SELECT * FROM Level WHERE id = " + levelId;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(getLevel, null);

        if (cursor.moveToFirst()) {//put all level data in leveModel
            int id = cursor.getInt(0);
            int category = cursor.getInt(1);
            int level = cursor.getInt(2);
            int mapXSize = cursor.getInt(3);
            int mapYSize = cursor.getInt(4);
            String mapText = cursor.getString(5);
            String codeElement = cursor.getString(6);

            String[][] map = new String[mapYSize][mapXSize];
            for (int i = 0; i < mapYSize; i++)
                for (int j = 0; j < mapXSize; j++) {
                    map[i][j] = String.valueOf(mapText.charAt(0));//select first character
                    mapText = mapText.substring(1);//delete first character
                }

            levelModel = new Level(id, category, level, mapXSize, mapYSize, map, codeElement);
        }
        cursor.close();
        db.close();
        return levelModel;
    }

    public int selectNextLevelId(int categoryId, int level){
        int levelId = -1;
        String select = "SELECT id FROM level WHERE Category = " + categoryId + " AND Level = " + level;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(select, null);

        if (cursor.moveToFirst()) {//put all level in level list
            levelId = cursor.getInt(0);
        }
        return levelId;
    }

    public boolean addLevel(Level level) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        String mapString = "";//convert level in string
        for (int i = 0; i < level.getMapYSize(); i++) {
            for (int j = 0; j < level.getMapXSize(); j++) {
                mapString += level.getMap()[i][j];
            }
        }

        if (level.getId()>0)
            contentValues.put("Id",level.getId());
        contentValues.put("Category", level.getCategoryId());
        contentValues.put("Level", level.getLevel());
        contentValues.put("MapXSize", level.getMapXSize());
        contentValues.put("MapYSize", level.getMapYSize());
        contentValues.put("Map", mapString);
        contentValues.put("CodeElement",level.getCodeElement());
        long result = db.insert("Level", null, contentValues);

        if (result == -1)
            return false;
        return true;
    }
}
