package com.boltfood.delivery.weather.model;

import com.boltfood.delivery.common.City;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Observation {

    @Id
    @GeneratedValue (strategy = GenerationType.UUID)
    private UUID id;

    private long observationsTimestamp;

    private String stationName;

    @Enumerated(EnumType.STRING)
    private City city;

    private Integer stationWmoCode;

    private Double airTemperature;

    private Double windSpeed;

    private String weatherPhenomenon;

    public Observation(
            long observationsTimestamp,
            String stationName,
            City city,
            Integer stationWmoCode,
            Double airTemperature,
            Double windSpeed,
            String weatherPhenomenon
    ) {
        this.observationsTimestamp = observationsTimestamp;
        this.stationName = stationName;
        this.city = city;
        this.stationWmoCode = stationWmoCode;
        this.airTemperature = airTemperature;
        this.windSpeed = windSpeed;
        this.weatherPhenomenon = weatherPhenomenon;
    }
}
