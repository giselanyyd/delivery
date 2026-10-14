package com.boltfood.delivery.weather.model;

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

    private String stationName;

    private String stationWmoCode;

    private String airTemperature;

    private String windSpeed;

    private String weatherPhenomenon;

    public Observation(
            String stationName,
            String stationWmoCode,
            String airTemperature,
            String windSpeed,
            String weatherPhenomenon
    ) {
        this.stationName = stationName;
        this.stationWmoCode = stationWmoCode;
        this.airTemperature = airTemperature;
        this.windSpeed = windSpeed;
        this.weatherPhenomenon = weatherPhenomenon;
    }
}
