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
        weatherRepository.save(new Observation(1792505700, "Tallinn-Harku", City.TALLINN, 26038, 1.0, 2.0, "Light rain"));              //observationTimestamp time = 17:15
        weatherRepository.save(new Observation(1792502100, "Tallinn-Harku", City.TALLINN, 26038, -3.0, 5.0, "Light snow shower"));      //observationTimestamp time = 16:15
        weatherRepository.save(new Observation(1792498500, "Tallinn-Harku", City.TALLINN, 26038, 0.0, 3.0, "Clear"));                   //observationTimestamp time = 15:15
        weatherRepository.save(new Observation(1792494900, "Tartu-Tõravere", City.TARTU, 26242, 2.0, 1.0, "Clear"));                    //observationTimestamp time = 14:15
    }

    @Test
    void findFirstByCity_returnsLatestObservation() {
        Optional<Observation> result = weatherRepository.findFirstByCityOrderByObservationsTimestampDesc(City.TALLINN);

        assertThat(result).isPresent();
        assertThat(result.get().getObservationsTimestamp()).isEqualTo(1792505700);
    }

    @Test
    void findFirstByCity_returnsObservationOnlyForThatCity() {
        Optional<Observation> result = weatherRepository.findFirstByCityOrderByObservationsTimestampDesc(City.TARTU);

        assertThat(result).isPresent();
        assertThat(result.get().getCity()).isEqualTo(City.TARTU);
        assertThat(result.get().getObservationsTimestamp()).isEqualTo(1792494900);
    }

    @Test
    void findFirstByCity_noObservations_returnsEmpty() {
        Optional<Observation> result = weatherRepository.findFirstByCityOrderByObservationsTimestampDesc(City.PÄRNU);

        assertThat(result).isEmpty();
    }

    @Test
    void findFirstByCityAndObservationsTimestamp_observationValidOnTimestamp() {
        Optional<Observation> result = weatherRepository.findFirstByCityAndObservationsTimestampLessThanEqualOrderByObservationsTimestampDesc(City.TALLINN, 1792501200);                 //observationTimestamp time = 16:00

        assertThat(result).isPresent();
        assertThat(result.get().getObservationsTimestamp()).isEqualTo(1792498500);
    }

}
