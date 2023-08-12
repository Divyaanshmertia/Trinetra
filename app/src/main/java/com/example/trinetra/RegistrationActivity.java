package com.example.trinetra;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.trinetra.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class RegistrationActivity extends AppCompatActivity {
    private EditText mNameEditText, mEmailEditText, mPasswordEditText,mConfirmPasswordEditText, mDOBEditText;
    private TextView login;
    private ImageView logo;
    private Button mRegisterButton;
    private String selectedGender;


    private FirebaseAuth mAuth;
    private DatabaseReference mUsersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registration_activity);


        // Define the list of gender options
        String[] genderOptions = {"Male", "Female", "Other"};

// Get the reference to the gender dropdown menu
        Spinner genderSpinner = findViewById(R.id.gender_spinner);

// Create an adapter for the dropdown menu
        ArrayAdapter<String> genderAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, genderOptions);

// Set the adapter to the dropdown menu
        genderSpinner.setAdapter(genderAdapter);



        genderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Get the selected gender
                 selectedGender = parent.getItemAtPosition(position).toString();



            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do something when nothing is selected

            }
        });


        // Initialize Firebase components
        mAuth = FirebaseAuth.getInstance();
        mUsersRef = FirebaseDatabase.getInstance().getReference().child("users");

        // Initialize views
        mNameEditText = findViewById(R.id.name);
        mEmailEditText = findViewById(R.id.email);
        mPasswordEditText = findViewById(R.id.password);
        mDOBEditText = findViewById(R.id.DOB);
        mConfirmPasswordEditText = findViewById(R.id.confirmPassword);
        mRegisterButton = findViewById(R.id.register);

        // goto login
        login = findViewById(R.id.login);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegistrationActivity.this, LoginActivity.class));
            }
        });

        // click on logo to refresh
        logo = findViewById(R.id.logo);
        logo.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = getIntent();
                finish();
                startActivity(intent);
            }
        });

        // Set click listener for register button
        mRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get user input from EditTexts
                String name = mNameEditText.getText().toString().trim();
                String email = mEmailEditText.getText().toString().trim();
                String password = mPasswordEditText.getText().toString().trim();
                String dob = mDOBEditText.getText().toString();
                String confirmPassword = mConfirmPasswordEditText.getText().toString().trim();
                String gender = selectedGender;


                // Validate input fields
                if (TextUtils.isEmpty(name)) {
                    mNameEditText.setError("Name is required");
                    return;
                }

                if (TextUtils.isEmpty(email)) {
                    mEmailEditText.setError("Email is required");
                    return;
                }

                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    mEmailEditText.setError("Invalid email format");
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    mPasswordEditText.setError("Password is required");
                    return;
                }

                if (password.length() < 6) {
                    mPasswordEditText.setError("Password should be at least 6 characters long");
                    return;
                }
                if (!password.equals( confirmPassword)) {
                    mConfirmPasswordEditText.setError("Password Mismatch!");
                    return;
                }


                if (TextUtils.isEmpty(dob)) {
                    mDOBEditText.setError("Date of birth is required");
                    return;
                }


                // Parse the date string and format it using ISO 8601 format
                SimpleDateFormat inputFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                String formattedDate = null;
                try {
                    Date date = inputFormat.parse(dob);
                    SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
                    formattedDate = outputFormat.format(date);
                    // Store the formatted date in Firebase
                } catch (ParseException e) {
                    e.printStackTrace();
                    // Handle the parse exception here
                }


                // Create a new user object


                User newUser = new User(name, email, formattedDate,gender);

                // Register the user with Firebase Authentication
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // User registration successful, proceed to save user data in Realtime Database
                                    FirebaseUser firebaseUser = mAuth.getCurrentUser();
                                    String userId = firebaseUser.getUid();

                                    mUsersRef.child(userId).setValue(newUser)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        // User data saved successfully
                                                        Toast.makeText(RegistrationActivity.this, "User registration successful", Toast.LENGTH_SHORT).show();
                                                        finish();
                                                    } else {
                                                        // Failed to save user data
                                                        Toast.makeText(RegistrationActivity.this, "Failed to save user data", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                } else {
                                    // User registration failed
                                    Toast.makeText(RegistrationActivity.this, "User registration failed", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }
}