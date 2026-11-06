# Temperature Converter

Run with `mvn javafx:run` and choose English, Finnish, Swedish, Japanese or Arabic
in the existing startup dialog. Arabic uses right-to-left layout. Run the tests
with `mvn clean verify`; the JaCoCo report is in `target/site/jacoco/index.html`.

## Database localization: table method

The database uses one localized unit table per language: `unit_en`, `unit_fi`,
`unit_sv`, `unit_ja` and `unit_ar`. Each has the same columns: `id`, `name` and
`description`. The ID references the existing `units` table, whose stable keys
are used for calculations and conversion history references. Localized names
are never used to decide which conversion formula to apply.

`src/main/resources/init.sql` creates and seeds the tables in UTF-8. It runs at
startup against `conversions.db`, including existing databases, without deleting
saved conversions. SQLite stores Unicode text directly, so Japanese and Arabic
do not require a separate server or connection character-set setting.

The selected language controls both the ResourceBundle and the DAO query.
Static labels remain in `.properties` files. Dropdown names, unit descriptions,
result unit names and history unit names come from the database. The DAO selects
the language table from a fixed allowlist and falls back to English for an
unsupported language or a missing unit translation.

The table method fits this small application because it has three units and a
fixed set of five languages. Each language can be inspected or exported
independently, and it adds localization without restructuring the existing
measurement tables. The tradeoff is repeated table structure: adding a language
requires a new table, seed data and a DAO allowlist change. Keeping those schemas
and IDs consistent is less convenient than a translation table as the number of
languages grows.
