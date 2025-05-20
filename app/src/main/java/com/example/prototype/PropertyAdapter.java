package com.example.prototype;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PropertyAdapter extends RecyclerView.Adapter<PropertyAdapter.PropertyViewHolder>
{
    private final List<Property> userList;
    private OnInquireClickListener listener = null;

    public interface OnInquireClickListener {
        void onInquireClick(Property user);
    }
    public void setOnInquireClickListener(OnInquireClickListener listener)
    {
        this.listener = listener;
    }

    public static class PropertyViewHolder extends RecyclerView.ViewHolder {
        TextView name, price, location;
        public PropertyViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.bh_name);
            price = itemView.findViewById(R.id.bh_price);
            location = itemView.findViewById(R.id.bh_location);
        }
    }

    public PropertyAdapter(List<Property> userList) {
        this.userList = userList;
    }

    @NonNull
    @Override
    public PropertyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.property_card_layout, parent, false);
        return new PropertyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PropertyViewHolder holder, int position) {
        Property user = userList.get(position);
        holder.name.setText(user.name != null ? user.name : "No Name");
        holder.location.setText(user.location != null ? user.location : "Unknown");
    }

    @Override
    public int getItemCount() {
        return userList != null ? userList.size() : 0;
    }
}
