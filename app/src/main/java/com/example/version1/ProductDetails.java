package com.example.version1;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProductDetails extends AppCompatActivity {
    Button home,callButton,addPanier;
    String phone;
    FirebaseUser currentUser;
    FirebaseAuth mAuth;
    String productId;
    FirebaseFirestore db;
    TextView username;
    ImageView imageOwner;
    String owner;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_product_details);
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        TextView titleView = findViewById(R.id.tittle);
        TextView categoryTextView = findViewById(R.id.category);
        TextView deliveryTextView = findViewById(R.id.livraison);
        TextView descriptionTextView = findViewById(R.id.description);
        TextView governmentTextView = findViewById(R.id.gevernment);
        TextView prix = findViewById(R.id.prix_);
         username = findViewById(R.id.owner);
        ImageView imageView = findViewById(R.id.image);
         imageOwner = findViewById(R.id.profileOwner);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String category = extras.getString("category");
            String delivery = extras.getString("delivery");
            String description = extras.getString("description");
            String government = extras.getString("government");
            String imageUrl = extras.getString("imageUrl");
            String phone = extras.getString("phone");
            String price = extras.getString("price");
            String title = extras.getString("title");
            productId = extras.getString("Id");
             owner = extras.getString("owner");



            // Set the received data to the views
            titleView.setText(title);
            categoryTextView.setText("Category :"+category);
            deliveryTextView.setText(delivery);
            descriptionTextView.setText(description);
            governmentTextView.setText(government);
            prix.setText(price);

            // Load the image using Glide (replace 'loading' with your default or placeholder image resource)
            Glide.with(this)
                    .load(imageUrl)
                    .placeholder(R.drawable.loading)
                    .error(R.drawable.loading)
                    .into(imageView);
        }
        initializeProfileImage();
        initializeLinkTextView(username, "username", "Put your Facebook account");
        home = findViewById(R.id.accueil_bott);
        callButton=findViewById(R.id.call);
        addPanier=findViewById(R.id.addPanier);
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ProductDetails.this, chose.class));
            }
        });
        imageOwner.setOnClickListener(v -> {
            // Open UserProfile activity with owner information
            openUserProfile(owner.toString());
        });

        callButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initiatePhoneCall(phone);
            }
        });
        addPanier.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addProductToPanier(productId); // Replace with the actual product ID
            }
        });

    }
    private void initiatePhoneCall(String phoneNumber) {
        // Create an Intent with ACTION_DIAL
        Intent dialIntent = new Intent(Intent.ACTION_DIAL);

        // Set the phone number to dial
        dialIntent.setData(Uri.parse("tel:" + phoneNumber));

        // Check if there's an app that can handle this intent
        if (dialIntent.resolveActivity(getPackageManager()) != null) {
            // Start the activity
            startActivity(dialIntent);
        } else {
            // Handle the case where no activity can handle the intent
            Toast.makeText(this, "No app can handle this action", Toast.LENGTH_SHORT).show();
        }
    }



    private void addProductToPanier(String productID) {
        if (currentUser != null) {


            // Get a reference to the user's document in the "users" collection
            DocumentReference userDocRef = FirebaseFirestore.getInstance().collection("users").document(currentUser.getEmail());

            // Update the "panier" field with the new product ID
            userDocRef.update("panier", FieldValue.arrayUnion(productID))
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(ProductDetails.this, "Product added to panier", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(ProductDetails.this, "Failed to add product to panier", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            // Handle the case where the user is not authenticated
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
        }
    } private void initializeLinkTextView(TextView textView, String field, String defaultText) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            db = FirebaseFirestore.getInstance();
            DocumentReference userRef = db.collection("users").document(owner);

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

    }   private void openUserProfile(String ownerEmail) {
        Intent userProfileIntent = new Intent(this, userProfile.class);

        // Pass the owner's information to UserProfile activity
        userProfileIntent.putExtra("ownerEmail", ownerEmail);

        // Start the UserProfile activity
        startActivity(userProfileIntent);
    }
private void initializeProfileImage() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            db = FirebaseFirestore.getInstance();
            DocumentReference userRef = db.collection("users").document(owner);

            userRef.get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String imageUrl = documentSnapshot.getString("profileImageUrl");
                            if (imageUrl != null && !imageUrl.isEmpty()) {
                                // Use 'this' instead of 'requireContext()' since we are in AppCompatActivity
                                Glide.with(this)
                                        .load(imageUrl)
                                        .placeholder(R.drawable.loading)
                                        .error(R.drawable.loading)
                                        .into(imageOwner);

                                // Set click listener on the profile image
                                imageOwner.setOnClickListener(v -> {
                                    // Open UserProfile activity with owner information
                                    openUserProfile(owner.toString());
                                });
                            }
                        }
                    })
                    .addOnFailureListener(e -> {
                        // Handle failure
                    });
        }





}}