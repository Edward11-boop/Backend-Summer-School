package com.itsmartsystems.bookyourseat.service;

import com.itsmartsystems.bookyourseat.dto.WeatherInfo;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Service
public class WeatherService {


    private final RestTemplate restTemplate;

    public WeatherService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }


    public WeatherInfo getWeather(double latitude , double longitude, LocalDateTime targetHour)
    {
        // url ul pentru open meteo
        String url = "https://api.open-meteo.com/v1/forecast?latitude="+latitude+"&longitude="+longitude+"&current=temperature_2m,wind_speed_10m&hourly=temperature_2m,relative_humidity_2m,wind_speed_10m,precipitation,snowfall,weather_code";
        // trimit cererea http pentru GET
        ResponseEntity<Map> request = restTemplate.getForEntity(url, Map.class);
        /*
             "current": {
                "temperature_2m": 2.4,
                "wind_speed_10m": 11.9
              }
         */
        // preiau json-ul din raspunsul cererii

        Map<String , Object> bodyResponse = request.getBody();
        Map<String , Object> hourly = (Map<String , Object>) bodyResponse.get("hourly");
        List<String> time = (List<String>) hourly.get("time");
        List<Double> temperature = (List<Double>) hourly.get("temperature_2m");
        List<Double> windSpeed = (List<Double>) hourly.get("wind_speed_10m");
        List<Double>precipitation = (List<Double>) hourly.get("precipitation");
        List<Double>snowfall = (List<Double>) hourly.get("snowfall");
        List<Integer> weatherCode = (List<Integer>) hourly.get("weather_code");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
        String data = targetHour.format(formatter);
        int index = time.indexOf(data);
        if (index == -1) throw new IllegalArgumentException("Invalid data !");

        Double temp = temperature.get(index);
        Double wind = windSpeed.get(index);
        Double precip = precipitation.get(index);
        Double snow = snowfall.get(index);
        Integer code = weatherCode.get(index);

        WeatherInfo weatherInfo = new WeatherInfo(temp, wind, precip, snow, code);
        return weatherInfo;


    }



}
