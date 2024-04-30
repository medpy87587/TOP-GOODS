package com.example.version1;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.Timestamp;

import java.util.List;

public class AccueilAdapter extends RecyclerView.Adapter<AccueilAdapter.ViewHolder> {
    private List<product> itemList;
    private Context context;
    private OnItemClickListener onItemClickListener;

    public AccueilAdapter(List<product> itemList, Context context) {
        this.itemList = itemList;
        this.context = context;
    }

    public interface OnItemClickListener {
        void onItemClick(Bundle productBundle);
    }

    // Method to set the click listener
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    // Constructor and methods for updating the data

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, date;
        TextView prix;
        ImageView imageView;

        public ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            prix = itemView.findViewById(R.id.prix_);
            imageView = itemView.findViewById(R.id.image);
            date = itemView.findViewById(R.id.date);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemClickListener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            product clickedProduct = itemList.get(position);
                            Bundle productBundle = new Bundle();


                            productBundle.putString("Id", clickedProduct.getId());
                            productBundle.putString("category", clickedProduct.getCategory());
                            productBundle.putString("delivery", String.valueOf(clickedProduct.getDelivery()));
                            productBundle.putString("description", clickedProduct.getDescription());
                            productBundle.putString("government", clickedProduct.getGovernment());
                            productBundle.putString("imageUrl", clickedProduct.getImageUrl());
                            productBundle.putString("phone", clickedProduct.getPhone());
                            productBundle.putString("price", String.valueOf(clickedProduct.getPrice()));
                            productBundle.putString("title", clickedProduct.getTitle());
                            productBundle.putString("owner", clickedProduct.getOwner());

                            onItemClickListener.onItemClick(productBundle);
                        }
                    }
                }
            });
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_accueil, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Bind data to your views based on the item position
        product currentItem = itemList.get(position);
        // Set values to your views
       String timeSinceAdded = getTimeSinceAdded(currentItem.getDate());
        holder.date.setText(timeSinceAdded);
        holder.title.setText(currentItem.getTitle());
        holder.prix.setText(currentItem.getPrice()+ "TND");
        Uri imageUri = Uri.parse(currentItem.getImageUrl());
        if (imageUri != null) {
            Glide.with(holder.itemView.getContext())
                    .load(imageUri)
                    .placeholder(R.drawable.loading) // Set a placeholder while loading
                    .error(R.drawable.loading) // Set an error image if loading fails
                    .into(holder.imageView);
        } else {
            // Handle the case where the Uri is null or invalid
            // For example, set a default image or a placeholder
            holder.imageView.setImageResource(R.drawable.loading);
        }
    }

    @Override
    public int getItemCount() {
        // Return the size of your data list
        return itemList.size();
    }
    private String getTimeSinceAdded(Timestamp addedTimestamp) {
        // Use DateUtils to get a human-readable format
        return DateUtils.getRelativeTimeSpanString(
                addedTimestamp.toDate().getTime(),
                System.currentTimeMillis(),
                DateUtils.MINUTE_IN_MILLIS
        ).toString().replaceAll("^\\s+", "");
    }


}