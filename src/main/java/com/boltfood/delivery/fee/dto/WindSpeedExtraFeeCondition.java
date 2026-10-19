package com.boltfood.delivery.fee.dto;

import com.boltfood.delivery.fee.model.VehicleType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class WindSpeedExtraFeeCondition {

    @NotNull(message = "vehicle type is mandatory")
    private VehicleType vehicleType;

    private Double minValue;

    private Double maxValue;

    @NotNull(message = "fee is mandatory")
    @PositiveOrZero
    private BigDecimal fee;

    private boolean errorIsThrown;

}
