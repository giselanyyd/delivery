package com.boltfood.delivery.fee.controller;

import com.boltfood.delivery.fee.model.AirTemperatureExtraFee;
import com.boltfood.delivery.fee.dto.AirTemperatureExtraFeeCondition;
import com.boltfood.delivery.fee.service.AirTemperatureExtraFeeService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/air-temperature-extra-fee")
public class AirTemperatureExtraFeeController {

    private final AirTemperatureExtraFeeService airTemperatureExtraFeeService;

    public AirTemperatureExtraFeeController(AirTemperatureExtraFeeService airTemperatureExtraFeeService){
        this.airTemperatureExtraFeeService = airTemperatureExtraFeeService;
    }

    @GetMapping("/{id}")
    public AirTemperatureExtraFee getAirTemperatureExtraFeeById(@PathVariable UUID id){
        return airTemperatureExtraFeeService.getAtefById(id);
    }

    @GetMapping
    public List<AirTemperatureExtraFee> getAirTemperatureExtraFee(){
        return airTemperatureExtraFeeService.getAtef();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void postAirTemperatureExtraFee(@Valid @RequestBody AirTemperatureExtraFeeCondition condition){
        airTemperatureExtraFeeService.postAtef(condition);
    }

    @PutMapping("/{id}")
    public void putAirTemperatureExtraFee (@PathVariable UUID id, @Valid @RequestBody AirTemperatureExtraFeeCondition condition){
        airTemperatureExtraFeeService.putAtef(id, condition);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAirTemperatureExtraFee(@PathVariable UUID id){
        airTemperatureExtraFeeService.deleteAtefById(id);
    }
}
