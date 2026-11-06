import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
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
    record Unit(int id, String key, String name, String description) {
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
        try (InputStream script = ConversionDatabase.class.getResourceAsStream("/init.sql");
             Connection connection = connect(); Statement statement = connection.createStatement()) {
            if (script == null) {
                throw new SQLException("Missing init.sql");
            }
            for (String sql : new String(script.readAllBytes(), StandardCharsets.UTF_8).split(";")) {
                if (!sql.isBlank()) {
                    statement.executeUpdate(sql);
                }
            }
        } catch (IOException exception) {
            throw new SQLException("Cannot read init.sql", exception);
        }
    }

    private String unitTable(String language) {
        return switch (language) {
            case "fi", "sv", "ja", "ar" -> "unit_" + language;
            default -> "unit_en";
        };
    }

    List<Unit> getUnits(String language) throws SQLException {
        List<Unit> units = new ArrayList<>();
        String sql = "SELECT u.id, u.name AS unit_key, COALESCE(l.name, e.name) AS name, "
                + "COALESCE(l.description, e.description) AS description FROM units u "
                + "JOIN unit_en e ON e.id = u.id "
                + "LEFT JOIN " + unitTable(language) + " l ON l.id = u.id ORDER BY u.id";
        try (Connection connection = connect();
             Statement statement = connection.createStatement();
             ResultSet results = statement.executeQuery(sql)) {
            while (results.next()) {
                units.add(new Unit(results.getInt("id"), results.getString("unit_key"),
                        results.getString("name"), results.getString("description")));
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

    List<String> getHistory(String language) throws SQLException {
        List<String> history = new ArrayList<>();
        String sql = "SELECT c.input_value, COALESCE(f.name, ef.name) AS from_name, c.result_value, "
                + "COALESCE(t.name, et.name) AS to_name, c.created_at FROM conversions c "
                + "JOIN unit_en ef ON c.from_unit_id = ef.id "
                + "JOIN unit_en et ON c.to_unit_id = et.id "
                + "LEFT JOIN " + unitTable(language) + " f ON c.from_unit_id = f.id "
                + "LEFT JOIN " + unitTable(language) + " t ON c.to_unit_id = t.id "
                + "ORDER BY c.id DESC LIMIT 20";
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
