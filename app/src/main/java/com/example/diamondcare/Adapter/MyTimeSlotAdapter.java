package com.example.diamondcare.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.diamondcare.Model.TimeSlotData;
import com.example.diamondcare.R;

import java.util.ArrayList;

public class MyTimeSlotAdapter extends RecyclerView.Adapter<MyTimeSlotAdapter.ViewHolder>{
    private ArrayList<TimeSlotData> timeSlotData;
    private RecyclerViewClickListener listener;

    //Horários
    public MyTimeSlotAdapter(ArrayList<TimeSlotData> timeSlotData, RecyclerViewClickListener listener){
        this.timeSlotData = timeSlotData;
        this.listener = listener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView dateTxt;

        public ViewHolder(final View view){
            super(view);
            dateTxt = view.findViewById(R.id.txt_time_slot);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            listener.onRecyclerClick(view,getAdapterPosition());
        }
    }

    @NonNull
    @Override
    public MyTimeSlotAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.time_slot,parent,false);
       return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyTimeSlotAdapter.ViewHolder holder, int position) {
        String date = timeSlotData.get(position).getHour();
        holder.dateTxt.setText(date);
    }

    @Override
    public int getItemCount() {
        return timeSlotData.size();
    }

    public interface RecyclerViewClickListener{
        void onRecyclerClick(View v, int position);
    }
}
