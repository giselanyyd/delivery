package com.boltfood.delivery.fee.service;

import com.boltfood.delivery.common.City;
import com.boltfood.delivery.exception.EntityNotFoundException;
import com.boltfood.delivery.exception.InvalidTimestampException;
import com.boltfood.delivery.fee.model.VehicleType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FeeServiceTest {

    @Mock
    private RegionalBaseFeeService regionalBaseFeeService;

    @Mock
    private WeatherFeeService weatherFeeService;

    @InjectMocks
    private FeeService feeService;

    @ParameterizedTest
    @CsvSource({
            "TALLINN, CAR, 4, 1, 5",
            "TALLINN, SCOOTER, 3.5, 1, 4.5",
            "TALLINN, BIKE, 3, 1, 4",
            "TARTU, BIKE, 2.5, 1, 3.5",
            "PÄRNU, SCOOTER, 2.5, 1, 3.5"
    })
    void getDeliveryFee_returnsTotalFee(City city, VehicleType vehicleType, String baseFee, String weatherFee, String expectedTotal) {
        when(regionalBaseFeeService.getRegionalBaseFee(city, vehicleType)).thenReturn(new BigDecimal(baseFee));
        when(weatherFeeService.getWeatherConditionsExtraFee(city, vehicleType, null)).thenReturn(new BigDecimal(weatherFee));

        BigDecimal result = feeService.getDeliveryFee(city, vehicleType, null);

        assertThat(result).isEqualByComparingTo(expectedTotal);
        verify(regionalBaseFeeService).getRegionalBaseFee(city, vehicleType);
        verify(weatherFeeService).getWeatherConditionsExtraFee(city, vehicleType, null);
    }

    @Test
    void getDeliveryFeeWhenRegionalBaseFeeNotFound_throwsException() {
        when(regionalBaseFeeService.getRegionalBaseFee(City.TARTU, VehicleType.CAR))
                .thenThrow(new EntityNotFoundException("Regional base fee not found for TARTU and CAR"));

        assertThrows(EntityNotFoundException.class, () -> feeService.getDeliveryFee(City.TARTU, VehicleType.CAR, null));

        verifyNoInteractions(weatherFeeService);
    }

    @Test
    void getDeliveryFeeWithFutureTimestamp_throwsException() {
        LocalDateTime future = LocalDateTime.now(ZoneId.of("Europe/Tallinn")).plusDays(1);

        assertThrows(InvalidTimestampException.class,
                () -> feeService.getDeliveryFee(City.TALLINN, VehicleType.CAR, future));

        verifyNoInteractions(regionalBaseFeeService, weatherFeeService);
    }
}
