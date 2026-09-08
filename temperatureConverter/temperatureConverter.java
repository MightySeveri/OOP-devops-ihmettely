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

    boolean isExtremeTemperature(double celsius) {
        if (celsius<-40) {
            return true;
        } else if (celsius >50) {
            return true;
        } else {
            return false;
        }
    }
}