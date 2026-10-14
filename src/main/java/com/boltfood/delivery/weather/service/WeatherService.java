package com.boltfood.delivery.weather.service;

import com.boltfood.delivery.weather.model.Observation;
import com.boltfood.delivery.weather.repository.WeatherRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.net.URL;

@Slf4j
@Service
public class WeatherService {

    private final WeatherRepository weatherRepository;
    private final String WEATHER_URL = "https://www.ilmateenistus.ee/ilma_andmed/xml/observations.php";

    public WeatherService(WeatherRepository weatherRepository) {
        this.weatherRepository = weatherRepository;
    }

    public void getWeather(){
        parseData(WEATHER_URL);
    }

    public void parseData(String url) {

        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(new URL(url).openStream());

            doc.getDocumentElement().normalize();

            NodeList list = doc.getElementsByTagName("station");

            for (int temp = 0; temp < list.getLength(); temp++) {
                Node node = list.item(temp);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;
                    String stationName = getFieldValue(element,"name");
                    String stationWmoCode = getFieldValue(element,"wmocode");
                    String airTemperature = getFieldValue(element,"airtemperature");
                    String windSpeed = getFieldValue(element,"windspeed");
                    String weatherPhenomenon = getFieldValue(element,"phenomenon");

                    Observation entity = new Observation(stationName, stationWmoCode, airTemperature, windSpeed, weatherPhenomenon);
                    weatherRepository.save(entity);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getFieldValue(Element element, String fieldName){

        Node value = element.getElementsByTagName(fieldName).item(0);
        if (value != null){
            return value.getTextContent();
        }
        else return null;
    }
}
