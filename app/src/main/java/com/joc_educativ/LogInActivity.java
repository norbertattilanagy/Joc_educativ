package com.joc_educativ;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.database.DatabaseError;
import com.joc_educativ.Database.Category;
import com.joc_educativ.Database.DatabaseHelper;
import com.joc_educativ.Database.FirebaseDB;

import java.util.List;

public class LogInActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 100;
    private View decorView;
    private EditText emailEditText, passwordEditText;
    private TextInputLayout emailTextInputLayout, passwordTextInputLayout;
    private ImageButton backButton;
    private Button authenticationButton;
    private CustomButton googleSignInButton;
    private TextView createAccountLink;
    private GoogleSignInClient googleSignInClient;
    private FirebaseAuth firebaseAuth;
    Boolean existEmail = true;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //remove notch area
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
            layoutParams.layoutInDisplayCutoutMode =
                    WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
            getWindow().setAttributes(layoutParams);
        }

        setContentView(R.layout.activity_log_in);

        decorView = getWindow().getDecorView();//hide system bars
        decorView.setSystemUiVisibility(hideSystemBars());

        backButton = findViewById(R.id.backButton);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        emailTextInputLayout = findViewById(R.id.emailTextInputLayout);
        passwordTextInputLayout = findViewById(R.id.passwordTextInputLayout);
        authenticationButton = findViewById(R.id.authenticationButton);
        createAccountLink = findViewById(R.id.createAccountLink);
        googleSignInButton = findViewById(R.id.googleSignInButton);

        emailTextInputLayout.setHelperTextEnabled(false);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SetingsPreferencis.playClickSound(LogInActivity.this);
                openCategoryActivity();
            }
        });

        authenticationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SetingsPreferencis.playClickSound(LogInActivity.this);
                // Close the keyboard
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                authentication();
            }
        });

        createAccountLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SetingsPreferencis.playClickSound(LogInActivity.this);
                openCreateAccountActivity();
            }
        });

        //init firebase auth
        firebaseAuth = FirebaseAuth.getInstance();

        //configure the google sign in
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(LogInActivity.this, googleSignInOptions);

        googleSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SetingsPreferencis.playClickSound(LogInActivity.this);
                signOutAndSignIn();
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

    private void authentication() {
        String email = String.valueOf(emailEditText.getText());
        String password = String.valueOf(passwordEditText.getText());

        removeAllError();//remove all error message
        if (!email.contains("@")) {
            showError(emailTextInputLayout, getString(R.string.incorrect_email));
        }
        if (password.isEmpty()) {
            showError(passwordTextInputLayout, getString(R.string.incorrect_password));
        }
        if (email.contains("@") && !password.isEmpty()) {//if exist email and password in editText
            firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        questionProgress(new DialogCallback() {
                            @Override
                            public void onYesClicked() {
                                syncUnlockedLevel(true);//save in firebase
                                openCategoryActivity();
                            }
                            @Override
                            public void onNoClicked() {
                                syncUnlockedLevel(false);//save in local DB
                                openCategoryActivity();
                            }
                        });
                    } else {
                        Log.d("Sign In", task.getException().toString());
                        checkEmailExists();
                        if (existEmail)
                            showError(passwordTextInputLayout, getString(R.string.incorrect_password));
                        else
                            showError(emailTextInputLayout, getString(R.string.incorrect_email));
                    }
                }
            });
        }
    }

    //connect with google
    private void signOutAndSignIn() {//choose Google account every time
        googleSignInClient.signOut().addOnCompleteListener(this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                // After signing out, start the sign-in process
                Intent intent = googleSignInClient.getSignInIntent();
                startActivityForResult(intent, RC_SIGN_IN);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {

            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d("Sign In", "firebaseAuthWithGoogle:" + account.getId());
                firebaseAuthWithGoogleAccount(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed
                Log.w("Sign In", "Google sign in failed", e);
                Toast.makeText(LogInActivity.this, getString(R.string.authentication_failed), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogleAccount(String account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account, null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            questionProgress(new DialogCallback() {
                                @Override
                                public void onYesClicked() {
                                    syncUnlockedLevel(true);//save in firebase
                                    openCategoryActivity();
                                }

                                @Override
                                public void onNoClicked() {
                                    syncUnlockedLevel(false);//save in local DB
                                    openCategoryActivity();
                                }
                            });
                        } else {
                            Log.d("SignIn", "fail ", task.getException());
                            Toast.makeText(LogInActivity.this, getString(R.string.authentication_failed), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    //-----

    void checkEmailExists() {
        firebaseAuth.fetchSignInMethodsForEmail(emailEditText.getText().toString()).addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
            @Override
            public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                Log.d("Sign In", "" + task.getResult().getSignInMethods().size());
                if (task.getResult().getSignInMethods().size() == 0) {
                    existEmail = false;
                }
            }
        });
    }

    private void showError(TextInputLayout textInputLayout, String message) {
        textInputLayout.setHelperTextColor(ColorStateList.valueOf(Color.RED));
        textInputLayout.setHelperText(message);
    }

    private void removeAllError() {
        emailEditText.clearFocus();
        emailTextInputLayout.setHelperText("");
        emailTextInputLayout.setHintEnabled(false);

        passwordEditText.clearFocus();
        passwordTextInputLayout.setHelperText("");
        passwordTextInputLayout.setHintEnabled(false);
    }

    private void syncUnlockedLevel(Boolean syncFirebase) {
        FirebaseDB fdb = new FirebaseDB();
        DatabaseHelper dbh = new DatabaseHelper(this);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {//if connect User
            List<Category> allLocalCategory = dbh.selectAllCategory();//sync unlocked level
            for (Category category : allLocalCategory) {
                fdb.selectUserLevel(category.getId(), new FirebaseDB.UnlockedLevelCallback() {
                    @Override
                    public void onUnlockedLevelReceived(int unlockedLevel) {
                        if (syncFirebase) {
                            if (unlockedLevel < category.getUnlockedLevel())
                                fdb.saveUserLevel(category.getId(), category.getUnlockedLevel());//save in firebase
                            else if (unlockedLevel > category.getUnlockedLevel()) {
                                //dbh.updateUnlockedLevel(category.getId(), 2);
                                dbh.updateUnlockedLevel(category.getId(), unlockedLevel);//save in local db
                            }
                        } else {
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
    }


    public interface DialogCallback {
        void onYesClicked();
        void onNoClicked();
    }

    private void questionProgress(DialogCallback callback){

        Dialog dialog = new Dialog(this, R.style.CustomDialog);
        dialog.setContentView(R.layout.question_dialog);
        dialog.setCancelable(false);

        Button yesButton = dialog.findViewById(R.id.yesButton);
        Button noButton = dialog.findViewById(R.id.homeButton);

        yesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callback.onYesClicked();
                dialog.cancel();
            }
        });

        noButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callback.onNoClicked();
                dialog.cancel();
            }
        });

        dialog.show();
    }

    public void openCategoryActivity() {
        Intent intent = new Intent(this, CategoryMenuActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        overridePendingTransition(0,0);
    }

    public void openCreateAccountActivity() {
        Intent intent = new Intent(this, CreateAccountActivity.class);
        startActivity(intent);
        overridePendingTransition(0,0);
    }
}