import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

class ConversionDatabase {
    record Unit(int id, String name) {
        @Override
        public String toString() {
            return name;
        }
    }

    private final String url;

    ConversionDatabase(Path file) {
        url = "jdbc:sqlite:" + file.toAbsolutePath();
    }

    private Connection connect() throws SQLException {
        Connection connection = DriverManager.getConnection(url);
        try (Statement statement = connection.createStatement()) {
            statement.execute("PRAGMA foreign_keys = ON");
        }
        return connection;
    }

    void initialize() throws SQLException {
        try (Connection connection = connect(); Statement statement = connection.createStatement()) {
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS units ("
                    + "id INTEGER PRIMARY KEY, name TEXT NOT NULL UNIQUE)");
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS conversions ("
                    + "id INTEGER PRIMARY KEY, input_value REAL NOT NULL, "
                    + "from_unit_id INTEGER NOT NULL REFERENCES units(id), "
                    + "to_unit_id INTEGER NOT NULL REFERENCES units(id), "
                    + "result_value REAL NOT NULL, created_at TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP)");
            statement.executeUpdate("INSERT OR IGNORE INTO units (id, name) VALUES "
                    + "(1, 'Celsius'), (2, 'Fahrenheit'), (3, 'Kelvin')");
        }
    }

    List<Unit> getUnits() throws SQLException {
        List<Unit> units = new ArrayList<>();
        try (Connection connection = connect();
             Statement statement = connection.createStatement();
             ResultSet results = statement.executeQuery("SELECT id, name FROM units ORDER BY id")) {
            while (results.next()) {
                units.add(new Unit(results.getInt("id"), results.getString("name")));
            }
        }
        return units;
    }

    void saveConversion(double input, Unit from, Unit to, double result) throws SQLException {
        String sql = "INSERT INTO conversions (input_value, from_unit_id, to_unit_id, result_value) "
                + "VALUES (?, ?, ?, ?)";
        try (Connection connection = connect(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setDouble(1, input);
            statement.setInt(2, from.id());
            statement.setInt(3, to.id());
            statement.setDouble(4, result);
            statement.executeUpdate();
        }
    }

    List<String> getHistory() throws SQLException {
        List<String> history = new ArrayList<>();
        String sql = "SELECT c.input_value, f.name AS from_name, c.result_value, "
                + "t.name AS to_name, c.created_at FROM conversions c "
                + "JOIN units f ON c.from_unit_id = f.id "
                + "JOIN units t ON c.to_unit_id = t.id ORDER BY c.id DESC LIMIT 20";
        try (Connection connection = connect();
             Statement statement = connection.createStatement();
             ResultSet results = statement.executeQuery(sql)) {
            while (results.next()) {
                history.add(String.format("%s: %.2f %s = %.2f %s",
                        results.getString("created_at"), results.getDouble("input_value"),
                        results.getString("from_name"), results.getDouble("result_value"),
                        results.getString("to_name")));
            }
        }
        return history;
    }
}
