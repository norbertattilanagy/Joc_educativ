package com.joc_educativ;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.joc_educativ.Adaptors.CategoryAdaptor;
import com.joc_educativ.Database.CategoryModel;
import com.joc_educativ.Database.DatabaseHelper;

import java.util.List;

public class CategoryActivity extends AppCompatActivity {

    private View decorView;
    private ListView categoryButtonListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        decorView = getWindow().getDecorView();//hide system bars

        categoryButtonListView = findViewById(R.id.categoryButtonListView);

        DatabaseHelper dbh = new DatabaseHelper(this);
        List<CategoryModel> categoryList = dbh.selectAllCategory();//get all category

        CategoryAdaptor categoryAdaptor = new CategoryAdaptor(getApplicationContext(),categoryList);
        categoryButtonListView.setAdapter(categoryAdaptor);

        categoryButtonListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                int id = categoryList.get(position).getId();//get id of the chosen category
                openLevelActivity(id);
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

    public void openLevelActivity(int id){
        Intent intent = new Intent(this,LevelActivity.class);
        intent.putExtra("categoryId",id);//pass the category id in LevelActivity class
        startActivity(intent);
    }

}