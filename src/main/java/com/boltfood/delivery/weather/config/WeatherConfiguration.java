package com.boltfood.delivery.weather.config;

import com.boltfood.delivery.weather.service.WeatherService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WeatherConfiguration {

    @Bean
    CommandLineRunner commandLineRunner(WeatherService weatherService){
        return _ -> weatherService.getWeather();
    }
}
