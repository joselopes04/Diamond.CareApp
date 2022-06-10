package com.example.diamondcare.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.diamondcare.Model.Barber;
import com.example.diamondcare.R;

import java.security.AccessControlContext;
import java.util.List;

public class BarberAdapter extends RecyclerView.Adapter<BarberAdapter.ViewHolder> {

    //ainda não funciona

    Context context;
    List<Barber> barberList;

    public BarberAdapter(Context context, List<Barber> barberList) {
        this.context = context;
        this.barberList = barberList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.layout_barber,viewGroup,false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int i) {
        holder.txt_barber_name.setText(barberList.get(i).getName());
    }

    @Override
    public int getItemCount() {
        return barberList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView txt_barber_name;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txt_barber_name = (TextView)itemView.findViewById(R.id.txt_barber_name);

        }
    }
}
