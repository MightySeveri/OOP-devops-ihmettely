import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.sql.SQLException;
import java.sql.DriverManager;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class ConversionDatabaseTest {
    @TempDir
    Path directory;

    @Test
    void savesConversionsWithRelatedUnits() throws SQLException {
        ConversionDatabase database = new ConversionDatabase(directory.resolve("test.db"));
        database.initialize();
        List<ConversionDatabase.Unit> units = database.getUnits("en");

        assertEquals(List.of("Celsius", "Fahrenheit", "Kelvin"),
                units.stream().map(ConversionDatabase.Unit::name).toList());

        database.saveConversion(0, units.get(0), units.get(1), 32);
        assertEquals(1, database.getHistory("en").size());
        assertTrue(database.getHistory("en").get(0).endsWith("0.00 Celsius = 32.00 Fahrenheit"));

        assertThrows(SQLException.class, () -> database.saveConversion(0,
                new ConversionDatabase.Unit(999, "Unknown", "Unknown", "Unknown"), units.get(0), 0));
    }

    @Test
    void readsNamesAndDescriptionsFromEachLanguageTable() throws SQLException {
        ConversionDatabase database = new ConversionDatabase(directory.resolve("localized.db"));
        database.initialize();
        for (String language : List.of("en", "fi", "sv", "ja", "ar")) {
            List<ConversionDatabase.Unit> units = database.getUnits(language);
            assertEquals(3, units.size());
            assertEquals("Celsius", units.get(0).key());
            assertTrue(units.stream().allMatch(unit -> !unit.description().isBlank()));
            assertEquals(units.get(0).name(), units.get(0).toString());
        }
        assertEquals("摂氏", database.getUnits("ja").get(0).name());
        assertEquals("درجة مئوية", database.getUnits("ar").get(0).name());
        assertEquals("Vesi jäätyy 0 celsiusasteessa.", database.getUnits("fi").get(0).description());
        assertEquals("Vatten fryser vid 0 grader Celsius.", database.getUnits("sv").get(0).description());
    }

    @Test
    void fallsBackToEnglishAndUsesLiveDatabaseTranslations() throws SQLException {
        Path file = directory.resolve("fallback.db");
        ConversionDatabase database = new ConversionDatabase(file);
        database.initialize();
        List<ConversionDatabase.Unit> english = database.getUnits("en");
        assertEquals(english, database.getUnits("de"));
        assertEquals(english, database.getUnits("ar; DROP TABLE units"));
        try (var connection = DriverManager.getConnection("jdbc:sqlite:" + file.toAbsolutePath());
             var statement = connection.createStatement()) {
            statement.executeUpdate("DELETE FROM unit_ar WHERE id = 2");
            statement.executeUpdate("UPDATE unit_ar SET name = 'اسم من قاعدة البيانات', "
                    + "description = 'وصف من قاعدة البيانات' WHERE id = 1");
        }
        List<ConversionDatabase.Unit> arabic = database.getUnits("ar");
        assertEquals("اسم من قاعدة البيانات", arabic.get(0).name());
        assertEquals("وصف من قاعدة البيانات", arabic.get(0).description());
        assertEquals(english.get(1), arabic.get(1));

        double result = new temperatureConverter().convert(0, arabic.get(0).key(), arabic.get(1).key());
        assertEquals(32, result, 0.001);
        database.saveConversion(0, arabic.get(0), arabic.get(1), result);
        assertTrue(database.getHistory("ar").get(0).endsWith(
                "0.00 اسم من قاعدة البيانات = 32.00 Fahrenheit"));
        assertEquals(database.getHistory("en"), database.getHistory("de"));
    }

    @Test
    void initializesExistingDatabaseWithoutLosingHistory() throws SQLException {
        Path file = directory.resolve("existing.db");
        try (var connection = DriverManager.getConnection("jdbc:sqlite:" + file.toAbsolutePath());
             var statement = connection.createStatement()) {
            statement.executeUpdate("CREATE TABLE units (id INTEGER PRIMARY KEY, name TEXT NOT NULL UNIQUE)");
            statement.executeUpdate("CREATE TABLE conversions (id INTEGER PRIMARY KEY, input_value REAL NOT NULL, "
                    + "from_unit_id INTEGER NOT NULL REFERENCES units(id), "
                    + "to_unit_id INTEGER NOT NULL REFERENCES units(id), result_value REAL NOT NULL, "
                    + "created_at TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP)");
            statement.executeUpdate("INSERT INTO units VALUES (1, 'Celsius'), (2, 'Fahrenheit'), (3, 'Kelvin')");
            statement.executeUpdate("INSERT INTO conversions "
                    + "(input_value, from_unit_id, to_unit_id, result_value) VALUES (0, 1, 2, 32)");
        }
        ConversionDatabase database = new ConversionDatabase(file);
        database.initialize();
        database.initialize();
        assertEquals(1, database.getHistory("ja").size());
        assertTrue(database.getHistory("ja").get(0).endsWith("0.00 摂氏 = 32.00 華氏"));
    }
}
