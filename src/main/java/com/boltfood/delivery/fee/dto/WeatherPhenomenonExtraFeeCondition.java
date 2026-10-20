package com.boltfood.delivery.fee.dto;

import com.boltfood.delivery.fee.model.VehicleType;
import com.boltfood.delivery.fee.model.Phenomenon;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class WeatherPhenomenonExtraFeeCondition {

    @NotNull(message = "vehicle type is mandatory")
    private VehicleType vehicleType;

    @NotNull(message = "phenomenon is mandatory")
    private Phenomenon phenomenon;

    @NotNull(message = "fee is mandatory")
    @PositiveOrZero
    private BigDecimal fee;

    private boolean errorIsThrown;

}
