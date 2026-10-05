package com.itsmartsystems.bookyourseat.controller;

import com.itsmartsystems.bookyourseat.dto.RoutesOption;
import com.itsmartsystems.bookyourseat.dto.WeatherInfo;
import com.itsmartsystems.bookyourseat.service.AiAssistantService;
import com.itsmartsystems.bookyourseat.service.TrafficService;
import com.itsmartsystems.bookyourseat.service.WeatherService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
public class TrafficController {

    private final TrafficService trafficService;
    private final WeatherService weatherService;
    private final AiAssistantService aiAssistantService;

    public TrafficController(TrafficService trafficService, WeatherService weatherService, AiAssistantService aiAssistantService) {
        this.trafficService = trafficService;
        this.weatherService = weatherService;
        this.aiAssistantService = aiAssistantService;
    }

    @GetMapping("/traffic-routes")
    public List<RoutesOption> getRoutes(@RequestParam double lat, @RequestParam double lng, @RequestParam(defaultValue = "DRIVE") String metodaDeplasare) {
        return trafficService.getTrafficRoutes(lat, lng, metodaDeplasare);
    }

    @GetMapping("/weather-test")
    public WeatherInfo getWeatherInfo(@RequestParam double latitude, @RequestParam double longitude, @RequestParam String targetHour) {
        LocalDateTime dateTime = LocalDateTime.parse(targetHour);
        return weatherService.getWeather(latitude, longitude, dateTime);
    }

    @GetMapping("/recommendation")
    public String getRecommendation(@RequestParam double lat, @RequestParam double lng, @RequestParam String targetHour , @RequestParam String metodaDeplasare) {
        LocalDateTime dateTime = LocalDateTime.parse(targetHour);
        return aiAssistantService.getRecommendation(lat, lng, dateTime , metodaDeplasare);
    }

    @GetMapping("/route-url")
    public String getRouteUrl(@RequestParam double lat , @RequestParam double lng , @RequestParam String targetHour , @RequestParam String metodaDeplasare)
    {
        LocalDateTime dateTime = LocalDateTime.parse(targetHour);
        return aiAssistantService.createURL(lat , lng , dateTime , metodaDeplasare);
    }
}
