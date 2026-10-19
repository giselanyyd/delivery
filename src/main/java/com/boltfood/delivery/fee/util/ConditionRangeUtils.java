package com.boltfood.delivery.fee.util;

public final class ConditionRangeUtils {

    private ConditionRangeUtils(){
    }

    public static boolean isInRange(double airTemperature, Double minValue, Double maxValue) {
        if (minValue != null && maxValue != null) {
            return minValue <= airTemperature && airTemperature <= maxValue;
        }
        if (minValue != null) {
            return airTemperature > minValue;
        }
        if (maxValue != null) {
            return airTemperature < maxValue;
        }
        return true;
    }
}
