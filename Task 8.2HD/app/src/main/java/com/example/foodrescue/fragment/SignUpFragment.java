package com.example.foodrescue.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.example.foodrescue.R;
import com.example.foodrescue.data.DatabaseHelper;
import com.example.foodrescue.model.User;

public class SignUpFragment extends Fragment {

    public SignUpFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sign_up, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Button completeSignupButton = (Button) view.findViewById(R.id.completeSignupButton);
        EditText nameEditText = (EditText) view.findViewById(R.id.nameEditText);
        EditText emailEditText = (EditText) view.findViewById(R.id.emailEditText);
        EditText phoneEditText = (EditText) view.findViewById(R.id.phoneEditText);
        EditText addressEditText = (EditText) view.findViewById(R.id.addressEditText);
        EditText passwordEditText = (EditText) view.findViewById(R.id.passwordEditText);
        EditText confirmEditText = (EditText) view.findViewById(R.id.confirmEditText);
        DatabaseHelper db = new DatabaseHelper(getContext());

        completeSignupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String password = passwordEditText.getText().toString();
                String confirm = confirmEditText.getText().toString();
                if (password.equals(confirm)) {
                    User user = new User(
                        nameEditText.getText().toString(),
                        emailEditText.getText().toString(),
                        phoneEditText.getText().toString(),
                        addressEditText.getText().toString(),
                        passwordEditText.getText().toString()
                    );

                    long result = db.createUser(user);
                    if (result > 0) {
                        Toast.makeText(getContext(), "Signed up successfully!", Toast.LENGTH_SHORT).show();
                        requireActivity().onBackPressed();
                    }
                    else {
                        Toast.makeText(getContext(), "Email is already registered!", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    Toast.makeText(getContext(), "Passwords do not match!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void onDetach() {
        super.onDetach();
        FrameLayout frameLayout = requireActivity().findViewById(R.id.frameLayout);
        frameLayout.setVisibility(View.GONE);
    }
}
