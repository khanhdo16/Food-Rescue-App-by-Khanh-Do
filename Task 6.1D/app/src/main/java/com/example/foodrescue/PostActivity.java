package com.example.foodrescue;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.foodrescue.fragment.AccountFragment;
import com.example.foodrescue.fragment.CreateFragment;
import com.example.foodrescue.fragment.HomeFragment;
import com.example.foodrescue.fragment.MyListFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;


public class PostActivity extends AppCompatActivity {
    BottomNavigationView bottomNavView;
    FloatingActionButton fab;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        bottomNavView = findViewById(R.id.bottomNavigationView);
        fab = findViewById(R.id.floatingActionButton);

        bottomNavView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;

                switch (item.getItemId()) {
                    case R.id.homeFragment:
                        selectedFragment = new HomeFragment();
                        fab.setVisibility(View.VISIBLE);
                        break;
                    case R.id.myListFragment:
                        selectedFragment = new MyListFragment();
                        fab.setVisibility(View.VISIBLE);
                        break;
                    case R.id.accountFragment:
                        selectedFragment = new AccountFragment();
                        fab.setVisibility(View.GONE);
                        break;
                }

                getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment, selectedFragment)
                    .commit();

                return true;
            }
        });
    }

    public void createPost(View view) {
        fab.setVisibility(View.GONE);

        Fragment fragment = new CreateFragment();
        getSupportFragmentManager().beginTransaction()
            .replace(R.id.fragment, fragment)
            .addToBackStack(null)
            .commit();
    }


}
