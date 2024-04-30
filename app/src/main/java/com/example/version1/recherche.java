package com.example.version1;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class recherche extends Fragment {
    private RecyclerView recyclerView;
    private List<product> productList;
    private FirebaseFirestore db;
    private String category, government, deliveryOption;
    private int price = 0; // Change the type to 'int'
    private AccueilAdapter adapter;
    private EditText searchEditText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_recherche, container, false);
        recyclerView = rootView.findViewById(R.id.recycleView);
        searchEditText = rootView.findViewById(R.id.searchEditText);

        productList = new ArrayList<>();
        // Add a click listener to the filter image
        ImageView filterImage = rootView.findViewById(R.id.filterImag);
        filterImage.setOnClickListener(v -> showFilterDialog());

        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        layoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE);
        layoutManager.setOrientation(StaggeredGridLayoutManager.VERTICAL);

        // Set the layoutManager to your RecyclerView
        recyclerView.setLayoutManager(layoutManager);

        // Initialize the adapter
        adapter = new AccueilAdapter(productList, getActivity());
        adapter.setOnItemClickListener(productBundle -> {
            Intent intent = new Intent(getActivity(), ProductDetails.class);
            intent.putExtras(productBundle);
            startActivity(intent);
        });

        recyclerView.setAdapter(adapter);

        // Listen for changes in the searchEditText
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Not needed for this example
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Call the search function whenever text changes
                performTitleSearch(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // Not needed for this example
            }
        });

        return rootView;
    }

    private void performTitleSearch(String query) {
        // Convert the query to lowercase to make the search case-insensitive
        String lowercaseQuery = query.toLowerCase();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference productsRef = db.collection("products");

        Query titleQuery = productsRef
               // .whereEqualTo("isVisible", true) // Add this condition to filter visible products
               .orderBy("title")
                .startAt(lowercaseQuery)
                .endAt(lowercaseQuery + "\uf8ff")
                .limit(10);

        titleQuery.get().addOnCompleteListener(task -> {
            productList.clear();
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    product product = document.toObject(product.class);
                    productList.add(product);
                }
                // Notify the adapter that the data has changed
                adapter.notifyDataSetChanged();
            } else {
                // Handle errors
                Toast.makeText(getActivity(), "message", Toast.LENGTH_SHORT).show();

            }
        });
    }


    private void showFilterDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Filter Options");

        // Inflate the dialog layout
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_filter, null);
        builder.setView(dialogView);
        //SeekBar seekBarPrice = dialogView.findViewById(R.id.seekBarPrice);
        //TextView priceTextView = dialogView.findViewById(R.id.textPrice);

        // Set up the SeekBar for Price
//        seekBarPrice.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//            @Override
//            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//                // Update the TextView with the current price range
//                priceTextView.setText("Price: 0 DTN - " + progress + " DTN");
//
//                // Update the 'price' variable with the current progress
//                price = progress;
//
//                // Log the progress for debugging
//                Log.d("SeekBarProgress", "Progress: " + progress);
//            }
//
//            @Override
//            public void onStartTrackingTouch(SeekBar seekBar) {
//                // Not needed for this example
//            }
//
//            @Override
//            public void onStopTrackingTouch(SeekBar seekBar) {
//                // Not needed for this example
//            }
//        });

        // Find the Spinner, SeekBar, and RadioGroup within the inflated layout
        Spinner categorySpinner = dialogView.findViewById(R.id.spinnerCategory);
        Spinner governmentSpinner = dialogView.findViewById(R.id.spinnerGovernment);
        RadioGroup deliveryRadioGroup = dialogView.findViewById(R.id.radioGroupDelivery);

        // Set up the Spinner with adapter and other configurations
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, getCategories());
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(categoryAdapter);

        ArrayAdapter<String> governmentAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, getTunisianGovernments());
        governmentAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        governmentSpinner.setAdapter(governmentAdapter);

        builder.setPositiveButton("Apply", (dialog, which) -> {
            // Handle filter logic here
            // You can access the selected values from the dialog views
            // and apply the filter to your search

            if (categorySpinner.getSelectedItem() != null) {
                category = categorySpinner.getSelectedItem().toString();
            }

            if (governmentSpinner.getSelectedItem() != null) {
                government = governmentSpinner.getSelectedItem().toString();
            }

            // Get selected radio button value
            int selectedDeliveryId = deliveryRadioGroup.getCheckedRadioButtonId();
            deliveryOption = "";
            if (selectedDeliveryId != -1) {
                RadioButton selectedRadioButton = dialogView.findViewById(selectedDeliveryId);
                deliveryOption = selectedRadioButton.getText().toString();
            }

            // Perform filtering or query based on the selected values
            queryFirebase(category, government, price, deliveryOption);

            // Reset the filter components
            //seekBarPrice.setProgress(0);
           // priceTextView.setText("Price: 0 DTN");
            categorySpinner.setSelection(0);
            governmentSpinner.setSelection(0);
            deliveryRadioGroup.clearCheck();
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private List<String> getCategories() {
        return Arrays.asList("Électronique", "Vêtements", "Meubles", "Livres", "Jouets", "Articles de sport", "Autre", "Informatique", "Accessoires", "Bijoux", "Autre");
    }

    private List<String> getTunisianGovernments() {
        return Arrays.asList("Ariana", "Béja", "Ben Arous", "Bizerte", "Gabès", "Gafsa", "Jendouba", "Kairouan", "Kasserine", "Kébili", "Kef", "Mahdia", "Manouba", "Médenine", "Monastir", "Nabeul", "Sfax", "Sidi Bouzid", "Siliana", "Sousse", "Tataouine", "Tozeur", "Tunis", "Zaghouan");
    }

    private void queryFirebase(String category, String government, int price, String deliveryOption) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference productsRef = db.collection("products");

        Query query = productsRef.whereEqualTo("isVisible", true);

        if (category != null && !category.isEmpty()) {
            query = query.whereEqualTo("category", category);
        }

        if (government != null && !government.isEmpty()) {
            query = query.whereEqualTo("government", government);
        }

        if (price > 0) {
            query = query.whereLessThanOrEqualTo("price", price);
        }

        if (deliveryOption != null && !deliveryOption.isEmpty()) {
            query = query.whereEqualTo("delivery", deliveryOption);
        }

        query.get().addOnCompleteListener(task -> {
            productList.clear();
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    product product = document.toObject(product.class);
                    productList.add(product);
                }
                adapter.notifyDataSetChanged();
            } else {
                adapter.notifyDataSetChanged();
            }
        });
    }
}