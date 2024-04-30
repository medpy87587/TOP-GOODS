package com.example.version1;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class add extends Fragment {
    static final int PICK_IMAGE_REQUEST = 1;
    private EditText titleEditText, descriptionEditText, phoneEditText, priceEditText;
    private Spinner categorySpinner, governmentSpinner;
    private Button submitButton, selectImageButton;
    private TextView selectedCategoryTextView;
    private ImageView imageView;
    private String isDelivery = "sans livraison", userEmail;
    private ProgressDialog progressDialog;
    private RadioButton radioButton2, radioButton1;
    float price;
    Uri selectedImageUri = null; // Store the selected image URI

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_add, container, false);

        titleEditText = rootView.findViewById(R.id.editTitle);
        descriptionEditText = rootView.findViewById(R.id.editDescription);
        phoneEditText = rootView.findViewById(R.id.editPhone);
        priceEditText = rootView.findViewById(R.id.editPrice);
        categorySpinner = rootView.findViewById(R.id.spinnerCategory);
        governmentSpinner = rootView.findViewById(R.id.spinnerGovernment);
        submitButton = rootView.findViewById(R.id.btnSubmit);
        selectedCategoryTextView = rootView.findViewById(R.id.textCategory);
        imageView = rootView.findViewById(R.id.viewImage);
        selectImageButton = rootView.findViewById(R.id.add);

        selectImageButton.setOnClickListener(v -> openImagePicker());

        RadioGroup radioGroup = rootView.findViewById(R.id.radioGroup);
        radioButton1 = rootView.findViewById(R.id.radioButton1);
        radioButton2 = rootView.findViewById(R.id.radioButton2);

        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radioButton1) {
                radioButton2.setChecked(false);
                isDelivery = "avec livraison";
            } else if (checkedId == R.id.radioButton2) {
                radioButton1.setChecked(false);
            }
        });

        final String[] categories = {"Électronique", "Vêtements", "Meubles", "Livres", "Jouets", "Articles de sport", "Informatique", "Accessoires", "Bijoux", "Autre"};
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(getContext(), R.layout.simple_spinner_item, categories);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(categoryAdapter);

        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String selectedCategory = (String) parentView.getItemAtPosition(position);
                selectedCategoryTextView.setText("Selected Category: " + selectedCategory);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Handle case where no category is selected
            }
        });

        final String[] tunisianGovernments = {"Ariana", "Béja", "Ben Arous", "Bizerte", "Gabès", "Gafsa", "Jendouba", "Kairouan", "Kasserine", "Kébili", "Kef", "Mahdia", "Manouba", "Médenine", "Monastir", "Nabeul", "Sfax", "Sidi Bouzid", "Siliana", "Sousse", "Tataouine", "Tozeur", "Tunis", "Zaghouan"};
        ArrayAdapter<String> governmentAdapter = new ArrayAdapter<>(getContext(), R.layout.simple_spinner_item, tunisianGovernments);
        governmentAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        governmentSpinner.setAdapter(governmentAdapter);

        governmentSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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

        submitButton.setOnClickListener(v -> {
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setCancelable(false);
            progressDialog.setMessage("loading ...");
            progressDialog.show();

            // Handle form submission here
            String title = titleEditText.getText().toString();
            String description = descriptionEditText.getText().toString();
            String government = (String) governmentSpinner.getSelectedItem();
            String phone = phoneEditText.getText().toString();
            price = Float.parseFloat(priceEditText.getText().toString());
            String selectedCategory = (String) categorySpinner.getSelectedItem();

            if (title.isEmpty() || description.isEmpty() || government.isEmpty() || phone.isEmpty()) {
                // Handle validation and show an error message to the user
                // You can display an error message using Toast or a Snackbar
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
            } else {
                // You can now save the product data and image to Firestore
                saveProduct(title, description, isDelivery, government, phone, price, selectedCategory);
            }
        });

        return rootView;
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == getActivity().RESULT_OK && data != null) {
            selectedImageUri = data.getData();
            imageView.setImageURI(selectedImageUri);
        }
    }

    private void saveProduct(String title, String description, String isDelivery, String government, String phone, float price, String selectedCategory) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String, Object> productData = new HashMap<>();
        productData.put("title", title);
        productData.put("description", description);
        productData.put("delivery", isDelivery);
        productData.put("government", government);
        productData.put("phone", phone);
        productData.put("price", price);
        productData.put("category", selectedCategory);
        productData.put("date", new Timestamp(new Date()));
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        productData.put("owner", currentUser.getEmail());
        productData.put("isVisible", true);

        db.collection("products")
                .add(productData)
                .addOnSuccessListener(documentReference -> {
                    if (selectedImageUri != null) {
                        productData.put("documentId", documentReference.getId());
                        uploadImage(documentReference.getId());
                    }
                })
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Product added successfully, do any additional operations here if needed
                    } else {
                        // Handle any errors
                        // You can show an error message to the user
                        if (progressDialog != null && progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }
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
                        updateProductImage(documentId, downloadUri.toString(),price);
                        addProductToMyProduct(documentId);
                    } else {
                        if (progressDialog != null && progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }
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
    }

    private void addProductToMyProduct(String productID) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userEmail = currentUser.getEmail();
            System.out.println("Current user email: " + userEmail);
            DocumentReference userDocRef = FirebaseFirestore.getInstance().collection("users").document(currentUser.getEmail());

            userDocRef.update("MyProducts", FieldValue.arrayUnion(productID))
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(getActivity(), "Product added to MyProducts", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getActivity(), "Failed to add product to MyProducts", Toast.LENGTH_SHORT).show();
                    })
                    .addOnCompleteListener(task -> {
                        if (progressDialog != null && progressDialog.isShowing()) {
                            progressDialog.dismiss();
                            clearAllFields();
                            startActivity(new Intent(getActivity(), chose.class));
                        }
                    });
        } else {
            Toast.makeText(getActivity(), "User not authenticated", Toast.LENGTH_SHORT).show();
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        }
    }


    private void clearAllFields() {
        titleEditText.setText("");
        descriptionEditText.setText("");
        phoneEditText.setText("");
        priceEditText.setText("");
        categorySpinner.setSelection(0);
        governmentSpinner.setSelection(0);
        radioButton1.setChecked(false);
        radioButton2.setChecked(false);
        selectedCategoryTextView.setText("");
        imageView.setImageURI(null);
        selectedImageUri = null;

        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }
}
