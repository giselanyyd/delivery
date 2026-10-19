package com.boltfood.delivery.fee.service;

import com.boltfood.delivery.exception.DuplicateConditionException;
import com.boltfood.delivery.exception.EntityNotFoundException;
import com.boltfood.delivery.fee.dto.RegionalBaseFeeCondition;
import com.boltfood.delivery.common.City;
import com.boltfood.delivery.fee.model.RegionalBaseFee;
import com.boltfood.delivery.fee.model.VehicleType;
import com.boltfood.delivery.fee.repository.RegionalBaseFeeRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class RegionalBaseFeeService {
    private final RegionalBaseFeeRepository regionalBaseFeeRepository;

    public RegionalBaseFeeService(RegionalBaseFeeRepository regionalBaseFeeRepository){
        this.regionalBaseFeeRepository = regionalBaseFeeRepository;
    }

    public BigDecimal getRegionalBaseFee(City city, VehicleType vehicleType){
        Optional<RegionalBaseFee> regionalBaseFee = regionalBaseFeeRepository.findByCityAndVehicleType(city, vehicleType);

        if (regionalBaseFee.isEmpty()) {
            throw new EntityNotFoundException(
                    "Regional base fee not found for " + city + " and " + vehicleType);
        }

        return regionalBaseFee.get().getFee();
    }

    public RegionalBaseFee getRbfById(UUID id){
        return regionalBaseFeeRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(
                "Regional base fee not found with id" + id));
    }

    public List<RegionalBaseFee> getRbf(){
        return regionalBaseFeeRepository.findAll();
    }

    public void postRbf(RegionalBaseFeeCondition condition){
        checkForDuplication(condition, null);
        RegionalBaseFee regionalBaseFee = new RegionalBaseFee();
        regionalBaseFee.setCity(condition.getCity());
        regionalBaseFee.setVehicleType(condition.getVehicleType());
        regionalBaseFee.setFee(condition.getFee());
        regionalBaseFeeRepository.save(regionalBaseFee);
    }

    public void putRbf(UUID id, RegionalBaseFeeCondition condition){
        RegionalBaseFee existingCondition = getRbfById(id);
        checkForDuplication(condition, id);
        existingCondition.setCity(condition.getCity());
        existingCondition.setVehicleType(condition.getVehicleType());
        existingCondition.setFee(condition.getFee());
        regionalBaseFeeRepository.save(existingCondition);
    }

    public void deleteRbfById(UUID id){
        RegionalBaseFee condition = getRbfById(id);
        regionalBaseFeeRepository.delete(condition);
    }

    private void checkForDuplication(RegionalBaseFeeCondition condition, UUID currentId){
        Optional<RegionalBaseFee> existing = regionalBaseFeeRepository.findByCityAndVehicleType(condition.getCity(), condition.getVehicleType());
        if (existing.isPresent() && !existing.get().getId().equals(currentId)) {
            throw new DuplicateConditionException("Regional base fee for this city and vehicle type already exists");
        }
    }
}
