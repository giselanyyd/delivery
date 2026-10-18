package com.boltfood.delivery.fee.controller;
import com.boltfood.delivery.fee.model.City;
import com.boltfood.delivery.fee.model.VehicleType;
import com.boltfood.delivery.fee.service.FeeService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/fee")
public class FeeController {

    private final FeeService feeService;

    public FeeController(FeeService feeService){
        this.feeService = feeService;
    }

    @GetMapping
    public double getDeliveryFee(@RequestParam City city, @RequestParam VehicleType vehicleType){
        return feeService.getDeliveryFee(city, vehicleType);
    }
}
