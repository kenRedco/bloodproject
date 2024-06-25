package com.android.donblood.bloodbank.viewmodels;

public class DonorData {
    private int TotalDonate;
    private String LastDonate, Name, Mobile, UID, Address;

    public DonorData() {
        // Default constructor
    }

    public DonorData(int totalDonate, String lastDonate, String name, String mobile, String address, String UID) {
        this.TotalDonate = totalDonate;
        this.LastDonate = lastDonate;
        this.Name = name;
        this.Mobile = mobile;
        this.Address = address;
        this.UID = UID;
    }

    public int getTotalDonate() {
        return TotalDonate;
    }

    public void setTotalDonate(int totalDonate) {
        this.TotalDonate = totalDonate;
    }

    public String getLastDonate() {
        return LastDonate;
    }

    public void setLastDonate(String lastDonate) {
        this.LastDonate = lastDonate;
    }

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        this.Name = name;
    }

    public String getMobile() {
        return Mobile;
    }

    public void setMobile(String mobile) {
        this.Mobile = mobile;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        this.Address = address;
    }
}