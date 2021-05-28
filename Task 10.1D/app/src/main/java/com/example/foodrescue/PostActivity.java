package com.example.foodrescue;

import android.content.Context;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.foodrescue.fragment.AccountFragment;
import com.example.foodrescue.fragment.CartFragment;
import com.example.foodrescue.fragment.CreateFragment;
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

        bottomNavView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;

                switch (item.getItemId()) {
                    case R.id.homeFragment:
                        selectedFragment = new HomeFragment();
                        fab.setVisibility(View.VISIBLE);
                        break;
                    case R.id.cartFragment:
                        selectedFragment = new CartFragment();
                        fab.setVisibility(View.GONE);
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

    public static LinkedHashMap<Integer, Integer> getCart() {
        return cart;
    }
}
