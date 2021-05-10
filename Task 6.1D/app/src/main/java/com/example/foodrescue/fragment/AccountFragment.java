package com.example.foodrescue.fragment;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.foodrescue.MainActivity;
import com.example.foodrescue.R;
import com.example.foodrescue.data.DatabaseHelper;
import com.example.foodrescue.model.User;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AccountFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AccountFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String EMAIL = "email";

    // TODO: Rename and change types of parameters
    private String email;
    SharedPreferences sharedPref;

    public AccountFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param email Parameter 1.
     * @return A new instance of fragment AccountFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AccountFragment newInstance(String email) {
        AccountFragment fragment = new AccountFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
        sharedPref = getActivity().getSharedPreferences("currentAccount", Activity.MODE_PRIVATE);
        email = sharedPref.getString("email", null);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_account, container, false);
        Button accountUpdateButton = (Button) view.findViewById(R.id.accountUpdateButton);
        Button accountLogoutButton = (Button) view.findViewById(R.id.accountLogoutButton);
        TextView accountTextView = (TextView) view.findViewById(R.id.accountTextView);
        EditText accountNameEditText = (EditText) view.findViewById(R.id.accountNameEditText);
        EditText accountEmailEditText = (EditText) view.findViewById(R.id.accountEmailEditText);
        EditText accountPhoneEditText = (EditText) view.findViewById(R.id.accountPhoneEditText);
        EditText accountAddressEditText = (EditText) view.findViewById(R.id.accountAddressEditText);
        EditText oldPasswordEditText = (EditText) view.findViewById(R.id.oldPasswordEditText);
        EditText accountPasswordEditText = (EditText) view.findViewById(R.id.accountPasswordEditText);
        EditText accountConfirmEditText = (EditText) view.findViewById(R.id.accountConfirmEditText);
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
                    User user = new User(
                        accountNameEditText.getText().toString(),
                        accountEmailEditText.getText().toString(),
                        accountPhoneEditText.getText().toString(),
                        accountAddressEditText.getText().toString()
                    );

                    int result = db.updateUser(user);
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
                            getActivity().finish();
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
                getActivity().finish();
            }
        });
        return view;
    }

    public void onDetach() {
        super.onDetach();
        FloatingActionButton fab = getActivity().findViewById(R.id.floatingActionButton);
        fab.setVisibility(View.VISIBLE);
    }
}
