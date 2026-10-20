package com.boltfood.delivery.fee.service;

import com.boltfood.delivery.common.City;
import com.boltfood.delivery.exception.EntityNotFoundException;
import com.boltfood.delivery.fee.dto.RegionalBaseFeeCondition;
import com.boltfood.delivery.fee.model.RegionalBaseFee;
import com.boltfood.delivery.fee.model.VehicleType;
import com.boltfood.delivery.fee.repository.RegionalBaseFeeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegionalBaseFeeServiceTest {

    @Mock
    private RegionalBaseFeeRepository regionalBaseFeeRepository;

    @InjectMocks
    private RegionalBaseFeeService regionalBaseFeeService;

    @ParameterizedTest
    @EnumSource(VehicleType.class)
    void getRegionalBaseFee_returnsFeeForEveryVehicleType(VehicleType vehicleType) {
        RegionalBaseFee fee = new RegionalBaseFee();
        fee.setFee(new BigDecimal("3.5"));
        when(regionalBaseFeeRepository.findByCityAndVehicleType(City.TARTU, vehicleType)).thenReturn(Optional.of(fee));

        BigDecimal result = regionalBaseFeeService.getRegionalBaseFee(City.TARTU, vehicleType);

        assertThat(result).isEqualByComparingTo("3.5");
        verify(regionalBaseFeeRepository).findByCityAndVehicleType(City.TARTU, vehicleType);
    }

    @Test
    void getRegionalBaseFee_notFound_throwsException() {
        when(regionalBaseFeeRepository.findByCityAndVehicleType(City.PÄRNU, VehicleType.BIKE)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> regionalBaseFeeService.getRegionalBaseFee(City.PÄRNU, VehicleType.BIKE));
    }

    @Test
    void getRbfById_returnsFee() {
        UUID id = UUID.randomUUID();
        RegionalBaseFee fee = new RegionalBaseFee();
        fee.setId(id);
        when(regionalBaseFeeRepository.findById(id)).thenReturn(Optional.of(fee));

        RegionalBaseFee result = regionalBaseFeeService.getRbfById(id);

        assertThat(result.getId()).isEqualTo(id);
    }

    @Test
    void getRbfById_notFound_throwsException() {
        UUID id = UUID.randomUUID();
        when(regionalBaseFeeRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> regionalBaseFeeService.getRbfById(id));
    }

    @Test
    void getRbf_returnsAllFees() {
        var firstFee = mock(RegionalBaseFee.class);
        var secondFee = mock(RegionalBaseFee.class);

        List<RegionalBaseFee> fees = new ArrayList<>();
        fees.add(firstFee);
        fees.add(secondFee);

        when(regionalBaseFeeRepository.findAll()).thenReturn(fees);

        List<RegionalBaseFee> result = regionalBaseFeeService.getRbf();

        assertThat(result).hasSize(2);
        verify(regionalBaseFeeRepository).findAll();
    }

    @ParameterizedTest
    @EnumSource(VehicleType.class)
    void postRbf_savesFeeForEveryVehicleType(VehicleType vehicleType) {
        RegionalBaseFeeCondition condition = new RegionalBaseFeeCondition();
        condition.setCity(City.TALLINN);
        condition.setVehicleType(vehicleType);
        condition.setFee(new BigDecimal("4"));

        regionalBaseFeeService.postRbf(condition);

        ArgumentCaptor<RegionalBaseFee> captor = ArgumentCaptor.forClass(RegionalBaseFee.class);
        verify(regionalBaseFeeRepository).save(captor.capture());
        assertThat(captor.getValue().getCity()).isEqualTo(City.TALLINN);
        assertThat(captor.getValue().getVehicleType()).isEqualTo(vehicleType);
        assertThat(captor.getValue().getFee()).isEqualByComparingTo("4");
    }

    @Test
    void putRbf_updatesExistingFee() {
        UUID id = UUID.randomUUID();
        RegionalBaseFee existing = new RegionalBaseFee();
        existing.setId(id);
        existing.setCity(City.TALLINN);
        existing.setVehicleType(VehicleType.CAR);
        existing.setFee(new BigDecimal("4"));
        when(regionalBaseFeeRepository.findById(id)).thenReturn(Optional.of(existing));

        RegionalBaseFeeCondition condition = new RegionalBaseFeeCondition();
        condition.setCity(City.TARTU);
        condition.setVehicleType(VehicleType.SCOOTER);
        condition.setFee(new BigDecimal("3"));

        regionalBaseFeeService.putRbf(id, condition);

        verify(regionalBaseFeeRepository).save(existing);
        assertThat(existing.getCity()).isEqualTo(City.TARTU);
        assertThat(existing.getVehicleType()).isEqualTo(VehicleType.SCOOTER);
        assertThat(existing.getFee()).isEqualByComparingTo("3");
    }

    @Test
    void putRbf_notFound_throwsExceptionAndDoesNotSave() {
        UUID id = UUID.randomUUID();
        when(regionalBaseFeeRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> regionalBaseFeeService.putRbf(id, new RegionalBaseFeeCondition()));

        verify(regionalBaseFeeRepository, never()).save(any());
    }

    @Test
    void deleteRbfById_deletesFee() {
        UUID id = UUID.randomUUID();
        RegionalBaseFee existing = new RegionalBaseFee();
        when(regionalBaseFeeRepository.findById(id)).thenReturn(Optional.of(existing));

        regionalBaseFeeService.deleteRbfById(id);

        verify(regionalBaseFeeRepository).delete(existing);
    }

    @Test
    void deleteRbfById_notFound_throwsExceptionAndDoesNotDelete() {
        UUID id = UUID.randomUUID();
        when(regionalBaseFeeRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> regionalBaseFeeService.deleteRbfById(id));

        verify(regionalBaseFeeRepository, never()).delete(any());
    }
}
