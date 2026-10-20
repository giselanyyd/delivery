package com.boltfood.delivery.fee.service;

import com.boltfood.delivery.exception.VehicleTypeUsageForbiddenException;
import com.boltfood.delivery.fee.model.VehicleType;
import com.boltfood.delivery.fee.model.WindSpeedExtraFee;
import com.boltfood.delivery.fee.repository.WindSpeedExtraFeeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WindSpeedExtraFeeServiceTest {

    @Mock
    private WindSpeedExtraFeeRepository windSpeedExtraFeeRepository;

    @InjectMocks
    private WindSpeedExtraFeeService windSpeedExtraFeeService;

    @ParameterizedTest
    @CsvSource({
            "SCOOTER, 10, 0.5",
            "SCOOTER, 20, 0.5",
            "SCOOTER, 9.9, 0",
            "BIKE, 10, 0.5",
            "BIKE, 20, 0.5",
            "BIKE, 9.9, 0",
    })

    void getWsef_returnsCorrectFee(VehicleType vehicleType, double windSpeed, String expectedFee) {

        WindSpeedExtraFee windyCondition = new WindSpeedExtraFee();
        windyCondition.setVehicleType(vehicleType);
        windyCondition.setMinValue(10.0);
        windyCondition.setMaxValue(20.0);
        windyCondition.setFee(BigDecimal.valueOf(0.5));

        WindSpeedExtraFee forbiddenCondition = new WindSpeedExtraFee();
        forbiddenCondition.setVehicleType(vehicleType);
        forbiddenCondition.setMinValue(20.0);
        forbiddenCondition.setFee(BigDecimal.valueOf(0));
        forbiddenCondition.setErrorIsThrown(true);

        List<WindSpeedExtraFee> conditions = new ArrayList<>();
        conditions.add(windyCondition);
        conditions.add(forbiddenCondition);

        when(windSpeedExtraFeeRepository.findAllByVehicleType(vehicleType)).thenReturn(conditions);

        BigDecimal result = windSpeedExtraFeeService.getWsefByVehicleTypeAndWindSpeed(vehicleType, windSpeed);

        assertThat(result).isEqualByComparingTo(expectedFee);
        verify(windSpeedExtraFeeRepository).findAllByVehicleType(vehicleType);
    }

    @Test
    void getWsefWithExtremeConditions_throwsException() {

        WindSpeedExtraFee forbiddenCondition = new WindSpeedExtraFee();
        forbiddenCondition.setVehicleType(VehicleType.BIKE);
        forbiddenCondition.setMinValue(20.0);
        forbiddenCondition.setFee(BigDecimal.valueOf(0));
        forbiddenCondition.setErrorIsThrown(true);

        List<WindSpeedExtraFee> conditions = new ArrayList<>();
        conditions.add(forbiddenCondition);

        when(windSpeedExtraFeeRepository.findAllByVehicleType(VehicleType.BIKE)).thenReturn(conditions);

        assertThrows(VehicleTypeUsageForbiddenException.class,
                () -> windSpeedExtraFeeService.getWsefByVehicleTypeAndWindSpeed(VehicleType.BIKE, 25.0));
    }

    @Test
    void getWsefWhenThereAreNoConditions_returnsZero() {
        when(windSpeedExtraFeeRepository.findAllByVehicleType(VehicleType.SCOOTER)).thenReturn(new ArrayList<>());

        BigDecimal result = windSpeedExtraFeeService.getWsefByVehicleTypeAndWindSpeed(VehicleType.SCOOTER, 25.0);

        assertThat(result).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    void getWsefWithInvalidInput_returnsZero() {
        BigDecimal result = windSpeedExtraFeeService.getWsefByVehicleTypeAndWindSpeed(VehicleType.SCOOTER, null);

        assertThat(result).isEqualByComparingTo(BigDecimal.ZERO);
        verifyNoInteractions(windSpeedExtraFeeRepository);
    }

}
