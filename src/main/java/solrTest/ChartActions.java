package solrTest;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.FXCollections;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.chart.*;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Path;
import javafx.scene.text.Text;
import org.apache.solr.client.solrj.SolrServerException;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static java.time.temporal.ChronoUnit.DAYS;

/**
 * Created by vladislav on 25.03.14.
 */
public class ChartActions {

    static LineChart<LocalDateTime, Number> chart;
    static Double initYpos;
    static String dragArea;
    public static LineChart<LocalDateTime, Number> makeZoomLineChart(HashMap pointsToId,
                                                                     String pathStyleClass,
                                                                     Text valueDisplayLabel,
                                                                     ListView seriesListView,
                                                                     DateAxis310 localDateTimeAxis,
                                                                     NumberAxis numberAxis){

        chart = new LineChart<LocalDateTime, Number>(localDateTimeAxis, numberAxis){
            @Override
            protected void layoutPlotChildren() {
                super.layoutPlotChildren();
                for (Node mark : getChartChildren()) {
                    if (mark instanceof Region){

                        mark.setFocusTraversable(true);
                    }
                }
                int pointIterator = 0;
                for (Node mark : getPlotChildren()) {
                    if (!(mark instanceof StackPane)){
                        Path g = (Path) mark;
                        g.setStrokeWidth(1.5);
                        g.getStyleClass().add(pathStyleClass);
                        pointIterator = 0;
                        pointsToId.clear();
                    }
                    if (mark instanceof StackPane) {
                        pointsToId.put(mark.toString(), pointIterator);
                        pointIterator++;

                        Bounds bounds = mark.getBoundsInParent();
                        double posX = bounds.getMinX() + (bounds.getMaxX() - bounds.getMinX()) / 2.0;
                        double posY = bounds.getMinY() + (bounds.getMaxY() - bounds.getMinY()) / 2.0;
                        Double number = Double.valueOf(getYAxis().getValueForDisplay(posY).toString());
                        LocalDateTime date = getXAxis().getValueForDisplay(posX).truncatedTo(DAYS);
                        mark.setScaleX(0.5);
                        mark.setScaleY(0.50);

                        mark.setOnMouseEntered(new EventHandler<MouseEvent>() {
                            @Override
                            public void handle(MouseEvent mouseEvent) {
                                valueDisplayLabel.setVisible(true);
                                valueDisplayLabel.setTranslateX(posX + 40);
                                valueDisplayLabel.setTranslateY(posY + 10);
                                try {
                                    valueDisplayLabel.setText(SolrService.getCurrency(seriesListView.getSelectionModel().getSelectedItem().toString()) + " " + number.toString().substring(0, 5));

                                } catch (SolrServerException e) {
                                } catch (IOException e) {
                                } catch (NullPointerException e){
                                    try {
                                        valueDisplayLabel.setText(SolrService.getCurrency(seriesListView.getItems().get(0).toString()) + " " + number.toString().substring(0, 5));
                                    } catch (SolrServerException e1) {
                                    } catch (IOException e1) {
                                    }
                                }
                            }
                        });

                        mark.setOnMouseExited(new EventHandler<MouseEvent>() {
                            @Override
                            public void handle(MouseEvent mouseEvent) {
                                valueDisplayLabel.setVisible(false);
                                valueDisplayLabel.setTranslateX(posX + 40);
                                valueDisplayLabel.setTranslateY(posY + 10);
                            }
                        });
                    }
                }
            }
        };
        return chart;
    }
    
