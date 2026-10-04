import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.sql.SQLException;
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
        List<ConversionDatabase.Unit> units = database.getUnits();

        assertEquals(List.of("Celsius", "Fahrenheit", "Kelvin"),
                units.stream().map(ConversionDatabase.Unit::name).toList());

        database.saveConversion(0, units.get(0), units.get(1), 32);
        assertEquals(1, database.getHistory().size());
        assertTrue(database.getHistory().get(0).endsWith("0.00 Celsius = 32.00 Fahrenheit"));

        assertThrows(SQLException.class, () -> database.saveConversion(0,
                new ConversionDatabase.Unit(999, "Unknown"), units.get(0), 0));
    }
}
