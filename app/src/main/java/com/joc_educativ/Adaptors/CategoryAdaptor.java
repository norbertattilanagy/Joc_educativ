package com.joc_educativ.Adaptors;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;

import com.joc_educativ.Database.Category;
import com.joc_educativ.R;

import java.util.List;

public class CategoryAdaptor extends BaseAdapter {

    Context context;
    List<Category> categories;
    LayoutInflater inflater;

    public CategoryAdaptor(Context context, List<Category> categories){
        this.context = context;
        this.categories = categories;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return categories.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflater.inflate(R.layout.category_list_view, null);//get view
        Button button = view.findViewById(R.id.button);//get button from view
        button.setText(categories.get(i).getCategory());//set button text
        return view;
    }
}
