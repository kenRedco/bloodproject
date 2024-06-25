package com.android.donblood.bloodbank.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.donblood.bloodbank.R;
import com.android.donblood.bloodbank.activities.Dashboard;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

public class AchievmentsView extends Fragment {

    private int curDay, curMonth, curYear, day, month, year, totday;
    private Calendar calendar;
    private DatabaseReference dbRef, userRef;
    private FirebaseAuth mAuth;

    private TextView totalDonate, lastDonate, nextDonate, donateInfo;

    private String[] bloodgroup, divisionlist;
    private String lastDate;

    private Map<String, Integer> bloodGroupMap;
    private Map<String, Integer> divisionMap;

    private View view;
    private Button yes;
    private LinearLayout yesno;

    public AchievmentsView() {
        // Empty constructor
        bloodGroupMap = new HashMap<>();
        divisionMap = new HashMap<>();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.user_achievment_fragment, container, false);

        bloodgroup = getResources().getStringArray(R.array.Blood_Group);
        divisionlist = getResources().getStringArray(R.array.division_list);
        for (int i = 0; i < bloodgroup.length; i++) {
            bloodGroupMap.put(bloodgroup[i], i);
        }
        for (int i = 0; i < divisionlist.length; i++) {
            divisionMap.put(divisionlist[i], i);
        }

        lastDonate = view.findViewById(R.id.setLastDonate);
        totalDonate = view.findViewById(R.id.settotalDonate);
        donateInfo = view.findViewById(R.id.donateInfo);

        getActivity().setTitle("Achievements");
        mAuth = FirebaseAuth.getInstance();
        lastDate = "";

        dbRef = FirebaseDatabase.getInstance().getReference("donors");
        userRef = FirebaseDatabase.getInstance().getReference("users");

        // Call the new updateUI() method to initialize UI elements
        updateUI();

