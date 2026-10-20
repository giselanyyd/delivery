package com.boltfood.delivery.fee.service;

import com.boltfood.delivery.exception.EntityNotFoundException;
import com.boltfood.delivery.exception.VehicleTypeUsageForbiddenException;
import com.boltfood.delivery.fee.model.VehicleType;
import com.boltfood.delivery.fee.model.WindSpeedExtraFee;
import com.boltfood.delivery.fee.dto.WindSpeedExtraFeeCondition;
import com.boltfood.delivery.fee.repository.WindSpeedExtraFeeRepository;
import com.boltfood.delivery.fee.util.ConditionRangeUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
public class WindSpeedExtraFeeService {
    private final WindSpeedExtraFeeRepository windSpeedExtraFeeRepository;

    public WindSpeedExtraFeeService(WindSpeedExtraFeeRepository windSpeedExtraFeeRepository){
        this.windSpeedExtraFeeRepository = windSpeedExtraFeeRepository;
    }
    /**
     * Returns the wind speed extra fee from the first rule whose range
     * contains the given wind speed.
     *
     * @param vehicleType the courier's vehicle type
     * @param windSpeed   the observed wind speed in m/s, may be {@code null}
     * @return the matching rule's fee, or zero if the wind speed is {@code null} or no rule matches
     * @throws VehicleTypeUsageForbiddenException if the matching rule forbids the vehicle type
     */
    public BigDecimal getWsefByVehicleTypeAndWindSpeed(VehicleType vehicleType, Double windSpeed) {
        if (windSpeed == null) {
            return BigDecimal.ZERO;
        }
        List<WindSpeedExtraFee> conditions = windSpeedExtraFeeRepository.findAllByVehicleType(vehicleType);
        for(WindSpeedExtraFee condition : conditions){
            if(ConditionRangeUtils.isInRange(windSpeed, condition.getMinValue(), condition.getMaxValue()))
            {
                if(condition.isErrorIsThrown()) {
                    throw new VehicleTypeUsageForbiddenException("Usage of selected vehicle type is forbidden");
                }
                return condition.getFee();
            }
        }
        return BigDecimal.ZERO;
    }

    public WindSpeedExtraFee getWsefById(UUID id){
        return windSpeedExtraFeeRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(
                "Weather phenomenon condition not found with id" + id));
    }

    public List<WindSpeedExtraFee> getWsef(){
        return windSpeedExtraFeeRepository.findAll();
    }

    public void postWsef(WindSpeedExtraFeeCondition condition){
        WindSpeedExtraFee windSpeedExtraFee = new WindSpeedExtraFee();
        windSpeedExtraFee.setVehicleType(condition.getVehicleType());
        windSpeedExtraFee.setMinValue(condition.getMinValue());
        windSpeedExtraFee.setMaxValue(condition.getMaxValue());
        windSpeedExtraFee.setFee(condition.getFee());
        windSpeedExtraFee.setErrorIsThrown(condition.isErrorIsThrown());
        windSpeedExtraFeeRepository.save(windSpeedExtraFee);
    }

    public void putWsef(UUID id, WindSpeedExtraFeeCondition condition){
        WindSpeedExtraFee existingCondition = getWsefById(id);
        existingCondition.setVehicleType(condition.getVehicleType());
        existingCondition.setMinValue(condition.getMinValue());
        existingCondition.setMaxValue(condition.getMaxValue());
        existingCondition.setFee(condition.getFee());
        existingCondition.setErrorIsThrown(condition.isErrorIsThrown());
        windSpeedExtraFeeRepository.save(existingCondition);
    }

    public void deleteWsefById(UUID id){
        WindSpeedExtraFee condition = getWsefById(id);
        windSpeedExtraFeeRepository.delete(condition);
    }
}
