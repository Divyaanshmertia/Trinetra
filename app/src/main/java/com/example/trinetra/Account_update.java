package com.example.trinetra;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.trinetra.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import org.jetbrains.annotations.NotNull;

public class Account_update extends Fragment {

    private FirebaseUser user;
    private DatabaseReference reference;
    private String userID;
    private Button save_changes_button;
    private EditText userName;
    private EditText userEmail;
    private EditText userPassword;
    private EditText userDob;
    private EditText userGender;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account_update, container, false);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference().child("users");
        String userId = mAuth.getCurrentUser().getUid();

        userName = view.findViewById(R.id.username_edittext);
        userEmail = view.findViewById(R.id.useremail_edittext);
        userPassword = view.findViewById(R.id.userpassword_edittext);
        userGender = view.findViewById(R.id.usergender_edittext);
        userDob = view.findViewById(R.id.userDOB_edittext);

        Button save_changes_button = view.findViewById(R.id.save_button);
        save_changes_button.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            String newUsername = userName.getText().toString();
            String newEmail = userEmail.getText().toString();
            String newPassword = userPassword.getText().toString();
            String newGender = userGender.getText().toString();
            String newDOB = userDob.getText().toString();

            myRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()){
                        User ui = dataSnapshot.getValue(User.class);
                        if(ui != null){
                            // Update the user object with the new data
                            ui.setName(newUsername);
                            ui.setEmail(newEmail);
                            ui.setPassword(newPassword);
                            ui.setDob(newDOB);
                            ui.setGender(newGender);

                            myRef.child("users").child(userId).setValue(ui);
                            Toast.makeText(getContext(), "Users data updated successfully!!", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                @Override
                public void onCancelled (@NotNull DatabaseError error){
                }
            });
        }
    });
        return view;
    }
}