package com.boltfood.delivery.fee.service;

import com.boltfood.delivery.exception.DuplicateConditionException;
import com.boltfood.delivery.exception.EntityNotFoundException;
import com.boltfood.delivery.exception.VehicleTypeUsageForbiddenException;
import com.boltfood.delivery.fee.model.VehicleType;
import com.boltfood.delivery.weather.model.Phenomenon;
import com.boltfood.delivery.fee.model.WeatherPhenomenonExtraFee;
import com.boltfood.delivery.fee.dto.WeatherPhenomenonExtraFeeCondition;
import com.boltfood.delivery.fee.repository.WeatherPhenomenonExtraFeeRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class WeatherPhenomenonExtraFeeService {
    private final WeatherPhenomenonExtraFeeRepository weatherPhenomenonExtraFeeRepository;

    public WeatherPhenomenonExtraFeeService(WeatherPhenomenonExtraFeeRepository weatherPhenomenonExtraFeeRepository){
        this.weatherPhenomenonExtraFeeRepository = weatherPhenomenonExtraFeeRepository;
    }

    public BigDecimal getWpefByVehicleTypeAndPhenomenon(VehicleType vehicleType, String phenomenon){
        Optional<Phenomenon> value = Phenomenon.getPhenomenon(phenomenon);
        if(value.isPresent()){
            Optional<WeatherPhenomenonExtraFee> condition = weatherPhenomenonExtraFeeRepository.findByVehicleTypeAndPhenomenon(vehicleType, value.get());
            if (condition.isEmpty()){
                return BigDecimal.ZERO;
            }
            if(condition.get().isErrorIsThrown()) {
                throw new VehicleTypeUsageForbiddenException("Usage of selected vehicle type is forbidden");
            }
            return condition.get().getFee();
        }
        return BigDecimal.ZERO;
    }

    public WeatherPhenomenonExtraFee getWpefById(UUID id){
        return weatherPhenomenonExtraFeeRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(
                "Weather phenomenon condition not found with id" + id));
    }

    public List<WeatherPhenomenonExtraFee> getWpef(){
        return weatherPhenomenonExtraFeeRepository.findAll();
    }

    public void postWpef(WeatherPhenomenonExtraFeeCondition condition){
        checkForDuplication(condition, null);
        WeatherPhenomenonExtraFee weatherPhenomenonExtraFee = new WeatherPhenomenonExtraFee();
        weatherPhenomenonExtraFee.setVehicleType(condition.getVehicleType());
        weatherPhenomenonExtraFee.setPhenomenon(condition.getPhenomenon());
        weatherPhenomenonExtraFee.setFee(condition.getFee());
        weatherPhenomenonExtraFee.setErrorIsThrown(condition.isErrorIsThrown());
        weatherPhenomenonExtraFeeRepository.save(weatherPhenomenonExtraFee);
    }

    public void putWpef(UUID id, WeatherPhenomenonExtraFeeCondition condition){
        WeatherPhenomenonExtraFee existingCondition = getWpefById(id);
        checkForDuplication(condition, id);
        existingCondition.setVehicleType(condition.getVehicleType());
        existingCondition.setPhenomenon(condition.getPhenomenon());
        existingCondition.setFee(condition.getFee());
        existingCondition.setErrorIsThrown(condition.isErrorIsThrown());
        weatherPhenomenonExtraFeeRepository.save(existingCondition);
    }

    private void checkForDuplication(WeatherPhenomenonExtraFeeCondition condition, UUID currentId){
        Optional<WeatherPhenomenonExtraFee> existing = weatherPhenomenonExtraFeeRepository.findByVehicleTypeAndPhenomenon(condition.getVehicleType(), condition.getPhenomenon());
        if (existing.isPresent() && !existing.get().getId().equals(currentId)) {
            throw new DuplicateConditionException("Condition for this vehicle type and phenomenon already exists");
        }
    }

    public void deleteWpefById(UUID id){
        WeatherPhenomenonExtraFee condition = getWpefById(id);
        weatherPhenomenonExtraFeeRepository.delete(condition);
    }
}
