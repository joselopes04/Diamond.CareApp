package com.example.diamondcare.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.diamondcare.Model.ShowAppointment;
import com.example.diamondcare.R;

import java.util.ArrayList;

public class MySessionsAdapter extends RecyclerView.Adapter<MySessionsAdapter.ViewHolder> {

    Context context;
    ArrayList<ShowAppointment> list;

    public MySessionsAdapter(Context context, ArrayList<ShowAppointment> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.single_sessions, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        ShowAppointment appointment = list.get(position);
        holder.date.setText(appointment.getDate());
        holder.hour.setText(appointment.getHour());

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends  RecyclerView.ViewHolder{

        TextView date, hour;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            date = itemView.findViewById(R.id.txt_my_date);
            hour = itemView.findViewById(R.id.txt_my_hour);
        }
    }

}
