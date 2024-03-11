package com.joc_educativ.Database;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class FirebaseDB {
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    String successMsg;

    public interface DBVersionCallback {
        void onDBVersionReceived(Long dbVersion);
    }

    public void getDBVersion(DBVersionCallback callback) {
        databaseReference.child("appData").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Long dbVersion = snapshot.child("dbVersion").getValue(Long.class);
                callback.onDBVersionReceived(dbVersion);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public String saveCategory(Category category) {
        databaseReference.child("category").push().setValue(category).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                successMsg = "Save successfully";
            }
        });
        return successMsg;
    }

    public interface CategoryCallback {
        void onCategoryListLoaded(List<Category> allCategory);

        void onCancelled(DatabaseError error);
    }

    public void selectAllCategory(CategoryCallback callback) {
        List<Category> allCategory = new ArrayList<>();
        databaseReference.child("category").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Category category = dataSnapshot.getValue(Category.class);
                    Objects.requireNonNull(category).setKey(dataSnapshot.getKey());
                    allCategory.add(category);
                }
                callback.onCategoryListLoaded(allCategory);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onCancelled(error);
            }
        });
    }

    public String saveLevel(Level level) {
        String mapString = "";//convert level in string
        for (int i = 0; i < level.getMapYSize(); i++) {
            for (int j = 0; j < level.getMapXSize(); j++) {
                mapString += level.getMap()[i][j];
            }
        }

        Map<String, Object> levelMap = new HashMap<>();//create level map
        levelMap.put("id", level.getId());
        levelMap.put("categoryId", level.getCategoryId());
        levelMap.put("level", level.getLevel());
        levelMap.put("mapXSize", level.getMapXSize());
        levelMap.put("mapYSize", level.getMapYSize());
        levelMap.put("map", mapString);

        //put map in db
        databaseReference.child("level").push().setValue(levelMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                successMsg = "Save successfully";
            }
        });
        return successMsg;
    }

    public interface LevelCallback {
        void onLevelListLoaded(List<Level> allLevel);

        void onCancelled(DatabaseError error);
    }

    public void selectAllLevel(LevelCallback callback) {
        List<Level> allLevel = new ArrayList<>();
        databaseReference.child("level").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Level level = new Level();
                    level.setId(dataSnapshot.child("id").getValue(Integer.class));
                    level.setCategoryId(dataSnapshot.child("categoryId").getValue(Integer.class));
                    level.setLevel(dataSnapshot.child("level").getValue(Integer.class));
                    level.setMapXSize(dataSnapshot.child("mapXSize").getValue(Integer.class));
                    level.setMapYSize(dataSnapshot.child("mapYSize").getValue(Integer.class));

                    String mapString = dataSnapshot.child("map").getValue(String.class);//set map
                    String[][] map = new String[level.getMapYSize()][level.getMapXSize()];
                    for (int i = 0; i < level.getMapYSize(); i++)
                        for (int j = 0; j < level.getMapXSize(); j++) {
                            map[i][j] = String.valueOf(mapString.charAt(0));//select first character
                            mapString = mapString.substring(1);//delete first character
                        }
                    level.setMap(map);
                    level.setKey(dataSnapshot.getKey());

                    allLevel.add(level);
                }
                callback.onLevelListLoaded(allLevel);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onCancelled(error);
            }
        });
    }

    public void saveUserLevel(int categoryId, int unlockedLevel) {

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference userLevelRef = databaseReference.child("userLevel").child(userId);
        userLevelRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userLevelRef.child(String.valueOf(categoryId)).setValue(unlockedLevel);//update level
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public interface UnlockedLevelCallback {
        void onUnlockedLevelReceived(int unlockedLevel);

        void onCancelled(DatabaseError error);
    }

    public void selectUserLevel(int categoryId, UnlockedLevelCallback callback) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference userReference = databaseReference.child("userLevel").child(userId).child(String.valueOf(categoryId));

        userReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    int unlockedLevel = snapshot.getValue(Integer.class);
                    callback.onUnlockedLevelReceived(unlockedLevel);
                } else {
                    callback.onUnlockedLevelReceived(1);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onCancelled(error);
            }
        });
    }
}
