package com.example.foodrescue.fragment;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.foodrescue.R;
import com.example.foodrescue.data.DatabaseHelper;
import com.example.foodrescue.model.Post;
import com.example.foodrescue.util.Util;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

import static android.app.Activity.RESULT_OK;

public class CreateFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final int RESULT_LOAD_IMAGE = 1;

    // TODO: Rename and change types of parameters
    private String email;
    private Bitmap imageBitmap;
    private ImageView createImageView;
    private ActivityResultLauncher<String> requestPermissionLauncher;
    private ActivityResultLauncher<Intent> getImageLauncher;

    public CreateFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), result -> {
            if (result) {
                Toast.makeText(getContext(), "Camera Permission Granted", Toast.LENGTH_SHORT) .show();
            }
            else {
                Toast.makeText(getContext(), "No camera permission. Please grant permission to continue.", Toast.LENGTH_SHORT) .show();
            }
        });

        getImageLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == RESULT_OK) {
                    Intent data = result.getData();
                    Bundle extras = Objects.requireNonNull(data).getExtras();
                    imageBitmap = (Bitmap) extras.get("data");
                    createImageView.setImageBitmap(imageBitmap);
                }
                else {
                    Toast.makeText(requireContext(), "You haven't picked an image!",Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_create, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        createImageView = (ImageView) view.findViewById(R.id.createImageView);
        EditText titleEditText = (EditText) view.findViewById(R.id.createTitleEditText);
        EditText priceEditText = (EditText) view.findViewById(R.id.createPriceEditText);
        EditText desEditText = (EditText) view.findViewById(R.id.createDesEditText);
        EditText dateEditText = (EditText) view.findViewById(R.id.createDateEditText);
        EditText timeEditText = (EditText) view.findViewById(R.id.createTimeEditText);
        EditText quantityEditText = (EditText) view.findViewById(R.id.createQuantityEditText);
        EditText locationEditText = (EditText) view.findViewById(R.id.createLocationEditText);
        Button addImageButton = (Button) view.findViewById(R.id.addImageButton);
        Button savePostButton = view.findViewById(R.id.createSavePostButton);

        DatabaseHelper db = new DatabaseHelper(getContext());
        SharedPreferences sharedPref = getActivity().getSharedPreferences("currentAccount", Activity.MODE_PRIVATE);
        email = sharedPref.getString("email", null);


        addImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
                    requestPermissionLauncher.launch(Manifest.permission.CAMERA);
                }
                else {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    getImageLauncher.launch(intent);
                }
            }
        });

        savePostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                byte[] image = null;
                if (imageBitmap != null) {
                    image = db.imageToBytes(imageBitmap);
                }

                float price = 0;
                int quantity = 1;

                if (!TextUtils.isEmpty(quantityEditText.getText().toString())) {
                    quantity = Integer.parseInt(quantityEditText.getText().toString());
                }
                if (!TextUtils.isEmpty(priceEditText.getText().toString())) {
                    price = Float.parseFloat(priceEditText.getText().toString());
                }
                Post post = new Post(
                    image,
                    titleEditText.getText().toString(),
                    price,
                    desEditText.getText().toString(),
                    dateEditText.getText().toString(),
                    timeEditText.getText().toString(),
                    quantity,
                    locationEditText.getText().toString(),
                    email
                );

                long result = db.createPost(post);
                if (result > 0) {
                    Toast.makeText(getContext(), "Post created successfully!", Toast.LENGTH_SHORT).show();
                    getActivity().onBackPressed();
                }
                else {
                    Toast.makeText(getContext(), "Post creating failed! Please try again.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        datePicker(view);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        FloatingActionButton fab = getActivity().findViewById(R.id.floatingActionButton);
        fab.setVisibility(View.VISIBLE);
    }

    public void datePicker(View view) {
        final Calendar myCalendar = Calendar.getInstance();

        EditText dateEditText = (EditText) view.findViewById(R.id.createDateEditText);
        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                String myFormat = "dd/MM/yyyy";
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());
                dateEditText.setText(sdf.format(myCalendar.getTime()));
            }

        };

        dateEditText.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                new DatePickerDialog(getContext(), date, myCalendar
                    .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                    myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
    }
}
