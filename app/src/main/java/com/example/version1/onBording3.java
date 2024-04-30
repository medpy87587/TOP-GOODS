package com.example.version1;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;


public class onBording3 extends Fragment {
    private Button button1;





    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_on_bording3, container, false);
        button1=rootView.findViewById(R.id.button_onboarding3);

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an Intent to start the login activity
                Intent intent = new Intent(getActivity(), login1.class);
                startActivity(intent);
            }
        });



        return rootView;
    }


}