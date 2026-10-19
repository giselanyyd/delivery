package com.boltfood.delivery.fee.service;

import com.boltfood.delivery.exception.EntityNotFoundException;
import com.boltfood.delivery.fee.model.VehicleType;
import com.boltfood.delivery.fee.model.AirTemperatureExtraFee;
import com.boltfood.delivery.fee.dto.AirTemperatureExtraFeeCondition;
import com.boltfood.delivery.fee.repository.AirTemperatureExtraFeeRepository;
import com.boltfood.delivery.fee.util.ConditionRangeUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
public class AirTemperatureExtraFeeService {
    private final AirTemperatureExtraFeeRepository airTemperatureExtraFeeRepository;

    public AirTemperatureExtraFeeService(AirTemperatureExtraFeeRepository airTemperatureExtraFeeRepository){
        this.airTemperatureExtraFeeRepository = airTemperatureExtraFeeRepository;
    }

    public BigDecimal getAtefByVehicleTypeAndAirTemperature(VehicleType vehicleType, Double airTemperature) {
        if (airTemperature == null) {
            return BigDecimal.ZERO;
        }

        List<AirTemperatureExtraFee> conditions = airTemperatureExtraFeeRepository.findAllByVehicleType(vehicleType);
        for (AirTemperatureExtraFee condition : conditions) {
            if (ConditionRangeUtils.isInRange(airTemperature, condition.getMinValue(), condition.getMaxValue())) {
                return condition.getFee();
            }
        }
        return BigDecimal.ZERO;
    }

    public AirTemperatureExtraFee getAtefById(UUID id){
        return airTemperatureExtraFeeRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(
                "Air temperature condition not found with id " + id));
    }

    public List<AirTemperatureExtraFee> getAtef(){
        return airTemperatureExtraFeeRepository.findAll();
    }

    public void postAtef(AirTemperatureExtraFeeCondition condition){
        AirTemperatureExtraFee airTemperatureExtraFee = new AirTemperatureExtraFee();
        airTemperatureExtraFee.setVehicleType(condition.getVehicleType());
        airTemperatureExtraFee.setMinValue(condition.getMinValue());
        airTemperatureExtraFee.setMaxValue(condition.getMaxValue());
        airTemperatureExtraFee.setFee(condition.getFee());
        airTemperatureExtraFeeRepository.save(airTemperatureExtraFee);
    }

    public void putAtef(UUID id, AirTemperatureExtraFeeCondition condition){
        AirTemperatureExtraFee existingCondition = getAtefById(id);
        existingCondition.setVehicleType(condition.getVehicleType());
        existingCondition.setMinValue(condition.getMinValue());
        existingCondition.setMaxValue(condition.getMaxValue());
        existingCondition.setFee(condition.getFee());
        airTemperatureExtraFeeRepository.save(existingCondition);
    }

    public void deleteAtefById(UUID id){
        AirTemperatureExtraFee condition = getAtefById(id);
        airTemperatureExtraFeeRepository.delete(condition);
    }
}
