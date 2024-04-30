package com.example.version1;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


public class profil extends Fragment {
    Button signout,mesAnnonces;
    TextView facebook,instagram,username,eamil,description;
    FirebaseFirestore db;
    private static final int PICK_IMAGE_REQUEST = 1;
    private ImageView profileImageView;
    private Uri imageUri;
    private GoogleSignInClient mGoogleSignInClient;




    private FirebaseStorage storage;


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_profil, container, false);
        signout = rootView.findViewById(R.id.out); // Fix the findViewById statement
        facebook = rootView.findViewById(R.id.lienFace);
        facebook.setOnClickListener(v -> showFacebookInputDialog());
        instagram = rootView.findViewById(R.id.lienInsta);
        instagram.setOnClickListener(v -> showInstaInputDialog());
        mesAnnonces=rootView.findViewById(R.id.mesAnnouce);
        facebook = rootView.findViewById(R.id.lienFace);
        username=rootView.findViewById(R.id.username);
        eamil=rootView.findViewById(R.id.eamil_hold);
        description=rootView.findViewById(R.id.description);
        description.setOnClickListener(v -> showDescriptionInputDialog());
        initializeLinkTextView(facebook, "facebookLink", "Put your Facebook account");
        initializeLinkTextView(description, "description", "insert a description for your account");

        // Initialize  TextView
        instagram = rootView.findViewById(R.id.lienInsta);
        initializeLinkTextView(instagram, "instagramLink", "Put your Instagram account");
        initializeLinkTextView(username, "username", "");
        initializeLinkTextView(eamil, "email", "");
        profileImageView = rootView.findViewById(R.id.profileOwner);
        profileImageView.setOnClickListener(v -> openGallery());
      initializeProfileImage();
        signout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Sign out from Firebase
                FirebaseAuth.getInstance().signOut();

                // Sign out from Google (if using Google Sign-In)
                GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(getString(R.string.default_web_client_id))
                        .requestEmail()
                        .build();

                mGoogleSignInClient = GoogleSignIn.getClient(getActivity(), gso);
                mGoogleSignInClient.signOut();

                // Navigate to the login activity
                Intent intent = new Intent(getContext(), login1.class);
                startActivity(intent);
            }
        });

        mesAnnonces.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getContext(), MesAnnonces.class); // Use getContext() to get the context
                startActivity(intent);
            }
        });

        return rootView;
    }private void showDescriptionInputDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Insert your Description account");

        // Set up the input
        final EditText input = new EditText(getActivity());
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("OK", (dialog, which) -> {
            String description = input.getText().toString();
            // Save the Facebook link to Firebase
            saveDescriptionToFirestore(description);
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }
    private void showFacebookInputDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Add Facebook Profile Link");

        // Set up the input
        final EditText input = new EditText(getActivity());
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("OK", (dialog, which) -> {
            String facebookLink = input.getText().toString();
            // Save the Facebook link to Firebase
            saveFacebookLinkToFirestore(facebookLink);
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    } private void showInstaInputDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Add Instagram Profile Link");

        // Set up the input
        final EditText input = new EditText(getActivity());
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("OK", (dialog, which) -> {
            String facebookLink = input.getText().toString();
            // Save the intagram link to Firebase
            saveInstagramLinkToFirestore(facebookLink);
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }
    private void saveFacebookLinkToFirestore(String facebookLink) {
        // Validate that the input is a valid Facebook URL
        if (!isValidFacebookUrl(facebookLink)) {
            // Display an error message (e.g., using a Toast)
            Toast.makeText(getActivity(), "Invalid Facebook URL", Toast.LENGTH_SHORT).show();

            return;
        }
        // Get the current user ID or email
        db = FirebaseFirestore.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {

            // Update the user's data in Firestore with the Facebook link
            DocumentReference userRef = db.collection("users").document(currentUser.getEmail());

            userRef.update("facebookLink", facebookLink)
                    .addOnSuccessListener(aVoid -> {
                        // Handle success
                        // Optionally, you can add a success message or perform additional actions
                    })
                    .addOnFailureListener(e -> {
                        // Handle failure
                        // Optionally, you can show an error message or log the error
                    });}
        updateUIWithFacebookLink(facebookLink);
    }
    private void updateUIWithFacebookLink(String facebookLink) {
        // Update the TextView or any other UI component with the Facebook link
        facebook.setText( facebookLink);
    }private boolean isValidFacebookUrl(String url) {
        String facebookRegex = "^(https?://)?(www\\.)?facebook\\.com/.+";
        return url.matches(facebookRegex);
    }
    private void saveInstagramLinkToFirestore(String instagramLink) {
        // Validate that the input is a valid Instagram URL
        if (!isValidInstagramUrl(instagramLink)) {
            // Display an error message (e.g., using a Toast)

            Toast.makeText(getActivity(), "Invalid Instagram URL", Toast.LENGTH_SHORT).show();

            return;
        }

        db = FirebaseFirestore.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {

            // Update the user's data in Firestore with the Facebook link
            DocumentReference userRef = db.collection("users").document(currentUser.getEmail());

            userRef.update("instagramLink", instagramLink)
                    .addOnSuccessListener(aVoid -> {
                        // Handle success
                        // Optionally, you can add a success message or perform additional actions

                        // Update the UI to display the Instagram link
                        updateUIWithInstagramLink(instagramLink);
                    })
                    .addOnFailureListener(e -> {
                        // Handle failure
                        // Optionally, you can show an error message or log the error

                        Toast.makeText(getActivity(), "Failed to save Instagram link", Toast.LENGTH_SHORT).show();
                    });}
    }

    // Validate that the input is a valid Instagram URL
    private boolean isValidInstagramUrl(String url) {
        // Customize the regex pattern for a valid Instagram URL
        String instagramRegex = "^(https?://)?(www\\.)?instagram\\.com/.+";
        return url.matches(instagramRegex);
    }
    private void updateUIWithInstagramLink(String instagramLink) {
        // Update the TextView or any other UI component with the Facebook link
        instagram.setText( instagramLink);
    }
    private void initializeLinkTextView(TextView textView, String field, String defaultText) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            db = FirebaseFirestore.getInstance();
            DocumentReference userRef = db.collection("users").document(currentUser.getEmail());

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


    } private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            // Display the selected image in the ImageView
            Glide.with(requireContext())
                    .load(imageUri)
                    .placeholder(R.drawable.loading)
                    .error(R.drawable.loading)
                    .into(profileImageView);
            saveProfileImageToFirebase();
        }
    }



    private void saveProfileImageToFirebase() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null && imageUri != null) {
            StorageReference storageRef = FirebaseStorage.getInstance().getReference();
            StorageReference imageRef = storageRef.child("profile_images/" + currentUser.getUid() + ".jpg");

            // Upload the selected image to Firebase Storage
            imageRef.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        // Image upload successful
                        // You can get the download URL if needed
                        imageRef.getDownloadUrl()
                                .addOnSuccessListener(uri -> {
                                    // Save the image URL to Firestore
                                    saveImageUrlToFirestore(uri.toString());
                                })
                                .addOnFailureListener(e -> {
                                    // Handle failure
                                    Toast.makeText(getActivity(), "Failed to get image URL", Toast.LENGTH_SHORT).show();
                                });
                    })
                    .addOnFailureListener(e -> {
                        // Handle failure
                        Toast.makeText(getActivity(), "Failed to upload image", Toast.LENGTH_SHORT).show();
                    });
        }
    }

    private void saveImageUrlToFirestore(String imageUrl) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            // Update the user's data in Firestore with the image URL
            DocumentReference userRef = FirebaseFirestore.getInstance().collection("users").document(currentUser.getEmail());

            userRef.update("profileImageUrl", imageUrl)
                    .addOnSuccessListener(aVoid -> {
                        // Handle success
                    })
                    .addOnFailureListener(e -> {
                        // Handle failure
                        Toast.makeText(getActivity(), "Failed to update profile image URL", Toast.LENGTH_SHORT).show();
                    });
        }
    }private void initializeProfileImage() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            db = FirebaseFirestore.getInstance();
            DocumentReference userRef = db.collection("users").document(currentUser.getEmail());

            userRef.get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String imageUrl = documentSnapshot.getString("profileImageUrl");
                            if (imageUrl != null && !imageUrl.isEmpty()) {
                                Glide.with(requireContext())
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
    private void saveDescriptionToFirestore(String description) {
        // Validate that the input is a valid Facebook URL

        // Get the current user ID or email
        db = FirebaseFirestore.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {

            // Update the user's data in Firestore with the Facebook link
            DocumentReference userRef = db.collection("users").document(currentUser.getEmail());

            userRef.update("description", description)
                    .addOnSuccessListener(aVoid -> {
                        // Handle success
                        // Optionally, you can add a success message or perform additional actions
                    })
                    .addOnFailureListener(e -> {
                        // Handle failure
                        // Optionally, you can show an error message or log the error
                    });}
        updateUIWithDescription(description);
    }
    private void updateUIWithDescription(String desc) {
        // Update the TextView or any other UI component with the Facebook link
        description.setText(desc);}
}

