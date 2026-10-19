package com.boltfood.delivery.fee.repository;

import com.boltfood.delivery.fee.model.VehicleType;
import com.boltfood.delivery.fee.model.WindSpeedExtraFee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface WindSpeedExtraFeeRepository extends JpaRepository<WindSpeedExtraFee, UUID> {
    List<WindSpeedExtraFee> findAllByVehicleType(VehicleType vehicleType);
}
