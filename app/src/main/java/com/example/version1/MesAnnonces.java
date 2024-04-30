package com.example.version1;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class MesAnnonces extends AppCompatActivity {
    RecyclerView recyclerView;
    private FirebaseFirestore db;
    private List<product> productList;
    DocumentReference userDocRef;
    private List<String> myProductIds;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mes_annonces);

        recyclerView = findViewById(R.id.recycleView);
        productList = new ArrayList<>();
        db = FirebaseFirestore.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            userDocRef = db.collection("users").document(currentUser.getEmail());
            if (userDocRef != null) {
                fetchData();
            } else {
                // Handle the case where userDocRef is null
            }
        } else {
            // No user is signed in.
            System.out.println("No user signed in.");
        }





        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        layoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE); // Optional, to remove gaps between items
        layoutManager.setOrientation(StaggeredGridLayoutManager.VERTICAL);

        // Set the layoutManager to your RecyclerView
        recyclerView.setLayoutManager(layoutManager);

        annouce_adapter adapter = new annouce_adapter(productList, MesAnnonces.this);
        adapter.setOnItemClickListener(productBundle -> {
            Intent intent = new Intent(MesAnnonces.this, ProductDetails.class);
            intent.putExtras(productBundle);
            startActivity(intent);
        });

        recyclerView.setAdapter(adapter);
    }


    private void fetchData() {
        // Check if userEmail is not null before fetching data
     //   if (userEmail != null) {
            // Fetch MyProducts data from Firebase
        userDocRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document != null && document.exists()) {
                    // Check if the 'MyProducts' field exists in the document
                    if (document.contains("MyProducts")) {
                        myProductIds = (ArrayList<String>) document.get("MyProducts");
                        fetchProductDetails(myProductIds);
                    } else {
                        // Handle the case where 'MyProducts' field doesn't exist
                        // You can set myProductIds to an empty list or handle it accordingly
                        myProductIds = new ArrayList<>();
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
        });

        // }
    }

    private void fetchProductDetails(List<String> productIds) {
        productList.clear(); // Clear existing data before fetching new data
        for (String productId : productIds) {
            DocumentReference productDocRef = db.collection("products").document(productId);

            productDocRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot productDocument = task.getResult();
                    if (productDocument != null && productDocument.exists()) {
                        product productDetails = productDocument.toObject(product.class);

                        if (productDetails != null) {
                            productList.add(productDetails);

                            // Notify the adapter that the data set has changed

                        }
                    } recyclerView.getAdapter().notifyDataSetChanged();
                } else {
                    // Handle errors
                    Exception exception = task.getException();
                    if (exception != null) {
                        exception.printStackTrace();
                    }
                }
            });
        }
    }
}
