package com.boltfood.delivery.fee.service;

import com.boltfood.delivery.common.City;
import com.boltfood.delivery.fee.model.VehicleType;
import com.boltfood.delivery.weather.model.Observation;
import com.boltfood.delivery.weather.service.WeatherService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

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

    public BigDecimal getWeatherConditionsExtraFee(City city, VehicleType vehicleType) {

        Observation observation = weatherService.getObservation(city);
        return windSpeedExtraFeeService.getWsefByVehicleTypeAndWindSpeed(vehicleType, observation.getWindSpeed())
                .add(weatherPhenomenonExtraFeeService.getWpefByVehicleTypeAndPhenomenon(vehicleType, observation.getWeatherPhenomenon())
                        .add(airTemperatureExtraFeeService.getAtefByVehicleTypeAndAirTemperature(vehicleType, observation.getAirTemperature())));
    }

}
