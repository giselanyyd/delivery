package com.boltfood.delivery.fee.repository;

import com.boltfood.delivery.fee.model.VehicleType;
import com.boltfood.delivery.fee.model.AirTemperatureExtraFee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AirTemperatureExtraFeeRepository extends JpaRepository<AirTemperatureExtraFee, UUID> {
    List<AirTemperatureExtraFee> findAllByVehicleType(VehicleType vehicleType);
}
