package com.bscpe.vawcemergencyapp;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.service.autofill.UserData;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.Locale;

public class SignUpPage extends AppCompatActivity {

    Spinner sexDropdown, civilStatusDropdown;
    EditText birthdayInput,emailInput, passwordInput, repeatPassInput, lastNameInput, firstNameInput,contactNumInput, addressInput;
    Button submitBtn;

    ProgressBar spinner;

    FirebaseAuth mAuth;
    DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_page);

        // Initialize Firebase Authentication
         mAuth = FirebaseAuth.getInstance();
        // Initialize Firebase Database
        mDatabase = FirebaseDatabase.getInstance().getReference();
        sexDropdown = findViewById(R.id.sexSpinner);
        civilStatusDropdown = findViewById(R.id.civilStatusSpinner);
        birthdayInput = findViewById(R.id.Birthdate);
        submitBtn = findViewById(R.id.submitButton);
        emailInput = findViewById(R.id.regEmail);
        passwordInput = findViewById(R.id.regPassword);
        repeatPassInput = findViewById(R.id.regPasswordConfirm);
        firstNameInput = findViewById(R.id.regFirstName);
        lastNameInput = findViewById(R.id.regLastName);
        contactNumInput= findViewById(R.id.RegContactNum);
        addressInput = findViewById(R.id.RegAddress);
        spinner = findViewById(R.id.progress_loader);
        spinner.setVisibility(View.GONE);
        //SEX DROPDOWN
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.sex, R.layout.spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sexDropdown.setAdapter(adapter);


        //CIVIL STATUS
        ArrayAdapter<CharSequence> civilStatusAdapter = ArrayAdapter.createFromResource(this, R.array.civilStatus, R.layout.spinner_item);
        civilStatusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        civilStatusDropdown.setAdapter(civilStatusAdapter);

        //BIRTDATE DATE PICKER
        birthdayInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(SignUpPage.this,
                        R.style.DatePickerDialogTheme,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                // Update the EditText with the selected date in MM/DD/YYYY format
                                String selectedDate = String.format(Locale.US, "%02d/%02d/%04d", month + 1, dayOfMonth, year);
                                birthdayInput.setText(selectedDate);
                            }
                        }, year, month, day);

                datePickerDialog.show();
            }
        });

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailInput.getText().toString().trim();
                String password = passwordInput.getText().toString().trim();
                String confirmPassword = repeatPassInput.getText().toString().trim();
                String firstName = firstNameInput.getText().toString().trim();
                String lastName = lastNameInput.getText().toString().trim();
                String contactNum = contactNumInput.getText().toString().trim();
                String address = addressInput.getText().toString().trim();
                String birthdate = birthdayInput.getText().toString();
                String sex = sexDropdown.getSelectedItem().toString();
                String civilStatus = civilStatusDropdown.getSelectedItem().toString();

                //check if email is not empty and email is valid
                if (TextUtils.isEmpty(email) || !isValidEmail(email)) {
                    Toast.makeText(SignUpPage.this, "Enter a valid email address", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(SignUpPage.this, "Enter password", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!password.equals(confirmPassword)) {
                    Toast.makeText(SignUpPage.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (password.length() < 8) {
                    Toast.makeText(SignUpPage.this, "Password should be at least 8 characters long", Toast.LENGTH_SHORT).show();
                }
                if (TextUtils.isEmpty(firstName)) {
                    Toast.makeText(SignUpPage.this, "Please input first name", Toast.LENGTH_SHORT).show();
                }
                if (TextUtils.isEmpty(lastName)) {
                    Toast.makeText(SignUpPage.this, "Please input last name", Toast.LENGTH_SHORT).show();
                }
                if (TextUtils.isEmpty(contactNum)) {
                    Toast.makeText(SignUpPage.this, "Please input contact number", Toast.LENGTH_SHORT).show();
                }
                if (TextUtils.isEmpty(address)) {
                    Toast.makeText(SignUpPage.this, "Please input address", Toast.LENGTH_SHORT).show();
                }
                if (TextUtils.isEmpty(birthdate)) {
                    Toast.makeText(SignUpPage.this, "Please input birthday", Toast.LENGTH_SHORT).show();
                }

                spinner.setVisibility(View.VISIBLE);

                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                spinner.setVisibility(View.GONE);
                                if (task.isSuccessful()) {
                                    mAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                FirebaseUser user = mAuth.getCurrentUser();
                                                String userId = user.getUid();

                                                // Save user data to Firebase Realtime Database
                                                DatabaseReference userRef = mDatabase.child("users").child(userId);
                                                userRef.child("email").setValue(email);
                                                userRef.child("lastName").setValue(lastName);
                                                userRef.child("firstName").setValue(firstName);
                                                userRef.child("birthdate").setValue(birthdate);
                                                userRef.child("sex").setValue(sex);
                                                userRef.child("civilStatus").setValue(civilStatus);
                                                userRef.child("contactNum").setValue(contactNum);
                                                userRef.child("address").setValue(address);


                                                Toast.makeText(SignUpPage.this, "User registered successfully. Please verify your email.",
                                                        Toast.LENGTH_SHORT).show();
                                                Intent intent = new Intent(getApplicationContext(), LoginPage.class);
                                                startActivity(intent);
                                                finish();
                                            }else{
                                                Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                                Toast.makeText(SignUpPage.this, "Authentication failed. Please try again or contact admin",
                                                        Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                    Toast.makeText(SignUpPage.this, task.getException().getMessage(),
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }


    private boolean isValidEmail(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
        });



    }
}