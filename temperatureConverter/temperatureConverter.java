public class temperatureConverter {
    double fahrenheit;
    double celsius;

    double fahrenheitToCelsius (double fahrenheit) {
        double conversion = (fahrenheit - 32) * 5 / 9;

        return conversion;
    }

     double celsiusToFahrenheit (double celsius) {
        double conversion = (celsius * 9/5) + 32;

        return conversion;
    }

    double kelvinToCelsius(double kelvin) {
    double conversion = kelvin - 273.15;

    return conversion;
}

    boolean isExtremeTemperature(double celsius) {
        if (celsius<-40) {
            return true;
        } else if (celsius >50) {
            return true;
        } else {
            return false;
        }
    }

    double convert(double value, String from, String to) {
        double celsiusValue = switch (from) {
            case "Fahrenheit" -> fahrenheitToCelsius(value);
            case "Kelvin" -> kelvinToCelsius(value);
            default -> value;
        };
        return switch (to) {
            case "Fahrenheit" -> celsiusToFahrenheit(celsiusValue);
            case "Kelvin" -> celsiusValue + 273.15;
            default -> celsiusValue;
        };
    }
}
