package temp;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import solrTest.DateAxis310;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * User: hansolo
 * Date: 16.12.13
 * Time: 15:06
 */
public class Demo extends Application {
    private static final DateTimeFormatter   DTF = DateTimeFormatter.ofPattern("dd.MM.yy");
    private LineChart<LocalDateTime, Number> lineChart;
    private Popup                            popup;

    @Override public void init() {
        final StringConverter<LocalDateTime> STRING_CONVERTER = new StringConverter<LocalDateTime>() {
            @Override public String toString(LocalDateTime localDateTime) {
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yy\nHH:mm:ss");
                return dtf.format(localDateTime);
            }
            @Override public LocalDateTime fromString(String s) {
                return LocalDateTime.parse(s);
            }
        };

        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Temperature [°C]");

        DateAxis310 xAxis = new DateAxis310();
        xAxis.setTickLabelFormatter(STRING_CONVERTER);

        XYChart.Series series = new XYChart.Series<LocalDateTime, Number>();
        series.setName("Temperature [°C]");

        lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.setTitle("Temperature Chart");
        lineChart.getData().add(series);
        lineChart.setAnimated(false);
        xAxis.setTickLabelFormatter(STRING_CONVERTER);

        addData(series);
        applyDataPointMouseEvents(series);
    }

    @Override public void start(Stage stage) {
        StackPane pane = new StackPane();
        pane.setPadding(new Insets(0, 20, 0, 0));
        pane.getChildren().addAll(lineChart);

        Scene scene = new Scene(pane, Color.rgb(71, 71, 71));
        scene.getStylesheets().add("demo.css");

        stage.setScene(scene);
        stage.show();
    }

    @Override public void stop() {

    }

    private void addData(final XYChart.Series SERIES) {
        List<XYChart.Data<LocalDateTime, Number>> data = new ArrayList<>();
        data.add(new XYChart.Data<>(LocalDateTime.of(2013, 1, 13, 0, 0, 0), 8));
        data.add(new XYChart.Data<>(LocalDateTime.of(2013, 2, 27, 0, 0, 0), 4));
        data.add(new XYChart.Data<>(LocalDateTime.of(2013, 3, 27, 0, 0, 0), 1));
        data.add(new XYChart.Data<>(LocalDateTime.of(2013, 4, 27, 0, 0, 0), 7));
        data.add(new XYChart.Data<>(LocalDateTime.of(2013, 5, 27, 0, 0, 0), 5));
        data.add(new XYChart.Data<>(LocalDateTime.of(2013, 6, 27, 0, 0, 0), 3));

        SERIES.getData().setAll(data);
    }

    private void applyDataPointMouseEvents(final XYChart.Series SERIES) {
        Platform.runLater(new Runnable() {
            @Override public void run() {
                Label label = new Label("");
                label.getStyleClass().add("value-label");
                StackPane popupPane = new StackPane(label);
                popupPane.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, CornerRadii.EMPTY, Insets.EMPTY)));
                popup = new Popup();
                popup.getContent().add(popupPane);

                for (Object data : SERIES.getData()) {
                    XYChart.Data<LocalDateTime, Number> dataPoint = (XYChart.Data<LocalDateTime, Number>) data;
                    final Node node = dataPoint.getNode();

                    node.setOnMouseEntered(mouseEvent -> {
                        label.setText(DTF.format(dataPoint.getXValue()) + "\n" + dataPoint.getYValue() + " °C");
                        popup.show(SERIES.getNode().getScene().getWindow());
                    });

                    node.setOnMouseExited(mouseEvent -> {
                        popup.hide();
                    });
                }
            }
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}
