package com.boltfood.delivery.fee.controller;

import com.boltfood.delivery.fee.model.WindSpeedExtraFee;
import com.boltfood.delivery.fee.dto.WindSpeedExtraFeeCondition;
import com.boltfood.delivery.fee.service.WindSpeedExtraFeeService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/wind-speed-extra-fee")
public class WindSpeedExtraFeeController {

    private final WindSpeedExtraFeeService windSpeedExtraFeeService;

    public WindSpeedExtraFeeController(WindSpeedExtraFeeService windSpeedExtraFeeService){
        this.windSpeedExtraFeeService = windSpeedExtraFeeService;
    }

    @GetMapping("/{id}")
    public WindSpeedExtraFee getWindSpeedExtraFeeById(@PathVariable UUID id){
        return windSpeedExtraFeeService.getWsefById(id);
    }

    @GetMapping
    public List<WindSpeedExtraFee> getWindSpeedExtraFee(){
        return windSpeedExtraFeeService.getWsef();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void postWindSpeedExtraFee(@Valid @RequestBody WindSpeedExtraFeeCondition condition){
        windSpeedExtraFeeService.postWsef(condition);
    }

    @PutMapping("/{id}")
    public void putWindSpeedExtraFee (@PathVariable UUID id, @Valid @RequestBody WindSpeedExtraFeeCondition condition){
        windSpeedExtraFeeService.putWsef(id, condition);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteWindSpeedExtraFee(@PathVariable UUID id){
        windSpeedExtraFeeService.deleteWsefById(id);
    }
}
