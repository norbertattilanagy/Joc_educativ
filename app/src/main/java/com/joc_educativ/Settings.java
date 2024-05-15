package com.joc_educativ;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Settings {

    Context context;
    int categoryId;
    int languageIndex;
    ImageButton soundButton, vibrationButton, leftButton, rightButton, closeButton;
    TextView languageTextView;
    Button authenticationButton;
    Locale currentLanguage;

    public Settings(Context context, int categoryId) {
        this.context = context;
        this.categoryId = categoryId;
        currentLanguage = context.getResources().getConfiguration().locale;//save language when open dialog
    }

    public void openSettings() {
        Dialog dialog = new Dialog(context, R.style.CustomDialog);
        dialog.setContentView(R.layout.settings_dialog);
        //dialog.setCancelable(false);

        closeButton = dialog.findViewById(R.id.closeButton);
        soundButton = dialog.findViewById(R.id.soundButton);
        vibrationButton = dialog.findViewById(R.id.vibrationButton);
        leftButton = dialog.findViewById(R.id.leftButton);
        rightButton = dialog.findViewById(R.id.rightButton);
        languageTextView = dialog.findViewById(R.id.languageTextView);
        authenticationButton = dialog.findViewById(R.id.authenticationButton);

        if (SetingsPreferencis.getSound(context))
            soundButton.setImageResource(R.drawable.ic_baseline_volume_up_48);
        else
            soundButton.setImageResource(R.drawable.ic_baseline_volume_off_48);

        if (SetingsPreferencis.getVibration(context))
            vibrationButton.setImageResource(R.drawable.ic_baseline_vibration_48);
        else
            vibrationButton.setImageResource(R.drawable.ic_baseline_mobile_off_48);

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            authenticationButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            authenticationButton.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail() + "\n" + context.getString(R.string.log_out));
        }

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
                refreshActivity();
            }
        });

        soundButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SetingsPreferencis.playClickSound(context);
                setSound(soundButton);
            }
        });

        vibrationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SetingsPreferencis.playClickSound(context);
                setVibration(vibrationButton);
            }
        });

        leftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SetingsPreferencis.playClickSound(context);
                changeLanguage(false, dialog);
            }
        });

        rightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SetingsPreferencis.playClickSound(context);
                changeLanguage(true, dialog);
            }
        });

        authenticationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SetingsPreferencis.playClickSound(context);
                openSignIn();
            }
        });

        //System.out.println("SUCCESS: " + FirebaseAuth.getInstance().getCurrentUser().getUid() + "," + FirebaseAuth.getInstance().getCurrentUser().getEmail());
        dialog.show();
    }

    private void setSound(ImageButton soundButton) {
        SetingsPreferencis.saveSound(context, !SetingsPreferencis.getSound(context));//save

        if (SetingsPreferencis.getSound(context))
            soundButton.setImageResource(R.drawable.ic_baseline_volume_up_48);
        else
            soundButton.setImageResource(R.drawable.ic_baseline_volume_off_48);
    }

    private void setVibration(ImageButton vibrationButton) {
        SetingsPreferencis.saveVibration(context, !SetingsPreferencis.getVibration(context));//save

        if (SetingsPreferencis.getVibration(context))
            vibrationButton.setImageResource(R.drawable.ic_baseline_vibration_48);
        else
            vibrationButton.setImageResource(R.drawable.ic_baseline_mobile_off_48);
    }

    private void changeLanguage(Boolean nextLanguage, Dialog dialog) {
        List<String> languages = getSupportedLanguages();

        languageIndex = languages.indexOf(languageTextView.getText());

        if (nextLanguage && languages.size() - 1 > languageIndex) {
            languageIndex++;
        } else if (!nextLanguage && languageIndex > 0) {
            languageIndex--;
        } else {
            if (nextLanguage)
                languageIndex = 0;
            else
                languageIndex = languages.size() - 1;
        }
        languageTextView.setText(languages.get(languageIndex));

        //set new language
        Locale locale = new Locale(getLanguageCode(languages.get(languageIndex)));
        locale.setDefault(locale);
        Resources resources = context.getResources();
        Configuration config = resources.getConfiguration();
        config.setLocale(locale);
        resources.updateConfiguration(config, resources.getDisplayMetrics());

        openSettings();//refresh dialog
        dialog.cancel();
    }

    private List<String> getSupportedLanguages() {//return supported language
        List<String> languages = new ArrayList<>();
        languages.add("English");
        languages.add("Română");
        return languages;
    }

    private String getLanguageCode(String language) {
        String code;

        switch (language) {
            case "English":
                code = "en";
                break;
            case "Română":
                code = "ro";
                break;
            default:
                code = "en";
        }

        return code;
    }

    private void refreshActivity() {
        Locale languageCode = context.getResources().getConfiguration().locale;
        if (currentLanguage != languageCode) {
            SetingsPreferencis.saveLanguage(context, String.valueOf(languageCode));//save language preferences

            Intent intent = new Intent(context, context.getClass());//reopen the activity
            if (categoryId != -1)
                intent.putExtra("categoryId", categoryId);//pass the category id in LevelActivity class
            context.startActivity(intent);
        }
    }

    //log out or open sign in activity
    private void openSignIn() {
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            FirebaseAuth.getInstance().signOut();
            closeButton.performClick();
        } else {
            closeButton.performClick();//close dialog
            Intent intent = new Intent(context, LogInActivity.class);
            context.startActivity(intent);
        }
    }
}
