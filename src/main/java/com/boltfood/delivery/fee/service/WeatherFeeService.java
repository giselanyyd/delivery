package com.boltfood.delivery.fee.service;

import com.boltfood.delivery.common.City;
import com.boltfood.delivery.exception.EntityNotFoundException;
import com.boltfood.delivery.exception.VehicleTypeUsageForbiddenException;
import com.boltfood.delivery.fee.model.VehicleType;
import com.boltfood.delivery.weather.model.Observation;
import com.boltfood.delivery.weather.service.WeatherService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class WeatherFeeService {

    private final WeatherService weatherService;
    private final WindSpeedExtraFeeService windSpeedExtraFeeService;
    private final WeatherPhenomenonExtraFeeService weatherPhenomenonExtraFeeService;
    private final AirTemperatureExtraFeeService airTemperatureExtraFeeService;

    public WeatherFeeService(WeatherService weatherService, WindSpeedExtraFeeService windSpeedExtraFeeService, WeatherPhenomenonExtraFeeService weatherPhenomenonExtraFeeService, AirTemperatureExtraFeeService airTemperatureExtraFeeService){
        this.weatherService = weatherService;
        this.windSpeedExtraFeeService = windSpeedExtraFeeService;
        this.weatherPhenomenonExtraFeeService = weatherPhenomenonExtraFeeService;
        this.airTemperatureExtraFeeService = airTemperatureExtraFeeService;
    }

    /**
     * Calculates the sum of all weather extra fees for the given city and vehicle type,
     * based on the latest observation, or the latest one at or before the timestamp.
     *
     * @param city        the city whose weather observation is used
     * @param vehicleType the courier's vehicle type
     * @param timestamp   optional point in time; if {@code null}, the latest observation is used
     * @return the sum of the air temperature, wind speed and weather phenomenon extra fees
     * @throws EntityNotFoundException            if no matching weather observation exists
     * @throws VehicleTypeUsageForbiddenException if the weather forbids the vehicle type
     */
    public BigDecimal getWeatherConditionsExtraFee(City city, VehicleType vehicleType, LocalDateTime timestamp) {

        Observation observation;
        if (timestamp == null) {
            observation = weatherService.getObservation(city);
        }
        else {
            observation = weatherService.getObservation(city, timestamp);
        }
        return windSpeedExtraFeeService.getWsefByVehicleTypeAndWindSpeed(vehicleType, observation.getWindSpeed())
                .add(weatherPhenomenonExtraFeeService.getWpefByVehicleTypeAndPhenomenon(vehicleType, observation.getWeatherPhenomenon())
                        .add(airTemperatureExtraFeeService.getAtefByVehicleTypeAndAirTemperature(vehicleType, observation.getAirTemperature())));
    }

}
