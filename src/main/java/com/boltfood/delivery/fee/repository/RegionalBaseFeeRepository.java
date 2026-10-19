package com.boltfood.delivery.fee.repository;

import com.boltfood.delivery.common.City;
import com.boltfood.delivery.fee.model.RegionalBaseFee;
import com.boltfood.delivery.fee.model.VehicleType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface RegionalBaseFeeRepository extends JpaRepository<RegionalBaseFee,UUID> {
    Optional<RegionalBaseFee> findByCityAndVehicleType(City city, VehicleType vehicleType);
}
