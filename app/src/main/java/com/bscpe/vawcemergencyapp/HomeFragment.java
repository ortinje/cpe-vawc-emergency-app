package com.bscpe.vawcemergencyapp;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class HomeFragment extends Fragment {

    // Method to create a new instance of HomeFragment and set arguments
    public static HomeFragment newInstance(String firstName) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString("firstName", firstName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Access the TextView to show the user's first name
        TextView showFirstNameTextView = view.findViewById(R.id.showFirstName);

        // Retrieve the user's first name from the arguments
        Bundle args = getArguments();
        if (args != null) {
            String firstName = args.getString("firstName");
            if (firstName != null) {
                // Set the user's first name in the TextView
                showFirstNameTextView.setText(firstName);
            }
        }

        return view;
    }
}
