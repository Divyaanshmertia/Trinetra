package com.example.trinetra;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.trinetra.model.Incident;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class CurrentIncidentsFragment extends Fragment {

    private DatabaseReference databaseReference;
    private RecyclerView recyclerView;
    private List<Incident> incidentList;
    private IncidentAdapter incidentAdapter;

    public CurrentIncidentsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_current_incidents, container, false);

        databaseReference = FirebaseDatabase.getInstance().getReference("incidents");
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        incidentList = new ArrayList<>();
        incidentAdapter = new IncidentAdapter(incidentList, requireContext());
        recyclerView.setAdapter(incidentAdapter);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                incidentList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Incident incident = snapshot.getValue(Incident.class);
                    incidentList.add(incident);
                }
                incidentAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), "Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }
}