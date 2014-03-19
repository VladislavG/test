package solrTest;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.chart.LineChart;
import javafx.scene.control.Label;

import java.time.LocalDateTime;

/**
* Created by vladislav on 03.02.14.
*/
class Listeners implements ChangeListener<Number> {
    private LineChart<LocalDateTime,Number> lineChart;
    private Label displayAtPosition;

    public Listeners(Main main, LineChart<LocalDateTime, Number> lineChart, Label displayAtPosition) {
        this.lineChart = lineChart;
        this.displayAtPosition = displayAtPosition;
    }

    @Override
    public void changed(ObservableValue<? extends Number> observableValue, Number oldXposition, Number newXPosition) {
        String displayValueAtLocation = (String.valueOf(lineChart.getXAxis().getValueForDisplay((Double) newXPosition)));

        displayAtPosition.setText(displayValueAtLocation.substring(0, 10));
        displayAtPosition.setVisible(true);

    }
}
