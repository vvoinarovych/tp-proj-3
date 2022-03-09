package model.openweathermap;

import com.fasterxml.jackson.annotation.*;

import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class OpenWeatherMapForecast {

    private Main main;
    private Wind wind;
    private Coord coord;
    private Sys sys;
    private String name;

    @JsonProperty("id")
    private int cityId;

    @JsonIgnore
    private final Map<String, Object> additionalProperties = new HashMap<>();

    public Main getMain() {
        return main;
    }

    public Wind getWind() {
        return wind;
    }

    public String getName() {
        return name;
    }

    public int getCityId() {
        return cityId;
    }

    public Sys getSys() {
        return sys;
    }

    public Coord getCoord() {
        return coord;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    @Override
    public String toString() {
        return "OpenWeatherMapForecast{" +
                "main=" + main +
                ", wind=" + wind +
                ", coord=" + coord +
                ", sys=" + sys +
                ", name='" + name + '\'' +
                ", cityId=" + cityId +
                '}';
    }
}
