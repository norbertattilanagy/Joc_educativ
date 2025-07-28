package com.joc_educativ.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    public DatabaseHelper(@Nullable Context context) {
        super(context, "educative_game.db", null, 2);//create db
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CategoryTable = "CREATE TABLE Category (id INTEGER PRIMARY KEY AUTOINCREMENT, Category TEXT, UnlockedLevel INTEGER, roCategory TEXT, enCategory TEXT)";
        String LevelTable = "CREATE TABLE Level (id INTEGER PRIMARY KEY AUTOINCREMENT, Category INTEGER, Level INTEGER, MapXSize INTEGER, MapYSize INTEGER, Map TEXT, CodeElement TEXT, starStage2 INTEGER, starStage3 INTEGER, userStar INTEGER)";
        String appData = "CREATE TABLE AppData (id INTEGER PRIMARY KEY AUTOINCREMENT, DBVersion TEXT)";

        //create table in db
        db.execSQL(CategoryTable);
        db.execSQL(LevelTable);
        db.execSQL(appData);

        //insert category type
        String insertCategory = "INSERT INTO Category (Category,UnlockedLevel,roCategory,enCategory) VALUES ('cars',1,'Mașini','Cars'),('people',1,'Persoană','People')";
        db.execSQL(insertCategory);

        //insert level type
        String insertLevel = "INSERT INTO Level (Category,Level,MapXSize,MapYSize,Map, CodeElement,starStage2,starStage3,userStar) VALUES " +
                "(1,1,6,4,'RRTTTTRTTTTTCXXXXHTTTTTT','right;',5,5,0)" +
                ",(1,2,6,4,'RXXXXTCXTTXTRRRTXHRRRRRR','right;up;down;',8,8,0)" +
                ",(1,3,6,4,'RXXXTTRXTXTTCXRXXHRRRRRR','right;up;down;',9,9,0)" +
                ",(1,4,8,5,'TTTTTTTTRXXXXTTTCXTRXRTHRRRTXXXXRRTTTTTT','right;up;down;',11,11,0)" +
                ",(1,5,8,5,'TRRRRRTTRRXXXXTTCXXTTXXHRRRTTTTTRRTTTTTT','right;up;down;',9,9,0)" +
                ",(1,6,8,5,'RRRTTTTTRRTTTTTTCXXTTXXHRRXXRXTTTRRXXXTT','right;up;down;',11,11,0)" +
                ",(1,7,8,5,'TTTXXXXHTTTXRRRRTTTXXRRRTTTTXRRRCXXXXRRR','right;left;up;',13,13,0)" +
                ",(1,8,8,5,'TTTTTTRRTTTTRRRRCXXXXXXHTTTTTRRRTTTTRRRR','right;repeat;nr1;nr2;nr3;nr4;nr5;nr6;nr7;nr8;nr9;',2,3,0)" +
                ",(1,9,8,5,'RRTXXXXHRRTXTTTTRRTXXXXTRRTTTTXTCXXXXXXT','right;left;up;repeat;nr1;nr2;nr3;nr4;nr5;nr6;nr7;nr8;nr9;',14,13,0)" +
                ",(1,10,10,6,'RRTTXXXXXHRRTTXTTTRRRRTTXTTTRRRRTTXTTTRRRRTTXTTTRRCXXXXTTTRR','right;up;repeat;nr1;nr2;nr3;nr4;nr5;nr6;nr7;nr8;nr9;',10,9,0)" +
                //",(1,11,8,5,'XXXXXXXXXXXXXXXXCXXXXXXHXXXXXXXXXXXXXXXX','right;left;up;down;jump;repeat;if;')" +

                ",(2,1,6,4,'RRTTTTRTXXXHPXXTTTTTTTTT','right;up;',6,6,0)" +
                ",(2,2,6,4,'RRXLXHRTXTTTPXXTTTTTTTTT','right;up;jump;',7,7,0)" +
                ",(2,3,6,4,'RRTTTTRTTTTTPXLXXHTTTTTT','right;jump;',5,5,0)" +
                ",(2,4,8,5,'RRRRXXXHRRRRXTTTPXXLXTTTRRRTTTTTRRTTTTTT','right;up;jump;repeat;nr1;nr2;nr3;nr4;nr5;nr6;nr7;nr8;nr9;',9,9,0)" +
                ",(2,5,8,5,'RRRRRTTTRRRRTTTTPXXLXXXHRRRTTTTTRRTTTTTT','right;jump;repeat;nr1;nr2;nr3;nr4;nr5;nr6;nr7;nr8;nr9;if;logRight;logLeft;logUp;logDown;',6,6,0)" +
                ",(2,6,8,5,'RRRRXXXHRRRTXTTTTTTTLTRRRRRTXTTTPXXXXTTT','right;up;jump;repeat;nr1;nr2;nr3;nr4;nr5;nr6;nr7;nr8;nr9;if;logRight;logLeft;logUp;logDown;',12,10,0)" +
                ",(2,7,8,5,'PXLXXXLXRRRTTTTXRRTLTTTXRRRTLTTXRRRRTTTH','right;down;jump;repeat;nr1;nr2;nr3;nr4;nr5;nr6;nr7;nr8;nr9;if;logRight;logLeft;logUp;logDown;',10,9,0)" +
                ",(2,8,8,5,'PXLXXXLXRRRTTTTXRRTLTTTXRRRTTTTXHXXXLXXX','right;left;down;jump;repeat;nr1;nr2;nr3;nr4;nr5;nr6;nr7;nr8;nr9;if;logRight;logLeft;logUp;logDown;',16,15,0)" +
                ",(2,9,10,6,'PXLXXXXXTRRRRTTTTXTTRRTTTTTXXHRRRLTTTTTTRRRRTLTTTTRRRRRTTTRR','right;down;jump;repeat;nr1;nr2;nr3;nr4;nr5;nr6;nr7;nr8;nr9;if;logRight;logLeft;logUp;logDown;',10,9,0)" +
                ",(2,10,10,6,'PXLXXLXXTRRRRTTTTXTTRRTTXXXXTTRRRLXTTTTTRRRRXXXXXHRRRRRTTTRR','right;left;down;jump;repeat;nr1;nr2;nr3;nr4;nr5;nr6;nr7;nr8;nr9;if;logRight;logLeft;logUp;logDown;',18,16,0)";

        db.execSQL(insertLevel);

        String insertAppData = "INSERT INTO AppData (DBVersion) VALUES ('0.1')";
        db.execSQL(insertAppData);

        Log.d("db","CREATE DB");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        //delete table
        db.execSQL("DROP TABLE IF EXISTS Category");
        db.execSQL("DROP TABLE IF EXISTS Level");
        db.execSQL("DROP TABLE IF EXISTS AppData");

        Log.d("db","UPDATE");
        //create table again
        onCreate(db);
    }

    public String getDbVersion(){
        String version = "";
        String query = "SELECT DBVersion FROM AppData";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {//put all level in level list
            version = cursor.getString(0);
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
                String roCategory = cursor.getString(3);
                String enCategory = cursor.getString(4);
                allCategory.add(new Category(id, category,unlockedLevel,roCategory,enCategory));
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
            String roCategory = cursor.getString(3);
            String enCategory = cursor.getString(4);
            category = new Category(id, cat,unlockedLevel,roCategory,enCategory);
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
        contentValues.put("roCategory",category.getRoCategory());
        contentValues.put("enCategory",category.getEnCategory());
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


    public void updateCategory(int categoryId, String category, String roCategory, String enCategory){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("Category", category);
        values.put("roCategory",roCategory);
        values.put("enCategory",enCategory);
        db.update("Category", values, "id = ?", new String[]{String.valueOf(categoryId)});
    }

    public void updateUnlockedLevel(int categoryId, int unlockedLevel){//save next unlocked level
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("UnlockedLevel", unlockedLevel);
        db.update("Category", values, "id = ?", new String[]{String.valueOf(categoryId)});
        db.close();
    }

    public void updateUserStar(int levelId, int userStar){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("userStar", userStar);
        db.update("Level",values,"id = ?", new String[]{String.valueOf(levelId)});
        db.close();
    }

    public List<Level> selectUserStar(){
        List<Level> allLevel = new ArrayList<>();
        String getLevel = "SELECT id,Category,userStar FROM Level";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(getLevel, null);

        if (cursor.moveToFirst()) {//put all level in level list
            do {
                int id = cursor.getInt(0);
                int category = cursor.getInt(1);
                int userStar = cursor.getInt(2);

                allLevel.add(new Level(id,category, userStar));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        return allLevel;
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
                int starStage2 = cursor.getInt(7);
                int starStage3 = cursor.getInt(8);

                String[][] map = new String[mapYSize][mapXSize];
                int index = 0;
                for (int i = 0; i < mapYSize; i++) {
                    for (int j = 0; j < mapXSize; j++) {
                        map[i][j] = String.valueOf(mapText.charAt(0));//select first character
                        mapText = mapText.substring(1);//delete first character
                    }
                }
                allLevelByCategory.add(new Level(id, category, level, mapXSize, mapYSize, map, codeElement,starStage2,starStage3));
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
            int starStage2 = cursor.getInt(7);
            int starStage3 = cursor.getInt(8);

            String[][] map = new String[mapYSize][mapXSize];
            for (int i = 0; i < mapYSize; i++)
                for (int j = 0; j < mapXSize; j++) {
                    map[i][j] = String.valueOf(mapText.charAt(0));//select first character
                    mapText = mapText.substring(1);//delete first character
                }

            levelModel = new Level(id, category, level, mapXSize, mapYSize, map, codeElement, starStage2, starStage3);
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
        contentValues.put("starStage2",level.getStarStage2());
        contentValues.put("starStage3",level.getStarStage3());

        long result = db.insert("Level", null, contentValues);

        if (result == -1)
            return false;
        return true;
    }

    public int getUserStar(int id){
        int userStar = -1;
        String select = "SELECT userStar FROM Level WHERE id = " + id;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(select, null);

        if (cursor.moveToFirst()) {
            userStar = cursor.getInt(0);
        }
        return userStar;
    }

    public boolean addUserStar(int userStar,int id){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("userStar",userStar);
        long result = db.update("Level",values,"id = ?", new String[]{String.valueOf(id)});

        if (result == -1)
            return false;
        return true;
    }
}
