package com.android.donblood.bloodbank.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.donblood.bloodbank.R;
import com.android.donblood.bloodbank.viewmodels.UserData;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

public class PostActivity extends AppCompatActivity {

    AlertDialog progressDialog;

    EditText text1, text2;
    // Replace Spinner with a more appropriate view (e.g., RecyclerView, ListView)
    Button btnpost;

    FirebaseDatabase fdb;
    DatabaseReference db_ref;
    FirebaseAuth mAuth;

    Calendar cal;
    String uid;
    String Time, Date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Loading...")
                .setMessage("Please wait...")
                .setCancelable(false);
        progressDialog = builder.create();

        getSupportActionBar().setTitle("Post Blood Request");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        text1 = findViewById(R.id.getMobile);
        text2 = findViewById(R.id.getLocation);

        // Replace Spinner with a more appropriate view (e.g., RecyclerView, ListView)

        btnpost = findViewById(R.id.postbtn);

        cal = Calendar.getInstance();

        int day = cal.get(Calendar.DAY_OF_MONTH);
        int month = cal.get(Calendar.MONTH);
        int year = cal.get(Calendar.YEAR);
        int hour = cal.get(Calendar.HOUR);
        int min = cal.get(Calendar.MINUTE);
        month += 1;
        Time = "";
        Date = "";
        String ampm = "AM";

        if (cal.get(Calendar.AM_PM) == 1) {
            ampm = "PM";
        }

        if (hour < 10) {
            Time += "0";
        }
        Time += hour;
        Time += ":";

        if (min < 10) {
            Time += "0";
        }

        Time += min;
        Time += (" " + ampm);

        Date = day + "/" + month + "/" + year;

        FirebaseUser cur_user = FirebaseAuth.getInstance().getCurrentUser();

        if (cur_user == null) {
            startActivity(new Intent(PostActivity.this, LoginActivity.class));
        } else {
            uid = cur_user.getUid();
        }

        mAuth = FirebaseAuth.getInstance();
        fdb = FirebaseDatabase.getInstance();
        db_ref = fdb.getReference("posts");

        try {
            btnpost.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    progressDialog.show();
                    final Query findUserData = fdb.getReference("users").child(uid);

                    if (text1.getText().length() == 0) {
                        Toast.makeText(getApplicationContext(), "Enter your contact number!",
                                Toast.LENGTH_LONG).show();
                    } else if (text2.getText().length() == 0) {
                        Toast.makeText(getApplicationContext(), "Enter your location!",
                                Toast.LENGTH_LONG).show();
                    } else {
                        findUserData.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    UserData userData = dataSnapshot.getValue(UserData.class);
                                    db_ref.child(uid).child("Name").setValue(userData.getName());
                                    db_ref.child(uid).child("Contact").setValue(text1.getText().toString());
                                    db_ref.child(uid).child("Address").setValue(text2.getText().toString());
                                    db_ref.child(uid).child("BloodGroup").setValue(userData.getBloodGroup());
                                    db_ref.child(uid).child("Division").setValue(userData.getDivision());
                                    db_ref.child(uid).child("Time").setValue(Time);
                                    db_ref.child(uid).child("Date").setValue(Date);
                                    Toast.makeText(PostActivity.this, "Your post has been created successfully",
                                            Toast.LENGTH_LONG).show();
                                    startActivity(new Intent(PostActivity.this, Dashboard.class));
                                } else {
                                    Toast.makeText(getApplicationContext(), "Database error occurred.",
                                            Toast.LENGTH_LONG).show();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Log.d("User", databaseError.getMessage());
                            }
                        });
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        progressDialog.dismiss();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}