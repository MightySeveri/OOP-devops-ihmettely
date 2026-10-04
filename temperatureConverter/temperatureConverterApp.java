import java.nio.file.Path;
import java.sql.SQLException;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
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
        database.initialize();

        TextField input = new TextField();
        input.setPromptText("Temperature");
        ComboBox<ConversionDatabase.Unit> from = new ComboBox<>();
        ComboBox<ConversionDatabase.Unit> to = new ComboBox<>();
        from.getItems().addAll(database.getUnits());
        to.getItems().addAll(database.getUnits());
        from.getSelectionModel().selectFirst();
        to.getSelectionModel().select(1);

        Label result = new Label("Enter a temperature to convert.");
        ListView<String> history = new ListView<>();
        history.getItems().addAll(database.getHistory());

        Button convert = new Button("Convert and save");
        convert.setOnAction(event -> {
            try {
                double value = Double.parseDouble(input.getText());
                if (!Double.isFinite(value)) {
                    throw new NumberFormatException();
                }
                double converted = converter.convert(value, from.getValue().name(), to.getValue().name());
                database.saveConversion(value, from.getValue(), to.getValue(), converted);
                result.setText(String.format("%.2f %s = %.2f %s", value,
                        from.getValue(), converted, to.getValue()));
                history.getItems().setAll(database.getHistory());
            } catch (NumberFormatException exception) {
                result.setText("Enter a valid number.");
            } catch (SQLException exception) {
                result.setText("Database error: " + exception.getMessage());
            }
        });

        HBox choices = new HBox(10, input, from, new Label("to"), to);
        VBox content = new VBox(10, choices, convert, result, new Label("Recent conversions"), history);
        content.setPadding(new Insets(15));
        stage.setTitle("Temperature Converter");
        stage.setScene(new Scene(content, 550, 350));
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
