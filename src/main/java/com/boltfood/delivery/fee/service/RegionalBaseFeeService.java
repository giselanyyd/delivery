package com.boltfood.delivery.fee.service;

import com.boltfood.delivery.fee.dto.RegionalBaseFeeCondition;
import com.boltfood.delivery.fee.model.City;
import com.boltfood.delivery.fee.model.RegionalBaseFee;
import com.boltfood.delivery.fee.model.VehicleType;
import com.boltfood.delivery.fee.repository.RegionalBaseFeeRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class RegionalBaseFeeService {
    private final RegionalBaseFeeRepository regionalBaseFeeRepository;

    public RegionalBaseFeeService(RegionalBaseFeeRepository regionalBaseFeeRepository){
        this.regionalBaseFeeRepository = regionalBaseFeeRepository;
    }

    public double getRbfByCityAndVehicleType(City city, VehicleType vehicleType){
        RegionalBaseFee object = regionalBaseFeeRepository.findByCityAndVehicleType(city, vehicleType);
        return object.getFee();
    }

    public RegionalBaseFee getRbfById(UUID id){
        return regionalBaseFeeRepository.findById(id).orElseThrow(() -> new IllegalArgumentException(
                "Regional base fee not found with id" + id));
    }

    public List<RegionalBaseFee> getRbf(){
        return regionalBaseFeeRepository.findAll();
    }

    public void postRbf(RegionalBaseFeeCondition condition){
        RegionalBaseFee regionalBaseFee = new RegionalBaseFee();
        regionalBaseFee.setCity(condition.getCity());
        regionalBaseFee.setVehicleType(condition.getVehicleType());
        regionalBaseFee.setFee(condition.getFee());
        regionalBaseFeeRepository.save(regionalBaseFee);
    }

    public void putRbf(UUID id, RegionalBaseFeeCondition condition){
        RegionalBaseFee existingCondition = getRbfById(id);
        existingCondition.setCity(condition.getCity());
        existingCondition.setVehicleType(condition.getVehicleType());
        existingCondition.setFee(condition.getFee());
        regionalBaseFeeRepository.save(existingCondition);
    }

    public void deleteRbfById(UUID id){
        regionalBaseFeeRepository.deleteById(id);
    }
}
