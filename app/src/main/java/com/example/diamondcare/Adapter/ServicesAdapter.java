package com.example.diamondcare.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.diamondcare.Model.ServicesData;
import com.example.diamondcare.Model.TimeSlotData;
import com.example.diamondcare.R;
import com.example.diamondcare.SessionsServices;

import java.util.ArrayList;

public class ServicesAdapter extends RecyclerView.Adapter<ServicesAdapter.ViewHolder> {

    private RecyclerViewClickListener listener;
    private ArrayList<ServicesData> servicesData;

    public ServicesAdapter(RecyclerViewClickListener listener, ArrayList<ServicesData> servicesData){
        this.listener = listener;
        this.servicesData = servicesData;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        ImageView serviceImage;
        TextView serviceTitle, serviceDesc, servicePrice;

        public ViewHolder(final View itemView) {
            super(itemView);
            serviceImage = itemView.findViewById(R.id.serviceImage);
            serviceTitle = itemView.findViewById(R.id.serviceTitle);
            serviceDesc = itemView.findViewById(R.id.serviceDesc);
            servicePrice = itemView.findViewById(R.id.servicePrice);
            itemView.setOnClickListener(this);
        }
        @Override
        public void onClick(View view) {listener.onRecyclerClick(view,getAdapterPosition());}
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.single_service,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String title = servicesData.get(position).getServiceTitle();
        holder.serviceTitle.setText(title);
        Integer image = servicesData.get(position).getServiceImage();
        holder.serviceImage.setImageResource(image);
        String desc = servicesData.get(position).getServiceDescription();
        holder.serviceDesc.setText(desc);
        String price = servicesData.get(position).getPrice();
        holder.servicePrice.setText(price);
    }

    @Override
    public int getItemCount() {
        return servicesData.size();
    }


    public interface RecyclerViewClickListener{
        void onRecyclerClick(View v, int position);
    }
}
