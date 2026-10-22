import java.nio.file.Path;
import java.sql.SQLException;
import java.util.Locale;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class temperatureConverterApp extends Application {
    private final temperatureConverter converter = new temperatureConverter();
    private final ConversionDatabase database = new ConversionDatabase(Path.of("conversions.db"));

    @Override
    public void start(Stage stage) throws SQLException {
        ChoiceDialog<String> language = new ChoiceDialog<>(
                "English", "English", "Suomi", "Svenska", "日本語");
        language.setTitle("Language");
        language.setHeaderText("Select a language");
        language.setContentText("Language:");
        String selected = language.showAndWait().orElse("English");
        I18n.setLocale(switch (selected) {
            case "Suomi" -> Locale.forLanguageTag("fi");
            case "Svenska" -> Locale.forLanguageTag("sv");
            case "日本語" -> Locale.JAPANESE;
            default -> Locale.ENGLISH;
        });

        database.initialize();

        TextField input = new TextField();
        input.setPromptText(I18n.get("temperature"));
        ComboBox<ConversionDatabase.Unit> from = new ComboBox<>();
        ComboBox<ConversionDatabase.Unit> to = new ComboBox<>();
        from.getItems().addAll(database.getUnits());
        to.getItems().addAll(database.getUnits());
        from.getSelectionModel().selectFirst();
        to.getSelectionModel().select(1);

        Label result = new Label(I18n.get("enterTemperature"));
        ListView<String> history = new ListView<>();
        history.getItems().addAll(database.getHistory());

        Button convert = new Button(I18n.get("convert"));
        convert.setOnAction(event -> {
            try {
                double value = Double.parseDouble(input.getText());
                if (!Double.isFinite(value)) {
                    throw new NumberFormatException();
                }
                double converted = converter.convert(value, from.getValue().name(), to.getValue().name());
                database.saveConversion(value, from.getValue(), to.getValue(), converted);
                result.setText(I18n.format("conversionResult", value,
                        from.getValue(), converted, to.getValue()));
                history.getItems().setAll(database.getHistory());
            } catch (NumberFormatException exception) {
                result.setText(I18n.get("invalidNumber"));
            } catch (SQLException exception) {
                result.setText(I18n.format("databaseError", exception.getMessage()));
            }
        });

        HBox choices = new HBox(10, input, from, new Label(I18n.get("to")), to);
        VBox content = new VBox(10, choices, convert, result, new Label(I18n.get("recentConversions")), history);
        content.setPadding(new Insets(15));
        stage.setTitle(I18n.get("title"));
        stage.setScene(new Scene(content, 550, 350));
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
