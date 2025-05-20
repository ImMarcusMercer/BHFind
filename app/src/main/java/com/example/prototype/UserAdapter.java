package com.example.prototype;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder>
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

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView name, price, location;
        Button inquireBtn;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.bh_name);
            price = itemView.findViewById(R.id.bh_price);
            location = itemView.findViewById(R.id.bh_location);
            inquireBtn = itemView.findViewById(R.id.inquire);
        }
    }

    public UserAdapter(List<Property> userList) {
        this.userList = userList;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.user_card_layout, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        Property user = userList.get(position);
        holder.name.setText(user.name != null ? user.name : "No Name");
        holder.location.setText(user.location != null ? user.location : "Unknown");

        holder.inquireBtn.setOnClickListener(v -> {
            if (listener != null) {
                listener.onInquireClick(user);
            }
        });
    }

    @Override
    public int getItemCount() {
        return userList != null ? userList.size() : 0;
    }
}
