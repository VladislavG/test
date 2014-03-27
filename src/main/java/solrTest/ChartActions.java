package solrTest;

import com.sun.javafx.charts.Legend;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.Parent;
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
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.function.Consumer;

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
                                                                     String pathStyleClassMark,
                                                                     Text valueDisplayLabel,
                                                                     ListView seriesListView,
                                                                     DateAxis310 localDateTimeAxis,
                                                                     NumberAxis numberAxis) {

        chart = new LineChart<LocalDateTime, Number>(localDateTimeAxis, numberAxis) {
            @Override
            protected void layoutPlotChildren() {
                super.layoutPlotChildren();
                for (Node mark : getChartChildren()) {
                    if (mark instanceof Region) {

                        mark.setFocusTraversable(true);
                    }
                }
                for (Node mark : getChildren()) {
                    if (mark instanceof Legend) {
                        ((Legend) mark).getChildrenUnmodifiable().forEach(new Consumer<Node>() {
                            @Override
                            public void accept(Node node) {
                                ((Label) node).getChildrenUnmodifiable().forEach(new Consumer<Node>() {
                                    @Override
                                    public void accept(Node node) {
                                        if (node instanceof Region) {
                                            node.getStyleClass().add(pathStyleClassMark);
                                        }
                                    }
                                });
                            }
                        });
                    }
                }
                int pointIterator = 0;
                int pathCount = 0;
                for (Node mark : getPlotChildren()) {
                    if (!(mark instanceof StackPane)) {
                        pathCount++;
                        Path g = (Path) mark;
                        g.setStrokeWidth(1.5);
                        if (pathCount > 1) {
                            g.getStyleClass().add("chart-series-line-overview2");
                        } else {
                            g.getStyleClass().add(pathStyleClass);
                        }
                        pointIterator = 0;
                        pointsToId.clear();
                    }
                    if (mark instanceof StackPane) {
                        pointsToId.put(mark.toString(), pointIterator);
                        pointIterator++;
                        mark.getStyleClass().add(pathStyleClassMark);
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
                                } catch (NullPointerException e) {
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
                                           String dragZone) {
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
                if (mouseEvent.getSceneY() > 379) {
                    dragArea = "bottom";
                } else if (mouseEvent.getSceneY() > 213) {
                    dragArea = "middle";
                } else {
                    dragArea = "top";
                }
            }
        });

        yAxis.setOnMouseDragged((MouseEvent mouseEvent) -> {
            yAxis.setAutoRanging(false);
            double currentLowerBound = yAxis.getLowerBound();
            double currentUpperBound = yAxis.getUpperBound();

            double range = currentUpperBound - currentLowerBound;
            double dragStrength = range / 425;
            double pullDistance = dragStrength * (initYpos - mouseEvent.getSceneY());
            switch (dragArea) {
                case "top":
                    upperBound.set(currentUpperBound - pullDistance);
                    break;
                case "middle":
                    upperBound.set(currentUpperBound - pullDistance);
                    lowerBound.set(currentLowerBound - pullDistance);
                    break;
                case "bottom":
                    lowerBound.set(currentLowerBound - pullDistance);
                    break;
            }
            initYpos = mouseEvent.getSceneY();
            mouseEvent.consume();
        });
        yAxis.setForceZeroInRange(false);

        return yAxis;
    }

    public static void setChartsLowerBound(LocalDateTime localDateTimeStart, LineChart firstSelectionChart, LineChart secondSelectionChart, NumberAxis firstSelectionYAxis, NumberAxis secondSelectionYAxis,
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

    public static void setChartsUpperBound(LocalDateTime localDateTimeFinish, LineChart firstSelectionChart, LineChart secondSelectionChart, NumberAxis firstSelectionYAxis, NumberAxis secondSelectionYAxis,
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

    public static XYChart.Data createChartDataFrom(Price price) {
        return new XYChart.Data(LocalDateTime.parse(Main.toUtcDate(price.getPrice_date())), Float.valueOf(price.getPrice()));
    }

    public static void turnOffPickOnBoundsFor(Node n) {
        n.setPickOnBounds(false);
        if (n instanceof Parent) {
            for (Node c : ((Parent) n).getChildrenUnmodifiable()) {
                if (!(c instanceof Axis)) {
                    turnOffPickOnBoundsFor(c);
                }
            }
        }
    }
}
