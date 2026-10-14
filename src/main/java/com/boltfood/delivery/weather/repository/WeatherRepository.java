package com.boltfood.delivery.weather.repository;

import com.boltfood.delivery.weather.model.Observation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface WeatherRepository extends JpaRepository<Observation, UUID> {
}