    public static NumberAxis makeZoomYAxis(SimpleDoubleProperty lowerBound,
                                           SimpleDoubleProperty upperBound,
                                           Double initY,
                                           String dragZone){
        NumberAxis yAxis = new NumberAxis();
        initYpos = initY;
        dragArea = dragZone;
        yAxis.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                yAxis.setAutoRanging(false);
                lowerBound.set(yAxis.getLowerBound());
                upperBound.set(yAxis.getUpperBound());
                yAxis.lowerBoundProperty().bind(lowerBound);
                yAxis.upperBoundProperty().bind(upperBound);
                initYpos = mouseEvent.getSceneY();
                if (mouseEvent.getSceneY() > 379 ){
                    dragArea="bottom";
                }else if (mouseEvent.getSceneY() > 213){
                    dragArea="middle";
                }else{
                    dragArea="top";
                }
            }
        });

        yAxis.setOnMouseDragged((mouseEvent) -> {
            yAxis.setAutoRanging(false);
            double currentLowerBound = yAxis.getLowerBound();
            double currentUpperBound = yAxis.getUpperBound();

            double range = currentUpperBound - currentLowerBound;
            double dragStrength = range / 425;
            switch (dragArea) {
                case "top":
                    upperBound.set(currentUpperBound - dragStrength * (initYpos - mouseEvent.getSceneY()));
                    break;
                case "middle":
                    upperBound.set(currentUpperBound - dragStrength * (initYpos - mouseEvent.getSceneY()));
                    lowerBound.set(currentLowerBound - dragStrength * (initYpos - mouseEvent.getSceneY()));
                    break;
                case "bottom":
                    lowerBound.set(currentLowerBound - dragStrength * (initYpos - mouseEvent.getSceneY()));
                    break;
            }
            initYpos = mouseEvent.getSceneY();
            mouseEvent.consume();
        });
        yAxis.setForceZeroInRange(false);

        return yAxis;
    }

    public static void cutLeft(double deltaLeft,
                               XYChart.Data<LocalDateTime, Float> lastData,
                               XYChart.Series series, List dataPointRemovedFromBack,
                               String lastEarliestValue) {

        Date filterDate = new Date();
        Date currentDate = new Date();
        try {
            filterDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS").parse(lastEarliestValue);
            currentDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm").parse((String.valueOf(lastData.getXValue())));
        } catch (ParseException e) {
            try {
                filterDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm").parse(lastEarliestValue);
            } catch (ParseException e1) {
                e1.printStackTrace();
            }
        }

        try{
            if (deltaLeft < 0) {
                while (filterDate.getTime() > currentDate.getTime()) {
                    if (series.getData().size() > 2) {
                        XYChart.Data<LocalDateTime, Float> data = (XYChart.Data<LocalDateTime, Float>) series.getData().get(0);
                        dataPointRemovedFromBack.add(data);
                        series.getData().remove(0);
                        lastData = (XYChart.Data<LocalDateTime, Float>) series.getData().get(1);
                        try {
                            currentDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm").parse((String.valueOf(lastData.getXValue())));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }else{
                        return;
                    }
                }
            } else if (deltaLeft > 0) {
                while (filterDate.getTime() < currentDate.getTime()) {
                    if (dataPointRemovedFromBack.size() == 0) return;
                    XYChart.Data<LocalDateTime, Float> backData =(XYChart.Data) dataPointRemovedFromBack.get(dataPointRemovedFromBack.size() - 1);
                    dataPointRemovedFromBack.remove(backData);
                    String dateBack = String.valueOf(backData.getXValue());
                    Float valueBack = backData.getYValue();

                    series.getData().add(0, new XYChart.Data(backData.getXValue(), valueBack));
                    lastData = (XYChart.Data<LocalDateTime, Float>) series.getData().get(0);
                    try {
                        currentDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm").parse((String.valueOf(lastData.getXValue())));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }

        }catch (Exception e){

        }
    }

    public static void cutRight(double deltaRight,
                                XYChart.Data<LocalDateTime, Float> firstDate,
                                XYChart.Series series,
                                List dataPointsRemovedFromFront,
                                String lastLatestValue) {

        Date filterDate = new Date();
        Date currentDate = new Date();
        try {
            filterDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS").parse((lastLatestValue));
            currentDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm").parse((String.valueOf(firstDate.getXValue())));
        } catch (ParseException e) {
            try {
                filterDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm").parse((lastLatestValue));
            } catch (ParseException e1) {
            }
        }
        try {
            if (deltaRight > 0) {
                while (filterDate.getTime() < currentDate.getTime()) {
                    if (series.getData().size() > 2){
                        XYChart.Data<LocalDateTime, Float> data = (XYChart.Data<LocalDateTime, Float>) series.getData().get(series.getData().size() - 1);
                        dataPointsRemovedFromFront.add(data);
                        series.getData().remove(data);
                        firstDate = (XYChart.Data<LocalDateTime, Float>) series.getData().get(series.getData().size() - 2);
                        try {
                            currentDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm").parse((String.valueOf(firstDate.getXValue())));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }else{
                        return;
                    }
                }
            } else if (deltaRight < 0) {
                while (filterDate.getTime() > currentDate.getTime()) {
                    if (dataPointsRemovedFromFront.size() == 0) return;
                    XYChart.Data<LocalDateTime, Float> frontData =  (XYChart.Data) dataPointsRemovedFromFront.get(dataPointsRemovedFromFront.size() - 1);
                    dataPointsRemovedFromFront.remove(frontData);
                    String dateFront = String.valueOf(frontData.getXValue());
                    Float valueFront = frontData.getYValue();
                    series.getData().add(new XYChart.Data(frontData.getXValue(), valueFront));
                    try {
                        currentDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm").parse((String.valueOf(frontData.getXValue())));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }

        }catch (Exception e){

        }
    }


    public static void setChartsLowerBound(LocalDateTime localDateTimeStart,
                                     LineChart firstSelectionChart,
                                     LineChart secondSelectionChart,
                                     NumberAxis firstSelectionYAxis,
                                     NumberAxis secondSelectionYAxis,
                                     AreaChart diffBarChart) {
        DateAxis310 firstSelectionChartXAxis = (DateAxis310) firstSelectionChart.getXAxis();
        firstSelectionChartXAxis.setAutoRanging(false);
        firstSelectionChartXAxis.setLowerBound(localDateTimeStart);


        DateAxis310 secondSelectionChartXAxis = (DateAxis310) secondSelectionChart.getXAxis();
        secondSelectionChartXAxis.setAutoRanging(false);
        secondSelectionChartXAxis.setLowerBound(localDateTimeStart);

        secondSelectionYAxis.lowerBoundProperty().unbind();
        secondSelectionYAxis.upperBoundProperty().unbind();
        secondSelectionYAxis.setAutoRanging(true);
        firstSelectionYAxis.lowerBoundProperty().unbind();
        firstSelectionYAxis.upperBoundProperty().unbind();
        firstSelectionYAxis.setAutoRanging(true);

        ((NumberAxis) diffBarChart.getYAxis()).setAutoRanging(true);
    }

    public static void setChartsUpperBound(LocalDateTime localDateTimeFinish,
                                     LineChart firstSelectionChart,
                                     LineChart secondSelectionChart,
                                     NumberAxis firstSelectionYAxis,
                                     NumberAxis secondSelectionYAxis,
                                     AreaChart diffBarChart) {
        DateAxis310 firstSelectionChartXAxis = (DateAxis310) firstSelectionChart.getXAxis();
        firstSelectionChartXAxis.setAutoRanging(false);
        firstSelectionChartXAxis.setUpperBound(localDateTimeFinish);

        DateAxis310 secondSelectionChartXAxis = (DateAxis310) secondSelectionChart.getXAxis();
        secondSelectionChartXAxis.setAutoRanging(false);
        secondSelectionChartXAxis.setUpperBound(localDateTimeFinish);

        secondSelectionYAxis.lowerBoundProperty().unbind();
        secondSelectionYAxis.upperBoundProperty().unbind();
        secondSelectionYAxis.setAutoRanging(true);
        firstSelectionYAxis.lowerBoundProperty().unbind();
        firstSelectionYAxis.upperBoundProperty().unbind();
        firstSelectionYAxis.setAutoRanging(true);

        ((NumberAxis) diffBarChart.getYAxis()).setAutoRanging(true);
    }

}
