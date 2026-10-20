package com.boltfood.delivery.fee.controller;
import com.boltfood.delivery.common.City;
import com.boltfood.delivery.fee.model.VehicleType;
import com.boltfood.delivery.fee.service.FeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/api/v1/fee")
public class FeeController {

    private final FeeService feeService;

    public FeeController(FeeService feeService){
        this.feeService = feeService;
    }

    /**
     * Returns the total delivery fee for the given city and vehicle type.
     *
     * @param city        the delivery city
     * @param vehicleType the courier's vehicle type
     * @param timestamp   optional point in time to use weather data from;
     *                    if omitted, the latest weather data is used
     * @return the total delivery fee
     */
    @GetMapping
    public BigDecimal getDeliveryFee(@RequestParam City city, @RequestParam VehicleType vehicleType, @RequestParam(required = false) LocalDateTime timestamp){
        return feeService.getDeliveryFee(city, vehicleType, timestamp);
    }
}
