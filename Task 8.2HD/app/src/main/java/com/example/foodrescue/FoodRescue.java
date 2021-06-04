package com.example.foodrescue;

import android.app.Application;

import com.paypal.checkout.PayPalCheckout;
import com.paypal.checkout.config.CheckoutConfig;
import com.paypal.checkout.config.Environment;
import com.paypal.checkout.config.SettingsConfig;
import com.paypal.checkout.createorder.CurrencyCode;
import com.paypal.checkout.createorder.UserAction;

public class FoodRescue extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        CheckoutConfig checkoutConfig = new CheckoutConfig(
            this,
            getString(R.string.pp_id),
            Environment.SANDBOX,
            String.format("%s://paypalpay", BuildConfig.APPLICATION_ID),
            CurrencyCode.AUD,
            UserAction.PAY_NOW,
            new SettingsConfig(
                false,
                false
            )
        );

        PayPalCheckout.setConfig(checkoutConfig);
    }
}
