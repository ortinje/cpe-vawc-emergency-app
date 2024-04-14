package com.bscpe.vawcemergencyapp;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MenuViewHealthInfo extends Fragment {

    EditText bloodType, weight, height, foodAllergy, medicalHistory, surgery;

    FrameLayout spinner;

    private DatabaseReference mDatabase;
    Spinner sex, civilStatus;
    Button submitButton;

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_menu_view_health_info, container, false);

        bloodType = view.findViewById(R.id.updateBloodType);
        weight = view.findViewById(R.id.updateWeight);
        height = view.findViewById(R.id.updateHeight);
        foodAllergy = view.findViewById(R.id.updateAllergy);
        medicalHistory = view.findViewById(R.id.updateMedicalHistory);
        surgery = view.findViewById(R.id.updateSurgeries);

        submitButton = view.findViewById(R.id.submitButton);
        spinner = view.findViewById(R.id.progress_loader03);
        spinner.setVisibility(View.VISIBLE);

        // Initialize Firebase Authentication
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        // Initialize Firebase Database
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Get the current user
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            // Get the user's ID
            String userId = currentUser.getUid();

            // Get a reference to the "users" node in the database
            mDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(userId);
            spinner.setVisibility(View.VISIBLE);
            // Add a listener to retrieve the user's data
            mDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    // Retrieve user data from the dataSnapshot
                    String userBloodType = dataSnapshot.child("HealthInfo").child("BloodType").getValue(String.class);
                    String userWeight = dataSnapshot.child("HealthInfo").child("Weight").getValue(String.class);
                    String userHeight = dataSnapshot.child("HealthInfo").child("Height").getValue(String.class);
                    String userFoodAllergy = dataSnapshot.child("FoodAllergy").child("01").getValue(String.class);
                    String userMedicalHistory = dataSnapshot.child("MedicalHistory").child("01").getValue(String.class);
                    String userSurgery = dataSnapshot.child("Surgeries").child("01").getValue(String.class);


                    spinner.setVisibility(View.GONE);
                    // Set the retrieved data to the EditText and Spinners
                    bloodType.setText(userBloodType);
                    weight.setText(userWeight);
                    height.setText(userHeight);
                    foodAllergy.setText(userFoodAllergy);
                    medicalHistory.setText(userMedicalHistory);
                    surgery.setText(userSurgery);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Handle database error
                }
            });
        }

        // Set a click listener for the submit button
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Call a method to update the user's data in the database
                updateUserData();
            }
        });

        return view;
    }

    // Method to update the user's data in the database
    private void updateUserData() {
        // Get the current user
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            // Get the user's ID
            String userId = currentUser.getUid();

            // Get a reference to the "users" node in the database
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users").child(userId);

            userRef.child("HealthInfo").child("BloodType").setValue(bloodType.getText().toString());
            userRef.child("HealthInfo").child("Weight").setValue(weight.getText().toString());
            userRef.child("HealthInfo").child("Height").setValue(height.getText().toString());
            userRef.child("FoodAllergy").child("01").setValue(foodAllergy.getText().toString());
            userRef.child("MedicalHistory").child("01").setValue(medicalHistory.getText().toString());
            userRef.child("Surgeries").child("01").setValue(surgery.getText().toString())
                    .addOnSuccessListener(aVoid -> {
                        // Display a toast message indicating a successful update
                        Toast.makeText(requireContext(), "User data updated successfully", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        // Handle the failure to update the data
                        Toast.makeText(requireContext(), "Failed to update user data", Toast.LENGTH_SHORT).show();
                    });
        }
    }
}

