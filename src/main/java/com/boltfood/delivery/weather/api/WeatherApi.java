package com.boltfood.delivery.weather.api;

import com.boltfood.delivery.common.City;
import com.boltfood.delivery.exception.WeatherApiException;
import com.boltfood.delivery.weather.model.Observation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class WeatherApi {

    private static final String WEATHER_URL = "https://www.ilmateenistus.ee/ilma_andmed/xml/observations.php";
    private static final Map<String, City> STATION_CITIES = Map.of(
        "Tallinn-Harku", City.TALLINN,
        "Tartu-Tõravere", City.TARTU,
        "Pärnu", City.PÄRNU
    );

    public List<Observation> getWeatherFromApi(){
        Document document = fetchData();
        return parseData(document);
    }

    private Document fetchData() {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            return db.parse(URI.create(WEATHER_URL).toURL().openStream());
        } catch (Exception e) {
            log.error("Couldn't fetch weather data", e);
            throw new WeatherApiException("Couldn't fetch weather data");
        }
    }

    private List<Observation> parseData(Document doc) {
        List<Observation> observations = new ArrayList<>();
        try {
            doc.getDocumentElement().normalize();
            String observationsTimestamp = doc.getDocumentElement().getAttribute("timestamp");

            NodeList list = doc.getElementsByTagName("station");

            for (int temp = 0; temp < list.getLength(); temp++) {
                Node node = list.item(temp);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;
                    String stationName = getFieldValue(element,"name");
                    if(STATION_CITIES.containsKey(stationName)) {
                        String stationWmoCode = getFieldValue(element, "wmocode");
                        String airTemperature = getFieldValue(element, "airtemperature");
                        String windSpeed = getFieldValue(element, "windspeed");
                        String weatherPhenomenon = getFieldValue(element, "phenomenon");

                        Observation entity = new Observation(Long.parseLong(observationsTimestamp), stationName, STATION_CITIES.get(stationName), convertStringToInteger(stationWmoCode), convertStringToDouble(airTemperature), convertStringToDouble(windSpeed), weatherPhenomenon);
                        observations.add(entity);
                    }
                }
            }
            return observations;
        } catch (Exception e) {
            log.error("Couldn't parse weather data", e);
            throw new WeatherApiException("Couldn't parse weather data");

        }
    }

    private String getFieldValue(Element element, String fieldName){

        Node value = element.getElementsByTagName(fieldName).item(0);
        if (value != null){
            return value.getTextContent();
        }
        else return null;
    }

    private Integer convertStringToInteger(String value){
        if(value != null){
            return  Integer.parseInt(value);
        }
        return null;
    }

    private Double convertStringToDouble(String value){
        if(value != null){
            return  Double.parseDouble(value);
        }
        return null;
    }

}
