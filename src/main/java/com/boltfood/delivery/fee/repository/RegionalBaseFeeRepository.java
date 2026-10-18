package com.boltfood.delivery.fee.repository;

import com.boltfood.delivery.fee.model.City;
import com.boltfood.delivery.fee.model.RegionalBaseFee;
import com.boltfood.delivery.fee.model.VehicleType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RegionalBaseFeeRepository extends JpaRepository<RegionalBaseFee,UUID> {
    RegionalBaseFee findByCityAndVehicleType(City city, VehicleType vehicleType);

}
