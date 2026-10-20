package com.boltfood.delivery.weather.repository;

import com.boltfood.delivery.common.City;
import com.boltfood.delivery.weather.model.Observation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class WeatherRepositoryTest {

    @Autowired
    private WeatherRepository weatherRepository;

    @BeforeEach
    void setUp() {
        weatherRepository.save(new Observation(1792511770, "Tallinn-Harku", City.TALLINN, 26038, 1.0, 2.0, "Light rain"));
        weatherRepository.save(new Observation(1792511769, "Tallinn-Harku", City.TALLINN, 26038, -3.0, 5.0, "Light snow shower"));
        weatherRepository.save(new Observation(1792511768, "Tallinn-Harku", City.TALLINN, 26038, 0.0, 3.0, "Clear"));
        weatherRepository.save(new Observation(1792511767, "Tartu-Tõravere", City.TARTU, 26242, 2.0, 1.0, "Clear"));
    }

    @Test
    void findFirstByCity_returnsLatestObservation() {
        Optional<Observation> result = weatherRepository.findFirstByCityOrderByObservationsTimestampDesc(City.TALLINN);

        assertThat(result).isPresent();
        assertThat(result.get().getObservationsTimestamp()).isEqualTo(1792511770);
    }

    @Test
    void findFirstByCity_returnsObservationOnlyForThatCity() {
        Optional<Observation> result = weatherRepository.findFirstByCityOrderByObservationsTimestampDesc(City.TARTU);

        assertThat(result).isPresent();
        assertThat(result.get().getCity()).isEqualTo(City.TARTU);
        assertThat(result.get().getObservationsTimestamp()).isEqualTo(1792511767);
    }

    @Test
    void findFirstByCity_noObservations_returnsEmpty() {
        Optional<Observation> result = weatherRepository.findFirstByCityOrderByObservationsTimestampDesc(City.PÄRNU);

        assertThat(result).isEmpty();
    }
}
