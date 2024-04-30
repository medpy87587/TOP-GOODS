package com.example.version1;

import static com.example.version1.add.PICK_IMAGE_REQUEST;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

public class EditProductDialogFragment extends DialogFragment {
    String isDelivery;
    private EditText editTitle;
    private EditText editPrice;
    private EditText editDescription;
    private EditText editPhone;
    private RadioButton radioButton1;
    private RadioButton radioButton2;
    private Spinner spinnerCategory;
    private Spinner spinnerGovernment;
    String[] categories;
    private ImageView imageView;
    private Button selectImageButton;
    Uri selectedImageUri = null;
    String urlImage, government, category, id;
    String[] tunisianGovernments;
    static Bundle args;

    // Interface to communicate with the hosting activity or fragment
    public interface EditProductDialogListener {
        void onEditProductConfirmed(String updatedTitle, String updatedPrice, String updatedDescription,
                                    String updatedPhone, boolean isDelivery, String government,
                                    String category, Uri image, String documentId);

    }

    // Static method to create a new instance of the dialog and pass product details
    public static EditProductDialogFragment newInstance(String title, String price, String description,
                                                        String phone, String isDelivery, String category,
                                                        String government, String urlImage, String id) {
        EditProductDialogFragment fragment = new EditProductDialogFragment();
        args = new Bundle();
        args.putString("title", title);
        args.putString("price", price);
        args.putString("description", description);
        args.putString("phone", phone);
        args.putString("delivery", isDelivery);
        args.putString("category", category);
        args.putString("government", government);
        args.putString("imageUrl", urlImage);
        args.putString("Id", id);
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());

        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_update_announce, null);

        // Find all EditText and other fields in fragment_add.xml
        selectImageButton = view.findViewById(R.id.add);

        selectImageButton.setOnClickListener(v -> openImagePicker());

        editTitle = view.findViewById(R.id.editTitle);
        editPrice = view.findViewById(R.id.editPrice);
        editDescription = view.findViewById(R.id.editDescription);
        editPhone = view.findViewById(R.id.editPhone);
        radioButton1 = view.findViewById(R.id.radioButton1);
        radioButton2 = view.findViewById(R.id.radioButton2);
        spinnerCategory = view.findViewById(R.id.spinnerCategory);
        imageView = view.findViewById(R.id.viewImage);
        spinnerGovernment = view.findViewById(R.id.spinnerGovernment);
        categories = new String[]{"Électronique", "Vêtements", "Meubles", "Livres", "Jouets", "Articles de sport", "Autre", "Informatique", "Accessoires", "Bijoux", "Autre"};
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(getContext(), R.layout.simple_spinner_item, categories);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(categoryAdapter);

        spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String selectedCategory = (String) parentView.getItemAtPosition(position);
                //selectedCategoryTextView.setText("Selected Category: " + selectedCategory);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Handle case where no category is selected
            }
        });

        tunisianGovernments = new String[]{"Ariana", "Béja", "Ben Arous", "Bizerte", "Gabès", "Gafsa", "Jendouba", "Kairouan", "Kasserine", "Kébili", "Kef", "Mahdia", "Manouba", "Médenine", "Monastir", "Nabeul", "Sfax", "Sidi Bouzid", "Siliana", "Sousse", "Tataouine", "Tozeur", "Tunis", "Zaghouan"};
        ArrayAdapter<String> governmentAdapter = new ArrayAdapter<>(getContext(), R.layout.simple_spinner_item, tunisianGovernments);
        governmentAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGovernment.setAdapter(governmentAdapter);

        spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String selectedGovernment = (String) parentView.getItemAtPosition(position);
                // Do something with the selected government, e.g., store it in a variable or display it
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Handle case where no government is selected
            }
        });

        // Retrieve product details from arguments and pre-fill the EditText fields
        Bundle args = getArguments();
        if (args != null) {
            editTitle.setText(args.getString("title", ""));
            editPrice.setText(args.getString("price", ""));
            editDescription.setText(args.getString("description", ""));
            editPhone.setText(args.getString("phone", ""));
            urlImage = args.getString("imageUrl", "");
            loadImage(urlImage);
            isDelivery = args.getString("delivery", "");
            if ("avec livraison".equals(isDelivery)) {
                radioButton1.setChecked(true);
            } else {
                radioButton2.setChecked(true);
            }
            category = args.getString("category", "");
            int categoryIndex = getCategoryIndex(category);
            spinnerCategory.setSelection(categoryIndex);

            government = args.getString("government", "");
            int governmentIndex = getGovernmentIndex(government);
            spinnerGovernment.setSelection(governmentIndex);

            // Set values for other fields (spinnerCategory, spinnerGovernment, etc.)
            // You'll need to modify this part based on how you store and retrieve data
            // for these fields in your app.
            // Example:
            // String category = args.getString("category", "");
            // int categoryIndex = getCategoryIndex(category);
            // spinnerCategory.setSelection(categoryIndex);

            // Set values for other fields similarly

            // Retrieve and set other properties
        }

        builder.setView(view)
                .setTitle("Edit Product")
                .setPositiveButton("Save", (dialog, which) -> {
                    // Pass the updated data to the listener
//                    EditProductDialogListener listener = (EditProductDialogListener) requireActivity();
//                    listener.onEditProductConfirmed(
//                            editTitle.getText().toString(),
//                            editPrice.getText().toString(),
//                            editDescription.getText().toString(),
//                            editPhone.getText().toString(),
//                            radioButton1.isChecked(),
//                            government,
//                            category,
//                            selectedImageUri,
//                            id
//                    );
                    Toast.makeText(getActivity(), args.getString("Id", "")+government+category, Toast.LENGTH_SHORT).show();

                    saveProduct(editTitle.getText().toString(),editDescription.getText().toString(), isDelivery, (String) spinnerGovernment.getSelectedItem(), editPhone.getText().toString(), Float.parseFloat(editPrice.getText().toString()), (String) spinnerCategory.getSelectedItem());

                })
                .setNegativeButton("Cancel", null);

        return builder.create();
    }

    private int getCategoryIndex(String category) {
        // Implement logic to find the index of the category in your array
        // Return the index or -1 if not found
        // Example:
        for (int i = 0; i < categories.length; i++) {
            if (categories[i].equals(category)) {
                return i;
            }
        }
        return -1;
    }

    private int getGovernmentIndex(String government) {
        // Implement logic to find the index of the government in your array
        // Return the index or -1 if not found
        // Example:
        for (int i = 0; i < tunisianGovernments.length; i++) {
            if (tunisianGovernments[i].equals(government)) {
                return i;
            }
        }
        return -1;
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == getActivity().RESULT_OK && data != null) {
            selectedImageUri = Uri.parse(urlImage);

            selectedImageUri = data.getData();
            imageView.setImageURI(selectedImageUri);
        }
    }

    private void loadImage(String imageUrl) {
        Uri imageUri = Uri.parse(imageUrl);
        if (imageUri != null) {
            Glide.with(requireContext())
                    .load(imageUri)
                    .placeholder(R.drawable.loading) // Placeholder image while loading
                    .error(R.drawable.loading) // Error image if the load fails
                    .into(imageView);
        } else {
            imageView.setImageResource(R.drawable.loading);
        }
    }

    private void saveProduct(String title, String description, String isDelivery, String government, String phone, float price, String selectedCategory) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference productRef = db.collection("products").document(args.getString("Id", ""));

        Map<String, Object> productData = new HashMap<>();
        productData.put("title", title);
        productData.put("description", description);
        productData.put("delivery", isDelivery);
        productData.put("government", government);
        productData.put("phone", phone);
        productData.put("price", price);
        productData.put("category", selectedCategory);
       // productData.put("date", new Timestamp(new Date()));
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        productData.put("owner", currentUser.getEmail());

        productRef.update(productData)
                .addOnSuccessListener(documentReference -> {
                    if (selectedImageUri != null) {
                        productData.put("documentId", args.getString("Id", ""));
                        uploadImage(args.getString("Id", ""));
                    }
                })
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Product added successfully, do any additional operations here if needed
                    } else {
                        // Handle any errors
                        // You can show an error message to the user
                    }
                });
    }

    private void uploadImage(String documentId) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

        StorageReference imageRef = storageRef.child("images/" + documentId + ".jpg");

        imageRef.putFile(selectedImageUri)
                .continueWithTask(task -> {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return imageRef.getDownloadUrl();
                })
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        updateProductImage(documentId, downloadUri.toString(),Float.parseFloat(editPrice.getText().toString()));

                    } else {
                    }
                });
    }

    private void updateProductImage(String documentId, String imageUrl,float price) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference productRef = db.collection("products").document(documentId);
        productRef.update("id", documentId)
                .addOnSuccessListener(aVoid -> {
                    Log.d("MyApp", "Firestore document updated with image URL");
                })
                .addOnFailureListener(e -> {
                    Log.d("MyApp", "Failed to update Firestore document with image URL");
                });
        productRef.update("imageUrl", imageUrl)
                .addOnSuccessListener(aVoid -> {
                    Log.d("MyApp", "Firestore document updated with image URL");
                })
                .addOnFailureListener(e -> {
                    Log.d("MyApp", "Failed to update Firestore document with image URL");
                });
        productRef.update("price", price) // Change to float
                .addOnSuccessListener(aVoid -> {
                    Log.d("MyApp", "Firestore document updated with price");
                })
                .addOnFailureListener(e -> {
                    Log.d("MyApp", "Failed to update Firestore document with price");
                });
    }}