package com.joc_educativ.Adaptors;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;

import com.joc_educativ.Database.DatabaseHelper;
import com.joc_educativ.Database.Level;
import com.joc_educativ.LevelMenuActivity;
import com.joc_educativ.R;
import com.joc_educativ.SetingsPreferencis;

import java.util.List;

public class LevelAdaptor extends BaseAdapter {

    Context context;
    List<List<Level>> levelModels;
    LayoutInflater inflater;

    public LevelAdaptor(Context context, List<List<Level>> levelModels) {
        this.context = context;
        this.levelModels = levelModels;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return levelModels.size();
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
    public View getView(int i, View view, ViewGroup viewGroup){
        view = inflater.inflate(R.layout.level_list_view,null);

        DatabaseHelper db= new DatabaseHelper(context);
        int unlockedLevel = db.selectUnlockedLevel(levelModels.get(i).get(0).getCategoryId());

        Button button1 = view.findViewById(R.id.button1);
        Button button2 = view.findViewById(R.id.button2);
        Button button3 = view.findViewById(R.id.button3);
        Button button4 = view.findViewById(R.id.button4);
        Button button5 = view.findViewById(R.id.button5);

        button1.setText(String.valueOf(levelModels.get(i).get(0).getLevel()));

        if (Integer.parseInt((String) button1.getText()) > unlockedLevel)//disable next level
            button1.setEnabled(false);

       if (levelModels.get(i).size() > 1) {
            button2.setVisibility(View.VISIBLE);
            button2.setText(String.valueOf(levelModels.get(i).get(1).getLevel()));

           if (Integer.parseInt((String) button2.getText()) > unlockedLevel)//disable next level
               button2.setEnabled(false);
        }

        if (levelModels.get(i).size() > 2) {
            button3.setVisibility(View.VISIBLE);
            button3.setText(String.valueOf(levelModels.get(i).get(2).getLevel()));

            if (Integer.parseInt((String) button3.getText()) > unlockedLevel)//disable next level
                button3.setEnabled(false);
        }

        if (levelModels.get(i).size() > 3) {
            button4.setVisibility(View.VISIBLE);
            button4.setText(String.valueOf(levelModels.get(i).get(3).getLevel()));

            if (Integer.parseInt((String) button4.getText()) > unlockedLevel)//disable next level
                button4.setEnabled(false);
        }

        if (levelModels.get(i).size() > 4) {
            button5.setVisibility(View.VISIBLE);
            button5.setText(String.valueOf(levelModels.get(i).get(4).getLevel()));

            if (Integer.parseInt((String) button5.getText()) > unlockedLevel)//disable next level
                button5.setEnabled(false);
        }

        LevelMenuActivity levelMenuActivity = new LevelMenuActivity();

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SetingsPreferencis.playClickSound(context);
                levelMenuActivity.openGameActivity(context,levelModels.get(i).get(0).getId());
            }
        });
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SetingsPreferencis.playClickSound(context);
                levelMenuActivity.openGameActivity(context,levelModels.get(i).get(1).getId());
            }
        });
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SetingsPreferencis.playClickSound(context);
                levelMenuActivity.openGameActivity(context,levelModels.get(i).get(2).getId());
            }
        });
        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SetingsPreferencis.playClickSound(context);
                levelMenuActivity.openGameActivity(context,levelModels.get(i).get(3).getId());
            }
        });
        button5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SetingsPreferencis.playClickSound(context);
                levelMenuActivity.openGameActivity(context,levelModels.get(i).get(4).getId());
            }
        });


        return view;
    }
}
