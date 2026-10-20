package com.boltfood.delivery.fee.service;

import com.boltfood.delivery.common.City;
import com.boltfood.delivery.exception.EntityNotFoundException;
import com.boltfood.delivery.exception.InvalidTimestampException;
import com.boltfood.delivery.exception.VehicleTypeUsageForbiddenException;
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

    /**
     * Calculates the total delivery fee for the given city and vehicle type.
     * The total is the regional base fee plus the weather extra fees
     * (air temperature, wind speed and weather phenomenon).
     *
     * @param city        the delivery city
     * @param vehicleType the courier's vehicle type
     * @param timestamp   optional point in time (Europe/Tallinn) to use weather data from;
     *                    if {@code null}, the latest observation is used
     * @return the total delivery fee
     * @throws InvalidTimestampException          if the timestamp is in the future
     * @throws EntityNotFoundException            if no base fee or weather observation is found
     * @throws VehicleTypeUsageForbiddenException if the weather forbids the vehicle type
     */
    public BigDecimal getDeliveryFee(City city, VehicleType vehicleType, LocalDateTime timestamp){
        if (timestamp != null && timestamp.isAfter(LocalDateTime.now(ZoneId.of("Europe/Tallinn")))) {
            throw new InvalidTimestampException(
                    "Timestamp " + timestamp + " can't be in the future");
        }

        return regionalBaseFeeService.getRegionalBaseFee(city, vehicleType)
                .add(weatherFeeService.getWeatherConditionsExtraFee(city, vehicleType, timestamp));
    }
}
