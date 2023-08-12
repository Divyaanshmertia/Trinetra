package com.example.trinetra;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.trinetra.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import org.jetbrains.annotations.NotNull;

public class AccountFragment extends Fragment {

    private FirebaseUser user;
    private DatabaseReference reference;
    private String userID;
    private Button modify_button;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account, container, false);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference().child("users");
        String userId = mAuth.getCurrentUser().getUid();

        myRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    User ui = dataSnapshot.getValue(User.class);
                    if(ui != null){
                        TextView username = view.findViewById(R.id.username_textview);
                        username.setText(ui.getName());

                        TextView useremail = view.findViewById(R.id.useremail_textview);
                        useremail.setText(ui.getEmail());

                        TextView userpassword = view.findViewById(R.id.userpassword_textview);
                        userpassword.setText(ui.getPassword());

                        TextView userdob = view.findViewById(R.id.userDOB_textview);
                        userdob.setText(ui.getDob());

                        TextView usergender = view.findViewById(R.id.usergender_textview);
                        usergender.setText(ui.getGender());
                    }
                }
            }
            @Override
            public void onCancelled (@NotNull DatabaseError error){
            }
        });
        Button modify_button = view.findViewById(R.id.update_button);

        modify_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment newFragment = new Account_update();

                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container, newFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });
        return view;
    }
}