package com.boltfood.delivery.fee.service;

import com.boltfood.delivery.fee.model.City;
import com.boltfood.delivery.fee.model.VehicleType;
import org.springframework.stereotype.Service;

@Service
public class FeeService {

    private final RegionalBaseFeeService regionalBaseFeeService;

    public FeeService(RegionalBaseFeeService regionalBaseFeeService){
        this.regionalBaseFeeService = regionalBaseFeeService;
    }

    public double getDeliveryFee(City city, VehicleType vehicleType){
        return regionalBaseFeeService.getRbfByCityAndVehicleType(city, vehicleType);
    }
}
