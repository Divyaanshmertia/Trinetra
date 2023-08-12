package com.example.trinetra;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
TextView register;
    FirebaseAuth mAuth;



    EditText email, password;
Button login;
ImageView logo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        mAuth=FirebaseAuth.getInstance();
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        register = findViewById(R.id.register);
        login = findViewById(R.id.login);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, RegistrationActivity.class));
            }
        });
        logo = findViewById(R.id.logo);
        logo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = getIntent();
                finish();
                startActivity(intent);

            }
        });
        login.setOnClickListener(v ->{
            String userEmail = email.getText().toString().trim();
            String userPassword = password.getText().toString().trim();
            //          Email and password validation
            if(userEmail.isEmpty())
            {
                email.setError("Email is empty");
                email.requestFocus();
                return;
            }
            if(!Patterns.EMAIL_ADDRESS.matcher(userEmail).matches())
            {
                email.setError("Enter the valid email");
                email.requestFocus();
                return;
            }
            if(userPassword.isEmpty())
            {
                password.setError("Password is empty");
                password.requestFocus();
                return;
            }
            if(userEmail.length()<6)
            {
                password.setError("Length of password is more than 6");
                password.requestFocus();
                return;
            }

            //            Functionality of signing in with email and password
            mAuth.signInWithEmailAndPassword(userEmail,userPassword)
                    .addOnCompleteListener(task -> {
                        if(task.isSuccessful())
                        {
                            Toast.makeText(LoginActivity.this,
                                    "Success!!",
                                    Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(LoginActivity.this, HomePage.class));
                        }
                        else
                        {
                            Toast.makeText(LoginActivity.this,
                                    "Please Check Your login Credentials",
                                    Toast.LENGTH_SHORT).show();
                        }

                    });
        });
        }

    }
