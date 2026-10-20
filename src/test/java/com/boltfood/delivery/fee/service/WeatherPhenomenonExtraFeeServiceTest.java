package com.boltfood.delivery.fee.service;

import com.boltfood.delivery.exception.VehicleTypeUsageForbiddenException;
import com.boltfood.delivery.fee.model.VehicleType;
import com.boltfood.delivery.fee.model.WeatherPhenomenonExtraFee;
import com.boltfood.delivery.fee.repository.WeatherPhenomenonExtraFeeRepository;
import com.boltfood.delivery.fee.model.Phenomenon;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WeatherPhenomenonExtraFeeServiceTest {

    @Mock
    private WeatherPhenomenonExtraFeeRepository weatherPhenomenonExtraFeeRepository;

    @InjectMocks
    private WeatherPhenomenonExtraFeeService weatherPhenomenonExtraFeeService;

    @ParameterizedTest
    @CsvSource({
            "SCOOTER, Light snow shower, SNOW, 1",
            "SCOOTER, Moderate sleet, SLEET, 1",
            "SCOOTER, Heavy rain, RAIN, 0.5",
            "BIKE, Heavy snowfall, SNOW, 1",
            "BIKE, Light sleet, SLEET, 1",
            "BIKE, Light shower, RAIN, 0.5"
    })
    void getWpef_returnsCorrectFee(VehicleType vehicleType, String phenomenonText, Phenomenon phenomenon, String fee) {

        WeatherPhenomenonExtraFee condition = new WeatherPhenomenonExtraFee();
        condition.setVehicleType(vehicleType);
        condition.setPhenomenon(phenomenon);
        condition.setFee(new BigDecimal(fee));

        when(weatherPhenomenonExtraFeeRepository.findByVehicleTypeAndPhenomenon(vehicleType, phenomenon)).thenReturn(Optional.of(condition));

        BigDecimal result = weatherPhenomenonExtraFeeService.getWpefByVehicleTypeAndPhenomenon(vehicleType, phenomenonText);

        assertThat(result).isEqualByComparingTo(fee);
        verify(weatherPhenomenonExtraFeeRepository).findByVehicleTypeAndPhenomenon(vehicleType, phenomenon);
    }

    @ParameterizedTest
    @CsvSource({
            "SCOOTER, Glaze, GLAZE",
            "SCOOTER, Hail, HAIL",
            "SCOOTER, Thunder, THUNDER",
            "BIKE, Glaze, GLAZE",
            "BIKE, Hail, HAIL",
            "BIKE, Thunder, THUNDER"
    })
    void getWpefWithExtremeConditions_throwsException(VehicleType vehicleType, String phenomenonText, Phenomenon phenomenon) {

        WeatherPhenomenonExtraFee condition = new WeatherPhenomenonExtraFee();
        condition.setVehicleType(vehicleType);
        condition.setPhenomenon(phenomenon);
        condition.setFee(BigDecimal.valueOf(0));
        condition.setErrorIsThrown(true);

        when(weatherPhenomenonExtraFeeRepository.findByVehicleTypeAndPhenomenon(vehicleType, phenomenon)).thenReturn(Optional.of(condition));

        assertThrows(VehicleTypeUsageForbiddenException.class,
                () -> weatherPhenomenonExtraFeeService.getWpefByVehicleTypeAndPhenomenon(vehicleType, phenomenonText));
    }

    @ParameterizedTest
    @EnumSource(VehicleType.class)
    void getWpefWithInvalidInput_returnsZero(VehicleType vehicleType) {
        BigDecimal result = weatherPhenomenonExtraFeeService.getWpefByVehicleTypeAndPhenomenon(vehicleType, "Clear");

        assertThat(result).isEqualByComparingTo(BigDecimal.ZERO);
        verifyNoInteractions(weatherPhenomenonExtraFeeRepository);
    }

}
