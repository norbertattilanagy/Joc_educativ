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
        super(context, "educative_game.db", null, 5);//create db
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CategoryTable = "CREATE TABLE Category (id INTEGER PRIMARY KEY AUTOINCREMENT, Category Text)";
        String LevelTable = "CREATE TABLE Level (id INTEGER PRIMARY KEY AUTOINCREMENT, Category INTEGER, Level INTEGER)";

        //create table in db
        db.execSQL(CategoryTable);
        db.execSQL(LevelTable);

        //insert category type
        String inserCategory = "INSERT INTO Category (Category) VALUES ('Cars'),('People'),('Algorithm')";
        db.execSQL(inserCategory);

        //insert level type
        String inserLevel = "INSERT INTO Level (Category,Level) VALUES (1,1)" +
                ",(1,2)" +
                ",(1,3)" +
                ",(1,4)" +
                ",(1,5)" +
                ",(1,6)" +
                ",(1,7)" +
                ",(1,55)";
        db.execSQL(inserLevel);

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

    public List<CategoryModel> selectAllCategory(){

        List<CategoryModel> allCategory = new ArrayList<>();
        String getCategory = "SELECT * FROM Category";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(getCategory,null);

        if (cursor.moveToFirst()){//put all category in category list
            do {
                int id = cursor.getInt(0);
                String category = cursor.getString(1);

                allCategory.add(new CategoryModel(id,category));
            }while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return allCategory;
    }

    public List<LevelModel> selectAllLevelByCategory(int categoryId){

        List<LevelModel> allLevelByCategory = new ArrayList<>();
        String getLevel = "SELECT * FROM Level WHERE Category = " + categoryId;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(getLevel,null);

        if (cursor.moveToFirst()){//put all level in level list
            do {
                int id = cursor.getInt(0);
                int category = cursor.getInt(1);
                int level = cursor.getInt(2);

                allLevelByCategory.add(new LevelModel(id,category,level));
            }while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return allLevelByCategory;
    }

    public boolean addCategory(CategoryModel categoryModel){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put("Category", categoryModel.getCategory());
        long result = db.insert("Category",null,contentValues);

        if (result == -1)
            return false;
        return true;
    }
}
