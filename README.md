## Temperature Converter

The JavaFX app converts between Celsius, Fahrenheit, and Kelvin. Its unit choices
come from a SQLite `units` table. Each conversion is saved in a related
`conversions` table and the 20 most recent conversions appear in the window.

The app creates `conversions.db` in that directory on first launch.
