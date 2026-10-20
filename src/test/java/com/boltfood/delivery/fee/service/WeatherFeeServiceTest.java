package com.boltfood.delivery.fee.service;

import com.boltfood.delivery.common.City;
import com.boltfood.delivery.exception.EntityNotFoundException;
import com.boltfood.delivery.exception.VehicleTypeUsageForbiddenException;
import com.boltfood.delivery.fee.model.VehicleType;
import com.boltfood.delivery.weather.model.Observation;
import com.boltfood.delivery.weather.service.WeatherService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WeatherFeeServiceTest {

    @Mock
    private WeatherService weatherService;

    @Mock
    private WindSpeedExtraFeeService windSpeedExtraFeeService;

    @Mock
    private WeatherPhenomenonExtraFeeService weatherPhenomenonExtraFeeService;

    @Mock
    private AirTemperatureExtraFeeService airTemperatureExtraFeeService;

    @InjectMocks
    private WeatherFeeService weatherFeeService;

    @ParameterizedTest
    @CsvSource({
            "BIKE, 1, 0.5, 1, 2.5",
            "SCOOTER, 1, 0.5, 1, 2.5",
    })
    void getWeatherConditionsExtraFee_returnsSumOfExtraFees(VehicleType vehicleType, String windFee, String phenomenonFee, String temperatureFee, String expectedTotal) {
        Observation observation = new Observation(1792511767, "Tartu-Tõravere", City.TARTU, 26242, -10.1, 20.0, "Light snow shower");

        when(weatherService.getObservation(City.TARTU)).thenReturn(observation);
        when(windSpeedExtraFeeService.getWsefByVehicleTypeAndWindSpeed(vehicleType, 20.0)).thenReturn(new BigDecimal(windFee));
        when(weatherPhenomenonExtraFeeService.getWpefByVehicleTypeAndPhenomenon(vehicleType, "Light snow shower")).thenReturn(new BigDecimal(phenomenonFee));
        when(airTemperatureExtraFeeService.getAtefByVehicleTypeAndAirTemperature(vehicleType, -10.1)).thenReturn(new BigDecimal(temperatureFee));

        BigDecimal result = weatherFeeService.getWeatherConditionsExtraFee(City.TARTU, vehicleType, null);

        assertThat(result).isEqualByComparingTo(expectedTotal);
        verify(weatherService).getObservation(City.TARTU);
    }

    @Test
    void getWeatherConditionsExtraFeeWhenForbidden_throwsException() {
        Observation observation = new Observation(1792511767, "Pärnu", City.PÄRNU, 26242, 5.0, 25.0, "Light snow shower");
        when(weatherService.getObservation(City.PÄRNU)).thenReturn(observation);
        when(windSpeedExtraFeeService.getWsefByVehicleTypeAndWindSpeed(VehicleType.BIKE, 25.0))
                .thenThrow(new VehicleTypeUsageForbiddenException("Usage of selected vehicle type is forbidden"));

        assertThrows(VehicleTypeUsageForbiddenException.class,
                () -> weatherFeeService.getWeatherConditionsExtraFee(City.PÄRNU, VehicleType.BIKE, null));
    }

    @Test
    void getWeatherConditionsExtraFeeWithNoWeatherData_throwsException() {
        when(weatherService.getObservation(City.TALLINN)).thenThrow(new EntityNotFoundException("No weather data found for TALLINN"));

        assertThrows(EntityNotFoundException.class,
                () -> weatherFeeService.getWeatherConditionsExtraFee(City.TALLINN, VehicleType.CAR, null));

        verifyNoInteractions(windSpeedExtraFeeService, weatherPhenomenonExtraFeeService, airTemperatureExtraFeeService);
    }
}
