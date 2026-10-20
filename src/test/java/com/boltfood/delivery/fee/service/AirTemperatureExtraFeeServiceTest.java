package com.boltfood.delivery.fee.service;

import com.boltfood.delivery.fee.model.AirTemperatureExtraFee;
import com.boltfood.delivery.fee.model.VehicleType;
import com.boltfood.delivery.fee.repository.AirTemperatureExtraFeeRepository;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AirTemperatureExtraFeeServiceTest {

    @Mock
    private AirTemperatureExtraFeeRepository airTemperatureExtraFeeRepository;

    @InjectMocks
    private AirTemperatureExtraFeeService airTemperatureExtraFeeService;

    @ParameterizedTest
    @CsvSource({
            "SCOOTER, -10.1, 1",
            "SCOOTER, -10, 0.5",
            "SCOOTER, 0, 0.5",
            "SCOOTER, 0.1, 0",
            "BIKE, -10.1, 1",
            "BIKE, -10, 0.5",
            "BIKE, 0, 0.5",
            "BIKE, 0.1, 0"

    })
    void getAtef_returnsCorrectFee(VehicleType vehicleType, double airTemperature, String expectedFee) {

        AirTemperatureExtraFee badCondition = new AirTemperatureExtraFee();
        badCondition.setVehicleType(vehicleType);
        badCondition.setMaxValue(-10.0);
        badCondition.setFee(BigDecimal.valueOf(1));

        AirTemperatureExtraFee unpleasantCondition = new AirTemperatureExtraFee();
        unpleasantCondition.setVehicleType(vehicleType);
        unpleasantCondition.setMinValue(-10.0);
        unpleasantCondition.setMaxValue(0.0);
        unpleasantCondition.setFee(BigDecimal.valueOf(0.5));

        List<AirTemperatureExtraFee> conditions = new ArrayList<>();
        conditions.add(badCondition);
        conditions.add(unpleasantCondition);

        when(airTemperatureExtraFeeRepository.findAllByVehicleType(vehicleType)).thenReturn(conditions);

        BigDecimal result = airTemperatureExtraFeeService.getAtefByVehicleTypeAndAirTemperature(vehicleType, airTemperature);

        assertThat(result).isEqualByComparingTo(expectedFee);
        verify(airTemperatureExtraFeeRepository).findAllByVehicleType(vehicleType);
    }

    @Test
    void getAtefWhenNoConditions_returnsZero() {
        when(airTemperatureExtraFeeRepository.findAllByVehicleType(VehicleType.CAR)).thenReturn(new ArrayList<>());

        BigDecimal result = airTemperatureExtraFeeService.getAtefByVehicleTypeAndAirTemperature(VehicleType.CAR, -20.0);

        assertThat(result).isEqualByComparingTo(BigDecimal.ZERO);
    }

}
