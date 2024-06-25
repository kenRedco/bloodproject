package com.android.donblood.bloodbank.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.donblood.bloodbank.R;
import com.android.donblood.bloodbank.adapters.SearchDonorAdapter;
import com.android.donblood.bloodbank.viewmodels.DonorData;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SearchDonorFragment extends Fragment {

    private View view;
    private FirebaseAuth mAuth;
    private FirebaseUser fuser;
    private FirebaseDatabase fdb;
    private DatabaseReference dbRef;
    private Spinner bloodGroup, division;
    private Button btnSearch;
    private List<DonorData> donorItem;
    private RecyclerView recyclerView;
    private SearchDonorAdapter sdadapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.search_donor_fragment, container, false);
        mAuth = FirebaseAuth.getInstance();
        fuser = mAuth.getCurrentUser();
        fdb = FirebaseDatabase.getInstance();
        dbRef = fdb.getReference("donors");
        bloodGroup = view.findViewById(R.id.btngetBloodGroup);
        division = view.findViewById(R.id.btngetDivison);
        btnSearch = view.findViewById(R.id.btnSearch);
        getActivity().setTitle("Find Blood Donor");

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                donorItem = new ArrayList<>();
                sdadapter = new SearchDonorAdapter(donorItem);
                recyclerView = view.findViewById(R.id.showDonorList);
                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                recyclerView.setItemAnimator(new DefaultItemAnimator());
                recyclerView.addItemDecoration(new DividerItemDecoration(requireActivity(), LinearLayoutManager.VERTICAL));
                recyclerView.setAdapter(sdadapter);

                Query query = dbRef.child(division.getSelectedItem().toString())
                        .child(bloodGroup.getSelectedItem().toString());
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot singleItem : dataSnapshot.getChildren()) {
                                DonorData donorData = singleItem.getValue(DonorData.class);
                                donorItem.add(donorData);
                                sdadapter.notifyDataSetChanged();
                            }
                        } else {
                            Toast.makeText(getActivity(), "Database is empty now!", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.d("User", databaseError.getMessage());
                    }
                });
            }
        });

        return view;
    }
}
