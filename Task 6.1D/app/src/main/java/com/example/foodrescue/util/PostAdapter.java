package com.example.foodrescue.util;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodrescue.R;
import com.example.foodrescue.model.Post;

import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {
    private List<Post> postList;
    private Context context;
    private OnClickListener listener;

    public PostAdapter(List<Post> postList, Context context, OnClickListener listener) {
        this.postList = postList;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.post_row, parent, false);
        return new ViewHolder(itemView, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (postList.get(position).getImage() != null) {
            Bitmap bitmap = bytesToImage(postList.get(position).getImage());
            holder.postImageView.setImageBitmap(bitmap);
        }
        holder.postTextView.setText(postList.get(position).getTitle());
        holder.postLocationTextView.setText(postList.get(position).getLocation());
        holder.postDesTextView.setText(postList.get(position).getDescription());
        holder.shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String shareSubject = "!!! FREE FOOD ALERT !!!";
                String shareBody = "Pick up " + postList.get(position).getTitle() + " at "
                    + postList.get(position).getLocation() + " now!\n"
                    + "Details: " + postList.get(position).getDescription()
                    + "\nQuantity: " + postList.get(position).getQuantity()
                    + "\nDate: " + postList.get(position).getDate()
                    + "\nTime: " + postList.get(position).getTime();
                sharingIntent.putExtra(Intent.EXTRA_SUBJECT, shareSubject);
                sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
                v.getContext().startActivity(Intent.createChooser(sharingIntent, "Share via"));
            }
        });
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ImageView postImageView;
        public TextView postTextView;
        public TextView postLocationTextView;
        public TextView postDesTextView;
        public ImageButton shareButton;
        public OnClickListener listener;

        public ViewHolder(@NonNull View itemView, OnClickListener listener) {
            super(itemView);
            postImageView = itemView.findViewById(R.id.postImageView);
            postTextView = itemView.findViewById(R.id.postTextView);
            shareButton = itemView.findViewById(R.id.shareButton);
            postLocationTextView = itemView.findViewById(R.id.postLocationTextView);
            postDesTextView = itemView.findViewById(R.id.postDesTextView);

            this.listener = listener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            listener.onItemClick(getAdapterPosition());
        }
    }

    public interface OnClickListener {
        void onItemClick(int position);
    }

    public Bitmap bytesToImage(byte[] image) {
        return BitmapFactory.decodeByteArray(image, 0, image.length);
    }
}
