package com.joc_educativ;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.joc_educativ.Adaptors.CategoryAdaptor;
import com.joc_educativ.Database.Category;
import com.joc_educativ.Database.DatabaseHelper;

import java.util.List;
import java.util.Locale;

public class CategoryMenuActivity extends AppCompatActivity {

    private View decorView;
    private ListView categoryButtonListView;
    private ImageButton settingsButton;
    Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_menu);
        decorView = getWindow().getDecorView();//hide system bars

        categoryButtonListView = findViewById(R.id.categoryButtonListView);
        settingsButton = findViewById(R.id.settingsButton);

        DatabaseHelper dbh = new DatabaseHelper(this);
        List<Category> categoryList = dbh.selectAllCategory();//get all category

        for (Category category : categoryList) {//set category text

            if (context.getResources().getConfiguration().locale.getLanguage().equals("ro") && category.getRoCategory() != null) {
                category.setCategory(category.getRoCategory());
            } else if (category.getEnCategory() != null) {
                category.setCategory(category.getEnCategory());
            }
        }

        CategoryAdaptor categoryAdaptor = new CategoryAdaptor(getApplicationContext(), categoryList);
        categoryButtonListView.setAdapter(categoryAdaptor);

        categoryButtonListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                SetingsPreferencis.playClickSound(context);
                int id = categoryList.get(position).getId();//get id of the chosen category
                openLevelActivity(id);
            }
        });

        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SetingsPreferencis.playClickSound(context);
                Locale currentLocale = context.getResources().getConfiguration().locale;
                Settings settings = new Settings(context, -1);
                settings.openSettings();
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

    public void openLevelActivity(int id) {
        Intent intent = new Intent(this, LevelMenuActivity.class);
        intent.putExtra("categoryId", id);//pass the category id in LevelActivity class
        startActivity(intent);
    }
}