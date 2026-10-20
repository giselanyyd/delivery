package com.boltfood.delivery.weather.repository;

import com.boltfood.delivery.common.City;
import com.boltfood.delivery.weather.model.Observation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface WeatherRepository extends JpaRepository<Observation, UUID> {
    Optional<Observation> findFirstByCityOrderByObservationsTimestampDesc(City city);
    Optional<Observation> findFirstByCityAndObservationsTimestampLessThanEqualOrderByObservationsTimestampDesc(City city, long dateInSeconds);
}
