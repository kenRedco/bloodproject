package com.android.donblood.bloodbank.viewmodels;

/***
 Project Name: BloodBank
 Project Date: 10/12/18
 Created by: imshakil
 Email: mhshakil_ice_iu@yahoo.com
 ***/

public class UserData {

    private String Name, Email, Contact, Address, Gender, BloodGroup, Division;

    public UserData() {
        // Option 1: Simulate missing data can be removed if you have actual data
        // this.Name = "";
        // this.Email = "";
        // this.Contact = "";
    }

    public String getContact() {
        return Contact;
    }

    public void setContact(String contact) {
        Contact = contact;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        this.Address = address;
    }

    public String getDivision() {
        return Division;
    }

    public void setDivision(String division) {
        this.Division = division;
    }

    public String getName() {
        return Name;
    }

    public String getBloodGroup() {
        return BloodGroup;
    }

    public void setBloodGroup(String bloodGroup) {
        this.BloodGroup = bloodGroup;
    }

    public String getEmail() {
        return Email;
    }

    public String getGender() {
        return Gender;
    }

    public void setName(String name) {
        this.Name = name;
    }

    public void setEmail(String email) {
        this.Email = email;
    }

    public void setGender(String gender) {
        this.Gender = gender;
    }

    // Added method to retrieve the mobile number
    public String getMobile() {
        return getContact(); // Assuming mobile number is stored in the "Contact" field
    }
}
