package com.boltfood.delivery.fee.model;

import com.boltfood.delivery.common.City;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Entity
public class RegionalBaseFee {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Enumerated(EnumType.STRING)
    private City city;
    @Enumerated(EnumType.STRING)
    private VehicleType vehicleType;
    private BigDecimal fee;
}
