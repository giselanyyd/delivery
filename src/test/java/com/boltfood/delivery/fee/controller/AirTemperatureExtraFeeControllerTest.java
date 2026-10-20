package com.boltfood.delivery.fee.controller;

import com.boltfood.delivery.exception.EntityNotFoundException;
import com.boltfood.delivery.fee.dto.AirTemperatureExtraFeeCondition;
import com.boltfood.delivery.fee.model.AirTemperatureExtraFee;
import com.boltfood.delivery.fee.model.VehicleType;
import com.boltfood.delivery.fee.service.AirTemperatureExtraFeeService;
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

@WebMvcTest(AirTemperatureExtraFeeController.class)
class AirTemperatureExtraFeeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AirTemperatureExtraFeeService airTemperatureExtraFeeService;

    private final String URL = "/api/v1/air-temperature-extra-fee";

    @Test
    void getAll_returnsAllConditions() throws Exception {
        var firstCondition = mock(AirTemperatureExtraFee.class);
        var secondCondition = mock(AirTemperatureExtraFee.class);

        List<AirTemperatureExtraFee> conditions = new ArrayList<>();
        conditions.add(firstCondition);
        conditions.add(secondCondition);

        when(airTemperatureExtraFeeService.getAtef()).thenReturn(conditions);

        mockMvc.perform(get(URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(conditions.size())));

        verify(airTemperatureExtraFeeService).getAtef();
    }

    @Test
    void getById_returnsCondition() throws Exception {
        UUID id = UUID.randomUUID();

        AirTemperatureExtraFee condition = new AirTemperatureExtraFee();
        condition.setId(id);
        condition.setVehicleType(VehicleType.BIKE);
        condition.setMinValue(-10.0);
        condition.setMaxValue(0.0);
        condition.setFee(new BigDecimal("0.5"));

        when(airTemperatureExtraFeeService.getAtefById(id)).thenReturn(condition);

        mockMvc.perform(get(URL + "/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.vehicleType").value("BIKE"))
                .andExpect(jsonPath("$.minValue").value(-10.0))
                .andExpect(jsonPath("$.maxValue").value(0.0))
                .andExpect(jsonPath("$.fee").value(0.5));

        verify(airTemperatureExtraFeeService).getAtefById(id);
    }

    @Test
    void getById_returnsNotFound() throws Exception {
        UUID id = UUID.randomUUID();
        when(airTemperatureExtraFeeService.getAtefById(id)).thenThrow(new EntityNotFoundException("Air temperature condition not found"));

        mockMvc.perform(get(URL + "/" + id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not found"))
                .andExpect(jsonPath("$.message").value("Air temperature condition not found"));
    }

    @Test
    void post_createsCondition() throws Exception {

        AirTemperatureExtraFeeCondition condition = new AirTemperatureExtraFeeCondition();
        condition.setVehicleType(VehicleType.BIKE);
        condition.setMinValue(-10.0);
        condition.setMaxValue(0.0);
        condition.setFee(BigDecimal.valueOf(0.5));

        ObjectMapper objectMapper = new ObjectMapper();
        String requestBody = objectMapper.writeValueAsString(condition);

        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated());

        verify(airTemperatureExtraFeeService).postAtef(condition);
    }

    @Test
    void postWithInvalidInput_returnsBadRequest() throws Exception {

        AirTemperatureExtraFeeCondition condition = new AirTemperatureExtraFeeCondition();
        condition.setMinValue(-10.0);
        condition.setMaxValue(0.0);
        condition.setFee(BigDecimal.valueOf(0.5));

        ObjectMapper objectMapper = new ObjectMapper();
        String requestBody = objectMapper.writeValueAsString(condition);

        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Argument not valid"))
                .andExpect(jsonPath("$.message").value("vehicle type is mandatory"));

        verifyNoInteractions(airTemperatureExtraFeeService);
    }

    @Test
    void put_conditionUpdated() throws Exception {
        UUID id = UUID.randomUUID();

        AirTemperatureExtraFeeCondition condition = new AirTemperatureExtraFeeCondition();
        condition.setVehicleType(VehicleType.BIKE);
        condition.setMinValue(-10.0);
        condition.setMaxValue(0.0);
        condition.setFee(BigDecimal.valueOf(0.5));

        ObjectMapper objectMapper = new ObjectMapper();
        String requestBody = objectMapper.writeValueAsString(condition);

        mockMvc.perform(put(URL + "/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk());

        verify(airTemperatureExtraFeeService).putAtef(id, condition);
    }

    @Test
    void delete_conditionDeleted() throws Exception {
        UUID id = UUID.randomUUID();

        mockMvc.perform(delete(URL + "/" + id))
                .andExpect(status().isNoContent());

        verify(airTemperatureExtraFeeService).deleteAtefById(id);
    }
}
