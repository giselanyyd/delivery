package com.boltfood.delivery.fee.model;

import java.util.Arrays;
import java.util.Optional;
import java.util.Set;

public enum Phenomenon {
    SNOW("Light snow shower", "Moderate snow shower", "Heavy snow shower", "Light snowfall",
            "Moderate snowfall", "Heavy snowfall", "Blowing snow", "Drifting snow"),
    SLEET("Light sleet", "Moderate sleet"),
    RAIN("Light shower", "Moderate shower", "Heavy shower",
            "Light rain", "Moderate rain", "Heavy rain"),
    GLAZE("Glaze"),
    HAIL("Hail"),
    THUNDER("Thunder");

    private final Set<String> phenomenonTypes;

    Phenomenon(String... phenomenonTypes) {
        this.phenomenonTypes = Set.of(phenomenonTypes);
    }

    /**
     * Maps a weather phenomenon description from the weather service to its category.
     *
     * @param phenomenonType the description, e.g. "Light snow shower", may be {@code null}
     * @return the matching category, or empty if the description is {@code null} or unknown
     */
    public static Optional<Phenomenon> getPhenomenon(String phenomenonType) {
        if (phenomenonType == null) return Optional.empty();
        return Arrays.stream(values())
                .filter(c -> c.phenomenonTypes.contains(phenomenonType)).findFirst();
    }

}
