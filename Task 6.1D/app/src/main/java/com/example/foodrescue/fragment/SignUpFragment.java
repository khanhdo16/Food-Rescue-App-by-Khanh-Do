package com.example.foodrescue.fragment;

import android.os.Bundle;

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

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SignUpFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SignUpFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public SignUpFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SignUpFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SignUpFragment newInstance(String param1, String param2) {
        SignUpFragment fragment = new SignUpFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sign_up, container, false);
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
                        getActivity().onBackPressed();
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
        return view;
    }

    public void onDetach() {
        super.onDetach();
        FrameLayout frameLayout = getActivity().findViewById(R.id.frameLayout);
        frameLayout.setVisibility(View.GONE);
    }
}
