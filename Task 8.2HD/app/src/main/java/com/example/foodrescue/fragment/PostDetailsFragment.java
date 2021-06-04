package com.example.foodrescue.fragment;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
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

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.foodrescue.PostActivity;
import com.example.foodrescue.R;
import com.example.foodrescue.data.DatabaseHelper;
import com.example.foodrescue.model.Post;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.paypal.checkout.createorder.CreateOrderActions;
import com.paypal.checkout.createorder.CurrencyCode;
import com.paypal.checkout.createorder.OrderIntent;
import com.paypal.checkout.createorder.UserAction;
import com.paypal.checkout.order.Amount;
import com.paypal.checkout.order.AppContext;
import com.paypal.checkout.order.Order;
import com.paypal.checkout.order.PurchaseUnit;
import com.paypal.checkout.paymentbutton.PayPalButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

import static android.app.Activity.RESULT_OK;


public class PostDetailsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ID = "post_id";
    private static final String EMAIL = "email";

    // TODO: Rename and change types of parameters
    private ActivityResultLauncher<String> requestPermissionLauncher;
    private ActivityResultLauncher<Intent> getImageLauncher;
    private int id;
    private String email;
    ImageView editImageView;
    Bitmap bitmap;
    DatabaseHelper db;

    public PostDetailsFragment() {
        // Required empty public constructor
    }

    public static PostDetailsFragment newInstance(int id, String email) {
        PostDetailsFragment fragment = new PostDetailsFragment();
        Bundle args = new Bundle();
        args.putInt(ID, id);
        args.putString(EMAIL, email);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            id = getArguments().getInt(ID);
            email = getArguments().getString(EMAIL);
        }

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
                    bitmap = (Bitmap) extras.get("data");
                    editImageView.setImageBitmap(bitmap);
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
        db = new DatabaseHelper(requireContext());
        Post post = db.fetchPost(id);

        View view;

        if (post.getEmail() != null && post.getEmail().equals(email)) {
            view = inflater.inflate(R.layout.fragment_user_post_details, container, false);
        }
        else {
            view = inflater.inflate(R.layout.fragment_post_details, container, false);
        }

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Post post = db.fetchPost(id);
        bitmap = null;
        if (post.getImage() != null) {
            bitmap= db.bytesToImage(post.getImage());
        }

        if (post.getEmail() != null && post.getEmail().equals(email)) {
            editPostDetails(view, post);
        }
        else {
            setPostDetails(view, post);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        FloatingActionButton fab = requireActivity().findViewById(R.id.floatingActionButton);
        fab.setVisibility(View.VISIBLE);
    }

    public void editPostDetails (View view, Post post) {
        editImageView = (ImageView) view.findViewById(R.id.editImageView);
        EditText editTitleEditText = (EditText) view.findViewById(R.id.editTitleEditText);
        EditText editPriceEditText = (EditText) view.findViewById(R.id.editPriceEditText);
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
        editPriceEditText.setText(Float.toString(post.getPrice()));
        editDesEditText.setText(post.getDescription());
        editDateEditText.setText(post.getDate());
        editTimeEditText.setText(post.getTime());
        editQuantityEditText.setText(Integer.toString(post.getQuantity()));
        editLocationEditText.setText(post.getLocation());

        changeImageButton.setOnClickListener(new View.OnClickListener() {
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

        editSavePostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                byte[] image = null;
                if (bitmap != null) {
                    image = db.imageToBytes(bitmap);
                }

                float price = 0;
                int quantity = 1;

                if (!TextUtils.isEmpty(editQuantityEditText.getText().toString())) {
                    quantity = Integer.parseInt(editQuantityEditText.getText().toString());
                }
                if (!TextUtils.isEmpty(editPriceEditText.getText().toString())) {
                    price = Float.parseFloat(editPriceEditText.getText().toString());
                }
                Post updatedPost = new Post(
                    post.getId(),
                    image,
                    editTitleEditText.getText().toString(),
                    price,
                    editDesEditText.getText().toString(),
                    editDateEditText.getText().toString(),
                    editTimeEditText.getText().toString(),
                    quantity,
                    editLocationEditText.getText().toString()
                );

                int result = db.updatePost(updatedPost);
                if (result > 0) {
                    Toast.makeText(getContext(), "Post updated successfully!", Toast.LENGTH_SHORT).show();
                    requireActivity().onBackPressed();
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
                    requireActivity().onBackPressed();
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
        TextView priceTextView = (TextView) view.findViewById(R.id.priceTextView);
        TextView desTextView = (TextView) view.findViewById(R.id.desTextView);
        TextView dateTextView = (TextView) view.findViewById(R.id.dateTextView);
        TextView timeTextView = (TextView) view.findViewById(R.id.timeTextView);
        TextView quantityTextView = (TextView) view.findViewById(R.id.quantityTextView);
        TextView locationTextView = (TextView) view.findViewById(R.id.locationTextView);
        Button cartButton = (Button) view.findViewById(R.id.cartButton);
        PayPalButton payPalButton = (PayPalButton) view.findViewById(R.id.payPalButton);
        TextView soldOutTextView = (TextView) view.findViewById(R.id.soldOutTextView);

        if (bitmap != null) postDetailsImageView.setImageBitmap(bitmap);
        titleTextView.setText("Title: " + post.getTitle());
        priceTextView.setText("Price: $" + post.getPrice());
        desTextView.setText("Description: " + post.getDescription());
        dateTextView.setText("Date: " + post.getDate());
        timeTextView.setText("Pick up time: " + post.getTime());
        quantityTextView.setText("Quantity: " + post.getQuantity());
        locationTextView.setText("Location: " + post.getLocation());

        if (post.getQuantity() > 0) {
            soldOutTextView.setVisibility(View.GONE);
            cartButton.setVisibility(View.VISIBLE);

            cartButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (PostActivity.getCart().get(id) == null) {
                        PostActivity.getCart().put(id, 1);
                        Toast.makeText(getContext(), "Added to cart!", Toast.LENGTH_SHORT).show();
                    } else if (PostActivity.getCart().get(id) < post.getQuantity()) {
                        int qty = PostActivity.getCart().get(id) + 1;
                        PostActivity.getCart().put(id, qty);
                        Toast.makeText(getContext(), "Added to cart!", Toast.LENGTH_SHORT).show();
                    } else if (PostActivity.getCart().get(id) == post.getQuantity()) {
                        Toast.makeText(getContext(), "No more available stocks for this item!", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            payPalButton.setVisibility(View.VISIBLE);

            payPalButton.setup(
                createOrderActions -> {
                    ArrayList purchaseUnits = new ArrayList<>();
                    purchaseUnits.add(
                        new PurchaseUnit.Builder()
                            .amount(new Amount.Builder()
                                .currencyCode(CurrencyCode.AUD)
                                .value(Float.toString(post.getPrice()))
                                .build()
                            )
                            .build()
                    );

                    Order order = new Order(
                        OrderIntent.CAPTURE,
                        new AppContext.Builder()
                            .userAction(UserAction.PAY_NOW)
                            .build(),
                        purchaseUnits
                    );
                    createOrderActions.create(order, (CreateOrderActions.OnOrderCreated) null);
                },
                approval -> {
                    Toast.makeText(getContext(), "Payment successfully!", Toast.LENGTH_SHORT).show();

                    post.setQuantity((post.getQuantity() - 1));
                    db.updatePost(post);

                    quantityTextView.setText(post.getQuantity());

                    if (post.getQuantity() == 0) {
                        cartButton.setVisibility(View.GONE);
                        payPalButton.setVisibility(View.GONE);
                        soldOutTextView.setVisibility(View.VISIBLE);
                    }
                },
                () -> Toast.makeText(getContext(), "Payment cancelled!", Toast.LENGTH_SHORT).show()
            );
        }
        else {
            cartButton.setVisibility(View.GONE);
            payPalButton.setVisibility(View.GONE);
            soldOutTextView.setVisibility(View.VISIBLE);
        }
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
