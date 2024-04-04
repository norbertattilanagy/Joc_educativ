package com.joc_educativ;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;

import com.joc_educativ.Adaptors.LevelAdaptor;
import com.joc_educativ.Database.DatabaseHelper;
import com.joc_educativ.Database.Level;
import com.joc_educativ.Game.MoveGameActivity;

import java.util.ArrayList;
import java.util.List;

public class LevelMenuActivity extends AppCompatActivity {

    private View decorView;
    private ListView levelButtonListView;
    private ImageButton backButton,settingsButton;
    Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level_menu);
        decorView = getWindow().getDecorView();//hide system bars

        levelButtonListView = findViewById(R.id.levelButtonListView);

        backButton = findViewById(R.id.backButton);
        settingsButton = findViewById(R.id.settingsButton);

        Intent intent = getIntent();//get submitted id
        int categoryId = intent.getIntExtra("categoryId",-1);
        setButtonInListView(categoryId);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SetingsPreferencis.playClickSound(context);
                openCategoryActivity();
            }
        });

        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SetingsPreferencis.playClickSound(context);
                Settings settings = new Settings(context,categoryId);
                settings.openSettings();
            }
        });
    }

    //hide system bars
    @Override
    public void onWindowFocusChanged(boolean hasFocus){
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus){
            decorView.setSystemUiVisibility(hideSystemBars());
        }
    }
    private int hideSystemBars(){
        return View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
    }
    //---

    private void setButtonInListView(int categoryId){
        DatabaseHelper dbh = new DatabaseHelper(this);
        List<Level> levelListByCategory = dbh.selectAllLevelByCategory(categoryId);//get all level by category
        List<List<Level>> levelMatrix = new ArrayList<>();//create button matrix

        for (int i=0;i<levelListByCategory.size();i+=5){
            int nrButton = levelListByCategory.size() % 5;//set nr button in line
            if (i < levelListByCategory.size()-nrButton)
                nrButton=5;

            levelMatrix.add(levelListByCategory.subList(i,i+nrButton));//add button in matrix
        }

        LevelAdaptor levelAdaptor = new LevelAdaptor(this,levelMatrix);
        levelButtonListView.setAdapter(levelAdaptor);
    }

    public void openGameActivity(Context context,int levelId){
        Intent intent = new Intent(context, MoveGameActivity.class);
        intent.putExtra("levelId",levelId);//pass the category id in LevelActivity class
        context.startActivity(intent);
    }

    public void openCategoryActivity(){
        Intent intent = new Intent(this, CategoryMenuActivity.class);
        startActivity(intent);
    }

    public void refreshActivity(){
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }

}