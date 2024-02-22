package com.joc_educativ;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;

public class SetingsPreferencis {

    //save language preferences
    public static void saveLanguage(Context context, String language) {
        SharedPreferences preferences = context.getSharedPreferences("SettingsPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("language", language);
        editor.apply();
    }

    //get language preferences
    public static String getLanguage(Context context) {
        SharedPreferences preferences = context.getSharedPreferences("SettingsPrefs", Context.MODE_PRIVATE);
        String language = preferences.getString("language", null);
        return language;
    }

    //save sound preferences
    public static void saveSound(Context context, Boolean sound) {
        SharedPreferences preferences = context.getSharedPreferences("SettingsPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("sound", sound);
        editor.apply();
    }

    //get sound preferences
    public static Boolean getSound(Context context) {
        SharedPreferences preferences = context.getSharedPreferences("SettingsPrefs", Context.MODE_PRIVATE);
        Boolean sound = preferences.getBoolean("sound", true);
        return sound;
    }

    //save vibration preferences
    public static void saveVibration(Context context, Boolean vibration) {
        SharedPreferences preferences = context.getSharedPreferences("SettingsPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("vibration", vibration);
        editor.apply();
    }

    //get vibration preferences
    public static Boolean getVibration(Context context) {
        SharedPreferences preferences = context.getSharedPreferences("SettingsPrefs", Context.MODE_PRIVATE);
        Boolean vibration = preferences.getBoolean("vibration", true);
        return vibration;
    }

    public static void playClickSound(Context context){
        if (SetingsPreferencis.getSound(context)) {
            MediaPlayer mediaPlayer = MediaPlayer.create(context, R.raw.click);
            mediaPlayer.seekTo(0); // Rewind sound to beginning
            mediaPlayer.start();
        }
    }
}
