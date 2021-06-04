package com.example.foodrescue;

import android.content.Context;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavHostController;
import androidx.navigation.fragment.NavHostFragment;

import com.example.foodrescue.fragment.AccountFragment;
import com.example.foodrescue.fragment.CartFragment;
import com.example.foodrescue.fragment.CreateFragment;
import com.example.foodrescue.fragment.DetectFragment;
import com.example.foodrescue.fragment.HomeFragment;
import com.example.foodrescue.fragment.MyListFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.paypal.checkout.PayPalCheckout;
import com.paypal.checkout.approve.Approval;
import com.paypal.checkout.approve.OnApprove;
import com.paypal.checkout.config.CheckoutConfig;
import com.paypal.checkout.config.Environment;
import com.paypal.checkout.config.SettingsConfig;
import com.paypal.checkout.createorder.CreateOrder;
import com.paypal.checkout.createorder.CreateOrderActions;
import com.paypal.checkout.createorder.CurrencyCode;
import com.paypal.checkout.createorder.OrderIntent;
import com.paypal.checkout.createorder.UserAction;
import com.paypal.checkout.order.Amount;
import com.paypal.checkout.order.AppContext;
import com.paypal.checkout.order.CaptureOrderResult;
import com.paypal.checkout.order.Items;
import com.paypal.checkout.order.OnCaptureComplete;
import com.paypal.checkout.order.Order;
import com.paypal.checkout.order.PurchaseUnit;
import com.paypal.checkout.paymentbutton.PayPalButton;
import com.paypal.checkout.paymentbutton.PaymentButton;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;


public class PostActivity extends AppCompatActivity {
    NavHostFragment navHostFragment;
    BottomNavigationView bottomNavView;
    FloatingActionButton fab;
    private static String ppId;
    private static LinkedHashMap<Integer, Integer> cart = new LinkedHashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        bottomNavView = findViewById(R.id.bottomNavigationView);
        fab = findViewById(R.id.floatingActionButton);

        navHostFragment = (NavHostFragment) getSupportFragmentManager()
            .findFragmentById(R.id.fragment);



        if (navHostFragment != null) {

            bottomNavView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.homeFragment:
                            NavHostFragment.findNavController(navHostFragment).navigate(R.id.homeFragment);
                            fab.setVisibility(View.VISIBLE);
                            break;
                        case R.id.cartFragment:
                            NavHostFragment.findNavController(navHostFragment).navigate(R.id.cartFragment);
                            fab.setVisibility(View.GONE);
                            break;
                        case R.id.myListFragment:
                            NavHostFragment.findNavController(navHostFragment).navigate(R.id.myListFragment);
                            fab.setVisibility(View.VISIBLE);
                            break;
                        case R.id.accountFragment:
                            NavHostFragment.findNavController(navHostFragment).navigate(R.id.accountFragment);
                            fab.setVisibility(View.GONE);
                            break;
                        case R.id.detectFragment:
                            NavHostFragment.findNavController(navHostFragment).navigate(R.id.detectFragment);
                            fab.setVisibility(View.GONE);
                            break;
                    }

                    return true;
                }
            });
        }
    }

    @Override
    protected void onNightModeChanged(int mode) {
        super.onNightModeChanged(mode);
    }

    public void createPost(View view) {
        if (navHostFragment != null) {
            fab.setVisibility(View.GONE);

            NavHostFragment.findNavController(navHostFragment).navigate(R.id.createFragment);
        }
    }

    public static LinkedHashMap<Integer, Integer> getCart() {
        return cart;
    }
}
