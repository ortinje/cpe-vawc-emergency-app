package com.bscpe.vawcemergencyapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

import com.bscpe.vawcemergencyapp.databinding.ActivityMainPageBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainPage extends AppCompatActivity {

    ActivityMainPageBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainPageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        replaceFragment(new HomeFragment());
        binding.bottomNavigationView.setBackground(null);
        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.home) {
                replaceFragment(new HomeFragment());
            } else if (item.getItemId() == R.id.profile) {
                replaceFragment(new ProfileFragment());
            } else if (item.getItemId() == R.id.chat) {
                replaceFragment(new ChatFragment());
            }
            return true;
        });

        // Fetch and display user data from Firebase Realtime Database
        fetchAndDisplayUserData();
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout, fragment);
        fragmentTransaction.commit();
    }

    private void fetchAndDisplayUserData() {
        // Use Firebase Authentication to get the current user's ID
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();

            // Access the Firebase Realtime Database to fetch user data
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users").child(userId);
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // Retrieve user data from the database
                    String firstName = dataSnapshot.child("firstName").getValue(String.class);
                    String lastName = dataSnapshot.child("lastName").getValue(String.class);
                    // ... Retrieve other user data fields

                    // Pass the user data to the fragments
                    Bundle userData = new Bundle();
                    userData.putString("firstName", firstName);
                    userData.putString("lastName", lastName);

                    // ... Put other user data fields into the bundle

                    // Pass the user data to each fragment
                    HomeFragment homeFragment = HomeFragment.newInstance(firstName);
                    homeFragment.setArguments(userData);
                    replaceFragment(homeFragment);


                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Handle any errors that may occur
                    // For example:
                    // Log.e("MainPage", "Failed to fetch user data: " + databaseError.getMessage());
                }
            });
        }
    }
}
