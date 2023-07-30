package com.example.weatherapp.network;

public class WeatherModel {
    String time;
    String temp;
    String imgIcon;

    public WeatherModel(String time, String temp, String imgIcon) {
        this.time = time;
        this.temp = temp;
        this.imgIcon = imgIcon;
    }


    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTemp() {
        return temp;
    }

    public void setTemp(String temp) {
        this.temp = temp;
    }

    public String getImgIcon() {
        return imgIcon;
    }

    public void setImgIcon(String imgIcon) {
        this.imgIcon = imgIcon;
    }
}
