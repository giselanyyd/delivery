package com.boltfood.delivery.fee.controller;

import com.boltfood.delivery.fee.model.WeatherPhenomenonExtraFee;
import com.boltfood.delivery.fee.dto.WeatherPhenomenonExtraFeeCondition;
import com.boltfood.delivery.fee.service.WeatherPhenomenonExtraFeeService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/weather-phenomenon-extra-fee")
public class WeatherPhenomenonExtraFeeController {

    private final WeatherPhenomenonExtraFeeService weatherPhenomenonExtraFeeService;

    public WeatherPhenomenonExtraFeeController(WeatherPhenomenonExtraFeeService weatherPhenomenonExtraFeeService){
        this.weatherPhenomenonExtraFeeService = weatherPhenomenonExtraFeeService;
    }

    /**
     * Returns the regional base fee with the given ID.
     *
     * @param id the rule ID
     * @return the regional base fee
     */
    @GetMapping("/{id}")
    public WeatherPhenomenonExtraFee getWeatherPhenomenonExtraFeeById(@PathVariable UUID id){
        return weatherPhenomenonExtraFeeService.getWpefById(id);
    }

    @GetMapping
    public List<WeatherPhenomenonExtraFee> getWeatherPhenomenonExtraFee(){
        return weatherPhenomenonExtraFeeService.getWpef();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void postWeatherPhenomenonExtraFee(@Valid @RequestBody WeatherPhenomenonExtraFeeCondition condition){
        weatherPhenomenonExtraFeeService.postWpef(condition);
    }

    @PutMapping("/{id}")
    public void putWeatherPhenomenonExtraFee (@PathVariable UUID id, @Valid @RequestBody WeatherPhenomenonExtraFeeCondition condition){
        weatherPhenomenonExtraFeeService.putWpef(id, condition);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteWeatherPhenomenonExtraFee(@PathVariable UUID id){
        weatherPhenomenonExtraFeeService.deleteWpefById(id);
    }
}
