package com.boltfood.delivery.fee.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

import java.util.UUID;

@Data
@Entity
public class DeliveryFee {
    @Id
    private UUID  id;
    private double TotalCost;
}
