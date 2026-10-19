package com.boltfood.delivery.fee.controller;

import com.boltfood.delivery.fee.dto.RegionalBaseFeeCondition;
import com.boltfood.delivery.fee.model.RegionalBaseFee;
import com.boltfood.delivery.fee.service.RegionalBaseFeeService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/regional-base-fee")
public class RegionalBaseFeeController {

    private final RegionalBaseFeeService regionalBaseFeeService;

    public RegionalBaseFeeController(RegionalBaseFeeService regionalBaseFeeService){
        this.regionalBaseFeeService = regionalBaseFeeService;
    }

    @GetMapping("/{id}")
    public RegionalBaseFee getRegionalBaseFeeById(@PathVariable UUID id){
        return regionalBaseFeeService.getRbfById(id);
    }

    @GetMapping
    public List<RegionalBaseFee> getRegionalBaseFee(){
        return regionalBaseFeeService.getRbf();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void postRegionalBaseFee(@Valid @RequestBody RegionalBaseFeeCondition condition){
        regionalBaseFeeService.postRbf(condition);
    }

    @PutMapping("/{id}")
    public void putRegionalBaseFee (@PathVariable UUID id, @Valid @RequestBody RegionalBaseFeeCondition condition){
        regionalBaseFeeService.putRbf(id, condition);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteRegionalBaseFee(@PathVariable UUID id){
        regionalBaseFeeService.deleteRbfById(id);
    }
}
