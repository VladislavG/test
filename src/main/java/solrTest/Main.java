package solrTest;

import com.sun.javafx.charts.Legend;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.effect.InnerShadow;
import javafx.scene.effect.Reflection;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import org.apache.solr.client.solrj.SolrServerException;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Main extends Application {
    XYChart.Series firstSelectionPriceSeries = new XYChart.Series();
    XYChart.Series secondSelectionPriceSeries = new XYChart.Series();
    XYChart.Series deltaPriceSeries = new XYChart.Series();
    Double firstChartInitYPos;
    Double secondChartInitYPos;
    Double positionXOverviewChart;
    String dragArea;
    SimpleDoubleProperty firstSelectionUpperYBound = new SimpleDoubleProperty();
    SimpleDoubleProperty firstSelectionLowerYBound = new SimpleDoubleProperty();
    SimpleDoubleProperty secondSelectionUpperYBound = new SimpleDoubleProperty();
    SimpleDoubleProperty secondSelectionLowerYBound = new SimpleDoubleProperty();
    SimpleDoubleProperty leftHookPosition = new SimpleDoubleProperty();
    SimpleDoubleProperty rightHookPosition = new SimpleDoubleProperty();
    SimpleDoubleProperty trackXPosition = new SimpleDoubleProperty();
    SimpleDoubleProperty trackYPosition = new SimpleDoubleProperty();
    SimpleDoubleProperty trackXTargetPosition = new SimpleDoubleProperty();

    SimpleDoubleProperty initRectX = new SimpleDoubleProperty();
    SimpleDoubleProperty initRectY = new SimpleDoubleProperty();
    SimpleDoubleProperty rectX = new SimpleDoubleProperty();
    SimpleDoubleProperty rectY = new SimpleDoubleProperty();

    EventHandler<MouseEvent> chartEventHandler;
    Text placeHolder;
    TextField firstListSearch = new TextField();

    TextField secondListSearch = new TextField();
    HBox finalContainingLayout;
    Pane detailPane;
    VBox chartBox;
    VBox firstSelectionContainer = new VBox();
    VBox secondSelectionContainer = new VBox();

    VBox listBox;
    Pane overviewChartPane;
    NumberAxis firstSelectionYAxis;
    DateAxis310 firstSelectionXAxis;
    NumberAxis secondSelectionYAxis;

    DateAxis310 secondSelectionXAxis;
    ListView<String> firstListOfInstruments = new ListView<>();
    ListView<String> secondListOfInstruments = new ListView<>();
    ListView<String> firstListOfSeries = new ListView<>();

    ListView<String> secondListOfSeries = new ListView<>();
    Button resetButton = new Button("Reset zoom");
    Button openFirstInstrumentListButton = new Button("Select Instrument: ");
    Button openFirstSeriesListButton = new Button("Select Series: ");
    Button openSecondInstrumentListButton = new Button("Select Instrument: ");
    Button openSecondSeriesListButton = new Button("Select Series: ");

    public static final String YYYY_MM_DD_T_HH_MM = "yyyy-MM-dd'T'HH:mm";
    private static final String DD_MMM_YYYY = "dd MMM yyyy";
    private static final String YYYY_MM_DD = "yyyy-MM-dd";
    private static final String MMM_DD_YYYY_HH_MM_SS_Z = "MMM dd, yyyy hh:mm:ss Z";
    private static final String YYYY_MM_DD_T_HH_MM_SS_SSS = "yyyy-MM-dd'T'HH:mm:ss.SSS";
    private static final String BLUE_BUTTON_STYLE = "button2";
    private static final String YELLOW_BUTTON_STYLE = "button3";
    private static final String PRICE_SERIES_ONLY_CONTAINS_ONE_POINT_OF_DATA = "Price Series only contains one point of data";
    private static final String PANE_STYLE = "b2";
    private static final String RED_LISTVIEW_STYLE = "list-view2";
    private static final String BLUE_LISTVIEW_STYLE = "list-view3";
    private static final String RED_SERIES_STYLE = "chart-series-lineFirst";
    private static final String BLUE_SERIES_STYLE = "chart-series-lineSecond";
    private static final String BLUE_SYMBOL_STYLE = "chart-symbol2";
    private static final String OVERVIEW_SERIES_STYLE = "chart-series-line-overview";
    private static final String ALL = "*";

    ObservableList<String> firstObservableListInstruments;
    ObservableList<String> firstObservableListSeries;
    ObservableList<String> secondObservableListInstruments;

    ObservableList<String> secondObservableListSeries;

    final StringConverter<LocalDateTime> STRING_CONVERTER = new StringConverter<LocalDateTime>() {
        @Override public String toString(LocalDateTime localDateTime) {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern(DD_MMM_YYYY);
            return dtf.format(localDateTime);
        }
        @Override public LocalDateTime fromString(String s) {
            return LocalDateTime.parse(s);
        }
    };
    String lastValue;
    String firstValue;
    Rectangle zoomBounds;
    Line verticalTrackingLine = new Line(0, 550, 0, 0);
    Label displayAtPosition = new Label();
    Label displayAtTarget = new Label();
    Label labelInstruments;

    LineChart<LocalDateTime, Number> lineChartOverview;
    LineChart<LocalDateTime, Number> firstSelectionChart;
    LineChart<LocalDateTime, Number> secondSelectionChart;
    AreaChart<LocalDateTime, Number> diffBarChart;

    List<Price> firstSelectionResults;
    List<Price> secondSelectionResults;

    List<String> firstInstrumentResults;
    List<String> firstSeriesResults;
    List<String> secondInstrumentResults;
    List<String> secondSeriesResults;

    ArrayList<XYChart.Data<LocalDateTime, Float>> firstSelectionDataRemovedFromFront;
    ArrayList<XYChart.Data<LocalDateTime, Float>> firstSelectionDataRemovedFromBack;
    ArrayList<XYChart.Data<LocalDateTime, Float>> secondSelectionDataRemovedFromFront = new ArrayList<>();
    ArrayList<XYChart.Data<LocalDateTime, Float>> secondSelectionDataRemovedFromBack = new ArrayList<>();
    ArrayList<XYChart.Data<LocalDateTime, Float>> deltaDataRemovedFromFront = new ArrayList<>();
    ArrayList<XYChart.Data<LocalDateTime, Float>> deltaDataRemovedFromBack = new ArrayList<>();

    double initialLeftHookPosition = 0.0;
    double initialRightHookPosition = 0.0;
    Line lineIndicator = new Line(0, 550, 0, 0);
    Rectangle hookRight = new Rectangle(15,40);
    Rectangle hookLeft = new Rectangle(15,40);
    Rectangle leftRect = new Rectangle(30,150);
    Rectangle rightRect = new Rectangle(30,150);
    Separator separator;
    Pane propertiesPane = new Pane();
    Node chartPlotArea;

    SimpleDoubleProperty windowWidth;
    XYChart.Series firstSelectionSeriesTotal;
    XYChart.Series secondSelectionSeriesTotal;
    HashMap orderOfSeriesPointsToId = new HashMap();
    HashMap orderOfGraphPointsToId = new HashMap();
    HashMap firstSeriesDateToPrice = new HashMap();
    HashMap secondSeriesDateToPrice = new HashMap();
    HashMap overviewPointsDateToId = new HashMap();

    Pane lineChartsContainingPane;
    NumberAxis yAxis;
    DateAxis310 xAxis;
    NumberAxis yBarAxis;
    DateAxis310 xBarAxis;

    Text miniMapDetail;
    Text detail;
    Pane leftContainingPane;

    public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void stop() throws Exception {
	}

	@Override
	public void start(Stage stage) throws Exception {
		final Group root = new Group();
		Scene scene = new Scene(root, 1750, 850, Color.WHITESMOKE);

        firstInstrumentResults = SolrService.getInstruments(ALL);
        firstSeriesResults = SolrService.getSeries(firstInstrumentResults.get(0));
        secondInstrumentResults = SolrService.getInstruments(ALL);
        secondSeriesResults = SolrService.getSeries(secondInstrumentResults.get(0));

        firstObservableListInstruments = FXCollections.observableArrayList(firstInstrumentResults);
        firstObservableListSeries = FXCollections.observableArrayList(firstSeriesResults);
        secondObservableListInstruments = FXCollections.observableArrayList(secondInstrumentResults);
        secondObservableListSeries = FXCollections.observableArrayList(secondSeriesResults);

        firstSelectionResults = SolrService.getResults(ALL, ALL, firstObservableListInstruments.get(0), firstObservableListSeries.get(0));
        secondSelectionResults = SolrService.getResults(ALL, ALL, secondObservableListInstruments.get(0), secondObservableListSeries.get(1));

        firstListOfInstruments = ListViewActions.makeListView(firstObservableListInstruments);
        secondListOfInstruments = ListViewActions.makeListView(secondObservableListInstruments);

        firstListOfSeries.getItems().setAll(firstObservableListSeries);
        secondListOfSeries.getItems().setAll(secondObservableListSeries);

        lineChartOverview = ChartActions.makeZoomLineChart(overviewPointsDateToId, OVERVIEW_SERIES_STYLE, "", miniMapDetail, firstListOfSeries, xAxis, yAxis);
        firstSelectionChart = ChartActions.makeZoomLineChart(orderOfGraphPointsToId, RED_SERIES_STYLE, "", detail, firstListOfSeries, firstSelectionXAxis, firstSelectionYAxis);
        secondSelectionChart = ChartActions.makeZoomLineChart(orderOfGraphPointsToId, BLUE_SERIES_STYLE, BLUE_SYMBOL_STYLE, detail, secondListOfSeries, secondSelectionXAxis, secondSelectionYAxis);
        secondSelectionChart.setFocusTraversable(true);
        chartPlotArea = firstSelectionChart.lookup(".chart-plot-background");
        try {
            secondSelectionResults = SolrService.getResultsOnInstrumentAndSeries(secondObservableListInstruments.get(0), secondObservableListSeries.get(0));
            secondSelectionPriceSeries.getData().clear();
            secondSelectionSeriesTotal.getData().clear();
            for (int c = 0; c <= secondSelectionResults.size() - 1; c++) {
                Price price = secondSelectionResults.get(c);
                secondSeriesDateToPrice.put(price.getPrice_date(), price.getPrice());
                try {
                    secondSelectionPriceSeries.getData().add(ChartActions.createChartDataFrom(price));
                    secondSelectionSeriesTotal.getData().add(ChartActions.createChartDataFrom(price));
                    orderOfSeriesPointsToId.put(c, price.getId());
                } catch (Exception e) {
                    secondSelectionPriceSeries.getData().add(ChartActions.createChartDataFrom(price));
                    orderOfSeriesPointsToId.put(c, price.getId());

                    secondSelectionSeriesTotal.getData().add(ChartActions.createChartDataFrom(price));
                }
            }
        } catch (SolrServerException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            firstSelectionResults = SolrService.getResultsOnInstrumentAndSeries(firstObservableListInstruments.get(0), firstObservableListSeries.get(1));
            firstSelectionPriceSeries.getData().clear();
            firstSelectionSeriesTotal.getData().clear();
            populateAllSeriesAndMatchBounds(firstSelectionResults, secondSeriesDateToPrice, firstSelectionPriceSeries, firstSelectionSeriesTotal, firstSeriesDateToPrice, orderOfSeriesPointsToId, deltaPriceSeries);
            matchBoundsBetweenCharts();
        } catch (SolrServerException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        propertiesPane.setVisible(false);
        propertiesPane.getStyleClass().add(PANE_STYLE);
        detailPane.getStyleClass().add(PANE_STYLE);
        secondListOfInstruments.getStyleClass().add(RED_LISTVIEW_STYLE);
        secondListOfSeries.getStyleClass().add(RED_LISTVIEW_STYLE);
        firstListOfInstruments.getStyleClass().add(BLUE_LISTVIEW_STYLE);
        firstListOfSeries.getStyleClass().add(BLUE_LISTVIEW_STYLE);
        setupCharts();
        addListenersAndBindings();
        ChartActions.turnOffPickOnBoundsFor(firstSelectionChart);

        ((NumberAxis) firstSelectionChart.getYAxis()).setForceZeroInRange(false);
        lineChartsContainingPane.setFocusTraversable(true);
        finalContainingLayout.setMinWidth(scene.getWidth());
        firstListOfInstruments.getSelectionModel().selectFirst();
        secondListOfInstruments.getSelectionModel().select(2);

        lineChartsContainingPane.getChildren().addAll(secondSelectionChart, firstSelectionChart, lineIndicator);
        overviewChartPane.getChildren().addAll(lineChartOverview, leftRect, rightRect, hookRight, hookLeft, miniMapDetail);
        chartBox.getChildren().addAll(lineChartsContainingPane, separator, diffBarChart, overviewChartPane);
        leftContainingPane.getChildren().addAll(chartBox, zoomBounds, verticalTrackingLine, displayAtPosition, displayAtTarget, detail, propertiesPane);
        finalContainingLayout.getChildren().addAll(leftContainingPane, detailPane);
        root.getChildren().addAll(finalContainingLayout);
        stage.setTitle("Calculating Important Points");
		stage.setScene(scene);
        scene.getStylesheets().add("demo.css");

		stage.show();
	}

    public static String toUtcDate(String dateStr) {
        HashMap<String, String> monthToMonth = new HashMap<String, String>();
        monthToMonth.put("Jan", "01");
        monthToMonth.put("Feb", "02");
        monthToMonth.put("Mar", "03");
        monthToMonth.put("Apr", "04");
        monthToMonth.put("May", "05");
        monthToMonth.put("Jun", "06");
        monthToMonth.put("Jul", "07");
        monthToMonth.put("Aug", "08");
        monthToMonth.put("Sep", "09");
        monthToMonth.put("Oct", "10");
        monthToMonth.put("Nov", "11");
        monthToMonth.put("Dec", "12");
        String month = monthToMonth.get(dateStr.substring(4, 7));
        String year = dateStr.substring(24);
        String day = dateStr.substring(8, 10);
        dateStr = year + "-" + month + "-" + day;
        SimpleDateFormat out = new SimpleDateFormat(YYYY_MM_DD_T_HH_MM_SS_SSS);
        String[] dateFormats = {YYYY_MM_DD, MMM_DD_YYYY_HH_MM_SS_Z};
        for (String dateFormat : dateFormats) {
            try {
                return out.format(new SimpleDateFormat(dateFormat).parse(dateStr));
            } catch (ParseException ignore) { }
        }
        throw new IllegalArgumentException("Invalid date: " + dateStr);
    }

    private void cutLeftAndRight(XYChart.Series series, ArrayList dataRemovedFromBack, ArrayList dataRemovedFromFront, double deltaLeft, double deltaRight) {
        SeriesActions.cutLeft(deltaLeft, SeriesActions.getSecondPointIn(series), series, dataRemovedFromBack, lastValue);
        SeriesActions.cutRight(deltaRight, SeriesActions.getSecondToLatestPointIn(series), series, dataRemovedFromFront, firstValue);
    }

    private void setupCharts(){
        ((DateAxis310) firstSelectionChart.getXAxis()).setTickLabelsVisible(true);
        ((DateAxis310) firstSelectionChart.getXAxis()).setTickMarkVisible(false);
        firstSelectionChart.getData().add(firstSelectionPriceSeries);
        firstSelectionChart.setLegendVisible(false);
        firstSelectionChart.setHorizontalGridLinesVisible(true);
        firstSelectionChart.setMaxWidth(1390);
        firstSelectionChart.setMinWidth(1390);
        firstSelectionChart.setMaxHeight(550);
        firstSelectionChart.setMinHeight(550);
        firstSelectionChart.setVerticalZeroLineVisible(false);
        firstSelectionChart.setVerticalGridLinesVisible(false);
        firstSelectionChart.setHorizontalZeroLineVisible(false);
        firstSelectionChart.setVerticalZeroLineVisible(false);
        firstSelectionChart.setHorizontalZeroLineVisible(false);
        firstSelectionChart.setFocusTraversable(true);
        firstSelectionChart.setLegendVisible(true);
        firstSelectionChart.setAnimated(false);
        firstSelectionChart.setTitle("Instrument Price Series");
        firstSelectionChart.setVerticalZeroLineVisible(false);
        firstSelectionChart.setCreateSymbols(true);
        Rectangle rectangle = new Rectangle(0,0, 1375, 550);
        firstSelectionChart.setClip(rectangle);
        for (Node legend : firstSelectionChart.getChildrenUnmodifiable()){
            if (legend instanceof Legend){
                legend.setTranslateX(-75);
            }
        }

        secondSelectionChart.getData().add(secondSelectionPriceSeries);
        secondSelectionChart.setLegendVisible(false);
        secondSelectionChart.setMaxWidth(1390);
        secondSelectionChart.setMinWidth(1390);
        secondSelectionChart.setMaxHeight(512);
        secondSelectionChart.setMinHeight(512);
        secondSelectionChart.setHorizontalZeroLineVisible(true);
        secondSelectionChart.setHorizontalGridLinesVisible(false);
        secondSelectionChart.setVerticalGridLinesVisible(true);
        secondSelectionChart.setLegendVisible(true);
        secondSelectionChart.setAnimated(false);
        secondSelectionChart.setVerticalZeroLineVisible(false);
        secondSelectionChart.setCreateSymbols(true);
        secondSelectionChart.getXAxis().setTickLabelsVisible(false);
        secondSelectionChart.getXAxis().setTickMarkVisible(false);
        secondSelectionChart.getYAxis().setSide(Side.RIGHT);
        secondSelectionChart.getYAxis().setAutoRanging(true);
        secondSelectionChart.translateXProperty().bind(chartPlotArea.layoutXProperty().subtract(10.0));
        secondSelectionChart.setTranslateY(25);
        for (Node legend : secondSelectionChart.getChildrenUnmodifiable()){
            if (legend instanceof Legend){
                legend.setTranslateX(100);
                legend.setTranslateY(13);
            }
        }

        diffBarChart.getData().addAll(deltaPriceSeries);
        diffBarChart.setMaxWidth(1390);
        diffBarChart.setHorizontalGridLinesVisible(false);
        diffBarChart.getXAxis().setTickMarkVisible(false);
        diffBarChart.getXAxis().setTickLabelsVisible(false);
        ((NumberAxis) diffBarChart.getYAxis()).setAutoRanging(true);
        ((NumberAxis) diffBarChart.getYAxis()).setForceZeroInRange(false);
        diffBarChart.setTranslateX(6);
        diffBarChart.setMaxHeight(150);
        diffBarChart.setCreateSymbols(false);
        diffBarChart.setAnimated(false);

        lineChartOverview.legendVisibleProperty().setValue(false);
        lineChartOverview.getData().add(firstSelectionSeriesTotal);
        lineChartOverview.getData().add(secondSelectionSeriesTotal);
        lineChartOverview.setVerticalGridLinesVisible(false);
        lineChartOverview.setVerticalGridLinesVisible(false);
        lineChartOverview.setCreateSymbols(false);
        ((DateAxis310) lineChartOverview.getXAxis()).setTickLabelsVisible(false);
        ((DateAxis310) lineChartOverview.getXAxis()).setTickMarkVisible(false);
        lineChartOverview.setMaxHeight(150);
        lineChartOverview.setMinWidth(1390);
        lineChartOverview.setMaxWidth(1390);
        lineChartOverview.setAnimated(false);
    }

    private void addListenersAndBindings(){
        firstListSearch.opacityProperty().bind(firstListOfInstruments.opacityProperty());
        secondListSearch.opacityProperty().bind(secondListOfInstruments.opacityProperty());
        firstListSearch.managedProperty().bind(firstListOfInstruments.managedProperty());
        secondListSearch.managedProperty().bind(secondListOfInstruments.managedProperty());

        hookLeft.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                initialLeftHookPosition = mouseEvent.getSceneX();
            }
        });
        hookRight.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                initialRightHookPosition = mouseEvent.getSceneX();
            }
        });

        trackXPosition.addListener(new Listeners(this, firstSelectionChart, displayAtPosition));

        trackXTargetPosition.addListener(new XTargetChangeListener());

        hookLeft.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if (leftRect.getWidth() >= (hookRight.getLayoutX() - 50) || mouseEvent.getSceneX() - 50 >= (hookRight.getLayoutX() - 50)) {
                    leftHookPosition.set(hookRight.getLayoutX() - 60);
                    return;
                }

                if (mouseEvent.getSceneX() < 55) {
                    leftHookPosition.set(0);
                    return;
                }
                double deltaLeft = initialLeftHookPosition - mouseEvent.getSceneX();
                leftHookPosition.set(hookLeft.getLayoutX() - deltaLeft);
                LocalDateTime valueForDisplay = lineChartOverview.getXAxis().getValueForDisplay(mouseEvent.getSceneX() - 40);
                lastValue = String.valueOf(valueForDisplay);
                try {
                    if (firstSelectionDataRemovedFromBack.get(0) == secondSelectionDataRemovedFromBack.get(0)) {
                        firstSelectionChart.getXAxis().setAutoRanging(false);
                        secondSelectionChart.getXAxis().setAutoRanging(false);
                    } else {
                        firstSelectionChart.getXAxis().setAutoRanging(true);
                        secondSelectionChart.getXAxis().setAutoRanging(true);
                    }
                } catch (Exception e) {
                }
                SeriesActions.cutLeft(deltaLeft, SeriesActions.getSecondPointIn(secondSelectionPriceSeries), secondSelectionPriceSeries, secondSelectionDataRemovedFromBack, lastValue);
                SeriesActions.cutLeft(deltaLeft, SeriesActions.getSecondPointIn(firstSelectionPriceSeries), firstSelectionPriceSeries, firstSelectionDataRemovedFromBack, lastValue);
                SeriesActions.cutLeft(deltaLeft, SeriesActions.getSecondPointIn(deltaPriceSeries), deltaPriceSeries, deltaDataRemovedFromBack, lastValue);

                ChartActions.setChartsLowerBound(valueForDisplay, firstSelectionChart, secondSelectionChart, firstSelectionYAxis, secondSelectionYAxis, diffBarChart);
                initialLeftHookPosition = mouseEvent.getSceneX();
            }
        });

        hookLeft.setOnMouseReleased(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if (leftRect.getWidth() >= (hookRight.getLayoutX() - 50)) {
                    leftHookPosition.set(hookRight.getLayoutX() - 60);
                    return;
                }
                if (mouseEvent.getSceneX() < 55) {
                    leftHookPosition.set(0);
                }
            }
        });

        hookRight.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if (!isHookInBounds(mouseEvent)) return;
                double deltaRight = initialRightHookPosition - mouseEvent.getSceneX();
                rightHookPosition.set(hookRight.getLayoutX() - deltaRight);

                LocalDateTime valueForDisplay = lineChartOverview.getXAxis().getValueForDisplay(mouseEvent.getSceneX() - 45);
                firstValue = String.valueOf(valueForDisplay);
                firstSelectionChart. getXAxis().setAutoRanging(true);
                secondSelectionChart.getXAxis().setAutoRanging(true);

                XYChart.Data<LocalDateTime, Float> firstDate = SeriesActions.getSecondToLatestPointIn(firstSelectionPriceSeries);
                SeriesActions.cutRight(deltaRight, firstDate, firstSelectionPriceSeries, firstSelectionDataRemovedFromFront, firstValue);

                XYChart.Data<LocalDateTime, Float> secondSelectionFirstDate = SeriesActions.getSecondToLatestPointIn(secondSelectionPriceSeries);
                SeriesActions.cutRight(deltaRight, secondSelectionFirstDate, secondSelectionPriceSeries, secondSelectionDataRemovedFromFront, firstValue);

                XYChart.Data<LocalDateTime, Float> deltaLocalDateTimeFloatData = SeriesActions.getSecondToLatestPointIn(deltaPriceSeries);
                SeriesActions.cutRight(deltaRight, deltaLocalDateTimeFloatData, deltaPriceSeries, deltaDataRemovedFromFront, firstValue);

                ChartActions.setChartsUpperBound(valueForDisplay, firstSelectionChart, secondSelectionChart, firstSelectionYAxis, secondSelectionYAxis, diffBarChart);
                initialRightHookPosition = mouseEvent.getSceneX();

            }
        });

        hookRight.setOnMouseReleased(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                isHookInBounds(mouseEvent);
            }
        });
        openSecondInstrumentListButton.getStyleClass().add(BLUE_BUTTON_STYLE);
        openSecondSeriesListButton.getStyleClass().add(BLUE_BUTTON_STYLE);

        ListViewActions.makeListViewDisappear(secondListOfInstruments);
        ListViewActions.makeListViewDisappear(firstListOfInstruments);
        ListViewActions.makeListViewDisappear(secondListOfSeries);
        ListViewActions.makeListViewDisappear(secondListOfSeries);

        firstSelectionContainer.getChildren().addAll(openFirstInstrumentListButton, firstListSearch, firstListOfInstruments, openFirstSeriesListButton, firstListOfSeries);
        secondSelectionContainer.getChildren().addAll(openSecondInstrumentListButton, secondListSearch, secondListOfInstruments, openSecondSeriesListButton, secondListOfSeries);

        openFirstInstrumentListButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
               Animation.playOpenCloseAnimationOnMenu(firstListOfInstruments, firstListOfSeries, secondListOfInstruments, secondListOfSeries);
            }
        });
        openFirstSeriesListButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                Animation.playOpenCloseAnimationOnMenu(firstListOfSeries, firstListOfInstruments, secondListOfInstruments, secondListOfSeries);
            }
        });
        openSecondInstrumentListButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                Animation.playOpenCloseAnimationOnMenu(secondListOfInstruments, firstListOfInstruments, firstListOfSeries, secondListOfSeries);
            }
        });
        openSecondSeriesListButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                Animation.playOpenCloseAnimationOnMenu(secondListOfSeries, firstListOfInstruments, firstListOfSeries, secondListOfInstruments);
            }
        });
        firstListOfInstruments.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String s, String s2) {
                SeriesActions.updateSeriesListAndSetButtonText(s2, firstSeriesResults, firstListOfSeries, openFirstInstrumentListButton, firstSelectionPriceSeries);
            }
        });

        secondListOfInstruments.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String s, String s2) {
                SeriesActions.updateSeriesListAndSetButtonText(s2, secondSeriesResults, secondListOfSeries, openSecondInstrumentListButton, secondSelectionPriceSeries);
            }
        });

        resetButton.setCursor(Cursor.HAND);
        openFirstInstrumentListButton.setCursor(Cursor.HAND);
        openFirstSeriesListButton.setCursor(Cursor.HAND);
        openSecondInstrumentListButton.setCursor(Cursor.HAND);
        openSecondSeriesListButton.setCursor(Cursor.HAND);
        resetButton.getStyleClass().add(YELLOW_BUTTON_STYLE);
        firstListOfSeries.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableDateValue, String previousSelection, String newSelection) {
                if (newSelection==null){
                    return;
                }
                try {

                    firstSelectionResults = SolrService.getResultsOnInstrumentAndSeries(firstListOfInstruments.getSelectionModel().getSelectedItem(), firstListOfSeries.getSelectionModel().getSelectedItem());
                    if (firstSelectionResults.size() == 1){
                        displayLackOfDataInSeries();
                        return;
                    } else{
                        resetControlsForNewSelection();
                        SeriesActions.completeSeriesFromRemovedPoints(secondSelectionDataRemovedFromBack, secondSelectionDataRemovedFromFront, secondSelectionPriceSeries);
                        firstSelectionDataRemovedFromBack.clear();
                        firstSelectionDataRemovedFromFront.clear();
                        deltaDataRemovedFromBack.clear();
                        deltaDataRemovedFromFront.clear();
                        firstSelectionPriceSeries.getData().clear();
                        firstSeriesDateToPrice.clear();
                        firstSelectionSeriesTotal.getData().clear();
                        deltaPriceSeries.getData().clear();

                        populateAllSeriesAndMatchBounds(firstSelectionResults, secondSeriesDateToPrice, firstSelectionPriceSeries, firstSelectionSeriesTotal, firstSeriesDateToPrice, orderOfSeriesPointsToId, deltaPriceSeries);
                    }
                } catch (SolrServerException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                matchBoundsBetweenCharts();

                resetButton.fire();
                openFirstSeriesListButton.setText("Series: " + newSelection);
            }
        });
        secondListOfSeries.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableDateValue, String previousSelection, String newSelection) {
                if (newSelection==null){
                    return;
                }
                try {
                    secondSelectionResults = SolrService.getResultsOnInstrumentAndSeries(secondListOfInstruments.getSelectionModel().getSelectedItem(), secondListOfSeries.getSelectionModel().getSelectedItem());
                    if (secondSelectionResults.size() == 1){
                        displayLackOfDataInSeries();
                        return;
                    }else{
                        resetControlsForNewSelection();
                        SeriesActions.completeSeriesFromRemovedPoints(firstSelectionDataRemovedFromBack, firstSelectionDataRemovedFromFront, firstSelectionPriceSeries);
                        secondSelectionDataRemovedFromBack.clear();
                        secondSelectionDataRemovedFromFront.clear();
                        deltaDataRemovedFromBack.clear();
                        deltaDataRemovedFromFront.clear();
                        secondSelectionPriceSeries.getData().clear();
                        secondSelectionSeriesTotal.getData().clear();
                        deltaPriceSeries.getData().clear();
                        secondSeriesDateToPrice.clear();
                        populateAllSeriesAndMatchBounds(secondSelectionResults, firstSeriesDateToPrice, secondSelectionPriceSeries, secondSelectionSeriesTotal, secondSeriesDateToPrice, orderOfSeriesPointsToId, deltaPriceSeries);
                    }

                } catch (SolrServerException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                matchBoundsBetweenCharts();
                resetButton.fire();
                openSecondSeriesListButton.setText("Series: " + newSelection);
            }
        });

        firstSelectionContainer.setSpacing(10);
        secondSelectionContainer.setSpacing(10);
        secondSelectionChart.setOnMouseClicked(chartEventHandler);
        secondSelectionChart.setOnMouseDragged(chartEventHandler);
        secondSelectionChart.setOnMouseMoved(chartEventHandler);
        secondSelectionChart.setOnMousePressed(chartEventHandler);
        secondSelectionChart.setOnMouseReleased(chartEventHandler);
        secondSelectionChart.setOnMouseEntered(chartEventHandler);
        secondSelectionChart.setOnMouseExited(chartEventHandler);

        lineChartOverview.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                positionXOverviewChart = mouseEvent.getSceneX();
            }
        });

        lineChartOverview.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                double deltaX = positionXOverviewChart - mouseEvent.getSceneX();
                positionXOverviewChart = mouseEvent.getSceneX();
                if (positionXOverviewChart < 45 || positionXOverviewChart > 1390) return;
                if (rightHookPosition.getValue() - deltaX < 1390 && leftHookPosition.getValue() - deltaX >= 0) {

                    leftHookPosition .set(leftHookPosition.getValue() - deltaX);
                    rightHookPosition.set(rightHookPosition.getValue() - deltaX);
                }
                LocalDateTime valueForDisplayFinish = lineChartOverview.getXAxis().getValueForDisplay(rightHookPosition.getValue() - 45.0);
                LocalDateTime valueForDisplayStart  = lineChartOverview.getXAxis().getValueForDisplay(leftHookPosition.getValue());

                firstValue = String.valueOf(valueForDisplayFinish);
                lastValue = String.valueOf(valueForDisplayStart);

                firstSelectionChart .getXAxis().setAutoRanging(true);
                secondSelectionChart.getXAxis().setAutoRanging(true);

                cutLeftAndRight(firstSelectionPriceSeries, firstSelectionDataRemovedFromBack, firstSelectionDataRemovedFromFront, deltaX, deltaX);
                cutLeftAndRight(secondSelectionPriceSeries, secondSelectionDataRemovedFromBack, secondSelectionDataRemovedFromFront, deltaX, deltaX);
                cutLeftAndRight(deltaPriceSeries, deltaDataRemovedFromBack, deltaDataRemovedFromFront, deltaX, deltaX);

                ChartActions.setChartsUpperBound(valueForDisplayFinish, firstSelectionChart, secondSelectionChart, firstSelectionYAxis, secondSelectionYAxis, diffBarChart);
                ChartActions.setChartsLowerBound(valueForDisplayStart, firstSelectionChart, secondSelectionChart, firstSelectionYAxis, secondSelectionYAxis, diffBarChart);

                initialRightHookPosition = rightHookPosition.getValue();
                initialLeftHookPosition  = leftHookPosition.getValue();
            }
        });
    }

    private void displayLackOfDataInSeries() {
        detail.setText(PRICE_SERIES_ONLY_CONTAINS_ONE_POINT_OF_DATA);
        detail.setVisible(true);
        detail.setTranslateX(600);
        detail.setTranslateY(300);
        Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        detail.setVisible(false);
                    }
                });
            }
        };
        timer.schedule(timerTask, 2000);
    }

    private boolean isHookInBounds(MouseEvent mouseEvent) {
        if (rightRect.getLayoutX() - 50 < leftRect.getWidth() || mouseEvent.getSceneX() - 55 < leftRect.getWidth()) {
            rightHookPosition.set(leftRect.getWidth() + 60);
            return false;
        } else if (mouseEvent.getSceneX() > 1370) {
            rightHookPosition.setValue(1385);
            return false;
        } else{
            return true;
        }
    }

    private void resetControlsForNewSelection() {
        leftHookPosition .setValue(0);
        rightHookPosition.setValue(1380);
        propertiesPane   .setVisible(false);
        orderOfSeriesPointsToId.clear();
    }

    private void matchBoundsBetweenCharts() {
        DateAxis310 barChartXAxis = (DateAxis310) diffBarChart.getXAxis();
        DateAxis310 firstSelectionChartXAxis = (DateAxis310) firstSelectionChart.getXAxis();
        DateAxis310 secondSelectionChartXAxis = (DateAxis310) secondSelectionChart.getXAxis();

        barChartXAxis.setAutoRanging(false);
        barChartXAxis.lowerBoundProperty().bind(firstSelectionChartXAxis.lowerBoundProperty());
        barChartXAxis.upperBoundProperty().bind(firstSelectionChartXAxis.upperBoundProperty());

        firstSelectionChartXAxis.setAutoRanging(false);
        secondSelectionChartXAxis.setAutoRanging(false);
        Date firstSelectionUpper = new Date();
        Date secondSelectionUpper = new Date();
        XYChart.Data upperDataFirst = (XYChart.Data) firstSelectionPriceSeries.getData().get(firstSelectionPriceSeries.getData().size() - 1);
        XYChart.Data upperDataSecond = (XYChart.Data) secondSelectionPriceSeries.getData().get(secondSelectionPriceSeries.getData().size() - 1);
        try {
            firstSelectionUpper = getSimpleDateFormat(upperDataFirst);
            secondSelectionUpper = getSimpleDateFormat(upperDataSecond);
        } catch (ParseException e) {

        }
        Date firstSelectionLower = new Date();
        Date secondSelectionLower = new Date();
        XYChart.Data lowerDataFirst  = (XYChart.Data) firstSelectionPriceSeries.getData().get(0);
        XYChart.Data lowerDataSecond = (XYChart.Data) secondSelectionPriceSeries.getData().get(0);
        try {
            firstSelectionLower = getSimpleDateFormat(lowerDataFirst);
            secondSelectionLower = getSimpleDateFormat(lowerDataSecond);
        } catch (ParseException e) {

        }
        long lowerBoundFirstSelectionInNano = firstSelectionLower.getTime();
        long lowerBoundSecondSelectionInNano = secondSelectionLower.getTime();

        long upperBoundFirstSelectionInNano = firstSelectionUpper.getTime();
        long upperBoundSecondSelectionInNano = secondSelectionUpper.getTime();
        firstSelectionChartXAxis.setAutoRanging(false);
        secondSelectionChartXAxis.setAutoRanging(false);

        if (lowerBoundFirstSelectionInNano < lowerBoundSecondSelectionInNano){
            secondSelectionChartXAxis.setLowerBound(getLocalDateTime(lowerDataFirst));
            firstSelectionChartXAxis.setLowerBound(getLocalDateTime(lowerDataFirst));
        }else{
            firstSelectionChartXAxis.setLowerBound(getLocalDateTime(lowerDataSecond));
            secondSelectionChartXAxis.setLowerBound(getLocalDateTime(lowerDataSecond));
        }

        if (upperBoundFirstSelectionInNano > upperBoundSecondSelectionInNano){
            secondSelectionChartXAxis.setUpperBound(getLocalDateTime(upperDataFirst));
            firstSelectionChartXAxis.setUpperBound(getLocalDateTime(upperDataFirst));
        }else{
            firstSelectionChartXAxis.setUpperBound(getLocalDateTime(upperDataSecond));
            secondSelectionChartXAxis.setUpperBound(getLocalDateTime(upperDataSecond));
        }
    }

    private Date getSimpleDateFormat(XYChart.Data upperDataFirst) throws ParseException {
        return new SimpleDateFormat(YYYY_MM_DD_T_HH_MM).parse(((LocalDateTime) upperDataFirst.getXValue()).toString());
    }

    public void populateAllSeriesAndMatchBounds(List results, HashMap otherSeriesDateToPrice, XYChart.Series series, XYChart.Series totalSeries, HashMap seriesDateToPrice, HashMap seriesPointsToId, XYChart.Series differenceSeries) {
        for (int c = 0; c <= results.size() - 1; c++) {
            Price price = (Price) results.get(c);
            seriesDateToPrice.put(price.getPrice_date(), price.getPrice());
            try{
                series.getData().add(ChartActions.createChartDataFrom(price));
                totalSeries.getData().add(ChartActions.createChartDataFrom(price));
                seriesPointsToId.put(c, price.getId());
            }catch (Exception e){
                series.getData().add(ChartActions.createChartDataFrom(price));
                seriesPointsToId.put(c, price.getId());
                totalSeries.getData().add(ChartActions.createChartDataFrom(price));
            }
            try{
                Float firstPrice  =  Float.valueOf(otherSeriesDateToPrice.get(price.getPrice_date()).toString());
                Float secondPrice = Float.valueOf(price.getPrice());

                Float difference = Math.abs(firstPrice - secondPrice);
                differenceSeries.getData().add(new XYChart.Data((LocalDateTime.parse(Main.toUtcDate(price.getPrice_date()))), difference));
            }catch (Exception e){
            }
        }
        matchBoundsBetweenCharts();
    }

    private LocalDateTime getLocalDateTime(XYChart.Data lowerDataFirst) {
        return LocalDateTime.parse(lowerDataFirst.getXValue().toString());
    }

    private void formatDisplayLabel(Label label) {
        label.layoutYProperty().bind(trackYPosition);
        label.setMouseTransparent(true);
        label.setTranslateX(10);
        label.setTranslateY(-10);
        label.setEffect(new InnerShadow(2, Color.ORANGE));
        label.setFont(Font.font(null, FontWeight.BOLD, 10));
    }

    private class XTargetChangeListener implements ChangeListener<Number> {
        @Override
        public void changed(ObservableValue<? extends Number> observableValue, Number number, Number number2) {
            String displayValueAtLocation = (String.valueOf(firstSelectionChart.getXAxis().getValueForDisplay((Double) number2)));

            displayAtTarget.setText(displayValueAtLocation.substring(0, 10));
        }
    }

    public void search(String newVal, ObservableList list, ListView listView) throws SolrServerException, IOException {
            List<String> instrumentsFromName = SolrService.getInstrumentsFromName(newVal);
            list = FXCollections.observableArrayList(instrumentsFromName);
            listView.setItems(list);
    }
    @Override
    public void init() throws Exception {
        firstSelectionDataRemovedFromFront = new ArrayList<>();
        firstSelectionDataRemovedFromBack = new ArrayList<>();
        firstListOfInstruments = new ListView<>();

        lineChartsContainingPane = new Pane();
        lineChartsContainingPane.setFocusTraversable(true);

        yAxis = new NumberAxis();
        xAxis = new DateAxis310();
        yBarAxis = new NumberAxis();
        xBarAxis = new DateAxis310();

        firstSelectionYAxis = ChartActions.makeZoomYAxis(firstSelectionLowerYBound, firstSelectionUpperYBound, firstChartInitYPos, dragArea);
        firstSelectionXAxis = new DateAxis310();
        firstSelectionXAxis.setTickLabelFormatter(STRING_CONVERTER);

        secondSelectionYAxis = ChartActions.makeZoomYAxis(secondSelectionLowerYBound, secondSelectionUpperYBound, secondChartInitYPos, dragArea);
        secondSelectionXAxis = new DateAxis310();
        secondSelectionXAxis.setTickLabelFormatter(STRING_CONVERTER);

        xBarAxis.setTickLabelFormatter(STRING_CONVERTER);
        finalContainingLayout = new HBox();
        detailPane = new Pane();
        overviewChartPane = new Pane();
        chartBox = new VBox();

        diffBarChart = new AreaChart<>(xBarAxis,yBarAxis);

        separator = new Separator(Orientation.HORIZONTAL);
        leftRect.setTranslateX(45);
        leftRect.setFill(Color.web("gray", 0.1));
        leftRect.setStroke(Color.GRAY);
        leftRect.widthProperty().bind(leftHookPosition);

        hookLeft.setFill(Color.web("gray", 0.6));
        hookLeft.setTranslateX(37.5);
        hookLeft.setTranslateY(55);
        hookLeft.setStroke(Color.DARKGRAY);
        hookLeft.layoutXProperty().bind(leftHookPosition);
        windowWidth = new SimpleDoubleProperty(1450);

        rightRect.setWidth(0);
        rightRect.setFill(Color.web("gray", 0.1));
        rightRect.setStroke(Color.GRAY);
        rightRect.layoutXProperty().bind(rightHookPosition);
        rightRect.widthProperty().bind(windowWidth.subtract(rightRect.layoutXProperty()));

        hookRight.setFill(Color.web("gray", 0.6));
        hookRight.setTranslateX(-7.5);
        hookRight.setLayoutX(1310);
        hookRight.setTranslateY(55);
        hookRight.setStroke(Color.DARKGRAY);
        hookRight.layoutXProperty().bind(rightHookPosition);
        rightHookPosition.set(1385);

        formatDisplayLabel(displayAtPosition);
        formatDisplayLabel(displayAtTarget);

        verticalTrackingLine.setMouseTransparent(true);
        verticalTrackingLine.layoutXProperty().bind(trackXPosition);
        verticalTrackingLine.setStroke(Color.DARKGRAY);
        displayAtPosition.layoutXProperty().bind(trackXPosition);
        displayAtTarget.layoutXProperty().bind(trackXTargetPosition);

        zoomBounds = new Rectangle();
        zoomBounds.setFill(Color.web("gray", 0.1));
        zoomBounds.setStroke(Color.DARKGRAY);
        zoomBounds.setStrokeDashOffset(50);
        zoomBounds.setMouseTransparent(true);
        zoomBounds.widthProperty().bind(rectX.subtract(initRectX));
        zoomBounds.heightProperty().bind(rectY.subtract(initRectY));

        labelInstruments = new Label("List of Instruments");
        labelInstruments.setFont(new Font("Calibri", 22));
        labelInstruments.setTranslateX(50);

        firstListSearch.textProperty().addListener(new ChangeListener() {
            public void changed(ObservableValue observable, Object oldVal,
                                Object newVal) {
                firstListOfInstruments.getSelectionModel().clearSelection();
                try {
                    search((String) newVal, firstObservableListInstruments, firstListOfInstruments);
                } catch (SolrServerException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        secondListSearch.textProperty().addListener(new ChangeListener() {
            public void changed(ObservableValue observable, Object oldVal,
                                Object newVal) {
                secondListOfInstruments.getSelectionModel().clearSelection();
                try {
                    search((String) newVal, secondObservableListInstruments, secondListOfInstruments);
                } catch (SolrServerException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        chartEventHandler = (mouseEvent) -> {
            if (mouseEvent.getSceneX() < 55 || mouseEvent.getSceneX() > 1380) {
                verticalTrackingLine.           setVisible(false);
                displayAtPosition.setVisible(false);
                displayAtTarget.  setVisible(false);
                return;
            }
            if (mouseEvent.getEventType() == MouseEvent.MOUSE_PRESSED) {
                zoomBounds.setX(mouseEvent.getSceneX());
                zoomBounds.setY(-20);
                initRectX.set(mouseEvent.getSceneX());
                initRectY.set(0);
            } else if (mouseEvent.getEventType() == MouseEvent.MOUSE_DRAGGED) {
                trackXTargetPosition.set(mouseEvent.getSceneX());
                rectX.               set(mouseEvent.getSceneX());
                rectY.               set(firstSelectionChart.getHeight() + 20);
                displayAtTarget.setVisible(true);
                lineIndicator.  setVisible(false);

            } else if (mouseEvent.getEventType() == MouseEvent.MOUSE_RELEASED) {
                displayAtTarget.  setVisible(false);
                displayAtPosition.setVisible(false);
                if (rectX.getValue() - initRectX.getValue() < 20){
                    rectX.set(0);
                    rectY.set(0);

                    return;
                }
                String valueForDisplayStart = "";
                String valueForDisplayEnd = "";
                LocalDateTime localDateTimeStart  = null;
                LocalDateTime localDateTimeFinish = null;
                try{
                    localDateTimeStart  = firstSelectionChart.getXAxis().getValueForDisplay(initRectX.getValue() - 55.0);
                    localDateTimeFinish = firstSelectionChart.getXAxis().getValueForDisplay(rectX.getValue() - 55.0);

                    valueForDisplayStart = String.valueOf(localDateTimeStart);
                    valueForDisplayEnd   = String.valueOf(localDateTimeFinish);
                    firstValue = valueForDisplayEnd;
                    lastValue = valueForDisplayStart;
                    double left  = lineChartOverview.getXAxis().getDisplayPosition(LocalDateTime.parse(valueForDisplayStart));
                    double right = lineChartOverview.getXAxis().getDisplayPosition(LocalDateTime.parse(valueForDisplayEnd));

                    String startDate = valueForDisplayStart.substring(0, 19).concat("Z");
                    String endDate   = valueForDisplayEnd.substring(0, 19).concat("Z");

                    leftHookPosition. set(left);
                    rightHookPosition.set(right + 60);

                    firstSelectionResults  = SolrService.getResults(startDate, endDate, firstListOfInstruments.getSelectionModel().getSelectedItem(), firstListOfSeries.getSelectionModel().getSelectedItem());
                    secondSelectionResults = SolrService.getResults(startDate, endDate, secondListOfInstruments.getSelectionModel().getSelectedItem(), secondListOfSeries.getSelectionModel().getSelectedItem());
                } catch (SolrServerException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (Exception e){
                }

                ChartActions.setChartsUpperBound(localDateTimeFinish, firstSelectionChart, secondSelectionChart, firstSelectionYAxis, secondSelectionYAxis, diffBarChart);
                ChartActions.setChartsLowerBound(localDateTimeStart, firstSelectionChart, secondSelectionChart, firstSelectionYAxis, secondSelectionYAxis, diffBarChart);

                cutLeftAndRight(secondSelectionPriceSeries, secondSelectionDataRemovedFromBack, secondSelectionDataRemovedFromFront, -1.0, 1.0);
                cutLeftAndRight(firstSelectionPriceSeries, firstSelectionDataRemovedFromBack, firstSelectionDataRemovedFromFront, -1.0, 1.0);
                cutLeftAndRight(deltaPriceSeries, deltaDataRemovedFromBack, deltaDataRemovedFromFront, -1.0, 1.0);
                rectX.set(0);
                rectY.set(0);

            }else if (mouseEvent.getEventType() == MouseEvent.MOUSE_MOVED){
                verticalTrackingLine.setVisible(true);
                trackXPosition.set(mouseEvent.getSceneX());
                trackYPosition.set(mouseEvent.getSceneY());
            }else if (mouseEvent.getEventType() == MouseEvent.MOUSE_EXITED){
                verticalTrackingLine.setVisible(false);
                displayAtPosition.setVisible(false);
                displayAtTarget.setVisible(false);
            }else if (mouseEvent.getEventType() == MouseEvent.MOUSE_ENTERED){
                verticalTrackingLine.setVisible(true);
                displayAtPosition.setVisible(true);
            }
        };

        Reflection r = new Reflection();
        r.setFraction(0.1);
        firstSelectionContainer.getStyleClass().add(PANE_STYLE);
        secondSelectionContainer.getStyleClass().add(PANE_STYLE);
        secondSelectionContainer.setMaxWidth(265);
        firstSelectionContainer.setMaxWidth(265);
        secondSelectionContainer.setMinWidth(265);
        firstSelectionContainer.setMinWidth(265);

        openSecondSeriesListButton.setEffect(r);
        openSecondInstrumentListButton.setEffect(r);
        openFirstInstrumentListButton.setEffect(r);
        openFirstSeriesListButton.setEffect(r);

        openSecondSeriesListButton.setMinWidth(265);
        openSecondInstrumentListButton.setMinWidth(265);
        openFirstInstrumentListButton.setMinWidth(265);
        openFirstSeriesListButton.setMinWidth(265);

        listBox = new VBox(labelInstruments, resetButton, firstSelectionContainer, secondSelectionContainer);
        listBox.setPadding(new Insets(10, 10, 10, 10));
        listBox.setSpacing(15);
        detailPane.getChildren().addAll(listBox);
        detailPane.setMinWidth(400);

        firstSelectionSeriesTotal = new XYChart.Series();
        secondSelectionSeriesTotal = new XYChart.Series();

        miniMapDetail = new Text();
        miniMapDetail.setFill(Color.WHITE);
        miniMapDetail.setEffect(new InnerShadow(2, Color.BLACK));
        miniMapDetail.setFont(Font.font(null, FontWeight.BOLD, 10));
        miniMapDetail.setVisible(false);

        detail = new Text();
        detail.setCache(true);
        detail.setFill(Color.GRAY);
        detail.setEffect(new InnerShadow(2, Color.BLACK));
        detail.setFont(Font.font(null, FontWeight.BOLD, 13));
        detail.setVisible(false);

        leftContainingPane = new Pane();

        resetButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                secondSelectionYAxis.lowerBoundProperty().unbind();
                secondSelectionYAxis.upperBoundProperty().unbind();
                secondSelectionYAxis.setAutoRanging(true);
                firstSelectionYAxis.lowerBoundProperty().unbind();
                firstSelectionYAxis.upperBoundProperty().unbind();
                firstSelectionYAxis.setAutoRanging(true);

            }
        });

        lineIndicator.setStroke(Color.DARKGRAY);
        yAxis.setForceZeroInRange(false);
        separator.prefWidthProperty().bind(overviewChartPane.widthProperty());
        firstListOfInstruments.setMaxWidth(250);
        firstListOfSeries.setMaxWidth(250);
        firstListOfInstruments.setMaxHeight(300);
        firstListOfSeries.setMaxHeight(300);

        placeHolder = new Text();
        placeHolder.setFill(Color.WHITE);
        placeHolder.setEffect(new InnerShadow(2, Color.BLACK));
        placeHolder.setFont(Font.font(null, FontWeight.BOLD, 13));
        placeHolder.setTranslateY(-100);
        placeHolder.setText("No series");
        firstListOfInstruments.setPlaceholder(placeHolder);
        secondListOfInstruments.setPlaceholder(placeHolder);

    }
}