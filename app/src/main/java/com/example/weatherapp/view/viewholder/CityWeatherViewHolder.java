package com.example.weatherapp.view.viewholder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weatherapp.R;

public class CityWeatherViewHolder extends RecyclerView.ViewHolder {
    public ImageView imgIcon;
    public TextView tvTemp, tvCity;
    public CityWeatherViewHolder(@NonNull View itemView) {
        super(itemView);
        imgIcon = itemView.findViewById(R.id.imgIcon);
        tvCity = itemView.findViewById(R.id.tvCity);
        tvTemp = itemView.findViewById(R.id.tvTemp);
    }
}
