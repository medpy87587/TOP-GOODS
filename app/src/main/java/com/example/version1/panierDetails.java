package com.example.version1;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class panierDetails extends AppCompatActivity {

    Button contactSeller;
    String phone;
    FirebaseUser currentUser;
    FirebaseAuth mAuth;
    String productId, emailowner;
    private String facebookLink, instagramLink, idUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_panier_details);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        TextView titleView = findViewById(R.id.tittle);
        TextView categoryTextView = findViewById(R.id.category);
        TextView deliveryTextView = findViewById(R.id.livraison);
        TextView descriptionTextView = findViewById(R.id.description);
        TextView governmentTextView = findViewById(R.id.gevernment);
        TextView prix = findViewById(R.id.prix_);
        ImageView imageView = findViewById(R.id.image);

        contactSeller = findViewById(R.id.commande);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String category = extras.getString("category");
            String delivery = extras.getString("delivery");
            String description = extras.getString("description");
            String government = extras.getString("government");
            String imageUrl = extras.getString("imageUrl");
            phone = extras.getString("phone");
            String price = extras.getString("price");
            String title = extras.getString("title");
            productId = extras.getString("Id");
            emailowner = extras.getString("owner");

            titleView.setText(title);
            categoryTextView.setText("Category: " + category);
            deliveryTextView.setText(delivery);
            descriptionTextView.setText(description);
            governmentTextView.setText(government);
            prix.setText(price);

            Glide.with(this)
                    .load(imageUrl)
                    .placeholder(R.drawable.loading)
                    .error(R.drawable.loading)
                    .into(imageView);
        }

        fetchSocialMediaLinks();
        contactSeller.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showContactDialog();
            }
        });
    }

    private void showContactDialog() {
        // Create a dialog with three options (Instagram, Facebook, and Phone)
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Contact Seller")
                .setIcon(R.drawable.smartphone)
                .setItems(new CharSequence[]{"Instagram", "Facebook", "Phone"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        switch (which) {
                            case 0:
                                // Instagram
                                contactInstagram();
                                break;
                            case 1:
                                // Facebook
                                contactFacebook();
                                break;
                            case 2:
                                // Phone
                                initiatePhoneCall(phone);
                                break;
                        }
                    }
                });

        // Create and show the dialog
        builder.create().show();
    }

    private void contactInstagram() {
        if (instagramLink != null && !instagramLink.isEmpty()) {
            // If Instagram link is available, open the Instagram app
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(instagramLink));
            intent.setPackage("com.instagram.android");
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            } else {
                // If Instagram app is not installed, you can open the link in a browser
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(instagramLink));
                startActivity(browserIntent);
            }
        } else {
            Toast.makeText(this, "Instagram link not available", Toast.LENGTH_SHORT).show();
        }
    }

    private void contactFacebook() {
        if (facebookLink != null && !facebookLink.isEmpty()) {
            // If Facebook link is available, open the Facebook app
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(facebookLink));
            intent.setPackage("com.facebook.katana");
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            } else {
                // If Facebook app is not installed, you can open the link in a browser
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(facebookLink));
                startActivity(browserIntent);
            }
        } else {
            Toast.makeText(this, "Facebook link not available", Toast.LENGTH_SHORT).show();
        }
    }

    private void initiatePhoneCall(String phoneNumber) {
        Intent dialIntent = new Intent(Intent.ACTION_DIAL);
        dialIntent.setData(Uri.parse("tel:" + phoneNumber));
        if (dialIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(dialIntent);
        } else {
            Toast.makeText(this, "No app can handle this action", Toast.LENGTH_SHORT).show();
        }
    }

    private void fetchSocialMediaLinks() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference linksDocRef = db.collection("users").document(emailowner);

        linksDocRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document != null && document.exists()) {
                    // Make sure to update the UI or perform actions with the fetched links
                    facebookLink = document.getString("facebookLink");
                    instagramLink = document.getString("instagramLink");
                    // Call the methods to contact Instagram or Facebook here if needed
                    // For example: contactInstagram() or contactFacebook()
                } else {
                    // Handle the case where the document doesn't exist
                    Toast.makeText(this, "User document not found", Toast.LENGTH_SHORT).show();
                }
            } else {
                // Handle the case where the task was not successful
                Exception exception = task.getException();
                if (exception != null) {
                    exception.printStackTrace();
                }
                Toast.makeText(this, "Failed to fetch social media links", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
