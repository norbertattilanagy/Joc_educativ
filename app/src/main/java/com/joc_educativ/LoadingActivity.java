package com.joc_educativ;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.joc_educativ.CustomView.ProgressBarAnimation;
import com.joc_educativ.Database.Category;
import com.joc_educativ.Database.DatabaseHelper;
import com.joc_educativ.Database.FirebaseDB;
import com.joc_educativ.Database.Level;

import java.util.List;
import java.util.Locale;

public class LoadingActivity extends AppCompatActivity {

    private View decorView;
    private ProgressBar progressBar;
    private TextView percentageText;
    private Button playButton;

    private Context context = this;
    List<Category> allCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_loading);

        progressBar = findViewById(R.id.progressBar);
        percentageText = findViewById(R.id.percentageText);
        playButton = findViewById(R.id.playButton);

        decorView = getWindow().getDecorView();//hide system bars

        progressAnimation();//loading

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SetingsPreferencis.playClickSound(context);
                openCategoryActivity();
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

        setLanguage(context);

        updateDBData();

        ProgressBarAnimation animation = new ProgressBarAnimation(this, progressBar, percentageText, 0, 100, playButton);
        animation.setDuration(8000);
        progressBar.setAnimation(animation);
    }

    public void updateDBData() {
        FirebaseDB fdb = new FirebaseDB();
        DatabaseHelper dbh = new DatabaseHelper(this);

        if (NetworkConnection.isNetworkAvailable(this)) {//if exist network connection

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
        for (Level level : levelList){
            fdb.saveLevel(level);
        }*/

            fdb.getDBVersion(new FirebaseDB.DBVersionCallback() {//get db version from firebase
                @Override
                public void onDBVersionReceived(Long version) {
                    if (version != null) {
                        if (version > dbh.getDbVersion()) {//update local db when exist new db version
                            fdb.selectAllCategory(new FirebaseDB.CategoryCallback() {//select category from firebase
                                @Override
                                public void onCategoryListLoaded(List<Category> categories) {
                                /*dbh.clearTable("Category");//clear category table data

                                for (Category category : categories) {//insert category from firebase
                                    dbh.addCategory(category);
                                }*/
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

    public void setLanguage(Context context) {
        String languageCode = SetingsPreferencis.getLanguage(context);
        if (languageCode != null) {
            //set new language
            Locale locale = new Locale(languageCode);
            locale.setDefault(locale);
            Resources resources = context.getResources();
            Configuration config = resources.getConfiguration();
            config.setLocale(locale);
            resources.updateConfiguration(config, resources.getDisplayMetrics());
        }
    }

    public void openCategoryActivity() {
        Intent intent = new Intent(this, CategoryMenuActivity.class);
        startActivity(intent);
        finish();
    }
}