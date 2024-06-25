package com.android.donblood.bloodbank.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.donblood.bloodbank.R;
import com.android.donblood.bloodbank.adapters.BloodRequestAdapter;
import com.android.donblood.bloodbank.viewmodels.CustomUserData;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/***
 Project Name: BloodBank
 Project Date: 10/12/18
 Created by: imshakil
 Email: mhshakil_ice_iu@yahoo.com
 ***/

public class HomeView extends Fragment {

    private View view;
    private RecyclerView recentPosts;

    private DatabaseReference donorRef;
    private FirebaseAuth mAuth;
    private BloodRequestAdapter restAdapter;
    private List<CustomUserData> postLists;

    public HomeView() {
        // Empty constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.home_view_fragment, container, false);
        recentPosts = view.findViewById(R.id.recyleposts);



        recentPosts.setLayoutManager(new LinearLayoutManager(getContext()));

        donorRef = FirebaseDatabase.getInstance().getReference();
        postLists = new ArrayList<>();

        mAuth = FirebaseAuth.getInstance();
        getActivity().setTitle("Blood Point");


        restAdapter = new BloodRequestAdapter(postLists);

        RecyclerView.LayoutManager pmLayout = new LinearLayoutManager(getContext());
        recentPosts.setLayoutManager(pmLayout);
        recentPosts.setItemAnimator(new DefaultItemAnimator());
        recentPosts.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));
        recentPosts.setAdapter(restAdapter);

        addPosts();
        return view;
    }

    private void addPosts() {
        Query allPosts = donorRef.child("posts");
        allPosts.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot singlePost : dataSnapshot.getChildren()) {
                        CustomUserData customUserData = singlePost.getValue(CustomUserData.class);
                        postLists.add(customUserData);

                        restAdapter.notifyDataSetChanged();
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
}
