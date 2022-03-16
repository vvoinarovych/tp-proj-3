package service;

import dao.LocationDao;
import dao.WeatherDao;
import model.Location;
import utils.mapper.LocationMapper;
import utils.mapper.WeatherMapper;
import model.WeatherDto;
import model.Weather;
import utils.averager.Averager;

import java.time.LocalDate;
import java.util.List;

public class WeatherService {

    final WeatherClient openWeatherMapClient;
    final WeatherClient weatherStackClient;
    final Averager<WeatherDto> weatherAverager;
    final WeatherMapper weatherMapper;
    final LocationMapper locationMapper;
    final LocationDao locationDao;
    final WeatherDao weatherDao;


    public WeatherService(OpenWeatherMapClient openWeatherMapClient, WeatherStackClient weatherStackClient, Averager<WeatherDto> weatherAverager, WeatherMapper weatherMapper, LocationMapper locationMapper, LocationDao locationDao, WeatherDao weatherDao) {
        this.openWeatherMapClient = openWeatherMapClient;
        this.weatherStackClient = weatherStackClient;
        this.weatherAverager = weatherAverager;
        this.weatherMapper = weatherMapper;
        this.locationMapper = locationMapper;
        this.locationDao = locationDao;
        this.weatherDao = weatherDao;
    }


    public WeatherDto getAverageWeatherDtoByCityNameFromBase(String city) {
        Double lat = null;
        Double lon = null;
        try {
            Location location = locationDao.findByCity(city);
            lat = location.getLatitude();
            lon = location.getLongitude();
        } catch (Exception e) {
            System.out.println("no such location in base");
        }
        return getAverageWeatherDtoByCoordinates(lat, lon);
    }

    public WeatherDto getAverageWeatherDtoByCoordinates(Double lat, Double lon) {
        WeatherDto weather1 = openWeatherMapClient.getWeatherByCoordinates(lat, lon);
        WeatherDto weather2 = weatherStackClient.getWeatherByCoordinates(lat, lon);

        WeatherDto averageWeatherDto = getAverageWeatherDto(weather1, weather2);
        averageWeatherDto.setDate(LocalDate.now());

        return averageWeatherDto;
    }

    public void saveWeather(WeatherDto weatherDto) {
        Weather weather = weatherMapper.toEntity(weatherDto);
        Location location = locationDao.findByCity(weather.getLocation().getCityName());
        if (location != null) {
            weather.setLocation(location);
            weatherDao.save(weather);
        } else {
            weatherDao.save(weather);
        }
    }

    public WeatherDto getAverageWeatherDto(WeatherDto... weathers) {
        WeatherDto weatherDto = weatherAverager.getAverage(weathers);
        Location location = locationDao.findByCity(weatherDto.getCityName());
        weatherDto.setCountryName(location.getCountryName());
        weatherDto.setRegion(location.getRegion());
        return weatherDto;
    }

    public List<Weather> getAllWeathersByDate(LocalDate date, String cityName) {
        return weatherDao.getWeatherByDateAndCity(date, cityName);
    }

    public void displayWeathers(List<Weather> weathers) {
        List<WeatherDto> listToDisplay = weathers.stream()
                .map(weatherMapper::toDto)
                .toList();

        for (WeatherDto weather : listToDisplay) {
            displayWeather(weather);
            System.out.println();
        }
    }
    public void displayWeather(WeatherDto weatherDto) {
        System.out.println("City: " + weatherDto.getCityName() +
                "\nCountry: " + weatherDto.getCountryName() +
                "\nRegion: " + weatherDto.getRegion() +
                "\nDate: " + weatherDto.getDate() +
                "\nTemperature: " + weatherDto.getTemperature() + " C" +
                "\nPressure: " + weatherDto.getPressure() + " Pa" +
                "\nHumidity: " + weatherDto.getHumidity() + " %" +
                "\nWind direction: " + weatherDto.getWindDirection() +
                "\nWind speed: " + weatherDto.getWindSpeed() + " km/hour");
    }

}
