# Temperature Converter

## 1. Assignment Description

This project is a desktop temperature converter for Celsius, Fahrenheit, and Kelvin. The deliverable is a JavaFX application that accepts a temperature and two units, displays the converted value, and saves each successful conversion to a SQLite database. The window also shows the 20 most recent conversions. The repository includes automated tests, a Maven build, a Docker image definition, and a Jenkins pipeline.

## 2. Technologies & Tools Used

| Tool | Purpose |
| --- | --- |
| Java 21 | Application language and compilation target |
| JavaFX 21.0.8 | Desktop user interface |
| SQLite and sqlite-jdbc 3.46.1.0 | Local storage through JDBC |
| Maven | Dependency management, build, and test execution |
| JUnit Jupiter 5.11.4 | Automated unit and database tests |
| JaCoCo 0.8.12 | Test coverage reports |
| Docker | Container build and execution |
| Jenkins | Build, test, coverage, and Docker pipeline |

## 3. Design Approach & Implementation Method

The application separates conversion calculations, persistence, and the user interface:

- `temperatureConverter.java` contains the temperature formulas. Its `convert` method uses Celsius as an intermediate value when converting between units. It also has a helper that identifies Celsius values below −40 or above 50 as extreme; this helper is tested but is not displayed in the interface.
- `ConversionDatabase.java` creates `units` and `conversions` tables if needed, inserts the three supported units, and stores conversions with foreign keys to those units. It uses prepared statements for inserts and retrieves the newest 20 records by ID.
- `temperatureConverterApp.java` builds the JavaFX window. It loads unit choices and history from the database, rejects invalid or non-finite numeric input, performs the conversion, saves it, and refreshes the history. Database failures are shown in the result label.

The database file is `conversions.db` in the process's working directory. It is created on first launch, so no separate database setup is required.

## 4. Testing & Quality Assurance Steps

Run the automated tests from `temperatureConverter/` with `mvn test`. Maven Surefire writes results under `target/surefire-reports/`, and JaCoCo writes an HTML report to `target/site/jacoco/index.html`. The Jenkinsfile runs the Maven build and tests, publishes test and coverage reports, then builds, tests, and pushes the Docker image when its Docker Hub credentials are configured.

### Test Cases & Results

| Scenario | Expected result | Result |
| --- | --- | --- |
| Fahrenheit to Celsius and Celsius to Fahrenheit at freezing point | `32 °F = 0 °C` and `0 °C = 32 °F` | Passed |
| Kelvin to Celsius at 0 K, 273.15 K, and 373.15 K | Correct values within 0.001 | Passed |
| Conversions among Celsius, Fahrenheit, and Kelvin | Correct values within 0.001 | Passed |
| Extreme temperature helper at −41 °C, 20 °C, and 51 °C | Extreme, normal, extreme | Passed |
| Database initialization, unit list, save, and history | Three units and a saved conversion in history | Passed |
| Save with an unknown unit ID | SQLite foreign key error | Passed |

On 7 October 2026, `mvn test` completed with **6 tests, 0 failures, 0 errors, and 0 skipped**. The run used JDK 25 and JaCoCo printed `Unsupported class file major version 69` instrumentation warnings; the test result passed, but use JDK 21 for reliable coverage collection.

For a manual GUI check, launch the app, convert `100` Celsius to Fahrenheit, and confirm the result is `212.00 Fahrenheit` and appears at the top of **Recent conversions**. Restart the app to check that history persists. Try nonnumeric input to check the validation message. These are manual verification steps; the automated tests do not launch the GUI.

## 5. How to Run

Prerequisites: JDK 21, Maven, and a graphical desktop session. Run commands from the repository root:

```bash
cd temperatureConverter
mvn clean test
mvn javafx:run
```

To build the application without launching the GUI, run `mvn package` from `temperatureConverter/`.

Docker is optional. Build the image from `temperatureConverter/` with `docker build -t temperature-converter .`. On a Linux desktop with X11/Xwayland and a valid `XAUTHORITY` file, the following command passes the display through to the container:

```bash
docker run --rm --network none \
  --security-opt label=disable \
  -e DISPLAY -e XAUTHORITY=/tmp/.Xauthority \
  -v /tmp/.X11-unix:/tmp/.X11-unix \
  -v "$XAUTHORITY:/tmp/.Xauthority:ro" \
  temperature-converter
```

The Docker command uses a temporary container, so its `conversions.db` history is lost when the container exits unless a persistent volume is mounted.
