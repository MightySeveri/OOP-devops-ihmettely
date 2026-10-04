# OOP-devops-ihmettely

## Temperature Converter

The JavaFX app converts between Celsius, Fahrenheit, and Kelvin. Its unit choices
come from a SQLite `units` table. Each conversion is saved in a related
`conversions` table and the 20 most recent conversions appear in the window.

Run it from the `temperatureConverter` directory with Java 21 or newer:

```sh
mvn javafx:run
```

The app creates `conversions.db` in that directory on first launch. Run the
tests with `mvn test`.
