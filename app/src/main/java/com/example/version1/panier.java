package com.example.version1;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class panier extends Fragment {
    private FirebaseFirestore db;
    private List<product> productList;
    private RecyclerView recyclerView;
    private List<String> panierProductIds;
    private DocumentReference userDocRef;
    private String userEmail;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_panier, container, false);

        productList = new ArrayList<>();
        db = FirebaseFirestore.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            // User is signed in.
            userEmail = currentUser.getEmail();
            // Now 'userEmail' contains the email address of the currently authenticated user.
            System.out.println("Current user email: " + userEmail);
        } else {
            // No user is signed in.
            System.out.println("No user signed in.");
        }

        userDocRef = db.collection("users").document(userEmail);

        recyclerView = rootView.findViewById(R.id.recyclerview1);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        // Fetch data and set up the adapter
        fetchPanierData();
        ProductAdapter adapter = new ProductAdapter(getActivity(), productList);
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new ProductAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Bundle productBundle) {
                // Handle the item click
                // Open a new screen, show details, etc.
                Intent intent = new Intent(getActivity(), panierDetails.class);
                intent.putExtras(productBundle); // Pass the product details
                startActivity(intent);
            }
        });

        adapter.setOnSupprimerClickListener(new ProductAdapter.OnSupprimerClickListener() {
            @Override
            public void onSupprimerClick(String productId) {
                // Handle the supprimer button click for the specific product ID
                removeProductFromPanier(userDocRef, productId);
            }
        });

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Fetch data again when the Fragment is resumed (becomes visible)
        fetchPanierData();
    }

    private void fetchPanierData() {
        // Fetch panier data from Firebase
        userDocRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null && document.exists()) {
                        // Check if the 'panier' field exists in the document
                        if (document.contains("panier")) {
                            panierProductIds = (ArrayList<String>) document.get("panier");
                            fetchProductDetails(panierProductIds);
                        } else {
                            // Handle the case where the 'panier' field doesn't exist
                            // You can set panierProductIds to an empty list or handle it accordingly
                            panierProductIds = new ArrayList<>();
                            // Do something else if needed
                        }
                    } else {
                        // Handle the case where the user document doesn't exist
                    }
                } else {
                    // Handle errors
                    Exception exception = task.getException();
                    if (exception != null) {
                        exception.printStackTrace();
                    }
                }
            }
        });

    }

    private void fetchProductDetails(List<String> productIds) {
        productList.clear(); // Clear existing data before fetching new data
        for (String productId : productIds) {
            DocumentReference productDocRef = db.collection("products").document(productId);

            productDocRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot productDocument = task.getResult();
                        if (productDocument != null && productDocument.exists()) {
                            product productDetails = productDocument.toObject(product.class);

                            if (productDetails != null) {
                                productList.add(productDetails);

                                // Notify the adapter that the data set has changed
                                recyclerView.getAdapter().notifyDataSetChanged();
                            }
                        } else {
                            // Handle the case where the product document doesn't exist
                        }
                    } else {
                        // Handle errors
                        Exception exception = task.getException();
                        if (exception != null) {
                            exception.printStackTrace();
                        }
                    }
                }
            });
        }
    }

    private void removeProductFromPanier(DocumentReference userDocRef, String productId) {
        // Remove the selected product ID from the panier field in Firebase
        userDocRef.update("panier", FieldValue.arrayRemove(productId))
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getActivity(), "Product removed from panier", Toast.LENGTH_SHORT).show();
                            removeProductFromList(productId);

                            // Notify the adapter that the data set has changed
                            recyclerView.getAdapter().notifyDataSetChanged();
                        } else {
                            Toast.makeText(getActivity(), "Failed to remove product from panier", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void removeProductFromList(String productId) {
        for (int i = 0; i < productList.size(); i++) {
            if (productList.get(i).getId().equals(productId)) {
                productList.remove(i);
                break;
            }
        }
    }
}
