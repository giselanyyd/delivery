package com.boltfood.delivery.fee.service;

import com.boltfood.delivery.common.City;
import com.boltfood.delivery.fee.model.VehicleType;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class FeeService {

    private final RegionalBaseFeeService regionalBaseFeeService;
    private final WeatherFeeService weatherFeeService;

    public FeeService(RegionalBaseFeeService regionalBaseFeeService, WeatherFeeService weatherFeeService){
        this.regionalBaseFeeService = regionalBaseFeeService;
        this.weatherFeeService = weatherFeeService;
    }

    public BigDecimal getDeliveryFee(City city, VehicleType vehicleType){
        return regionalBaseFeeService.getRegionalBaseFee(city, vehicleType)
                .add(weatherFeeService.getWeatherConditionsExtraFee(city, vehicleType));
    }
}
