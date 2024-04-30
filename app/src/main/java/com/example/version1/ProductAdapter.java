package com.example.version1;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private List<product> productList;
    private OnItemClickListener onItemClickListener; // Click listener interface
    private OnSupprimerClickListener onSupprimerClickListener; // Supprimer click listener interface
    private Context context;

    // Constructor to set the initial product list
    public ProductAdapter(Context context, List<product> productList) {
        this.context = context;
        this.productList = productList;
    }

    // Click listener interface
    public interface OnItemClickListener {
        void onItemClick(Bundle productBundle);
    }

    // Method to set the click listener
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    // Supprimer click listener interface
    public interface OnSupprimerClickListener {
        void onSupprimerClick(String productId);
    }

    // Method to set the supprimer click listener
    public void setOnSupprimerClickListener(OnSupprimerClickListener listener) {
        this.onSupprimerClickListener = listener;
    }

    // Inner ViewHolder class
    public class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        TextView descriptionTextView, supprimer;
        ImageView imageView;

        public ProductViewHolder(View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.title);
            descriptionTextView = itemView.findViewById(R.id.prix_);
            imageView = itemView.findViewById(R.id.image);
            supprimer = itemView.findViewById(R.id.supprimer);

            // Set an OnClickListener to the itemView
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemClickListener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            product clickedProduct = productList.get(position);
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
                            productBundle.putString("owner", String.valueOf(clickedProduct.getOwner()));

                            onItemClickListener.onItemClick(productBundle);
                        }
                    }
                }
            });

            // Set an OnClickListener to the supprimer TextView
            supprimer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && onSupprimerClickListener != null) {
                        onSupprimerClickListener.onSupprimerClick(productList.get(position).getId());
                    }
                }
            });
        }
    }

    // onCreateViewHolder method
    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.panier_item, parent, false);
        return new ProductViewHolder(view);
    }

    // onBindViewHolder method
    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        product product = productList.get(position);

        holder.titleTextView.setText(product.getTitle());
        holder.descriptionTextView.setText(String.valueOf(product.getPrice()));

        // Use Glide to load the image
        Glide.with(context)
                .load(product.getImageUrl())
                .placeholder(R.drawable.loading)
                .into(holder.imageView);
    }

    // getItemCount method
    @Override
    public int getItemCount() {
        return productList.size();
    }

    // Method to remove an item (you can add more methods as needed)
    public void removeItem(int position) {
        productList.remove(position);
        notifyItemRemoved(position);
    }
}
