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
        super(context, "educative_game.db", null, 6);//create db
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CategoryTable = "CREATE TABLE Category (id INTEGER PRIMARY KEY AUTOINCREMENT, Category Text)";
        String LevelTable = "CREATE TABLE Level (id INTEGER PRIMARY KEY AUTOINCREMENT, Category INTEGER, Level INTEGER, MapXSize INTEGER, MapYSize INTEGER, Map TEXT)";

        //create table in db
        db.execSQL(CategoryTable);
        db.execSQL(LevelTable);

        //insert category type
        String inserCategory = "INSERT INTO Category (Category) VALUES ('Cars'),('People'),('Algorithm')";
        db.execSQL(inserCategory);

        //insert level type
        String inserLevel = "INSERT INTO Level (Category,Level,MapXSize,MapYSize,Map) VALUES " +
                "(1,1,8,5,'TRRXXXTTRRXXRXTTCXXTTXXHRRRTTTTTRRTTTTTT')" +
                ",(1,2,8,5,'TRRXXXTTRRXXRXTTCXXTTXXHRRRTTTTTRRTTTTTT')" +
                ",(1,3,8,5,'TRRXXXTTRRXXRXTTCXXTTXXHRRRTTTTTRRTTTTTT')";/* +
                ",(1,4,,,)" +
                ",(1,5,,,)" +
                ",(1,6,,,)" +
                ",(1,7,,,)" +
                ",(1,55,,,)";*/
        db.execSQL(inserLevel);
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

    public List<CategoryModel> selectAllCategory() {

        List<CategoryModel> allCategory = new ArrayList<>();
        String getCategory = "SELECT * FROM Category";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(getCategory, null);

        if (cursor.moveToFirst()) {//put all category in category list
            do {
                int id = cursor.getInt(0);
                String category = cursor.getString(1);

                allCategory.add(new CategoryModel(id, category));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return allCategory;
    }

    public List<LevelModel> selectAllLevelByCategory(int categoryId) {

        List<LevelModel> allLevelByCategory = new ArrayList<>();
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

                String[][] map = new String[mapYSize][mapXSize];
                int index = 0;
                for (int i = 0; i < mapYSize; i++) {
                    for (int j = 0; j < mapXSize; j++) {
                        map[i][j] = String.valueOf(mapText.charAt(0));//select first character
                        mapText = mapText.substring(1);//delete first character
                    }
                }
                allLevelByCategory.add(new LevelModel(id, category, level, mapXSize, mapYSize, map));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return allLevelByCategory;
    }

    public LevelModel selectLevelById(int levelId) {

        LevelModel levelModel = null;
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

            String[][] map = new String[mapYSize][mapXSize];
            for (int i = 0; i < mapYSize; i++)
                for (int j = 0; j < mapXSize; j++) {
                    map[i][j] = String.valueOf(mapText.charAt(0));//select first character
                    mapText = mapText.substring(1);//delete first character
                }

            levelModel = new LevelModel(id, category, level, mapXSize, mapYSize, map);
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

    public boolean addCategory(CategoryModel categoryModel) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put("Category", categoryModel.getCategory());
        long result = db.insert("Category", null, contentValues);

        if (result == -1)
            return false;
        return true;
    }
}
