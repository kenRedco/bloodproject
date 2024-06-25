package com.android.donblood.bloodbank.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.donblood.bloodbank.R;
import com.android.donblood.bloodbank.viewmodels.UserData;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class ProfileActivity extends AppCompatActivity {

    private EditText inputemail, inputpassword, retypePassword, fullName, address, contact;
    private FirebaseAuth mAuth;
    private Button btnSignup;
    private ProgressDialog pd;
    private Spinner gender, bloodgroup, division;

    private boolean isUpdate = false;

    private DatabaseReference db_ref, donor_ref;
    private FirebaseDatabase db_User;
    private CheckBox isDonor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pd = new ProgressDialog(this);
        pd.setMessage("Loading...");
        pd.setCancelable(true);
        pd.setCanceledOnTouchOutside(false);
        pd.show();
        setContentView(R.layout.activity_profile);

        db_User = FirebaseDatabase.getInstance();
        db_ref = db_User.getReference("users");
        donor_ref = db_User.getReference("donors");
        mAuth = FirebaseAuth.getInstance();

        inputemail = findViewById(R.id.input_userEmail);
        inputpassword = findViewById(R.id.input_password);
        retypePassword = findViewById(R.id.input_password_confirm);
        fullName = findViewById(R.id.input_fullName);
        gender = findViewById(R.id.gender);
        address = findViewById(R.id.inputAddress);
        division = findViewById(R.id.inputDivision);
        bloodgroup = findViewById(R.id.inputBloodGroup);
        contact = findViewById(R.id.inputMobile);
        isDonor = findViewById(R.id.checkbox);

        btnSignup = findViewById(R.id.button_register);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (mAuth.getCurrentUser() != null) {

            inputemail.setVisibility(View.GONE);
            inputpassword.setVisibility(View.GONE);
            retypePassword.setVisibility(View.GONE);
            btnSignup.setText("Update Profile");
            pd.dismiss();
            getSupportActionBar().setTitle("Profile");
            findViewById(R.id.image_logo).setVisibility(View.GONE);
            isUpdate = true;

            Query Profile = db_ref.child(mAuth.getCurrentUser().getUid());
            Profile.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                    UserData userData = dataSnapshot.getValue(UserData.class);

                    if (userData != null) {
                        pd.show();
                        fullName.setText(userData.getName());
                        gender.setSelection(getIndex(gender, userData.getGender()));
                        address.setText(userData.getAddress());
                        contact.setText(userData.getMobile());
                        bloodgroup.setSelection(getIndex(bloodgroup, userData.getBloodGroup()));
                        division.setSelection(getIndex(division, userData.getDivision()));
                        String divisionValue = division.getSelectedItem().toString();
                        String bloodGroupValue = bloodgroup.getSelectedItem().toString();
                        Query donor = donor_ref.child(divisionValue)
                                .child(bloodGroupValue)
                                .child(mAuth.getCurrentUser().getUid());

                        donor.addListenerForSingleValueEvent(new ValueEventListener() {

                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                if(dataSnapshot.exists())
                                {
                                    isDonor.setChecked(true);
                                    isDonor.setText("Unmark this to leave from donors");
                                }
                                else
                                {
                                    Toast.makeText(ProfileActivity.this, "Your are not a donor! Be a donor and save life by donating blood.",
                                            Toast.LENGTH_LONG).show();
                                }
                                pd.dismiss();

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Log.d("User", databaseError.getMessage());
                            }

                        });
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.d("User", databaseError.getMessage());
                }
            });


        } else pd.dismiss();
        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = inputemail.getText().toString();
                final String password = inputpassword.getText().toString();
                final String ConfirmPassword = retypePassword.getText().toString();
                final String Name = fullName.getText().toString();
                final String genderValue = gender.getSelectedItem().toString();
                final String Contact = contact.getText().toString();
                final String bloodGroupValue = bloodgroup.getSelectedItem().toString();
                final String Address = address.getText().toString();
                final String divisionValue = division.getSelectedItem().toString();

                try {

                    if (Name.length() <= 2) {
                        ShowError("Name");
                        fullName.requestFocusFromTouch();
                    } else if (Contact.length() < 10) {
                        ShowError("Contact Number");
                        contact.requestFocusFromTouch();
                    } else if (Address.length() <= 2) {
                        ShowError("Address");
                        address.requestFocusFromTouch();
                    } else {
                        if (!isUpdate) {
                            if (email.length() == 0) {
                                ShowError("Email ID");
                                inputemail.requestFocusFromTouch();
                            } else if (password.length() <= 5) {
                                ShowError("Password");
                                inputpassword.requestFocusFromTouch();
                            } else if (password.compareTo(ConfirmPassword) != 0) {
                                Toast.makeText(ProfileActivity.this, "Password did not match!", Toast.LENGTH_LONG)
                                        .show();
                                retypePassword.requestFocusFromTouch();
                            } else {
                                pd.show();
                                mAuth.createUserWithEmailAndPassword(email, password)
                                        .addOnCompleteListener(ProfileActivity.this, new OnCompleteListener<AuthResult>() {
                                            @Override
                                            public void onComplete(@NonNull Task<AuthResult> task) {

                                                if (!task.isSuccessful()) {
                                                    Toast.makeText(ProfileActivity.this, "Registration failed! try agian.", Toast.LENGTH_LONG)
                                                            .show();
                                                    Log.v("error", task.getException().getMessage());
                                                } else {
                                                    String id = mAuth.getCurrentUser().getUid();
                                                    db_ref.child(id).child("name").setValue(Name);
                                                    db_ref.child(id).child("gender").setValue(genderValue);
                                                    db_ref.child(id).child("mobile").setValue(Contact);
                                                    db_ref.child(id).child("bloodGroup").setValue(bloodGroupValue);
                                                    db_ref.child(id).child("address").setValue(Address);
                                                    db_ref.child(id).child("division").setValue(divisionValue);

                                                    if(isDonor.isChecked())
                                                    {
                                                        donor_ref.child(divisionValue).child(bloodGroupValue).child(id).child("uid").setValue(id);
                                                        donor_ref.child(divisionValue).child(bloodGroupValue).child(id).child("lastDonate").setValue("Don't donate yet!");
                                                        donor_ref.child(divisionValue).child(bloodGroupValue).child(id).child("totalDonate").setValue(0);
                                                        donor_ref.child(divisionValue).child(bloodGroupValue).child(id).child("name").setValue(Name);
                                                        donor_ref.child(divisionValue).child(bloodGroupValue).child(id).child("mobile").setValue(Contact);
                                                        donor_ref.child(divisionValue).child(bloodGroupValue).child(id).child("address").setValue(Address);

                                                    }


                                                    Toast.makeText(getApplicationContext(), "Welcome, your account has been created!", Toast.LENGTH_LONG)
                                                            .show();
                                                    Intent intent = new Intent(ProfileActivity.this, Dashboard.class);
                                                    startActivity(intent);

                                                    finish();
                                                }
                                                pd.dismiss();

                                            }

                                        });
                            }

                        } else {

                            String id = mAuth.getCurrentUser().getUid();
                            db_ref.child(id).child("name").setValue(Name);
                            db_ref.child(id).child("gender").setValue(genderValue);
                            db_ref.child(id).child("mobile").setValue(Contact);
                            db_ref.child(id).child("bloodGroup").setValue(bloodGroupValue);
                            db_ref.child(id).child("address").setValue(Address);
                            db_ref.child(id).child("division").setValue(divisionValue);

                            if(isDonor.isChecked())
                            {
                                donor_ref.child(divisionValue).child(bloodGroupValue).child(id).child("uid").setValue(id);
                                donor_ref.child(divisionValue).child(bloodGroupValue).child(id).child("lastDonate").setValue("Don't donate yet!");
                                donor_ref.child(divisionValue).child(bloodGroupValue).child(id).child("totalDonate").setValue(0);
                                donor_ref.child(divisionValue).child(bloodGroupValue).child(id).child("name").setValue(Name);
                                donor_ref.child(divisionValue).child(bloodGroupValue).child(id).child("mobile").setValue(Contact);
                                donor_ref.child(divisionValue).child(bloodGroupValue).child(id).child("address").setValue(Address);

                            }
                            else
                            {

                                donor_ref.child(divisionValue).child(bloodGroupValue).child(id).removeValue();

                            }
                            Toast.makeText(getApplicationContext(), "Your account has been updated!", Toast.LENGTH_LONG)
                                    .show();
                            Intent intent = new Intent(ProfileActivity.this, Dashboard.class);
                            startActivity(intent);
                            finish();
                        }
                        pd.dismiss();

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
    }

    private int getIndex(Spinner spinner, String value) {
        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(value)) {
                return i;
            }
        }
        return 0; // Return the default index if the value is not found
    }

    private void ShowError(String error) {

        Toast.makeText(ProfileActivity.this, "Please, Enter a valid "+error,
                Toast.LENGTH_LONG).show();
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