package com.example.trinetra;

import android.os.Bundle;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;
public class userprofile extends AppCompatActivity {

    private FirebaseUser user;
    private DatabaseReference reference;
    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.userprofile);

        user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("users");

        userID = user.getUid();
        final EditText nameEditText = (EditText) findViewById(R.id.usernameEditText);
        final EditText emailEditText = (EditText) findViewById(R.id.emailEditText);
        final EditText passwordEditText = (EditText) findViewById(R.id.passwordEditText);
        final EditText dobEditText = (EditText) findViewById(R.id.dateOfBirthEditText);
        final EditText genEditText = (EditText) findViewById(R.id.genderEditText);


        reference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                    User userprofile = snapshot.getValue(User.class);

                    if (userprofile != null) {
                        String username = userprofile.name;
                        String email = userprofile.email;
                        String password = userprofile.password;
                        String dob = userprofile.dob;
                        String gender = userprofile.gender;

//                        nameEditText.setText(stringusername);
                        emailEditText.setText(email);
                        passwordEditText.setText((password));
                        dobEditText.setText(dob);
                        genEditText.setText(gender);
                    }
            }
                @Override
                public void onCancelled (@NotNull DatabaseError error){
                }
        });
    }
    public class User {
        private String name;
        private String email;
        private String password;
        private String dob;
        private String gender;


        public User() {
        }

        public User(String name, String email,String password, String dob, String gender) {
            this.name = name;
            this.email = email;
            this.password = password;
            this.dob = dob;
            this.gender = gender;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPassword(){ return password; }

        public void setPassword(String password){ this.password = password; }

        public String getDob() {
            return dob;
        }

        public void setDob(String dob) {
            this.dob = dob;
        }

        public String getGender() {
            return gender;
        }

        public void setGender(String gender) {
            this.gender = gender;
        }

    }
}