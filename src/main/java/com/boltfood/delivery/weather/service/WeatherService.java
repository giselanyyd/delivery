package com.boltfood.delivery.weather.service;

import com.boltfood.delivery.exception.EntityNotFoundException;
import com.boltfood.delivery.common.City;
import com.boltfood.delivery.weather.api.WeatherApi;
import com.boltfood.delivery.weather.model.Observation;
import com.boltfood.delivery.weather.repository.WeatherRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.*;

@Slf4j
@Service
public class WeatherService {

    private final WeatherRepository weatherRepository;
    private final WeatherApi weatherApi;

    public WeatherService(WeatherRepository weatherRepository, WeatherApi weatherApi) {
        this.weatherRepository = weatherRepository;
        this.weatherApi = weatherApi;
    }

    public void getWeather(){
        List<Observation> observations = weatherApi.getWeatherFromApi();
        weatherRepository.saveAll(observations);
    }

    public Observation getObservation(City city){
        return weatherRepository.findFirstByCityOrderByObservationsTimestampDesc(city)
                .orElseThrow(() -> new EntityNotFoundException("No weather data found for " + city));
    }
}
