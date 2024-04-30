package com.example.version1;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class userProfile extends AppCompatActivity {
    RecyclerView recyclerView;
    private FirebaseFirestore db;
    private List<product> productList;
    private List<String> ProductIds;
    TextView username,eamil,description;

    private static final int PICK_IMAGE_REQUEST = 1;
    private ImageView profileImageView,facebook,instagram;
    private Uri imageUri;
    DocumentReference userDocRef;
    String ownerEmail,facebookLink,instagramLink;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        username=findViewById(R.id.username);
        eamil=findViewById(R.id.eamil_hold);
        description=findViewById(R.id.description_);
        facebook=findViewById(R.id.facebook);
        instagram=findViewById(R.id.intagram);
        profileImageView=findViewById(R.id.profileOwner);
        Intent intent = getIntent();
        if (intent != null) {
             ownerEmail = intent.getStringExtra("ownerEmail");}
        Toast.makeText(userProfile.this, ownerEmail, Toast.LENGTH_SHORT).show();

        initializeLinkTextView(username,"username","username");
       initializeLinkTextView(description,"description","description ...");
        initializeLinkTextView(eamil,"email","");
        initializeProfileImage();
        retrieveSocialMediaLinks(ownerEmail);
        recyclerView=findViewById(R.id.recycleView);
        productList=new ArrayList<>();


        // Initialize Firebase
        db = FirebaseFirestore.getInstance();


        userDocRef = db.collection("users").document(ownerEmail);

        // Fetch data from Firestore
        fetchDataFromFirestore();
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        layoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE); // Optional, to remove gaps between items
        layoutManager.setOrientation(StaggeredGridLayoutManager.VERTICAL);


        // Set the layoutManager to your RecyclerView
        recyclerView.setLayoutManager(layoutManager);

        AccueilAdapter adapter = new AccueilAdapter(productList, this);
        adapter.setOnItemClickListener(new AccueilAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Bundle productBundle) {
                Intent intent = new Intent(userProfile.this, ProductDetails.class);
                intent.putExtras(productBundle);
                startActivity(intent);
            }
        });

        recyclerView.setAdapter(adapter);

    }private void initializeProfileImage() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            db = FirebaseFirestore.getInstance();
            DocumentReference userRef = db.collection("users").document(ownerEmail);

            userRef.get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String imageUrl = documentSnapshot.getString("profileImageUrl");
                            if (imageUrl != null && !imageUrl.isEmpty()) {
                                Glide.with(this)
                                        .load(imageUrl)
                                        .placeholder(R.drawable.loading)
                                        .error(R.drawable.loading)
                                        .into(profileImageView);
                            }
                        }
                    })
                    .addOnFailureListener(e -> {
                        // Handle failure
                    });
        }
    }
    private void initializeLinkTextView(TextView textView, String field, String defaultText) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            db = FirebaseFirestore.getInstance();
            DocumentReference userRef = db.collection("users").document(ownerEmail);

            userRef.get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String link = documentSnapshot.getString(field);
                            if (link != null && !link.isEmpty()) {
                                textView.setText(link);
                            } else {
                                textView.setText(defaultText);
                            }
                        } else {
                            textView.setText(defaultText);
                        }
                    })
                    .addOnFailureListener(e -> {
                        // Handle failure
                        textView.setText(defaultText);
                    });
        } else {
            textView.setText(defaultText);
        }
    }private void retrieveSocialMediaLinks(String ownerEmail) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userRef = db.collection("users").document(ownerEmail);

        userRef.get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        facebookLink = documentSnapshot.getString("facebookLink");
                        instagramLink = documentSnapshot.getString("instagramLink");

                        // Set click listener on Facebook ImageView
                        setFacebookClickListener();

                        // Set click listener on Instagram ImageView
                        setInstagramClickListener();
                    }
                })
                .addOnFailureListener(e -> {
                    // Handle failure
                });
    }

    private void setFacebookClickListener() {
        // Assuming you have a Facebook ImageView with id facebook


        // Set click listener on Facebook ImageView
        facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check if the Facebook link is not empty
                if (facebookLink != null && !facebookLink.isEmpty()) {
                    // Create an Intent to open the link in a web browser
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(facebookLink));

                    // Check if there's an app that can handle this intent
                    if (intent.resolveActivity(getPackageManager()) != null) {
                        // Start the activity
                        startActivity(intent);
                    } else {
                        // Handle the case where no app can handle the intent
                        Toast.makeText(userProfile.this, "No app can handle this action", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Handle the case where the Facebook link is empty
                    Toast.makeText(userProfile.this, "Facebook link is not available", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setInstagramClickListener() {
        // Assuming you have an Instagram ImageView with id instagram

        // Set click listener on Instagram ImageView
        instagram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check if the Instagram link is not empty
                if (instagramLink != null && !instagramLink.isEmpty()) {
                    // Create an Intent to open the link in a web browser
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(instagramLink));

                    // Check if there's an app that can handle this intent
                    if (intent.resolveActivity(getPackageManager()) != null) {
                        // Start the activity
                        startActivity(intent);
                    } else {
                        // Handle the case where no app can handle the intent
                        Toast.makeText(userProfile.this, "No app can handle this action", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Handle the case where the Instagram link is empty
                    Toast.makeText(userProfile.this, "Instagram link is not available", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void fetchDataFromFirestore() {
        // Fetch panier data from Firebase
        userDocRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null && document.exists()) {
                        // Check if the 'panier' field exists in the document
                        if (document.contains("panier")) {
                            ProductIds = (ArrayList<String>) document.get("MyProducts");
                            fetchProductDetails(ProductIds);
                        } else {
                            // Handle the case where the 'panier' field doesn't exist
                            // You can set panierProductIds to an empty list or handle it accordingly
                            ProductIds = new ArrayList<>();
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
        }}
}