package com.boltfood.delivery.fee.model;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Entity
public class WindSpeedExtraFee {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Enumerated(EnumType.STRING)
    private VehicleType vehicleType;

    private Double minValue;

    private Double maxValue;

    private BigDecimal fee;

    private boolean errorIsThrown;

}
