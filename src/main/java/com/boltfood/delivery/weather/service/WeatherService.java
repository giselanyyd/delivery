package com.boltfood.delivery.weather.service;

import com.boltfood.delivery.exception.EntityNotFoundException;
import com.boltfood.delivery.common.City;
import com.boltfood.delivery.exception.WeatherApiException;
import com.boltfood.delivery.weather.api.WeatherApi;
import com.boltfood.delivery.weather.model.Observation;
import com.boltfood.delivery.weather.repository.WeatherRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
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

    /**
     * Fetches the latest weather observations and saves them to the database.
     *
     * @throws WeatherApiException if the data can't be fetched or parsed
     */
    public void getWeather(){
        List<Observation> observations = weatherApi.getWeatherFromApi();
        weatherRepository.saveAll(observations);
    }

    /**
     * Returns the most recent weather observation for the given city.
     *
     * @param city the city
     * @return the latest observation
     * @throws EntityNotFoundException if no observation exists for the city
     */
    public Observation getObservation(City city){
        return weatherRepository.findFirstByCityOrderByObservationsTimestampDesc(city)
                .orElseThrow(() -> new EntityNotFoundException("No weather data found for " + city));
    }

    /**
     * Returns the most recent weather observation for the given city
     * at or before the given time.
     *
     * @param city      the city
     * @param timestamp the point in time, interpreted in the Europe/Tallinn time zone
     * @return the latest observation at or before the timestamp
     * @throws EntityNotFoundException if no such observation exists
     */
    public Observation getObservation(City city, LocalDateTime timestamp){

        long dateInSeconds = timestamp.atZone(ZoneId.of("Europe/Tallinn")).toEpochSecond();

        return weatherRepository.findFirstByCityAndObservationsTimestampLessThanEqualOrderByObservationsTimestampDesc(city, dateInSeconds)
                .orElseThrow(() -> new EntityNotFoundException("No weather data found for " + city + " at " + timestamp));
    }
}
