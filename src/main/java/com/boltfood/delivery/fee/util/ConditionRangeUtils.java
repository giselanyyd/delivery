package com.boltfood.delivery.fee.util;

public final class ConditionRangeUtils {

    private ConditionRangeUtils(){
    }

    /**
     * Checks whether a value falls within a rule's range.
     * <ul>
     *   <li>both bounds set: {@code minValue <= value <= maxValue}</li>
     *   <li>only {@code minValue} set: {@code value > minValue}</li>
     *   <li>only {@code maxValue} set: {@code value < maxValue}</li>
     *   <li>no bounds set: always {@code true}</li>
     * </ul>
     *
     * @param valueToCheck the value to check
     * @param minValue       the lower bound, may be {@code null}
     * @param maxValue       the upper bound, may be {@code null}
     * @return {@code true} if the value is in range
     */
    public static boolean isInRange(double valueToCheck, Double minValue, Double maxValue) {
        if (minValue != null && maxValue != null) {
            return minValue <= valueToCheck && valueToCheck <= maxValue;
        }
        if (minValue != null) {
            return valueToCheck > minValue;
        }
        if (maxValue != null) {
            return valueToCheck < maxValue;
        }
        return true;
    }
}
