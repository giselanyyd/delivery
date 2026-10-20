package com.boltfood.delivery.fee.controller;

import com.boltfood.delivery.common.City;
import com.boltfood.delivery.exception.EntityNotFoundException;
import com.boltfood.delivery.exception.VehicleTypeUsageForbiddenException;
import com.boltfood.delivery.fee.model.VehicleType;
import com.boltfood.delivery.fee.service.FeeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FeeController.class)
class FeeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private FeeService feeService;

    private static final String URL = "/api/v1/fee";

    @Test
    void getDeliveryFee_returnsFee() throws Exception {
        when(feeService.getDeliveryFee(City.TALLINN, VehicleType.CAR)).thenReturn(new BigDecimal("4.50"));

        mockMvc.perform(get(URL)
                        .param("city", "TALLINN")
                        .param("vehicleType", "CAR"))
                .andExpect(status().isOk())
                .andExpect(content().string("4.50"));

        verify(feeService).getDeliveryFee(City.TALLINN, VehicleType.CAR);
    }

    @Test
    void getDeliveryFeeWhenUsageIsForbidden_returnsBadRequest() throws Exception {
        when(feeService.getDeliveryFee(City.TARTU, VehicleType.BIKE))
                .thenThrow(new VehicleTypeUsageForbiddenException("Usage of selected vehicle type is forbidden"));

        mockMvc.perform(get(URL)
                        .param("city", "TARTU")
                        .param("vehicleType", "BIKE"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Vehicle usage forbidden"))
                .andExpect(jsonPath("$.message").value("Usage of selected vehicle type is forbidden"));

        verify(feeService).getDeliveryFee(City.TARTU, VehicleType.BIKE);
    }

    @Test
    void getDeliveryFeeWhenRegionalBaseFeeIsNotSet_returnsNotFound() throws Exception {
        when(feeService.getDeliveryFee(City.PÄRNU, VehicleType.SCOOTER))
                .thenThrow(new EntityNotFoundException("Regional base fee not found for PÄRNU and SCOOTER"));

        mockMvc.perform(get(URL)
                        .param("city", "PÄRNU")
                        .param("vehicleType", "SCOOTER"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Regional base fee not found for PÄRNU and SCOOTER"));
    }

    @Test
    void getDeliveryFeeWhenCityIsInvalid_returnsBadRequest() throws Exception {
        mockMvc.perform(get(URL)
                        .param("city", "RIGA")
                        .param("vehicleType", "CAR"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Invalid parameter"));

        verifyNoInteractions(feeService);
    }

}
