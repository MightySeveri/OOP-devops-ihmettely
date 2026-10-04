import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class temperatureConverterTest {

    private final temperatureConverter converter = new temperatureConverter();

    @Test
    void fahrenheitToCelsius() {
        assertEquals(0.0, converter.fahrenheitToCelsius(32.0), 0.001);
    }

    @Test
    void celsiusToFahrenheit() {
        assertEquals(32.0, converter.celsiusToFahrenheit(0.0), 0.001);
    }

    @Test
    void kelvinToCelsius() {
        assertEquals(0.0, converter.kelvinToCelsius(273.15), 0.001);
        assertEquals(100.0, converter.kelvinToCelsius(373.15), 0.001);
        assertEquals(-273.15, converter.kelvinToCelsius(0.0), 0.001);
}

    @Test
    void isExtremeTemperature() {
        assertTrue(converter.isExtremeTemperature(-41.0));
        assertTrue(converter.isExtremeTemperature(51.0));
        assertFalse(converter.isExtremeTemperature(20.0));
    }

    @Test
    void convertsBetweenAllUnits() {
        assertEquals(212, converter.convert(100, "Celsius", "Fahrenheit"), 0.001);
        assertEquals(373.15, converter.convert(212, "Fahrenheit", "Kelvin"), 0.001);
        assertEquals(32, converter.convert(273.15, "Kelvin", "Fahrenheit"), 0.001);
    }
}
