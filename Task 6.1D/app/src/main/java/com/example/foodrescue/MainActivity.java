package com.example.foodrescue;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.example.foodrescue.data.DatabaseHelper;
import com.example.foodrescue.fragment.SignUpFragment;

public class MainActivity extends AppCompatActivity {
    EditText loginUsernameEditText;
    EditText loginPasswordEditText;
    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;
    CheckBox checkBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loginUsernameEditText = findViewById(R.id.loginUsernameEditText);
        loginPasswordEditText = findViewById(R.id.loginPasswordEditText);
        checkBox = findViewById(R.id.checkBox);

        sharedPref = getSharedPreferences("currentAccount", Context.MODE_PRIVATE);
        editor = sharedPref.edit();

        Boolean remember_login = sharedPref.getBoolean("remember_login", false);
        if (remember_login) {
            Intent intent = new Intent(MainActivity.this, PostActivity.class);
            startActivity(intent);
            Toast.makeText(MainActivity.this, "Logged in successfully!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        editor.remove("email");
        editor.apply();
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
            default:
                throw new IllegalArgumentException("Something went wrong! Please try again.");
        }
    }
}
