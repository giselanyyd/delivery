package com.boltfood.delivery.fee.service;

import com.boltfood.delivery.fee.model.City;
import com.boltfood.delivery.fee.model.VehicleType;
import com.boltfood.delivery.fee.model.DeliveryFee;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class FeeService {
    public DeliveryFee getDeliveryFee(City city, VehicleType vehicleType){
        DeliveryFee fee = new DeliveryFee();
        fee.setId(UUID.randomUUID());
        fee.setTotalCost(100.00);
        return fee;
    }

}
