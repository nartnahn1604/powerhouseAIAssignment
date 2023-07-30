package com.example.weatherapp.view.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weatherapp.view.viewholder.HourWeatherViewHolder;
import com.example.weatherapp.R;
import com.example.weatherapp.network.WeatherModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class HourWeatherAdapter extends RecyclerView.Adapter<HourWeatherViewHolder> {
    private final ArrayList<WeatherModel> weatherModelArrayList;

    public HourWeatherAdapter(ArrayList<WeatherModel> weatherModelArrayList) {
        this.weatherModelArrayList = weatherModelArrayList;
    }

    @NonNull
    @Override
    public HourWeatherViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.item_hour_weather, parent, false);
        return new HourWeatherViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HourWeatherViewHolder holder, int position) {
        final WeatherModel weatherModel = this.weatherModelArrayList.get(position);

        holder.tvTime.setText(weatherModel.getTime());
        holder.tvTemp.setText(weatherModel.getTemp());

        String iconUrl = "http://openweathermap.org/img/w/" +  weatherModel.getImgIcon() + ".png";
        Picasso.get().load(iconUrl).into(holder.imgIcon);
    }

    @Override
    public int getItemCount() {
        return weatherModelArrayList.size();
    }
}
