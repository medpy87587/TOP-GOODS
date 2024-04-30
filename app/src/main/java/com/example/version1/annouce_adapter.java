package com.example.version1;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class annouce_adapter extends RecyclerView.Adapter<annouce_adapter.ViewHolder> {
    private List<product> itemList;
    private Context context;
    private OnItemClickListener onItemClickListener;
    boolean isVisible;
    product clickedProduct;
    public annouce_adapter(List<product> itemList, Context context) {
        this.itemList = itemList;
        this.context = context;
    }

    public interface OnItemClickListener {
        void onItemClick(Bundle productBundle);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, date, prix;
        ImageView imageView;
        TextView optionsMenu;

        public ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            prix = itemView.findViewById(R.id.prix_);
            imageView = itemView.findViewById(R.id.image);
            date = itemView.findViewById(R.id.date);
            optionsMenu = itemView.findViewById(R.id.edit);

            optionsMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   showPopupMenu(optionsMenu, getAdapterPosition());
                }
           });

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

        private void showPopupMenu(View view, int position) {
            PopupMenu popupMenu = new PopupMenu(context, view);
            popupMenu.getMenuInflater().inflate(R.menu.options_menu, popupMenu.getMenu());
            product clickedProduct = itemList.get(position);

            fetchVisibilityFromFirestore(clickedProduct.getId(), isVisible -> {
                clickedProduct.setVisible(isVisible);
                MenuItem hideOption = popupMenu.getMenu().findItem(R.id.hide_option);
                hideOption.setTitle(isVisible ? "Masquer" : "Afficher");

                popupMenu.setOnMenuItemClickListener(item -> {
                    int itemId = item.getItemId();

                    String documentId = clickedProduct.getId();

                    if (itemId == R.id.edit_option) {
                        showEditDialog(position);
                    } else if (itemId == R.id.delete_option) {
                        String imageUrl = clickedProduct.getImageUrl();
                        deleteProductFromFirestoreAndStorage(documentId, imageUrl);
                    } else if (itemId == R.id.hide_option) {
                        clickedProduct.setVisible(!isVisible);
                        updateVisibilityInFirestore(documentId, clickedProduct.isVisible());
                        hideOption.setTitle(isVisible ? "Masquer" : "Afficher");
                    }
                    return true;
                });

                popupMenu.show();
            });
        }




        }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.annonce_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        product currentItem = itemList.get(position);
        String timeSinceAdded = getTimeSinceAdded(currentItem.getDate());
        holder.date.setText(timeSinceAdded);
        holder.title.setText(currentItem.getTitle());
        holder.prix.setText(currentItem.getPrice() + "TND");
        Uri imageUri = Uri.parse(currentItem.getImageUrl());
        if (imageUri != null) {
            Glide.with(holder.itemView.getContext())
                    .load(imageUri)
                    .placeholder(R.drawable.loading)
                    .error(R.drawable.loading)
                    .into(holder.imageView);
        } else {
            holder.imageView.setImageResource(R.drawable.loading);
        }
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    private String getTimeSinceAdded(Timestamp addedTimestamp) {
        return DateUtils.getRelativeTimeSpanString(
                addedTimestamp.toDate().getTime(),
                System.currentTimeMillis(),
                DateUtils.MINUTE_IN_MILLIS
        ).toString().replaceAll("^\\s+", "");
    }
    private void showEditDialog(int position) {
        product clickedProduct = itemList.get(position);

        // Create a new instance of the dialog and pass product details
        EditProductDialogFragment editDialog = EditProductDialogFragment.newInstance(
                clickedProduct.getTitle(),
                String.valueOf(clickedProduct.getPrice()),
                clickedProduct.getDescription(),
                clickedProduct.getPhone(), clickedProduct.getDelivery(), clickedProduct.getCategory(),
                clickedProduct.getGovernment(),clickedProduct.getImageUrl(),clickedProduct.getId()
        );

        editDialog.show(((AppCompatActivity) context).getSupportFragmentManager(), "edit_dialog");
    }private void deleteProductFromFirestoreAndStorage(String documentId, String imageUrl) {
        // Get the reference to the document
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference productRef = db.collection("products").document(documentId);

        // Delete the document from Firestore
        productRef.delete()
                .addOnSuccessListener(aVoid -> {
                    // Document successfully deleted
                    Log.d("Firestore", "DocumentSnapshot successfully deleted!");

                    // If an image URL is present, delete the image from Firebase Storage
                    if (imageUrl != null && !imageUrl.isEmpty()) {
                        deleteImageFromStorage(documentId);
                    }
                })
                .addOnFailureListener(e -> {
                    // Handle errors
                    Log.w("Firestore", "Error deleting document", e);
                });
    }

    private void deleteImageFromStorage(String documentId) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

        // Get the reference to the image in Firebase Storage
        StorageReference imageRef = storageRef.child("images/" + documentId + ".jpg");

        // Delete the image from Firebase Storage
        imageRef.delete()
                .addOnSuccessListener(aVoid -> {
                    // File successfully deleted
                    Log.d("FirebaseStorage", "Image deleted successfully!");
                })
                .addOnFailureListener(e -> {
                    // Handle errors
                    Log.w("FirebaseStorage", "Error deleting image", e);
                });
    }

    private void fetchVisibilityFromFirestore(String documentId, OnSuccessListener<Boolean> onSuccessListener) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference productRef = db.collection("products").document(documentId);

        productRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                Boolean isVisible = documentSnapshot.getBoolean("isVisible");
                if (isVisible != null) {
                    onSuccessListener.onSuccess(isVisible);
                }
            }
        }).addOnFailureListener(e -> {
            // Handle failure
        });
    }

    private void updateVisibilityInFirestore(String documentId, boolean isVisible) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference productRef = db.collection("products").document(documentId);

        Map<String, Object> updateData = new HashMap<>();
        updateData.put("isVisible", isVisible);

        productRef.update(updateData)
                .addOnSuccessListener(aVoid -> {
                    // Handle success
                })
                .addOnFailureListener(e -> {
                    // Handle failure
                });
    }

}