        return view;
    }

    // New method to update UI elements
    private void updateUI() {
        Query userQuery = userRef.child(mAuth.getCurrentUser().getUid());

        userQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    final UserData userData = dataSnapshot.getValue(UserData.class);
                    final int getDivisionIndex = divisionMap.get(userData.getDivision());
                    final int getBloodGroupIndex = bloodGroupMap.get(userData.getBloodGroup());

                    final Query donorQuery = dbRef.child(divisionlist[getDivisionIndex])
                            .child(bloodgroup[getBloodGroupIndex])
                            .child(mAuth.getCurrentUser().getUid());

                    donorQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                final DonorData donorData = dataSnapshot.getValue(DonorData.class);
                                totalDonate.setText(donorData.getTotalDonate() + " times");
                                if (donorData.getTotalDonate() == 0) {
                                    lastDate = "01/01/2001";
                                    lastDonate.setText("Do not donate yet!");
                                } else {
                                    lastDate = donorData.getLastDonate();
                                    lastDonate.setText(donorData.getLastDonate());
                                }
                                totday = 0;
                                nextDonate = view.findViewById(R.id.nextDonate);
                                yesno = view.findViewById(R.id.yesnolayout);
                                if (!lastDate.isEmpty()) {
                                    int cnt = 0;
                                    int tot = 0;
                                    for (int i = 0; i < lastDate.length(); i++) {
                                        if (cnt == 0 && lastDate.charAt(i) == '/') {
                                            day = tot;
                                            tot = 0;
                                            cnt += 1;
                                        } else if (cnt == 1 && lastDate.charAt(i) == '/') {
                                            cnt += 1;
                                            month = tot;
                                            tot = 0;
                                        } else {
                                            tot = tot * 10 + (lastDate.charAt(i) - '0');
                                        }
                                    }
                                    year = tot;
                                    calendar = Calendar.getInstance(TimeZone.getDefault());
                                    curDay = calendar.get(Calendar.DAY_OF_MONTH);
                                    curMonth = calendar.get(Calendar.MONTH) + 1;
                                    curYear = calendar.get(Calendar.YEAR);

                                    if (day > curDay) {
                                        curDay += 30;
                                        curMonth -= 1;
                                    }
                                    totday += (curDay - day);

                                    if (month > curMonth) {
                                        curMonth += 12;
                                        curYear -= 1;
                                    }
                                    totday += ((curMonth - month) * 30);

                                    totday += ((curYear - year) * 365);

                                    if (totday > 120) {
                                        donateInfo.setText("Have you donated today?");
                                        nextDonate.setVisibility(View.GONE);
                                        yesno.setVisibility(View.VISIBLE);

                                        yes = view.findViewById(R.id.btnYes);
                                        // Call the new handleYesButtonClick() method
                                        handleYesButtonClick(getDivisionIndex, getBloodGroupIndex, donorData);
                                    } else {
                                        donateInfo.setText("Next donation available in:");
                                        yesno.setVisibility(View.GONE);
                                        nextDonate.setVisibility(View.VISIBLE);
                                        nextDonate.setText((120 - totday) + " days");
                                    }
                                } else {
                                    LinearLayout linearLayout = view.findViewById(R.id.donorAchiev);
                                    linearLayout.setVisibility(View.GONE);
                                    TextView tv = view.findViewById(R.id.ShowInof);
                                    tv.setVisibility(View.VISIBLE);
                                    Toast.makeText(getActivity(), "Update your profile to be a donor first.", Toast.LENGTH_LONG)
                                            .show();
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            // Handle database error
                        }
                    });
                } else {
                    Toast.makeText(getActivity(), "You are not a user.", Toast.LENGTH_LONG)
                            .show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("User", databaseError.getMessage());
            }
        });
    }

    // New method to handle the "Yes" button click
    private void handleYesButtonClick(final int getDivisionIndex, final int getBloodGroupIndex, final DonorData donorData) {
        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendar = Calendar.getInstance(TimeZone.getDefault());
                curDay = calendar.get(Calendar.DAY_OF_MONTH);
                curMonth = calendar.get(Calendar.MONTH) + 1;
                curYear = calendar.get(Calendar.YEAR);

                donorData.setLastDonate(curDay + "/" + curMonth + "/" + curYear);
                donorData.setTotalDonate(donorData.getTotalDonate() + 1);

                updateDonorDataInDatabase(getDivisionIndex, getBloodGroupIndex, donorData);
            }
        });
    }

    // New method to update donor data in the database
    private void updateDonorDataInDatabase(final int getDivisionIndex, final int getBloodGroupIndex, final DonorData donorData) {
        dbRef.child(divisionlist[getDivisionIndex])
                .child(bloodgroup[getBloodGroupIndex])
                .child(mAuth.getCurrentUser().getUid())
                .setValue(donorData, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                        if (databaseError == null) {
                            // Update successful
                            updateUI(); // Refresh UI after successful database update
                        } else {
                            // Handle database error
                        }
                    }
                });
    }

    // Define the UserData class
    public static class UserData {
        private String division;
        private String bloodGroup;

        public UserData() {
            // Default constructor required for calls to DataSnapshot.getValue(UserData.class)
        }

        public UserData(String division, String bloodGroup) {
            this.division = division;
            this.bloodGroup = bloodGroup;
        }

        public String getDivision() {
            return division;
        }

        public String getBloodGroup() {
            return bloodGroup;
        }
    }

    // Inside DonorData class
    public static class DonorData {
        private int totalDonate;
        private String lastDonate;

        public DonorData() {
            // Default constructor required for calls to DataSnapshot.getValue(DonorData.class)
        }

        public DonorData(int totalDonate, String lastDonate) {
            this.totalDonate = totalDonate;
            this.lastDonate = lastDonate;
        }

        public int getTotalDonate() {
            return totalDonate;
        }

        public String getLastDonate() {
            return lastDonate;
        }

        public void setTotalDonate(int totalDonate) {
            this.totalDonate = totalDonate;
        }

        public void setLastDonate(String lastDonate) {
            this.lastDonate = lastDonate;
        }
    }
}
