package com.example.foodrescue;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.biometric.BiometricPrompt;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.foodrescue.data.DatabaseHelper;
import com.example.foodrescue.fragment.SignUpFragment;

import java.util.concurrent.Executor;

import static androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG;
import static androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL;

public class MainActivity extends AppCompatActivity  {
    EditText loginUsernameEditText;
    EditText loginPasswordEditText;
    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;
    CheckBox checkBox;

    private String email;
    private Boolean remember_login;
    private Boolean fingerprint_login;
    private Executor executor;
    private BiometricPrompt biometricPrompt;
    private BiometricPrompt.PromptInfo promptInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loginUsernameEditText = findViewById(R.id.loginUsernameEditText);
        loginPasswordEditText = findViewById(R.id.loginPasswordEditText);
        checkBox = findViewById(R.id.checkBox);

        sharedPref = getSharedPreferences("currentAccount", Context.MODE_PRIVATE);
        editor = sharedPref.edit();

        remember_login = sharedPref.getBoolean("remember_login", false);
        email = sharedPref.getString("email", null);
        fingerprint_login = sharedPref.getBoolean("fingerprint_login", false);

        executor = ContextCompat.getMainExecutor(this);
        biometricPrompt = new BiometricPrompt(MainActivity.this, executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                if (!errString.equals("Cancel")) {
                    Toast.makeText(MainActivity.this,
                        errString, Toast.LENGTH_SHORT)
                        .show();
                }
            }

            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                Toast.makeText(getApplicationContext(),
                    "Authentication succeeded!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this, PostActivity.class);
                startActivity(intent);
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                Toast.makeText(getApplicationContext(), "Authentication failed",
                    Toast.LENGTH_SHORT)
                    .show();
            }
        });

        promptInfo = new BiometricPrompt.PromptInfo.Builder()
            .setTitle("Log in with biometric")
            .setNegativeButtonText("Cancel")
            .build();

        if (remember_login && email != null) {
            loginUsernameEditText.setText(email);
            checkBox.setChecked(true);
            if (fingerprint_login) {
                biometricPrompt.authenticate(promptInfo);
            }
        }
    }

    @Override
    protected void onNightModeChanged(int mode) {
        super.onNightModeChanged(mode);
    }

    public void buttonAction(View view) {

        switch (view.getId()) {
            case R.id.loginButton:
                DatabaseHelper db = new DatabaseHelper(MainActivity.this);
                boolean result = db.checkUser(
                    loginUsernameEditText.getText().toString(),
                    loginPasswordEditText.getText().toString()
                );

                if (result == true) {
                    if (checkBox.isChecked()) {
                        editor.putBoolean("remember_login", true);
                    }
                    editor.putString("email", loginUsernameEditText.getText().toString());
                    editor.apply();

                    Intent intent = new Intent(MainActivity.this, PostActivity.class);

                    startActivity(intent);
                    Toast.makeText(MainActivity.this, "Logged in successfully!", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(MainActivity.this, "Email/password incorrect! Please try again.", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.signupButton:
                Fragment fragment;
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                FrameLayout frameLayout = findViewById(R.id.frameLayout);

                fragment = new SignUpFragment();
                fragmentTransaction
                    .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                    .replace(R.id.frameLayout, fragment)
                    .addToBackStack(null)
                    .commit();
                frameLayout.setVisibility(View.VISIBLE);
                break;
            case R.id.fingerprintButton:
                if (remember_login && fingerprint_login && email != null) {
                    biometricPrompt.authenticate(promptInfo);
                }
                else {
                    Toast.makeText(MainActivity.this,
                        "Please enable Login with biometric in Account settings!",
                        Toast.LENGTH_SHORT).show();
                }

                break;
            default:
                throw new IllegalArgumentException("Something went wrong! Please try again.");
        }
    }
}
