package com.example.foodrescue.fragment;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.NonNull;
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
import android.widget.TextView;
import android.widget.Toast;

import com.example.foodrescue.R;
import com.example.foodrescue.data.DatabaseHelper;
import com.example.foodrescue.model.Post;
import com.example.foodrescue.util.PostAdapter;
import com.example.foodrescue.util.Util;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PostDetailsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PostDetailsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ID = "id";
    private static final int RESULT_LOAD_IMAGE = 1;

    // TODO: Rename and change types of parameters
    private int id;
    ImageView editImageView;
    Bitmap bitmap;

    public PostDetailsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param id Parameter 1.
     * @return A new instance of fragment PostDetailsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PostDetailsFragment newInstance(int id) {
        PostDetailsFragment fragment = new PostDetailsFragment();
        Bundle args = new Bundle();
        args.putInt(ID, id);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            id = getArguments().getInt(ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        DatabaseHelper db = new DatabaseHelper(getContext());
        Post post = db.fetchPost(id);
        bitmap = null;
        if (post.getImage() != null) {
            bitmap= db.bytesToImage(post.getImage());
        }

        SharedPreferences sharedPref = getActivity().getSharedPreferences("currentAccount", Activity.MODE_PRIVATE);
        String email = sharedPref.getString("email", null);

        View view;

        if (post.getEmail() != null && post.getEmail().equals(email)) {
            view = inflater.inflate(R.layout.fragment_user_post_details, container, false);

            editPostDetails(view, post);
        }
        else {
            view = inflater.inflate(R.layout.fragment_post_details, container, false);

            setPostDetails(view, post);
        }

        return view;
    }

    @Override
    public void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            bitmap = (Bitmap) extras.get("data");
            editImageView.setImageBitmap(bitmap);
        }else {
            Toast.makeText(getContext(), "You haven't picked an image!",Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        FloatingActionButton fab = getActivity().findViewById(R.id.floatingActionButton);
        fab.setVisibility(View.VISIBLE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == Util.CAMERA_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getContext(), "Camera Permission Granted", Toast.LENGTH_SHORT) .show();
            }
            else {
                Toast.makeText(getContext(), "Camera Permission Denied", Toast.LENGTH_SHORT) .show();
            }
        }
    }

    public void editPostDetails (View view, Post post) {
        editImageView = (ImageView) view.findViewById(R.id.editImageView);
        EditText editTitleEditText = (EditText) view.findViewById(R.id.editTitleEditText);
        EditText editDesEditText = (EditText) view.findViewById(R.id.editDesEditText);
        EditText editDateEditText = (EditText) view.findViewById(R.id.editDateEditText);
        EditText editTimeEditText = (EditText) view.findViewById(R.id.editTimeEditText);
        EditText editQuantityEditText = (EditText) view.findViewById(R.id.editQuantityEditText);
        EditText editLocationEditText = (EditText) view.findViewById(R.id.editLocationEditText);
        Button changeImageButton = (Button) view.findViewById(R.id.changeImageButton);
        Button editSavePostButton = view.findViewById(R.id.editSavePostButton);
        Button deletePostButton = view.findViewById(R.id.deletePostButton);

        if (bitmap != null) editImageView.setImageBitmap(bitmap);
        editTitleEditText.setText(post.getTitle());
        editDesEditText.setText(post.getDescription());
        editDateEditText.setText(post.getDate());
        editTimeEditText.setText(post.getTime());
        editQuantityEditText.setText(Integer.toString(post.getQuantity()));
        editLocationEditText.setText(post.getLocation());

        changeImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
                    ActivityCompat.requestPermissions(getActivity(), new String[] { Manifest.permission.CAMERA }, Util.CAMERA_PERMISSION_CODE);
                }
                else {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, RESULT_LOAD_IMAGE);
                }
            }
        });

        DatabaseHelper db = new DatabaseHelper(getContext());

        editSavePostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                byte[] image = null;
                if (bitmap != null) {
                    image = db.imageToBytes(bitmap);
                }
                int quantity = 1;

                if (!TextUtils.isEmpty(editQuantityEditText.getText().toString())) {
                    quantity = Integer.parseInt(editQuantityEditText.getText().toString());
                }
                Post updatedPost = new Post(
                    post.getId(),
                    image,
                    editTitleEditText.getText().toString(),
                    editDesEditText.getText().toString(),
                    editDateEditText.getText().toString(),
                    editTimeEditText.getText().toString(),
                    quantity,
                    editLocationEditText.getText().toString()
                );

                int result = db.updatePost(updatedPost);
                if (result > 0) {
                    Toast.makeText(getContext(), "Post updated successfully!", Toast.LENGTH_SHORT).show();
                    getActivity().onBackPressed();
                }
                else {
                    Toast.makeText(getContext(), "Post updating failed! Please try again.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        datePicker(view);

        deletePostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int result = db.deletePost(post.getId());
                if (result > 0) {
                    Toast.makeText(getContext(), "Post deleted successfully!", Toast.LENGTH_SHORT).show();
                    getActivity().onBackPressed();
                }
                else {
                    Toast.makeText(getContext(), "Post deleting failed! Please try again.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void setPostDetails (View view, Post post) {
        ImageView postDetailsImageView = (ImageView) view.findViewById(R.id.postDetailsImageView);
        TextView titleTextView = (TextView) view.findViewById(R.id.titleTextView);
        TextView desTextView = (TextView) view.findViewById(R.id.desTextView);
        TextView dateTextView = (TextView) view.findViewById(R.id.dateTextView);
        TextView timeTextView = (TextView) view.findViewById(R.id.timeTextView);
        TextView quantityTextView = (TextView) view.findViewById(R.id.quantityTextView);
        TextView locationTextView = (TextView) view.findViewById(R.id.locationTextView);

        if (bitmap != null) postDetailsImageView.setImageBitmap(bitmap);
        titleTextView.setText("Title: " + post.getTitle());
        desTextView.setText("Description: " + post.getDescription());
        dateTextView.setText("Date: " + post.getDate());
        timeTextView.setText("Pick up time: " + post.getTime());
        quantityTextView.setText("Quantity: " + post.getQuantity());
        locationTextView.setText("Location: " + post.getLocation());
    }

    public void datePicker(View view) {
        final Calendar myCalendar = Calendar.getInstance();

        EditText editDateEditText = (EditText) view.findViewById(R.id.editDateEditText);
        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                String myFormat = "dd/MM/yyyy";
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());
                editDateEditText.setText(sdf.format(myCalendar.getTime()));
            }

        };

        editDateEditText.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                new DatePickerDialog(getContext(), date, myCalendar
                    .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                    myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
    }
}
