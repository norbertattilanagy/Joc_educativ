package com.joc_educativ;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;

public class CreateAccount extends AppCompatActivity {

    private View decorView;
    private EditText emailEditText, password1EditText, password2EditText;
    private TextInputLayout emailTextInputLayout, password1TextInputLayout, password2TextInputLayout;
    private Button createAccountButton;
    private TextView signInLink;
    private ProgressDialog LoadingBar;

    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
        decorView = getWindow().getDecorView();//hide system bars

        emailEditText = findViewById(R.id.emailEditText);
        password1EditText = findViewById(R.id.password1EditText);
        password2EditText = findViewById(R.id.password2EditText);
        emailTextInputLayout = findViewById(R.id.emailTextInputLayout);
        password1TextInputLayout = findViewById(R.id.password1TextInputLayout);
        password2TextInputLayout = findViewById(R.id.password2TextInputLayout);
        createAccountButton = findViewById(R.id.createAccountButton);
        signInLink = findViewById(R.id.signInLink);

        firebaseAuth = FirebaseAuth.getInstance();
        LoadingBar = new ProgressDialog(this);

        createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createAccount();
            }
        });

        signInLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openSignInActivity();
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

    private void createAccount() {
        String email = String.valueOf(emailEditText.getText());
        String password1 = String.valueOf(password1EditText.getText());
        String password2 = String.valueOf(password2EditText.getText());
        Boolean correct = true;

        removeAllError();//remove all error message
        if (!email.contains("@")) {
            showError(emailTextInputLayout, getString(R.string.incorrect_email));
            correct = false;
        }
        if (password1.isEmpty()) {
            showError(password1TextInputLayout, getString(R.string.incorrect_password));
            correct = false;
        } else {
            Boolean upperCase = false;
            Boolean lowerCase = false;
            Boolean digit = false;

            for (int i = 0; i < password1.length(); i++) {
                if (Character.isUpperCase(password1.charAt(i)))//contain upper case
                    upperCase = true;
                else if (Character.isLowerCase(password1.charAt(i)))//contain lower case
                    lowerCase = true;
                else if (Character.isDigit(password1.charAt(i)))//contain digit
                    digit = true;
            }

            if (!upperCase) {
                showError(password1TextInputLayout, getString(R.string.not_contain_upper_case));
                correct = false;
            } else if (!lowerCase) {
                showError(password1TextInputLayout, getString(R.string.not_contain_lower_case));
                correct = false;
            } else if (!digit) {
                showError(password1TextInputLayout, getString(R.string.not_contain_digit));
                correct = false;
            }
        }
        if (password2.isEmpty()) {
            showError(password2TextInputLayout, getString(R.string.incorrect_password));
            correct = false;
        }
        if (!password1.equals(password2)) {
            showError(password2TextInputLayout, getString(R.string.password_not_match));
            correct = false;
        }

        if (correct) {
            //Toast.makeText(this."Call registration method",Toast.LENGTH_SHORT).show();
            /*LoadingBar.setTitle("Registration");
            LoadingBar.setMessage("Please wait");
            LoadingBar.setCanceledOnTouchOutside(false);
            LoadingBar.show();*/

            firebaseAuth.createUserWithEmailAndPassword(email, password1).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(CreateAccount.this, "Successful create account", Toast.LENGTH_SHORT).show();
                        openCategoryActivity();
                    } else {
                        if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {//incorrect email format
                            showError(emailTextInputLayout,getString(R.string.incorrect_email));
                        } else if (task.getException() instanceof FirebaseAuthUserCollisionException) {//the email is registered
                            showError(emailTextInputLayout,getString(R.string.registered_email));
                        } else {
                            Log.d("Create Account", task.getException().toString());
                        }
                    }
                }
            });
        }

    }

    private void showError(TextInputLayout textInputLayout, String message) {
        textInputLayout.setHelperTextColor(ColorStateList.valueOf(Color.RED));
        textInputLayout.setHelperText(message);
    }

    private void removeAllError() {
        emailEditText.clearFocus();
        emailTextInputLayout.setHelperText("");
        emailTextInputLayout.setHintEnabled(false);

        password1EditText.clearFocus();
        password1TextInputLayout.setHelperText("");
        password1TextInputLayout.setHintEnabled(false);

        password2EditText.clearFocus();
        password2TextInputLayout.setHelperText("");
        password2TextInputLayout.setHintEnabled(false);
    }

    public void openCategoryActivity() {
        Intent intent = new Intent(this, CategoryMenuActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    public void openSignInActivity() {//open sign in page
        Intent intent = new Intent(this, SignIn.class);
        startActivity(intent);
    }
}