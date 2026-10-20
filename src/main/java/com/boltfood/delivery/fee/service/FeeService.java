package com.boltfood.delivery.fee.service;

import com.boltfood.delivery.common.City;
import com.boltfood.delivery.exception.InvalidTimestampException;
import com.boltfood.delivery.fee.model.VehicleType;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Service
public class FeeService {

    private final RegionalBaseFeeService regionalBaseFeeService;
    private final WeatherFeeService weatherFeeService;

    public FeeService(RegionalBaseFeeService regionalBaseFeeService, WeatherFeeService weatherFeeService){
        this.regionalBaseFeeService = regionalBaseFeeService;
        this.weatherFeeService = weatherFeeService;
    }

    public BigDecimal getDeliveryFee(City city, VehicleType vehicleType, LocalDateTime timestamp){
        if (timestamp != null && timestamp.isAfter(LocalDateTime.now(ZoneId.of("Europe/Tallinn")))) {
            throw new InvalidTimestampException(
                    "Timestamp " + timestamp + " can't be in the future");
        }

        return regionalBaseFeeService.getRegionalBaseFee(city, vehicleType)
                .add(weatherFeeService.getWeatherConditionsExtraFee(city, vehicleType, timestamp));
    }
}
