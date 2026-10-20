package com.boltfood.delivery.weather.job;

import com.boltfood.delivery.weather.service.WeatherService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GetWeatherJob {

    private final WeatherService weatherService;

    /**
     * Imports weather data on the schedule set by {@code get-weather.job.cron}.
     */
    @Scheduled(cron = "${get-weather.job.cron}")
    public void getWeather(){
        weatherService.getWeather();
    }
}
