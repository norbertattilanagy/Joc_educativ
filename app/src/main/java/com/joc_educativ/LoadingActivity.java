package com.joc_educativ;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.joc_educativ.CustomView.ProgressBarAnimation;
import com.joc_educativ.Database.Category;
import com.joc_educativ.Database.DatabaseHelper;
import com.joc_educativ.Database.FirebaseDB;
import com.joc_educativ.Database.Level;

import java.util.List;

public class LoadingActivity extends AppCompatActivity {

    private View decorView;
    private ProgressBar progressBar;
    private TextView percentageText;
    private Button playButton, logInButton;

    List<Category> allCategory;
    String lastAppVersion = "";

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //remove notch area
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
            layoutParams.layoutInDisplayCutoutMode =
                    WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
            getWindow().setAttributes(layoutParams);
        }
        setContentView(R.layout.activity_loading);

        progressBar = findViewById(R.id.progressBar);
        percentageText = findViewById(R.id.percentageText);
        playButton = findViewById(R.id.playButton);
        logInButton = findViewById(R.id.logInButton);

        decorView = getWindow().getDecorView();//hide system bars

        progressAnimation();//loading

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SetingsPreferencis.playClickSound(LoadingActivity.this);
                openCategoryActivity();
            }
        });

        logInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SetingsPreferencis.playClickSound(LoadingActivity.this);
                openLogInActivity();
            }
        });
    }

    //hide system bars
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            decorView.setSystemUiVisibility(hideSystemBars());
        }
    }

    private int hideSystemBars() {
        return View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
    }
    //---

    private void progressAnimation() {
        updateDBData();

        ProgressBarAnimation animation = new ProgressBarAnimation(this, progressBar, percentageText, 0, 100, playButton, logInButton);
        animation.setDuration(8000);
        progressBar.setAnimation(animation);
    }

    public void updateDBData() {
        FirebaseDB fdb = new FirebaseDB();
        DatabaseHelper dbh = new DatabaseHelper(this);

        if (NetworkConnection.isNetworkAvailable(this)) {//if exist network connection

            DatabaseReference connectedRef = FirebaseDatabase.getInstance()
                    .getReference(".info/connected");

            connectedRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Boolean connected = snapshot.getValue(Boolean.class);
                    if (Boolean.TRUE.equals(connected)) {
                        Log.d("Firebase", "Connected to Firebase Realtime Database!");
                        // Now safe to read/write data
                    } else {
                        Log.d("Firebase", "Not connected. Waiting...");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("Firebase", "Listener was cancelled: " + error.getMessage());
                }
            });

            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {//if connect User
                List<Category> allLocalCategory = dbh.selectAllCategory();//sync unlocked level
                for (Category category : allLocalCategory) {
                    fdb.selectUserLevel(category.getId(), new FirebaseDB.UnlockedLevelCallback() {
                        @Override
                        public void onUnlockedLevelReceived(int unlockedLevel) {
                            if (unlockedLevel < category.getUnlockedLevel())
                                fdb.saveUserLevel(category.getId(), category.getUnlockedLevel());//save in firebase
                            else if (unlockedLevel > category.getUnlockedLevel()) {
                                dbh.updateUnlockedLevel(category.getId(), unlockedLevel);//save in local db
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError error) {
                            Log.d("Loading", error.toString());
                        }
                    });
                }
            }

            //write in firebase
            /*List<Category> categoryList = dbh.selectAllCategory();//get all category
            for (Category category : categoryList){
                fdb.saveCategory(category);
            }*/

            /*List<Level> levelList = dbh.selectAllLevelByCategory(1);
            //fdb.deleteAllLevel();
            for (Level level : levelList){
                fdb.saveLevel(level);
            }*/


            fdb.getUpdateLevel(new FirebaseDB.UpdateLevelCallback() {//wait update level not null
                @Override
                public void onUpdateLevelResult(Boolean updateLevel) {
                    if (updateLevel) {
                        fdb.getAppVersion(new FirebaseDB.appVersionCallback() {
                            @Override
                            public void onAppVersionReceived(String appVersion) {
                                lastAppVersion = appVersion;
                            }
                        });


                        fdb.getDBVersion(new FirebaseDB.DBVersionCallback() {//get db version from firebase
                            @Override
                            public void onDBVersionReceived(String DBversion) {
                                if (DBversion != null) {
                                    if (compareVersion(DBversion, dbh.getDbVersion()) == 1) {//update local db when exist new db version
                                        fdb.selectAllCategory(new FirebaseDB.CategoryCallback() {//select category from firebase
                                            @Override
                                            public void onCategoryListLoaded(List<Category> categories) {
                                                if (compareVersion(lastAppVersion, BuildConfig.VERSION_NAME) == 0) {//the last version
                                                    for (Category category : categories) {//insert category from firebase
                                                        dbh.updateCategory(category.getId(), category.getCategory(), category.getRoCategory(), category.getEnCategory());
                                                        if (dbh.selectCategoryById(category.getId()) == null) {
                                                            category.setUnlockedLevel(1);
                                                            dbh.addCategory(category);
                                                        }
                                                    }
                                                } else {
                                                    for (Category category : categories) {//update category from firebase in db and XML file
                                                        dbh.updateCategory(category.getId(), category.getCategory(), category.getRoCategory(), category.getEnCategory());
                                                    }
                                                }
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError error) {
                                                Log.d("Loading", error.toString());
                                            }
                                        });

                                        fdb.selectAllLevel(new FirebaseDB.LevelCallback() {//select level from firebase
                                            @Override
                                            public void onLevelListLoaded(List<Level> levels) {
                                                dbh.clearTable("Level");//clear level table data

                                                for (Level level : levels) {//insert level from firebase
                                                    dbh.addLevel(level);
                                                }
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError error) {
                                                Log.d("Loading", error.toString());
                                            }
                                        });

                                        //dbh.updateDbVersion(version);//update db version in local db
                                    }
                                }
                            }
                        });
                    }
                }
            });
        }

    }


    public void openCategoryActivity() {
        Intent intent = new Intent(this, CategoryMenuActivity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(0, 0);
    }

    public void openLogInActivity() {
        Intent intent = new Intent(this, LogInActivity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(0, 0);
    }

    public static int compareVersion(String version1, String version2) {
        String[] parts1 = version1.split("\\.");
        String[] parts2 = version2.split("\\.");

        int length = Math.max(parts1.length, parts2.length);

        for (int i = 0; i < length; i++) {
            int part1 = 0;
            int part2 = 0;

            if (i < parts1.length) {
                part1 = Integer.parseInt(parts1[i]);
            }
            if (i < parts2.length) {
                part2 = Integer.parseInt(parts2[i]);
            }


            if (part1 < part2) {
                return -1; //version1 is smaller
            }
            if (part1 > part2) {
                return 1; //version1 is bigger
            }
        }
        return 0; //versions are equal
    }
}