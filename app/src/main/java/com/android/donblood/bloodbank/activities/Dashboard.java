package com.android.donblood.bloodbank.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.android.donblood.bloodbank.R;
import com.android.donblood.bloodbank.fragments.AboutUs;
import com.android.donblood.bloodbank.fragments.AchievmentsView;
import com.android.donblood.bloodbank.fragments.BloodInfo;
import com.android.donblood.bloodbank.fragments.HomeView;
import com.android.donblood.bloodbank.fragments.NearByHospitalActivity;
import com.android.donblood.bloodbank.fragments.SearchDonorFragment;
import com.android.donblood.bloodbank.viewmodels.UserData;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Dashboard extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private FirebaseAuth mAuth;
    private TextView bloodGroupTextView;
    private TextView genderTextView;
    private TextView mobileTextView;
    private TextView addressTextView;
    private TextView divisionTextView;
    private TextView getUserName;
    private TextView getUserEmail;
    private FirebaseDatabase user_db;
    private FirebaseUser cur_user;
    private DatabaseReference userdb_ref;

    private AlertDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // Initialize TextViews
        bloodGroupTextView = findViewById(R.id.blood_group_text_view);
        genderTextView = findViewById(R.id.gender_text_view);
        mobileTextView = findViewById(R.id.mobile_text_view);
        addressTextView = findViewById(R.id.address_text_view);
        divisionTextView = findViewById(R.id.division_text_view);
        getUserName = findViewById(R.id.UserNameView);
        getUserEmail = findViewById(R.id.UserEmailView);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Loading...")
                .setMessage("Please wait...")
                .setCancelable(false);
        progressDialog = builder.create();

        mAuth = FirebaseAuth.getInstance();
        user_db = FirebaseDatabase.getInstance();
        cur_user = mAuth.getCurrentUser();
        userdb_ref = user_db.getReference("users");

        getUserEmail = findViewById(R.id.UserEmailView);
        getUserName = findViewById(R.id.UserNameView);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Dashboard.this, PostActivity.class));
            }
        });

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View header = navigationView.getHeaderView(0);

        getUserEmail = header.findViewById(R.id.UserEmailView);
        getUserName = header.findViewById(R.id.UserNameView);

        progressDialog.show();
        fetchUserData();

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentcontainer, new HomeView()).commit();
            navigationView.getMenu().getItem(0).setChecked(true);
        }
    }

    private void fetchUserData() {
        DatabaseReference singleuser = userdb_ref.child(cur_user.getUid());
        singleuser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    UserData userData = dataSnapshot.getValue(UserData.class);
                    if (userData != null) {
                        String name = userData.getName();
                        String email = userData.getEmail();
                        String bloodGroup = userData.getBloodGroup();
                        String mobile = userData.getMobile();
                        String address = userData.getAddress();
                        String division = userData.getDivision();
                        String gender = userData.getGender();

                        // Update the UI with the retrieved data
                        getUserName.setText(name);
                        getUserEmail.setText(email);
                        bloodGroupTextView.setText(bloodGroup);
                        genderTextView.setText(gender);
                        mobileTextView.setText(mobile);
                        addressTextView.setText(address);
                        divisionTextView.setText(division);
                    } else {
                        Log.e("Dashboard", "UserData object is null");
                    }
                } else {
                    Log.e("Dashboard", "DataSnapshot is null");
                    showDatabaseEmptyMessage();
                }

                if (cur_user != null) {
                    getUserEmail.setText(cur_user.getEmail());
                } else {
                    Log.e("Dashboard", "Current user is null");
                }

                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("User", databaseError.getMessage());
                progressDialog.dismiss();
            }
        });
    }

    private void showDatabaseEmptyMessage() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Database Empty")
                .setMessage("The database is empty for this user!")
                .setPositiveButton("OK", null)
                .show();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.dashboard, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.donateinfo) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentcontainer, new BloodInfo()).commit();
        }
        if (id == R.id.devinfo) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentcontainer, new AboutUs()).commit();
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.home) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentcontainer, new HomeView()).commit();

        } else if (id == R.id.userprofile) {
            startActivity(new Intent(getApplicationContext(), ProfileActivity.class));

        } else if (id == R.id.user_achiev) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentcontainer, new AchievmentsView()).commit();

        } else if (id == R.id.logout) {
            mAuth.signOut();
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.blood_storage) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentcontainer, new SearchDonorFragment()).commit();

        } else if (id == R.id.nearby_hospital) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentcontainer, new NearByHospitalActivity()).commit();

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }
}