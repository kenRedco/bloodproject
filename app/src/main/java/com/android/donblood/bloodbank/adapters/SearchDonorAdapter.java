package com.android.donblood.bloodbank.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.android.donblood.bloodbank.R;
import com.android.donblood.bloodbank.viewmodels.DonorData;
import java.util.List;

public class SearchDonorAdapter extends RecyclerView.Adapter<SearchDonorAdapter.PostHolder> {

    private List<DonorData> donorList;

    public class PostHolder extends RecyclerView.ViewHolder {
        TextView nameTextView, contactTextView, addressTextView, totalDonateTextView, lastDonateTextView;

        public PostHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.donorName);
            contactTextView = itemView.findViewById(R.id.donorContact);
            addressTextView = itemView.findViewById(R.id.donorAddress);
            totalDonateTextView = itemView.findViewById(R.id.totaldonate);
            lastDonateTextView = itemView.findViewById(R.id.lastdonate);
        }
    }

    public SearchDonorAdapter(List<DonorData> donorList) {
        this.donorList = donorList;
    }

    @NonNull
    @Override
    public PostHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View listItemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.search_donor_item, parent, false);
        return new PostHolder(listItemView);
    }

    @Override
    public void onBindViewHolder(@NonNull PostHolder holder, int position) {
        DonorData currentDonor = donorList.get(position);

        // Set background color based on position
        if (position % 2 == 0) {
            holder.itemView.setBackgroundColor(Color.parseColor("#C13F31"));
        } else {
            holder.itemView.setBackgroundColor(Color.parseColor("#FFFFFF"));
        }

        holder.nameTextView.setText("Name: " + currentDonor.getName());
        holder.contactTextView.setText("Contact: " + currentDonor.getMobile());
        holder.addressTextView.setText("Address: " + currentDonor.getAddress());
        holder.totalDonateTextView.setText("Total Donation: " + currentDonor.getTotalDonate() + " times");
        holder.lastDonateTextView.setText("Last Donation: " + currentDonor.getLastDonate());
    }

    @Override
    public int getItemCount() {
        return donorList.size();
    }

    public void setDonorList(List<DonorData> donorList) {
        this.donorList = donorList;
        notifyDataSetChanged();
    }
}