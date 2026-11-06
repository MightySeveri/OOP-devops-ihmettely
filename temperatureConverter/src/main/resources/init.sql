PRAGMA encoding = 'UTF-8';
CREATE TABLE IF NOT EXISTS units (
    id INTEGER PRIMARY KEY, name TEXT NOT NULL UNIQUE
);
CREATE TABLE IF NOT EXISTS conversions (
    id INTEGER PRIMARY KEY, input_value REAL NOT NULL,
    from_unit_id INTEGER NOT NULL REFERENCES units(id),
    to_unit_id INTEGER NOT NULL REFERENCES units(id),
    result_value REAL NOT NULL, created_at TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP
);
INSERT OR IGNORE INTO units (id, name) VALUES
    (1, 'Celsius'), (2, 'Fahrenheit'), (3, 'Kelvin');

CREATE TABLE IF NOT EXISTS unit_en (
    id INTEGER PRIMARY KEY REFERENCES units(id), name TEXT NOT NULL, description TEXT NOT NULL
);
CREATE TABLE IF NOT EXISTS unit_fi (
    id INTEGER PRIMARY KEY REFERENCES units(id), name TEXT NOT NULL, description TEXT NOT NULL
);
CREATE TABLE IF NOT EXISTS unit_sv (
    id INTEGER PRIMARY KEY REFERENCES units(id), name TEXT NOT NULL, description TEXT NOT NULL
);
CREATE TABLE IF NOT EXISTS unit_ja (
    id INTEGER PRIMARY KEY REFERENCES units(id), name TEXT NOT NULL, description TEXT NOT NULL
);
CREATE TABLE IF NOT EXISTS unit_ar (
    id INTEGER PRIMARY KEY REFERENCES units(id), name TEXT NOT NULL, description TEXT NOT NULL
);

INSERT OR IGNORE INTO unit_en (id, name, description) VALUES
    (1, 'Celsius', 'Water freezes at 0 degrees Celsius.'),
    (2, 'Fahrenheit', 'Water freezes at 32 degrees Fahrenheit.'),
    (3, 'Kelvin', 'Absolute zero is 0 kelvin.');
INSERT OR IGNORE INTO unit_fi (id, name, description) VALUES
    (1, 'Celsius', 'Vesi jäätyy 0 celsiusasteessa.'),
    (2, 'Fahrenheit', 'Vesi jäätyy 32 fahrenheitasteessa.'),
    (3, 'Kelvin', 'Absoluuttinen nollapiste on 0 kelviniä.');
INSERT OR IGNORE INTO unit_sv (id, name, description) VALUES
    (1, 'Celsius', 'Vatten fryser vid 0 grader Celsius.'),
    (2, 'Fahrenheit', 'Vatten fryser vid 32 grader Fahrenheit.'),
    (3, 'Kelvin', 'Den absoluta nollpunkten är 0 kelvin.');
INSERT OR IGNORE INTO unit_ja (id, name, description) VALUES
    (1, '摂氏', '水は摂氏0度で凍ります。'),
    (2, '華氏', '水は華氏32度で凍ります。'),
    (3, 'ケルビン', '絶対零度は0ケルビンです。');
INSERT OR IGNORE INTO unit_ar (id, name, description) VALUES
    (1, 'درجة مئوية', 'يتجمد الماء عند صفر درجة مئوية.'),
    (2, 'فهرنهايت', 'يتجمد الماء عند 32 درجة فهرنهايت.'),
    (3, 'كلفن', 'الصفر المطلق يساوي صفر كلفن.');
