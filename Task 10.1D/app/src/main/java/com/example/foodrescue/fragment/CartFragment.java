package com.example.foodrescue.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.foodrescue.PostActivity;
import com.example.foodrescue.R;
import com.example.foodrescue.data.DatabaseHelper;
import com.example.foodrescue.model.Post;
import com.example.foodrescue.util.CartAdapter;
import com.paypal.checkout.approve.Approval;
import com.paypal.checkout.approve.OnApprove;
import com.paypal.checkout.cancel.OnCancel;
import com.paypal.checkout.createorder.CreateOrder;
import com.paypal.checkout.createorder.CreateOrderActions;
import com.paypal.checkout.createorder.CurrencyCode;
import com.paypal.checkout.createorder.ItemCategory;
import com.paypal.checkout.createorder.OrderIntent;
import com.paypal.checkout.createorder.UserAction;
import com.paypal.checkout.error.ErrorInfo;
import com.paypal.checkout.error.OnError;
import com.paypal.checkout.order.Amount;
import com.paypal.checkout.order.AppContext;
import com.paypal.checkout.order.CaptureOrderResult;
import com.paypal.checkout.order.Items;
import com.paypal.checkout.order.OnCaptureComplete;
import com.paypal.checkout.order.Order;
import com.paypal.checkout.order.PurchaseUnit;
import com.paypal.checkout.order.UnitAmount;
import com.paypal.checkout.paymentbutton.PayPalButton;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CartFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CartFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private float total;
    DatabaseHelper db;

    public CartFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CartFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CartFragment newInstance(String param1, String param2) {
        CartFragment fragment = new CartFragment();
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
        db = new DatabaseHelper(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_cart, container, false);
        RecyclerView cartRecyclerView = (RecyclerView) view.findViewById(R.id.cartRecyclerView);
        CartAdapter cartAdapter = new CartAdapter(getContext());
        cartRecyclerView.setAdapter(cartAdapter);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        cartRecyclerView.setLayoutManager(layoutManager);

        total = 0;
        TextView cartTotalTextView = view.findViewById(R.id.cartTotalTextView);
        for (Map.Entry<Integer, Integer> entry : PostActivity.getCart().entrySet()) {
            Post post = db.fetchPost(entry.getKey());

            total += post.getPrice() * entry.getValue();
        }
        cartTotalTextView.setText("$" + total);

        cartAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                total = 0;
                TextView cartTotalTextView = view.findViewById(R.id.cartTotalTextView);
                for (Map.Entry<Integer, Integer> entry : PostActivity.getCart().entrySet()) {
                    Post post = db.fetchPost(entry.getKey());

                    total += post.getPrice() * entry.getValue();
                }
                cartTotalTextView.setText("$" + total);
            }
        });

        PayPalButton payPalButton = view.findViewById(R.id.payPalButton);

        if (total > 0) {
            payPalButton.setVisibility(View.VISIBLE);
            payPalButton.setup(
                createOrderActions -> {
                    ArrayList purchaseUnits = new ArrayList<>();
                    purchaseUnits.add(
                        new PurchaseUnit.Builder()
                            .amount(new Amount.Builder()
                                .currencyCode(CurrencyCode.AUD)
                                .value(Float.toString(total))
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
                    for (Map.Entry<Integer, Integer> entry : PostActivity.getCart().entrySet()) {
                        Post post = db.fetchPost(entry.getKey());
                        post.setQuantity((post.getQuantity() - entry.getValue()));

                        db.updatePost(post);
                    }

                    PostActivity.getCart().clear();
                    cartAdapter.notifyDataSetChanged();
                },
                new OnCancel() {
                    @Override
                    public void onCancel() {
                        Toast.makeText(getContext(), "Payment cancelled!", Toast.LENGTH_SHORT).show();
                    }
                }
            );
        }
        else {
            payPalButton.setVisibility(View.GONE);
        }

        return view;
    }
}
