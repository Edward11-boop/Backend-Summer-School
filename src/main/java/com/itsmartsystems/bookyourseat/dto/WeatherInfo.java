package com.itsmartsystems.bookyourseat.dto;

public class WeatherInfo {
    private double temperature ;
    private double windSpeed ;
    private double precipitation;
    private double snowfall;

    public WeatherInfo(double temperature, double windSpeed, double precipitation, double snowfall, int weatherCode) {
        this.temperature = temperature;
        this.windSpeed = windSpeed;
        this.precipitation = precipitation;
        this.snowfall = snowfall;
        this.weatherCode = weatherCode;
    }

    private int weatherCode;

    public int getWeatherCode() {
        return weatherCode;
    }

    public void setWeatherCode(int weatherCode) {
        this.weatherCode = weatherCode;
    }

    public double getSnowfall() {
        return snowfall;
    }

    public void setSnowfall(double snowfall) {
        this.snowfall = snowfall;
    }

    public double getPrecipitation() {
        return precipitation;
    }

    public void setPrecipitation(double precipitation) {
        this.precipitation = precipitation;
    }


    public double getWindSpeed() {
        return windSpeed;
    }

    public void setWindSpeed(double windSpeed) {
        this.windSpeed = windSpeed;
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }




}
