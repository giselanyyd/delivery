package com.boltfood.delivery.fee.controller;

import com.boltfood.delivery.exception.DuplicateConditionException;
import com.boltfood.delivery.exception.EntityNotFoundException;
import com.boltfood.delivery.fee.dto.WeatherPhenomenonExtraFeeCondition;
import com.boltfood.delivery.fee.model.Phenomenon;
import com.boltfood.delivery.fee.model.VehicleType;
import com.boltfood.delivery.fee.model.WeatherPhenomenonExtraFee;
import com.boltfood.delivery.fee.service.WeatherPhenomenonExtraFeeService;
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

@WebMvcTest(WeatherPhenomenonExtraFeeController.class)
class WeatherPhenomenonExtraFeeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private WeatherPhenomenonExtraFeeService weatherPhenomenonExtraFeeService;

    private final String URL = "/api/v1/weather-phenomenon-extra-fee";

    @Test
    void getAll_returnsAllConditions() throws Exception {
        var firstCondition = mock(WeatherPhenomenonExtraFee.class);
        var secondCondition = mock(WeatherPhenomenonExtraFee.class);

        List<WeatherPhenomenonExtraFee> conditions = new ArrayList<>();
        conditions.add(firstCondition);
        conditions.add(secondCondition);

        when(weatherPhenomenonExtraFeeService.getWpef()).thenReturn(conditions);

        mockMvc.perform(get(URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(conditions.size())));

        verify(weatherPhenomenonExtraFeeService).getWpef();
    }

    @Test
    void getById_returnsCondition() throws Exception {
        UUID id = UUID.randomUUID();

        WeatherPhenomenonExtraFee condition = new WeatherPhenomenonExtraFee();
        condition.setId(id);
        condition.setVehicleType(VehicleType.SCOOTER);
        condition.setPhenomenon(Phenomenon.SNOW);
        condition.setFee(new BigDecimal("1"));

        when(weatherPhenomenonExtraFeeService.getWpefById(id)).thenReturn(condition);

        mockMvc.perform(get(URL + "/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.vehicleType").value("SCOOTER"))
                .andExpect(jsonPath("$.phenomenon").value("SNOW"))
                .andExpect(jsonPath("$.fee").value(1));

        verify(weatherPhenomenonExtraFeeService).getWpefById(id);
    }

    @Test
    void getById_returnsNotFound() throws Exception {
        UUID id = UUID.randomUUID();
        when(weatherPhenomenonExtraFeeService.getWpefById(id)).thenThrow(new EntityNotFoundException("Weather phenomenon condition not found"));

        mockMvc.perform(get(URL + "/" + id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not found"))
                .andExpect(jsonPath("$.message").value("Weather phenomenon condition not found"));
    }

    @Test
    void post_createsCondition() throws Exception {

        WeatherPhenomenonExtraFeeCondition condition = new WeatherPhenomenonExtraFeeCondition();
        condition.setVehicleType(VehicleType.SCOOTER);
        condition.setPhenomenon(Phenomenon.SNOW);
        condition.setFee(BigDecimal.valueOf(1));

        ObjectMapper objectMapper = new ObjectMapper();
        String requestBody = objectMapper.writeValueAsString(condition);

        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated());

        verify(weatherPhenomenonExtraFeeService).postWpef(condition);
    }

    @Test
    void postWithErrorIsThrown_createsCondition() throws Exception {

        WeatherPhenomenonExtraFeeCondition condition = new WeatherPhenomenonExtraFeeCondition();
        condition.setVehicleType(VehicleType.BIKE);
        condition.setPhenomenon(Phenomenon.HAIL);
        condition.setFee(BigDecimal.valueOf(0));
        condition.setErrorIsThrown(true);

        ObjectMapper objectMapper = new ObjectMapper();
        String requestBody = objectMapper.writeValueAsString(condition);

        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated());

        verify(weatherPhenomenonExtraFeeService).postWpef(condition);
    }

    @Test
    void postWithInvalidInput_returnsBadRequest() throws Exception {

        WeatherPhenomenonExtraFeeCondition condition = new WeatherPhenomenonExtraFeeCondition();
        condition.setVehicleType(VehicleType.SCOOTER);
        condition.setFee(BigDecimal.valueOf(1));

        ObjectMapper objectMapper = new ObjectMapper();
        String requestBody = objectMapper.writeValueAsString(condition);

        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Argument not valid"))
                .andExpect(jsonPath("$.message").value("phenomenon is mandatory"));

        verifyNoInteractions(weatherPhenomenonExtraFeeService);
    }

    @Test
    void postDuplicate_returnsConflict() throws Exception {

        WeatherPhenomenonExtraFeeCondition condition = new WeatherPhenomenonExtraFeeCondition();
        condition.setVehicleType(VehicleType.SCOOTER);
        condition.setPhenomenon(Phenomenon.SNOW);
        condition.setFee(BigDecimal.valueOf(1));

        ObjectMapper objectMapper = new ObjectMapper();
        String requestBody = objectMapper.writeValueAsString(condition);

        doThrow(new DuplicateConditionException("Condition for this vehicle type and phenomenon already exists"))
                .when(weatherPhenomenonExtraFeeService).postWpef(condition);

        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.error").value("Condition already exists"))
                .andExpect(jsonPath("$.message").value("Condition for this vehicle type and phenomenon already exists"));

        verify(weatherPhenomenonExtraFeeService).postWpef(condition);
    }

    @Test
    void put_conditionUpdated() throws Exception {
        UUID id = UUID.randomUUID();

        WeatherPhenomenonExtraFeeCondition condition = new WeatherPhenomenonExtraFeeCondition();
        condition.setVehicleType(VehicleType.SCOOTER);
        condition.setPhenomenon(Phenomenon.SNOW);
        condition.setFee(BigDecimal.valueOf(1));

        ObjectMapper objectMapper = new ObjectMapper();
        String requestBody = objectMapper.writeValueAsString(condition);

        mockMvc.perform(put(URL + "/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk());

        verify(weatherPhenomenonExtraFeeService).putWpef(id, condition);
    }

    @Test
    void delete_conditionDeleted() throws Exception {
        UUID id = UUID.randomUUID();

        mockMvc.perform(delete(URL + "/" + id))
                .andExpect(status().isNoContent());

        verify(weatherPhenomenonExtraFeeService).deleteWpefById(id);
    }
}
