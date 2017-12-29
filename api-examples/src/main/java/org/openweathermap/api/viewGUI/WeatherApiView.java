package org.openweathermap.api.viewGUI;

import org.openweathermap.api.UrlConnectionWeatherClient;
import org.openweathermap.api.WeatherClient;
import org.openweathermap.api.model.Coordinate;
import org.openweathermap.api.model.currentweather.CurrentWeather;
import org.openweathermap.api.model.forecast.ForecastInformation;
import org.openweathermap.api.model.forecast.hourly.HourlyForecast;
import org.openweathermap.api.query.*;
import org.openweathermap.api.query.currentweather.CurrentWeatherOneLocationQuery;
import org.openweathermap.api.query.currentweather.InCycle;
import org.openweathermap.api.query.forecast.hourly.ByCityName;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;



public class WeatherApiView extends JFrame{
    private JTextField oneLocationCityTextField;
    private JTextField oneLocationCountryTextField;
    private JTextField oneLocationTemperatureTextField;
    private JTextField oneLocationHumidityTextField;
    private JTextField oneLocationPressureTextField;
    private JTextField multipleLocationLongitudeTextField;
    private JTextField multipleLocationLatitudeTextField;
    private JTextField multipleLocationNumberOfCitiesTextField;
    private JTextArea textArea1;
    private JTextField hourlyForecastCityTextField;
    private JTextField hourlyForecastCountryTextField;
    private JTextField hourlyForecastCountOfForecastTextField;
    private JTextArea textArea2;
    private JButton OneLocationButton;
    private JButton button2;
    private JButton button3;
    private JPanel mainPanel;

    private static final String API_KEY = "YOUR_API_KEY_HERE";

    public WeatherApiView() throws HeadlessException {

        setContentPane(mainPanel);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        pack();
        setVisible(true);
        setLocationRelativeTo(null);


        OneLocationButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                WeatherClient client = new UrlConnectionWeatherClient(API_KEY);
                CurrentWeatherOneLocationQuery currentWeatherOneLocationQuery = QueryBuilderPicker.pick()
                        .currentWeather()                   // get current weather
                        .oneLocation()                      // for one location
                        .byCityName(oneLocationCityTextField.getText())               // for Warsaw city
                        .countryCode(oneLocationCountryTextField.getText())                  // in Poland
                        .type(org.openweathermap.api.query.Type.ACCURATE)                // with Accurate search
                        .language(Language.ENGLISH)         // in English language
                        .responseFormat(ResponseFormat.JSON)// with JSON response format
                        .unitFormat(UnitFormat.METRIC)      // in metric units
                        .build();
                CurrentWeather currentWeather = client.getCurrentWeather(currentWeatherOneLocationQuery);
                oneLocationTemperatureTextField.setText(Double.toString(currentWeather.getMainParameters().getTemperature()));
                oneLocationHumidityTextField.setText(Double.toString(currentWeather.getMainParameters().getHumidity()));
                oneLocationPressureTextField.setText(Double.toString(currentWeather.getMainParameters().getPressure()));
            }
        });
        button2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                WeatherClient client = new UrlConnectionWeatherClient(API_KEY);
                InCycle inCycle = QueryBuilderPicker.pick()
                        .currentWeather()                                       // get current weather
                        .multipleLocations()                                    // for multiple locations
                        .inCycle(new Coordinate(multipleLocationLongitudeTextField.getText(), multipleLocationLatitudeTextField.getText()), Integer.parseInt(multipleLocationNumberOfCitiesTextField.getText()))    // get weather for 10 closest to coordinate cities
                        .language(Language.ENGLISH)                             // in English language
                        .unitFormat(UnitFormat.METRIC)                          // in metric units
                        .build();
                java.util.List<CurrentWeather> currentWeatherList = client.getCurrentWeather(inCycle);
                for (CurrentWeather currentWeather : currentWeatherList) {
                    textArea1.append(prettyPrint(currentWeather));
                    textArea1.append("\n");
                }
            }

            private String prettyPrint(CurrentWeather currentWeather) {
                return String.format(
                        "Current weather in %s(%s):\ntemperature: %.1f ℃\nhumidity: %.1f %%\npressure: %.1f hPa\n",
                        currentWeather.getCityName(), currentWeather.getSystemParameters().getCountry(),
                        currentWeather.getMainParameters().getTemperature(),
                        currentWeather.getMainParameters().getHumidity(),
                        currentWeather.getMainParameters().getPressure()
                );
            }
        });
        button3.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                WeatherClient client = new UrlConnectionWeatherClient(API_KEY);
                ByCityName byCityNameForecast = QueryBuilderPicker.pick()
                        .forecast()                                         // get forecast
                        .hourly()                                           // it should be hourly forecast
                        .byCityName(hourlyForecastCityTextField.getText())                              // for Warsaw city
                        .countryCode(hourlyForecastCountryTextField.getText())                                  // in Poland
                        .unitFormat(UnitFormat.METRIC)                      // in Metric units
                        .language(Language.ENGLISH)                         // in English
                        .count(Integer.parseInt(hourlyForecastCountOfForecastTextField.getText()))                                           // limit results to 5 forecasts
                        .build();
                ForecastInformation<HourlyForecast> forecastInformation = client.getForecastInformation(byCityNameForecast);
                System.out.println("Forecasts for " + forecastInformation.getCity() + ":");
                for (HourlyForecast forecast : forecastInformation.getForecasts()) {
                    System.out.println(prettyPrint(forecast));
                    textArea2.append(prettyPrint(forecast));
                    textArea2.append("\n");
                }
            }

            private  String prettyPrint(HourlyForecast hourlyForecast) {
                return String.format(
                        "Forecast for %s:\ntemperature: %.1f ℃\nhumidity: %.1f %%\npressure: %.1f hPa\n",
                        hourlyForecast.getDateTime().toString(),
                        hourlyForecast.getMainParameters().getTemperature(),
                        hourlyForecast.getMainParameters().getHumidity(),
                        hourlyForecast.getMainParameters().getPressure()
                );
            }
        });
    }
}
