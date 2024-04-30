package com.example.version1;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;



public class accueil extends Fragment {

RecyclerView recyclerView;
private FirebaseFirestore db;
private List<product> productList;
    String uri;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_accueil, container, false);

       recyclerView=rootView.findViewById(R.id.recyclerview1);
       productList=new ArrayList<>();


        // Initialize Firebase
        db = FirebaseFirestore.getInstance();




        // Fetch data from Firestore
        fetchDataFromFirestore();
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        layoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE); // Optional, to remove gaps between items
        layoutManager.setOrientation(StaggeredGridLayoutManager.VERTICAL);


        // Set the layoutManager to your RecyclerView
        recyclerView.setLayoutManager(layoutManager);

        AccueilAdapter adapter = new AccueilAdapter(productList, getActivity());
        adapter.setOnItemClickListener(new AccueilAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Bundle productBundle) {
                Intent intent = new Intent(getActivity(), ProductDetails.class);
                intent.putExtras(productBundle);
                startActivity(intent);
            }
        });

        recyclerView.setAdapter(adapter);

        return rootView;

    }

    private void fetchDataFromFirestore() {
        db.collection("products").whereEqualTo("isVisible", true)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        // Handle errors
                        return;
                    }

                    productList.clear(); // Clear the existing list

                    for (QueryDocumentSnapshot document : value) {
                        try {
                            // Assuming you have a Product class. Adjust accordingly.
                            product product = document.toObject(product.class);
                            productList.add(product);
                        } catch (Exception e) {
                            // Log the exception
                            e.printStackTrace();
                        }
                    }

                    // Notify the adapter that the data set has changed
                    recyclerView.getAdapter().notifyDataSetChanged();
                });
    }





}
