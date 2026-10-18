package com.boltfood.delivery.fee.dto;

import com.boltfood.delivery.fee.model.City;
import com.boltfood.delivery.fee.model.VehicleType;
import lombok.Data;

@Data
public class RegionalBaseFeeCondition {
    private City city;
    private VehicleType vehicleType;
    private double fee;
}
