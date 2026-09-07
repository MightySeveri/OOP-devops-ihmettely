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
    void isExtremeTemperature() {
        assertTrue(converter.isExtremeTemperature(-41.0));
        assertTrue(converter.isExtremeTemperature(51.0));
        assertFalse(converter.isExtremeTemperature(20.0));
    }
}
