package com.boltfood.delivery.fee.repository;

import com.boltfood.delivery.fee.model.VehicleType;
import com.boltfood.delivery.fee.model.Phenomenon;
import com.boltfood.delivery.fee.model.WeatherPhenomenonExtraFee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface WeatherPhenomenonExtraFeeRepository extends JpaRepository<WeatherPhenomenonExtraFee, UUID> {
    Optional<WeatherPhenomenonExtraFee> findByVehicleTypeAndPhenomenon(VehicleType vehicleType, Phenomenon phenomenon);
}
