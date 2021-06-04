package com.example.foodrescue.util;

import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodrescue.PostActivity;
import com.example.foodrescue.R;
import com.example.foodrescue.data.DatabaseHelper;
import com.example.foodrescue.model.Post;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Observer;

import static androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_IDLE;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder>{
    private Context context;
    private DatabaseHelper db;
    private RecyclerView.AdapterDataObserver mObserver;

    public CartAdapter(Context context) {
        this.context = context;
        db = new DatabaseHelper(context.getApplicationContext());
    }

    @NonNull
    @Override
    public CartAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.cart_row, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void registerAdapterDataObserver(@NonNull RecyclerView.AdapterDataObserver observer) {
        super.registerAdapterDataObserver(observer);
        mObserver = observer;
    }

    @Override
    public void onBindViewHolder(@NonNull CartAdapter.ViewHolder holder, int position) {
        int id = 0;
        int quantity = 0;

        int i = 0;
        for (Map.Entry<Integer, Integer> entry : PostActivity.getCart().entrySet()) {
            if (i == position) {
                id = entry.getKey();
                quantity = entry.getValue();
                break;
            }
            i++;
        }

        if (id > 0 && quantity > 0) {
            Post post = db.fetchPost(id);
            holder.cartTitleTextView.setText(post.getTitle());
            holder.cartQtyEditText.setText(Integer.toString(quantity));

            String price = "$" + Float.toString(post.getPrice() * quantity);
            holder.cartPriceTextView.setText(price);

            int finalId = id;

            holder.cartQtyEditText.addTextChangedListener(new TextWatcher() {

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (!TextUtils.isEmpty(holder.cartQtyEditText.getText().toString())) {
                        if (Integer.parseInt(holder.cartQtyEditText.getText().toString()) > post.getQuantity()) {
                            Toast.makeText(context.getApplicationContext(),
                                "The available quantity is: " + post.getQuantity(),
                                Toast.LENGTH_SHORT).show();
                        }
                        else if (Integer.parseInt(holder.cartQtyEditText.getText().toString()) == 0) {
                            PostActivity.getCart().remove(finalId);
                            Toast.makeText(context.getApplicationContext(),
                                "Item removed from cart!",
                                Toast.LENGTH_SHORT).show();
                            notifyDataSetChanged();
                        }
                        else {
                            PostActivity.getCart().put(finalId, Integer.parseInt(holder.cartQtyEditText.getText().toString()));
                            String price = "$" + Float.toString(post.getPrice() * Integer.parseInt(holder.cartQtyEditText.getText().toString()));
                            holder.cartPriceTextView.setText(price);
                            mObserver.onChanged();
                        }
                    }
                }
            });
            holder.cartDeleteImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PostActivity.getCart().remove(finalId);
                    Toast.makeText(context.getApplicationContext(),
                        "Item removed from cart!",
                        Toast.LENGTH_SHORT).show();
                    mObserver.onChanged();
                    notifyDataSetChanged();
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return PostActivity.getCart().size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView cartTitleTextView;
        EditText cartQtyEditText;
        TextView cartPriceTextView;
        ImageView cartDeleteImageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cartTitleTextView = itemView.findViewById(R.id.cartTitleTextView);
            cartQtyEditText = itemView.findViewById(R.id.cartQtyEditText);
            cartPriceTextView = itemView.findViewById(R.id.cartPriceTextView);
            cartDeleteImageView = itemView.findViewById(R.id.cartDeleteImageView);
        }
    }
}
