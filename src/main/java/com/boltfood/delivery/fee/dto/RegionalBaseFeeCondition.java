package com.boltfood.delivery.fee.dto;

import com.boltfood.delivery.common.City;
import com.boltfood.delivery.fee.model.VehicleType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class RegionalBaseFeeCondition {

    @NotNull(message = "city is mandatory")
    private City city;

    @NotNull(message = "vehicle type is mandatory")
    private VehicleType vehicleType;

    @NotNull(message = "fee is mandatory")
    @PositiveOrZero
    private BigDecimal fee;
}
