package com.boltfood.delivery.fee.controller;

import com.boltfood.delivery.common.City;
import com.boltfood.delivery.exception.DuplicateConditionException;
import com.boltfood.delivery.exception.EntityNotFoundException;
import com.boltfood.delivery.fee.dto.RegionalBaseFeeCondition;
import com.boltfood.delivery.fee.model.RegionalBaseFee;
import com.boltfood.delivery.fee.model.VehicleType;
import com.boltfood.delivery.fee.service.RegionalBaseFeeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RegionalBaseFeeController.class)
class RegionalBaseFeeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RegionalBaseFeeService regionalBaseFeeService;

    private final String URL = "/api/v1/regional-base-fee";

    @Test
    void getAll_returnsAllConditions() throws Exception {
        var firstCondition = mock(RegionalBaseFee.class);
        var secondCondition = mock(RegionalBaseFee.class);

        List<RegionalBaseFee> conditions = new ArrayList<>();
        conditions.add(firstCondition);
        conditions.add(secondCondition);

        when(regionalBaseFeeService.getRbf()).thenReturn(conditions);

        mockMvc.perform(get(URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(conditions.size())));

        verify(regionalBaseFeeService).getRbf();
    }

    @Test
    void getById_returnsCondition() throws Exception {
        UUID id = UUID.randomUUID();

        RegionalBaseFee condition = new RegionalBaseFee();
        condition.setId(id);
        condition.setCity(City.TALLINN);
        condition.setVehicleType(VehicleType.CAR);
        condition.setFee(new BigDecimal("4"));

        when(regionalBaseFeeService.getRbfById(id)).thenReturn(condition);

        mockMvc.perform(get(URL + "/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.city").value("TALLINN"))
                .andExpect(jsonPath("$.vehicleType").value("CAR"))
                .andExpect(jsonPath("$.fee").value(4));

        verify(regionalBaseFeeService).getRbfById(id);
    }

    @Test
    void getById_returnsNotFound() throws Exception {
        UUID id = UUID.randomUUID();
        when(regionalBaseFeeService.getRbfById(id)).thenThrow(new EntityNotFoundException("Regional base fee not found"));

        mockMvc.perform(get(URL + "/" + id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not found"))
                .andExpect(jsonPath("$.message").value("Regional base fee not found"));
    }

    @Test
    void post_createsCondition() throws Exception {

        RegionalBaseFeeCondition condition = new RegionalBaseFeeCondition();
        condition.setCity(City.TALLINN);
        condition.setVehicleType(VehicleType.CAR);
        condition.setFee(BigDecimal.valueOf(4));

        ObjectMapper objectMapper = new ObjectMapper();
        String requestBody = objectMapper.writeValueAsString(condition);

        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated());

        verify(regionalBaseFeeService).postRbf(condition);
    }

    @Test
    void postWithInvalidInput_returnsBadRequest() throws Exception {

        RegionalBaseFeeCondition condition = new RegionalBaseFeeCondition();
        condition.setCity(City.TALLINN);
        condition.setFee(BigDecimal.valueOf(4));

        ObjectMapper objectMapper = new ObjectMapper();
        String requestBody = objectMapper.writeValueAsString(condition);

        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Argument not valid"))
                .andExpect(jsonPath("$.message").value("vehicle type is mandatory"));

        verifyNoInteractions(regionalBaseFeeService);
    }

    @Test
    void postWithNegativeFee_returnsBadRequest() throws Exception {

        RegionalBaseFeeCondition condition = new RegionalBaseFeeCondition();
        condition.setCity(City.TALLINN);
        condition.setVehicleType(VehicleType.CAR);
        condition.setFee(BigDecimal.valueOf(-1));

        ObjectMapper objectMapper = new ObjectMapper();
        String requestBody = objectMapper.writeValueAsString(condition);

        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Argument not valid"))
                .andExpect(jsonPath("$.message").value("must be greater than or equal to 0"));

        verifyNoInteractions(regionalBaseFeeService);
    }

    @Test
    void postDuplicate_returnsConflict() throws Exception {

        RegionalBaseFeeCondition condition = new RegionalBaseFeeCondition();
        condition.setCity(City.TALLINN);
        condition.setVehicleType(VehicleType.CAR);
        condition.setFee(BigDecimal.valueOf(4));

        ObjectMapper objectMapper = new ObjectMapper();
        String requestBody = objectMapper.writeValueAsString(condition);

        doThrow(new DuplicateConditionException("Regional base fee for this city and vehicle type already exists"))
                .when(regionalBaseFeeService).postRbf(condition);

        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.error").value("Condition already exists"))
                .andExpect(jsonPath("$.message").value("Regional base fee for this city and vehicle type already exists"));

        verify(regionalBaseFeeService).postRbf(condition);
    }

    @Test
    void put_conditionUpdated() throws Exception {
        UUID id = UUID.randomUUID();

        RegionalBaseFeeCondition condition = new RegionalBaseFeeCondition();
        condition.setCity(City.TALLINN);
        condition.setVehicleType(VehicleType.CAR);
        condition.setFee(BigDecimal.valueOf(4));

        ObjectMapper objectMapper = new ObjectMapper();
        String requestBody = objectMapper.writeValueAsString(condition);

        mockMvc.perform(put(URL + "/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk());

        verify(regionalBaseFeeService).putRbf(id, condition);
    }

    @Test
    void delete_conditionDeleted() throws Exception {
        UUID id = UUID.randomUUID();

        mockMvc.perform(delete(URL + "/" + id))
                .andExpect(status().isNoContent());

        verify(regionalBaseFeeService).deleteRbfById(id);
    }
}
