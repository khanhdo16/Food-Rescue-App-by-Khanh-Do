package com.example.foodrescue.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.biometric.BiometricManager;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.fragment.NavHostFragment;

import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.foodrescue.MainActivity;
import com.example.foodrescue.R;
import com.example.foodrescue.data.DatabaseHelper;
import com.example.foodrescue.model.User;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import static androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG;

public class AccountFragment extends Fragment {

    private String email;
    SharedPreferences sharedPref;

    public AccountFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_account, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        NavHostFragment navHostFragment = (NavHostFragment) requireActivity().getSupportFragmentManager()
            .findFragmentById(R.id.fragment);

        sharedPref = requireActivity().getSharedPreferences("currentAccount", Activity.MODE_PRIVATE);
        email = sharedPref.getString("email", null);

        Button accountUpdateButton = (Button) view.findViewById(R.id.accountUpdateButton);
        Button accountLogoutButton = (Button) view.findViewById(R.id.accountLogoutButton);
        EditText accountNameEditText = (EditText) view.findViewById(R.id.accountNameEditText);
        EditText accountEmailEditText = (EditText) view.findViewById(R.id.accountEmailEditText);
        EditText accountPhoneEditText = (EditText) view.findViewById(R.id.accountPhoneEditText);
        EditText accountAddressEditText = (EditText) view.findViewById(R.id.accountAddressEditText);
        EditText oldPasswordEditText = (EditText) view.findViewById(R.id.oldPasswordEditText);
        EditText accountPasswordEditText = (EditText) view.findViewById(R.id.accountPasswordEditText);
        EditText accountConfirmEditText = (EditText) view.findViewById(R.id.accountConfirmEditText);
        Switch fingerprintSwitch = (Switch) view.findViewById(R.id.fingerprintSwitch);
        Button accountChatButton = (Button) view.findViewById(R.id.accountChatButton);
        DatabaseHelper db = new DatabaseHelper(getContext());

        User currentUser = db.fetchUser(email);

        accountNameEditText.setText(currentUser.getName());
        accountEmailEditText.setText(currentUser.getEmail());
        accountPhoneEditText.setText(currentUser.getPhone());
        accountAddressEditText.setText(currentUser.getAddress());

        accountUpdateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fetchUserPassword = currentUser.getPassword();
                String oldPassword = oldPasswordEditText.getText().toString();
                String password = accountPasswordEditText.getText().toString();
                String confirm = accountConfirmEditText.getText().toString();

                if(TextUtils.isEmpty(password) && TextUtils.isEmpty(oldPassword) && TextUtils.isEmpty(confirm)) {
                    int result = 0;
                    if (!accountEmailEditText.getText().toString().equals(currentUser.getEmail())) {
                        db.updateUserEmail(currentUser.getEmail(), accountEmailEditText.getText().toString());
                        email = accountEmailEditText.getText().toString();
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putString("email", accountEmailEditText.getText().toString());
                        editor.apply();

                        User user = new User(
                            accountNameEditText.getText().toString(),
                            accountEmailEditText.getText().toString(),
                            accountPhoneEditText.getText().toString(),
                            accountAddressEditText.getText().toString()
                        );

                        result = db.updateUser(user);
                    }
                    else {
                        User user = new User(
                            accountNameEditText.getText().toString(),
                            currentUser.getEmail(),
                            accountPhoneEditText.getText().toString(),
                            accountAddressEditText.getText().toString()
                        );

                        result = db.updateUser(user);
                    }
                    if (result > 0) {
                        Toast.makeText(getContext(), "Account updated successfully!", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Toast.makeText(getContext(), "Account updating failed! Please try again.", Toast.LENGTH_SHORT).show();
                    }
                }
                else if(oldPassword.equals(fetchUserPassword)) {
                    if (confirm.equals(password)) {

                        User user = new User(
                            accountNameEditText.getText().toString(),
                            accountEmailEditText.getText().toString(),
                            accountPhoneEditText.getText().toString(),
                            accountAddressEditText.getText().toString(),
                            password
                        );

                        int result = db.updateUserPassword(user);
                        if (result > 0) {
                            Toast.makeText(getContext(), "Account updated successfully!", Toast.LENGTH_SHORT).show();
                            SharedPreferences.Editor editor = sharedPref.edit();
                            editor.remove("email");
                            editor.putBoolean("remember_login", false);
                            editor.apply();

                            Intent intent = new Intent(getContext(), MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            requireActivity().finish();
                        }
                        else {
                            Toast.makeText(getContext(), "Account updating failed! Please try again.", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else {
                        Toast.makeText(getContext(), "Password confirmation do not match!", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    Toast.makeText(getContext(), "Current password is incorrect!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        if (sharedPref.getBoolean("fingerprint_login", false)) {
            fingerprintSwitch.setChecked(true);
        }

        fingerprintSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    BiometricManager biometricManager = BiometricManager.from(requireContext());

                    int getAuthenticator;
                    if (Build.VERSION.SDK_INT >= 30) {
                        getAuthenticator = biometricManager.canAuthenticate(BIOMETRIC_STRONG);
                    }
                    else {
                        getAuthenticator = biometricManager.canAuthenticate();
                    }

                    switch (getAuthenticator) {
                        case BiometricManager.BIOMETRIC_SUCCESS:
                            SharedPreferences.Editor editor = sharedPref.edit();
                            editor.putBoolean("fingerprint_login", true);
                            editor.apply();
                            Toast.makeText(getContext(), "Log in with biometric enabled!", Toast.LENGTH_SHORT).show();
                            break;
                        case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
                            Toast.makeText(getContext(), "No biometric features available on this device.", Toast.LENGTH_SHORT).show();
                            buttonView.setChecked(false);
                            break;
                        case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
                            Toast.makeText(getContext(), "Biometric features are currently unavailable.", Toast.LENGTH_SHORT).show();
                            buttonView.setChecked(false);
                            break;
                        case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
                            Toast.makeText(getContext(), "Please register your biometric and try again!", Toast.LENGTH_SHORT).show();
                            buttonView.setChecked(false);
                            break;
                    }

                }
                else {
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putBoolean("fingerprint_login", false);
                    editor.apply();
                    Toast.makeText(getContext(), "Log in with biometric disabled!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        accountChatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (navHostFragment != null) {
                    NavHostFragment.findNavController(navHostFragment).navigate(R.id.action_accountFragment_to_chatFragment);
                }
            }
        });

        accountLogoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.remove("email");
                editor.putBoolean("remember_login", false);
                editor.apply();

                Intent intent = new Intent(getContext(), MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                    | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                requireActivity().finish();
            }
        });
    }
}
