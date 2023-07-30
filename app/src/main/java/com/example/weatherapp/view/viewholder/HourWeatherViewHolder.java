package com.example.weatherapp.view.viewholder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weatherapp.R;

import java.util.ArrayList;

public class HourWeatherViewHolder extends RecyclerView.ViewHolder{
    public TextView tvTime, tvTemp;
    public ImageView imgIcon;
    public HourWeatherViewHolder(@NonNull View itemView) {
        super(itemView);
        tvTime = itemView.findViewById(R.id.tvTime);
        tvTemp = itemView.findViewById(R.id.tvTemp);
        imgIcon = itemView.findViewById(R.id.imgIcon);
    }
}
