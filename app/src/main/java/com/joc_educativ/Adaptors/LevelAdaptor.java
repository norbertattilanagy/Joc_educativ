package com.joc_educativ.Adaptors;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

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
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflater.inflate(R.layout.level_list_view, null);

        DatabaseHelper db = new DatabaseHelper(context);
        int unlockedLevel = db.selectUnlockedLevel(levelModels.get(i).get(0).getCategoryId());

        LevelMenuActivity levelMenuActivity = new LevelMenuActivity();

        int levelCount = levelModels.get(i).size();
        int[] levelIds = {R.id.level1, R.id.level2, R.id.level3, R.id.level4, R.id.level5};

        for (int j = 0; j < levelCount && j < levelIds.length; j++) {
            LinearLayout levelLayout = view.findViewById(levelIds[j]);
            levelLayout.setVisibility(View.VISIBLE);

            //set the level number
            Button levelButton = levelLayout.findViewById(R.id.button);
            levelButton.setText(String.valueOf(levelModels.get(i).get(j).getLevel()));

            //get star
            ImageView star1 = levelLayout.findViewById(R.id.star1);
            ImageView star2 = levelLayout.findViewById(R.id.star2);
            ImageView star3 = levelLayout.findViewById(R.id.star3);

            //if unlocked the level
            if (levelModels.get(i).get(j).getLevel() > unlockedLevel) {
                levelButton.setEnabled(false);
                star1.setAlpha(0.4f);
                star2.setAlpha(0.4f);
                star3.setAlpha(0.4f);
            }

            //verify the star
            if (levelModels.get(i).get(j).getUserStar() == 3) {
                star3.setImageResource(R.drawable.star_yellow);
                star2.setImageResource(R.drawable.star_yellow);
                star1.setImageResource(R.drawable.star_yellow);

            } else if (levelModels.get(i).get(j).getUserStar() == 2) {
                star2.setImageResource(R.drawable.star_yellow);
                star1.setImageResource(R.drawable.star_yellow);

            } else if (levelModels.get(i).get(j).getUserStar() == 1) {
                star1.setImageResource(R.drawable.star_yellow);
            }


            Level level = levelModels.get(i).get(j);//get the current level object
            levelButton.setOnClickListener(new View.OnClickListener() {//click the button
                @Override
                public void onClick(View view) {
                    SetingsPreferencis.playClickSound(context);
                    levelMenuActivity.openGameActivity(context, level.getId());
                }
            });
        }

        return view;
    }
}
