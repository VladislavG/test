package solrTest;

import javafx.application.Application;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.effect.InnerShadow;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Path;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.StringConverter;
import org.apache.solr.client.solrj.SolrServerException;
import temp.Delta;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static java.time.temporal.ChronoUnit.*;

public class Main extends Application {
    XYChart.Series seriesHighRaw = new XYChart.Series();
    int idFirstPoint;
    Double firstPointX;
    Double initYpos;
    String dragArea;
    SimpleDoubleProperty upperBoundForYMain = new SimpleDoubleProperty();
    SimpleDoubleProperty lowerBoundForYMain = new SimpleDoubleProperty();
    SimpleDoubleProperty selectedMarkSize = new SimpleDoubleProperty();
    Double firstPointY;
    Text placeHolder;
    Button resetButton = new Button("Reset zoom");
    Button resetTableButton = new Button("Clear Trable");
    Label chartItemSize = new Label();
    Label chartLowerBound = new Label();
    Label chartUpperBound = new Label();
    Label chartSpecialMarksCount = new Label();
    int markCount = 0;
    int idSecondPoint;
    Double secondPointX;
    Double secondPointY;
    SplitPane splitPane;
    Pane simplePane;
    VBox chartBox;
    Pane miniMapPane;
    double positionXOverviewChart;
    NumberAxis yLineAxis;
    DateAxis310 xCLineAxis;
    boolean ignoreSeriesChange;
    int c = 0;

    ListView<String> listOfMarks;
    ListView<String> listOfSeries = new ListView<>();
    ObservableList<String> observableListInstruments;
    ObservableList<String> observableListInstrumentsForSearch;
    ObservableList<String> observableListSeries;
    ObservableList<Item> observableListItems;
    List<StackPane> marks = new ArrayList<>();


    final StringConverter<LocalDateTime> STRING_CONVERTER = new StringConverter<LocalDateTime>() {
        @Override public String toString(LocalDateTime localDateTime) {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            return dtf.format(localDateTime);
        }
        @Override public LocalDateTime fromString(String s) {
            return LocalDateTime.parse(s);
        }
    };

    String lastEarliestValue;
    String lastLatestValue;

    Rectangle zoomBounds;
    Line trackX = new Line(0, 550, 0, 0);
    Label displayAtPosition = new Label();
    Label displayAtTarget = new Label();
    Label spikes;
    VBox listBox;
    SimpleDoubleProperty leftHookPosition = new SimpleDoubleProperty();
    SimpleDoubleProperty rightHookPosition = new SimpleDoubleProperty();
    SimpleDoubleProperty trackXPosition = new SimpleDoubleProperty();
    SimpleDoubleProperty trackXTargetPosition = new SimpleDoubleProperty();
    SimpleDoubleProperty trackYPosition = new SimpleDoubleProperty();

    SimpleDoubleProperty rectinitX = new SimpleDoubleProperty();
    SimpleDoubleProperty rectinitY = new SimpleDoubleProperty();
    SimpleDoubleProperty rectX = new SimpleDoubleProperty();
    SimpleDoubleProperty rectY = new SimpleDoubleProperty();
    SimpleDoubleProperty tableY = new SimpleDoubleProperty();
    SimpleDoubleProperty tablex = new SimpleDoubleProperty();
    SimpleDoubleProperty mouseY = new SimpleDoubleProperty();
    SimpleDoubleProperty mousex = new SimpleDoubleProperty();

    LineChart<LocalDateTime, Number> lineChartOverview;
    LineChart<LocalDateTime, Number> lineChart;

    double distanceBetweenPoint;
    List<Item> results;
    List<String> instrumentResults;
    List<String> seriesResults;
    List<Item> itemResults = new ArrayList<>();
    ArrayList<XYChart.Data<LocalDateTime, Float>> dataRemovedFromFront;
    ArrayList<XYChart.Data<LocalDateTime, Float>> dataRemovedFromBack;

    double initialLeftHookPosition = 0.0;
    double initialRightHookPosition = 0.0;
    Line lineIndicator = new Line(0, 550, 0, 0);
    Rectangle hookRight = new Rectangle(15,40);
    Rectangle hookLeft = new Rectangle(15,40);
    Rectangle leftRect = new Rectangle(30,150);
    Rectangle rightRect = new Rectangle(30,150);
    Separator separator;
//    String selectedDate;
    ChoiceBox<String> propertiesCB = new ChoiceBox<>();
    Pane propertiesPane = new Pane();

    ArrayList<String> tickValues;
    ArrayList<Double> valuesHighRaw;
    ArrayList<Double> changeOfvalues;
    double[] olsRegression;

    SimpleDoubleProperty width;
    Delta zoomFactor;
    ArrayList<LocalDateTime> aboveAverages;
    ObservableList<LocalDateTime> observableAboveAverages;
    XYChart.Series seriesTotal;
    XYChart.Series seriesAverageHigh;
    XYChart.Series seriesAverageLow;
    XYChart.Series seriesOpenRaw;
    ObservableList<String> possiblePriceProperties;
    String selectedId;
//    String selectedDate;
    HashMap orderOfSeriesPointsToId = new HashMap();
    HashMap orderOfGraphPointsToId = new HashMap();
    TableView tableOfProperties = new TableView();

    Pane lineChartPane;
    NumberAxis yAxis;
    DateAxis310 xAxis;
    Label propertiesLabel = new Label("Select a property");

    Text miniMapDetail;
    Text detail;
    Pane containingPane;
    VBox propertiesVBox = new VBox();

    public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void init() throws Exception {
	}

	@Override
	public void stop() throws Exception {
	}

	@Override
	public void start(Stage stage) throws Exception {
		final Group root = new Group();
		Scene scene = new Scene(root, 1750, 700, Color.WHITESMOKE);
        initComponents();

        System.out.println("Java Version             : " + com.sun.javafx.runtime.VersionInfo.getVersion());
        System.out.println("Java getRuntimeVersion   : " + com.sun.javafx.runtime.VersionInfo.getRuntimeVersion());

        for (double t = 0.0; t < 1.5; t=t+0.1){
            tickValues.add(String.valueOf(Math.round(t * 100.0) / 100.0));
        }
        instrumentResults = SolrService.getInstruments("*");
        seriesResults = SolrService.getSeries(instrumentResults.get(0));
        listOfMarks.setCellFactory(new Callback<ListView<String>,
                ListCell<String>>() {
            @Override
            public ListCell<String> call(ListView<String> list) {
                ListCell<String> cell = new ListCell<String>(){

                    @Override
                    protected void updateItem(String t, boolean bln) {
                        super.updateItem(t, bln);
                        if (t != null) {
                            try {
                                setText(SolrService.getShortName(t));
                            } catch (SolrServerException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }else{
                            setText("");
                        }
                    }
                };
                return cell;
            }
        });
        TableColumn activeColumn = new TableColumn("Active");
        TableColumn deliveryColumn = new TableColumn("Delivery Frequency");
        TableColumn priceColumn = new TableColumn("Price");
        TableColumn currencyColumn = new TableColumn("Currency");
        TableColumn orderColumn = new TableColumn("Order");
        TableColumn ubsColumn = new TableColumn("UBS Relevant");
        TableColumn priceDescriptionColumn = new TableColumn("Price Description");
        TableColumn tradeableDescriptionColumn = new TableColumn("Tradeable Description");

        activeColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Item, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Item, String> param) {
                SimpleStringProperty stringProperty = new SimpleStringProperty(param.getValue().getActive());
                return stringProperty;
            }
        });
        deliveryColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Item, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Item, String> param) {
                SimpleStringProperty stringProperty = new SimpleStringProperty(param.getValue().getDelivery_frequency());
                return stringProperty;
            }
        });
        priceColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Item, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Item, String> param) {
                SimpleStringProperty stringProperty = new SimpleStringProperty(param.getValue().getPrice());
                return stringProperty;
            }
        });
        currencyColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Item, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Item, String> param) {
                SimpleStringProperty stringProperty = new SimpleStringProperty(param.getValue().getCurrency());
                return stringProperty;
            }
        });
        orderColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Item, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Item, String> param) {
                SimpleStringProperty stringProperty = new SimpleStringProperty(param.getValue().getOrder_id());
                return stringProperty;
            }
        });
        ubsColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Item, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Item, String> param) {
                SimpleStringProperty stringProperty = new SimpleStringProperty(param.getValue().getUbs_relevant());
                return stringProperty;
            }
        });
        tradeableDescriptionColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Item, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Item, String> param) {
                SimpleStringProperty stringProperty = new SimpleStringProperty(param.getValue().getTradeable_description());
                return stringProperty;
            }
        });
        priceDescriptionColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Item, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Item, String> param) {
                SimpleStringProperty stringProperty = new SimpleStringProperty(param.getValue().getIm_price_short_description());
                return stringProperty;
            }
        });
        tableOfProperties.setMaxHeight(150);

        tableOfProperties.getColumns().addAll(activeColumn, deliveryColumn, priceColumn, currencyColumn, orderColumn, ubsColumn, priceDescriptionColumn, tradeableDescriptionColumn);
        possiblePriceProperties = FXCollections.observableArrayList("Active", "Delivery Frequency", "IM Status", "UBS Relevant", "Derived", "Expiration Description");
        propertiesCB.getItems().setAll(possiblePriceProperties);
        propertiesCB.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String previousSelection, String currentSelection) {
                Item item = null;
                try {
                    item = SolrService.getResultsFromId(selectedId);
                } catch (SolrServerException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                switch (currentSelection) {
                    case "Active":
                        propertiesLabel.setText(item.getActive());
                        break;
                    case "Delivery Frequency":
                        propertiesLabel.setText(item.getDelivery_frequency());
                        break;
                    case "IM Status":
                        propertiesLabel.setText(item.getIm_status_short_description());
                        break;
                    case "UBS Relevant":
                        propertiesLabel.setText(item.getUbs_relevant());
                        break;
                    case "Derived":
                        propertiesLabel.setText(item.getIs_derived());
                        break;
                    case "Expiration Description":
                        propertiesLabel.setText(item.getExpiration_description());
                        break;
                    default:
                        propertiesLabel.setText("N/A");
                        break;
                }
            }
        });
        propertiesVBox.getChildren().addAll(tableOfProperties);
        propertiesVBox.setSpacing(15);
        propertiesVBox.setPadding(new Insets(10, 10, 10, 10));
        propertiesPane.getChildren().addAll(propertiesVBox);
        propertiesPane.setLayoutX(370);
        propertiesPane.setLayoutY(45);

        propertiesPane.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                tablex.setValue(propertiesPane.getTranslateX());
                tableY.setValue(propertiesPane.getTranslateY());
                mouseY.setValue(mouseEvent.getSceneY());
                mousex.setValue(mouseEvent.getSceneX());
            }
        });

        propertiesPane.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                propertiesPane.setTranslateX(tablex.getValue() + mouseEvent.getSceneX() - mousex.getValue());
                propertiesPane.setTranslateY(tableY.getValue() + mouseEvent.getSceneY() - mouseY.getValue());
            }
        });
        observableListInstruments = FXCollections.observableArrayList(instrumentResults);
        observableListSeries = FXCollections.observableArrayList(seriesResults);
        results = SolrService.getResults("*", "*", observableListInstruments.get(0), observableListSeries.get(0));
        int size = results.size() - 1;
        List<String> providers = new ArrayList<>();
        listOfMarks.getItems().setAll(observableListInstruments);
        listOfSeries.getItems().setAll(observableListSeries);
        for (int c = 0; c <= size; c++) {

            Item item = results.get(c);
//            itemResults.add(item);
//            if (item.getId().equals("spike")){
//                aboveAverages.add(LocalDateTime.parse(toUtcDate(item.getCreation_date()).substring(0, 19)));
//                observableAboveAverages.add(LocalDateTime.parse(toUtcDate(item.getCreation_date()).substring(0, 19)));
//                markCount++;
//            }
              try{

                  seriesHighRaw.getData().add(new XYChart.Data(LocalDateTime.parse(toUtcDate(item.getPrice_date())), Float.valueOf(item.getPrice())));
              }catch (Exception e){

              }

            orderOfSeriesPointsToId.put(c, item.getId());

            seriesTotal.getData().add(new XYChart.Data(LocalDateTime.parse(toUtcDate(item.getPrice_date()).substring(0, 19)), Float.valueOf(item.getPrice())));
        }

        lineChartOverview = new LineChart<LocalDateTime, Number>(xAxis, yAxis){
                    @Override
                    protected void layoutPlotChildren() {
                        super.layoutPlotChildren();

                        for (Node mark : getPlotChildren()) {
                            if (!(mark instanceof StackPane)){
                                Path g = (Path) mark;
                                g.setStrokeWidth(1.5);
                            }
                            if (mark instanceof StackPane) {
                                mark.setScaleX(0.1);
                                mark.setScaleY(0.1);
                                Bounds bounds = mark.getBoundsInParent();
                                double posX = bounds.getMinX() + (bounds.getMaxX() - bounds.getMinX()) / 2.0;
                                double posY = bounds.getMinY() + (bounds.getMaxY() - bounds.getMinY()) / 2.0;
                                Double number = Double.valueOf(getYAxis().getValueForDisplay(posY).toString());
                                LocalDateTime date = getXAxis().getValueForDisplay(posX).truncatedTo(DAYS);
                                if (aboveAverages.contains(date)){
                                    mark.setScaleX(.5);
                                    mark.setScaleY(.5);
                                    ImageView flag = new ImageView("flag.png");
                                    flag.setScaleX(.4);
                                    flag.setScaleY(.4);
                                    flag.setTranslateY(-20);
                                    flag.setTranslateX(11);
//                                        ((StackPane) mark).getChildren().add(flag);
                                    mark.setEffect(new InnerShadow(2, Color.BLACK));

                                    mark.setOnMouseEntered(new EventHandler<MouseEvent>() {
                                        @Override
                                        public void handle(MouseEvent mouseEvent) {
                                            if (mark.getScaleX() == 0.5) {
                                                miniMapDetail.setText(String.valueOf((double) Math.round(number * 10) / 10));
                                                miniMapDetail.setTranslateX(mouseEvent.getSceneX() - 10);
                                                miniMapDetail.setTranslateY(mouseEvent.getSceneY() - 30);
                                                miniMapDetail.setVisible(true);
                                            }
                                        }
                                    });
                                    mark.setOnMouseExited(new EventHandler<MouseEvent>() {
                                        @Override
                                        public void handle(MouseEvent mouseEvent) {
                                            if (miniMapDetail.isVisible()) {
                                                miniMapDetail.setVisible(false);
                                            }
                                        }
                                    });
                                }
                            }
                        }
                    }
                };



        lineChart = new LineChart<LocalDateTime, Number>(xCLineAxis, yLineAxis){
                    @Override
                    protected void layoutPlotChildren() {
                        super.layoutPlotChildren();
//                        for (Node node : getChildren()){
//
//                            node.setOnZoom(new EventHandler<ZoomEvent>() {
//                                @Override
//                                public void handle(ZoomEvent event) {
//                                    zoomFactor.setX(event.getZoomFactor());
//                                }
//                            });
//
//                            node.setOnTouchPressed(new EventHandler<TouchEvent>() {
//                                @Override
//                                public void handle(TouchEvent touchEvent) {
//                                    idFirstPoint = touchEvent.getTouchPoints().get(0).getId();
//                                    firstPointX = touchEvent.getTouchPoints().get(0).getSceneX();
//                                    firstPointY = touchEvent.getTouchPoints().get(0).getSceneY();
//                                    try{
//                                        idSecondPoint = touchEvent.getTouchPoints().get(1).getId();
//                                        secondPointX = touchEvent.getTouchPoints().get(1).getSceneX();
//                                        secondPointY = touchEvent.getTouchPoints().get(1).getSceneY();
//
//                                        distanceBetweenPoint = firstPointX - secondPointX;
//                                    }catch (Exception e){
//                                    }
//                                }
//                            });
//
//                            node.setOnTouchMoved(new EventHandler<TouchEvent>() {
//                                @Override
//                                public void handle(TouchEvent touchEvent) {
//                                    List<TouchPoint> touchPoints = touchEvent.getTouchPoints();
//                                    for (TouchPoint touchPoint : touchPoints) {
//                                       if (touchPoint.getId() == idFirstPoint){
//                                           firstPointX = touchPoint.getSceneX();
//                                           firstPointY = touchPoint.getSceneY();
//
//                                       }else if (touchPoint.getId() == idSecondPoint){
//                                           secondPointX = touchPoint.getSceneX();
//                                           secondPointY = touchPoint.getSceneY();
//                                       }
//                                    }
//                                    distanceBetweenPoint = firstPointX - secondPointX;
//
//                                }
//                            });
//                            node.setOnTouchReleased(new EventHandler<TouchEvent>() {
//                                @Override
//                                public void handle(TouchEvent touchEvent) {
//                                    //refreshGraphFromSolr();
//                                }
//                            });
//                        }
                        for (Node mark : getPlotChildren()) {
                                if (!(mark instanceof StackPane)){
                                    Path g = (Path) mark;
                                    g.setStrokeWidth(1.5);
                                    c=0;
                                    orderOfGraphPointsToId.clear();
                                }
                            if (mark instanceof StackPane) {
//                                mark.setScaleX(0.0);
//                                mark.setScaleY(0.0);
                                orderOfGraphPointsToId.put(mark.toString(), c);
                                c++;
                                Bounds bounds = mark.getBoundsInParent();
                                double posX = bounds.getMinX() + (bounds.getMaxX() - bounds.getMinX()) / 2.0;
                                double posY = bounds.getMinY() + (bounds.getMaxY() - bounds.getMinY()) / 2.0;
                                Double number = Double.valueOf(getYAxis().getValueForDisplay(posY).toString());
                                LocalDateTime date = getXAxis().getValueForDisplay(posX).truncatedTo(DAYS);
                                    if (aboveAverages.contains(date)){
                                        mark.setScaleX(0.5);
                                        mark.setScaleY(0.50);
                                        ImageView flag = new ImageView("flag.png");
                                        flag.setScaleX(.5);
                                        flag.setScaleY(.5);
                                        flag.setTranslateY(-20);
                                        flag.setTranslateX(11);
//                                        ((StackPane) mark).getChildren().add(flag);
                                        mark.setEffect(new InnerShadow(2, Color.BLACK));
                                    }

                                    mark.setOnMouseEntered(new EventHandler<MouseEvent>() {
                                        @Override
                                        public void handle(MouseEvent mouseEvent) {
                                            detail.setVisible(true);
                                            detail.setTranslateX(posX + 40);
                                            detail.setTranslateY(posY + 10);
                                            try {
                                                detail.setText(SolrService.getCurrency(listOfSeries.getSelectionModel().getSelectedItem().toString()) + " " + number.toString().substring(0, 5));

                                            } catch (SolrServerException e) {
                                                e.printStackTrace();
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            } catch (NullPointerException e){
                                                try {
                                                    detail.setText(SolrService.getCurrency(listOfSeries.getItems().get(0).toString()) + " " + number.toString().substring(0, 5));
                                                } catch (SolrServerException e1) {
                                                    e1.printStackTrace();
                                                } catch (IOException e1) {
                                                    e1.printStackTrace();
                                                }
                                            }
                                        }
                                    });
//
                                    mark.setOnMouseClicked((mouseEvent) -> {
                                        marks.add((StackPane) mark);
                                        for (StackPane stackPane : marks) {
                                            stackPane.scaleXProperty().bind(selectedMarkSize);
                                            stackPane.scaleYProperty().bind(selectedMarkSize);
                                        }
                                        selectedMarkSize.setValue(2);
                                        String markString = mark.toString();
                                        selectedId = orderOfSeriesPointsToId.get(orderOfGraphPointsToId.get(markString)).toString();
                                        Item item = null;
                                        try {
                                            item = SolrService.getResultsFromId(selectedId);
                                        } catch (SolrServerException e) {
                                            e.printStackTrace();
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
//                                        itemResults.clear();
                                        itemResults.add(item);
                                        observableListItems = FXCollections.observableArrayList(itemResults);
                                        tableOfProperties.setItems(observableListItems);


                                        propertiesPane.setVisible(true);
//                                        propertiesPane.setTranslateX(posX + 50);
//                                        propertiesPane.setTranslateY(posY - 65);
                                    });

                                    mark.setOnMouseExited(new EventHandler<MouseEvent>() {
                                        @Override
                                        public void handle(MouseEvent mouseEvent) {
                                            detail.setVisible(false);
                                            detail.setTranslateX(posX + 40);
                                            detail.setTranslateY(posY + 10);
                                        }
                                    });
                            }
                        }
                    }
                };
        observableListItems = FXCollections.observableArrayList(itemResults);
        propertiesPane.setVisible(false);
        propertiesPane.getStyleClass().add("b2");
        setupCharts();
        addListeners();
        ((NumberAxis) lineChart.getYAxis()).setForceZeroInRange(false);
//        listOfMarks.getItems().setAll(observableAboveAverages);
        chartSpecialMarksCount.setText("Total peaks in graph: " + markCount);
        lineChartPane.getChildren().addAll(lineChart, lineIndicator);
        miniMapPane.getChildren().addAll(lineChartOverview, leftRect, rightRect, hookRight, hookLeft, miniMapDetail);
        chartBox.getChildren().addAll(lineChartPane, separator, miniMapPane);
        containingPane.getChildren().addAll(chartBox, zoomBounds, trackX, displayAtPosition, displayAtTarget, detail, propertiesPane);
        splitPane.getItems().addAll(containingPane, simplePane);
        splitPane.setMinWidth(scene.getWidth());
        root.getChildren().addAll(splitPane);

        stage.setTitle("Calculating Important Points");
		stage.setScene(scene);
        scene.getStylesheets().add("demo.css");

		stage.show();
	}

//    private void refreshGraphFromSolr() {
//        XYChart.Data<LocalDateTime, Float> stringFloatData = (XYChart.Data<LocalDateTime, Float>) seriesHighRaw.getData().get(seriesHighRaw.getData().size() - 1);
//        XYChart.Data<LocalDateTime, Float> stringFloatDatafirst = (XYChart.Data<LocalDateTime, Float>) seriesHighRaw.getData().get(0);
//        String endDate = (String.valueOf(stringFloatData.getXValue())).concat(":00Z");
//        String startDate = (String.valueOf(stringFloatDatafirst.getXValue())).concat(":00Z");
//        if (listOfMarks.getSelectionModel().getSelectedItems().size() < 1){
//            try {
//                results = SolrService.getResults(startDate, endDate, listOfMarks.getItems().get(0).toString(), listOfSeries.getItems().get(0).toString());
//                System.out.println("Getting results from: " + endDate.substring(0, 10) + " to " + startDate.substring(0, 10));
//            } catch (SolrServerException e) {
//                e.printStackTrace();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//        }else {
//            try {
//                results = SolrService.getResults(startDate, endDate, listOfMarks.getSelectionModel().getSelectedItem(), listOfSeries.getSelectionModel().getSelectedItem());
//                System.out.println("Getting results from: " + endDate.substring(0, 10) + " to " + startDate.substring(0, 10));
//            } catch (SolrServerException e) {
//                e.printStackTrace();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//        }
//
//        int size = results.size() - 1;
//        seriesHighRaw.getData().clear();
//        seriesTotal.getData().clear();
//        markCount = 0;
//        observableAboveAverages.clear();
//
//        for (int c = 0; c <= size; c++) {
//
//            Item item = results.get(c);
////            if (item.getId().equals("spike")){
////                aboveAverages.add(LocalDateTime.parse(toUtcDate(item.getCreation_date()).substring(0, 19)));
////                observableAboveAverages.add(LocalDateTime.parse(toUtcDate(item.getCreation_date()).substring(0, 19)));
////                markCount++;
////            }
//            try{
//
//                seriesHighRaw.getData().add(new XYChart.Data(LocalDateTime.parse(toUtcDate(item.getPrice_date())) , Float.valueOf(item.getPrice())));
//            }catch (Exception e){
//
//            }
//
//
//            seriesTotal.getData().add(new XYChart.Data(LocalDateTime.parse(toUtcDate(item.getPrice_date()).substring(0, 19)), Float.valueOf(item.getPrice())));
//        }
//
//    }

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
        SimpleDateFormat out = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
        String[] dateFormats = {"yyyy-MM-dd", "MMM dd, yyyy hh:mm:ss Z"};
        for (String dateFormat : dateFormats) {
            try {
                return out.format(new SimpleDateFormat(dateFormat).parse(dateStr));
            } catch (ParseException ignore) { }
        }
        throw new IllegalArgumentException("Invalid date: " + dateStr);
    }

    EventHandler<MouseEvent> mouseHandler = new EventHandler<MouseEvent>() {

        @Override
        public void handle(MouseEvent mouseEvent){
            if (mouseEvent.getEventType() == MouseEvent.MOUSE_PRESSED) {
//                System.out.println(lineChart.getData());
                zoomBounds.setX(mouseEvent.getX());
                zoomBounds.setY(-20);
                rectinitX.set(mouseEvent.getX());
                rectinitY.set(0);
            } else if (mouseEvent.getEventType() == MouseEvent.MOUSE_DRAGGED) {
                trackXTargetPosition.set(mouseEvent.getSceneX());
                rectX.set(mouseEvent.getX());
                rectY.set(lineChart.getHeight() + 20);
                displayAtTarget.setVisible(true);
                lineIndicator.setVisible(false);
            } else if (mouseEvent.getEventType() == MouseEvent.MOUSE_RELEASED) {
                displayAtTarget.setVisible(false);
                displayAtPosition.setVisible(false);
                if (rectX.getValue() - rectinitX.getValue() < 20){
                    rectX.set(0);
                    rectY.set(0);
                    return;

                }
                String valueForDisplayStart = "";
                String valueForDisplayEnd = "";
                try{
                    valueForDisplayStart = String.valueOf(lineChart.getXAxis().getValueForDisplay(rectinitX.getValue() - 55.0));
                    valueForDisplayEnd =  String.valueOf(lineChart.getXAxis().getValueForDisplay(rectX.getValue() - 55.0));

                    String startDate = valueForDisplayStart.substring(0, 19).concat("Z");
                    String endDate = valueForDisplayEnd.substring(0, 19).concat("Z");

                   double left = lineChartOverview.getXAxis().getDisplayPosition(LocalDateTime.parse(valueForDisplayStart));
                   double right = lineChartOverview.getXAxis().getDisplayPosition(LocalDateTime.parse(valueForDisplayEnd));

                leftHookPosition.set(left);
                rightHookPosition.set(right + 60);
                chartLowerBound.setText("Chart lower bound: " + lineChart.getXAxis().getValueForDisplay(rectinitX.getValue() - 55.0));
                chartUpperBound.setText("Chart upper bound: " +  lineChart.getXAxis().getValueForDisplay(rectX.getValue() - 55.0));


                    results = SolrService.getResults(startDate, endDate, listOfMarks.getSelectionModel().getSelectedItem(), listOfSeries.getSelectionModel().getSelectedItem());
                    System.out.println("Getting results from: " + endDate.substring(0, 10) + " to " + startDate.substring(0, 10));
                } catch (SolrServerException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (Exception e){

                }

                int size = results.size()-1;

                XYChart.Data<LocalDateTime, Float> firstDate = (XYChart.Data<LocalDateTime, Float>) seriesHighRaw.getData().get(seriesHighRaw.getData().size() - 1);
                lastLatestValue = valueForDisplayEnd;

                Date filterDate = new Date();
                Date currentDate = new Date();
                try {
                    filterDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS").parse((lastLatestValue));
                    currentDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm").parse((String.valueOf(firstDate.getXValue())));
                } catch (ParseException e) {
                }
                try{
                    while (filterDate.getTime() < currentDate.getTime()){

                        XYChart.Data<LocalDateTime, Float> data = (XYChart.Data<LocalDateTime, Float>) seriesHighRaw.getData().get(seriesHighRaw.getData().size() - 1);
                        dataRemovedFromFront.add(data);
                        seriesHighRaw.getData().remove(data);
                        chartUpperBound.setText("Chart upper bound: " + data.getXValue());
                        firstDate = (XYChart.Data<LocalDateTime, Float>) seriesHighRaw.getData().get(seriesHighRaw.getData().size() - 1);
                        try {
                            currentDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm").parse((String.valueOf(firstDate.getXValue())));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }

                    XYChart.Data<LocalDateTime, Float> lastData = (XYChart.Data<LocalDateTime, Float>) seriesHighRaw.getData().get(0);
                    lastEarliestValue = valueForDisplayStart;
                    Date filterDateFront = new Date();
                    Date currentDateFront = new Date();
                    try {
                        filterDateFront = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS").parse(lastEarliestValue);
                        currentDateFront = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm").parse((String.valueOf(lastData.getXValue())));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    while (filterDateFront.getTime() > currentDateFront.getTime()){

                        XYChart.Data<LocalDateTime, Float> data = (XYChart.Data<LocalDateTime, Float>) seriesHighRaw.getData().get(0);
                        dataRemovedFromBack.add(data);
                        seriesHighRaw.getData().remove(0);
                        chartLowerBound.setText("Chart lower bound: " + data.getXValue());
                        lastData = (XYChart.Data<LocalDateTime, Float>) seriesHighRaw.getData().get(0);
                        try {
                            currentDateFront = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm").parse((String.valueOf(lastData.getXValue())));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }catch (Exception e){}


                seriesHighRaw.getData().clear();
//                seriesTotal.getData().clear();
                resetButton.fire();
                markCount = 0;
                observableAboveAverages.clear();

                for (int c = 0; c <= size; c++) {

                    Item item = results.get(c);
//            if (item.getId().equals("spike")){
//                aboveAverages.add(LocalDateTime.parse(toUtcDate(item.getCreation_date()).substring(0, 19)));
//                observableAboveAverages.add(LocalDateTime.parse(toUtcDate(item.getCreation_date()).substring(0, 19)));
//                markCount++;
//            }
                    System.out.println(item.getPrice_date());
                    if (c > 0){

                        try{

                            XYChart.Data datalast = (XYChart.Data) seriesHighRaw.getData().get(seriesHighRaw.getData().size() -1);
                            Date currentDates = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm").parse((String.valueOf(datalast.getXValue())));
                            if (currentDates.getTime() < new SimpleDateFormat("yyyy-MM-dd'T'HH:mm").parse((String.valueOf(LocalDateTime.parse(toUtcDate(item.getPrice_date()))))).getTime()){

                                seriesHighRaw.getData().add(new XYChart.Data(LocalDateTime.parse(toUtcDate(item.getPrice_date())) , Float.valueOf(item.getPrice())));

                            }
                        }catch (Exception e){

                        }
                    } else{
                        seriesHighRaw.getData().add(new XYChart.Data(LocalDateTime.parse(toUtcDate(item.getPrice_date())) , Float.valueOf(item.getPrice())));
                    }


//            seriesTotal.getData().add(new XYChart.Data(LocalDateTime.parse(toUtcDate(item.getPrice_date()).substring(0, 19)), Float.valueOf(item.getPrice())));
                }



                if (seriesHighRaw.getData().size() == 0){
                    XYChart.Data<LocalDateTime, Float> frontData = dataRemovedFromFront.get(dataRemovedFromFront.size() - 1);
                    dataRemovedFromFront.remove(frontData);
                    chartUpperBound.setText("Chart upper bound: " + frontData.getXValue());
                    String dateFront = String.valueOf(frontData.getXValue());
                    Float valueFront = frontData.getYValue();
                    seriesHighRaw.getData().add(new XYChart.Data(frontData.getXValue(), valueFront));

                    XYChart.Data<LocalDateTime, Float> backData = dataRemovedFromBack.get(dataRemovedFromBack.size() - 1);
                    dataRemovedFromBack.remove(backData);
                    chartLowerBound.setText("Chart lower bound: " + backData.getXValue());
                    String dateBack = String.valueOf(backData.getXValue());
                    Float valueBack = backData.getYValue();

                    seriesHighRaw.getData().add(0, new XYChart.Data(backData.getXValue(), valueBack));
                }
                System.out.println("Zoom bounds : " + rectinitX.get()+ " to " + rectX.get());

                rectX.set(0);
                rectY.set(0);
            }else if (mouseEvent.getEventType() == MouseEvent.MOUSE_MOVED){
                trackX.setVisible(true);
                trackXPosition.set(mouseEvent.getSceneX());
                trackYPosition.set(mouseEvent.getSceneY());
            }else if (mouseEvent.getEventType() == MouseEvent.MOUSE_EXITED){
                trackX.setVisible(false);
                displayAtPosition.setVisible(false);
                displayAtTarget.setVisible(false);
            }
//            updateIndicator();

        }
    };

    private void setupCharts(){
        ((DateAxis310) lineChart.getXAxis()).setTickLabelsVisible(true);
        ((DateAxis310) lineChart.getXAxis()).setTickMarkVisible(false);
        lineChart.setLegendVisible(false);
        lineChart.setHorizontalGridLinesVisible(true);
        lineChart.getData().add(seriesHighRaw);
//        lineChart.getData().add(seriesAverageLow);
        lineChart.setMaxWidth(1390);
        lineChart.setMinWidth(1390);
        lineChart.setMaxHeight(550);
        lineChart.setMinHeight(550);
        lineChart.getCreateSymbols();
        lineChart.setVerticalZeroLineVisible(false);
        lineChart.setVerticalGridLinesVisible(false);
        lineChart.setHorizontalZeroLineVisible(false);
        lineChart.setVerticalZeroLineVisible(false);
        lineChart.setHorizontalZeroLineVisible(false);
        lineChart.setAnimated(false);
        lineChart.setTitle("Instrument Price Series");
//        lineChart.setCreateSymbols(true);
        lineChart.setVerticalZeroLineVisible(false);
        lineChart.setCreateSymbols(true);
        lineChartOverview.setCreateSymbols(true);
        lineChartOverview.legendVisibleProperty().setValue(false);
        lineChartOverview.getData().add(seriesTotal);
        lineChartOverview.setVerticalGridLinesVisible(false);
        lineChartOverview.setHorizontalZeroLineVisible(false);
        ((DateAxis310) lineChartOverview.getXAxis()).setTickLabelsVisible(false);
        ((DateAxis310) lineChartOverview.getXAxis()).setTickMarkVisible(false);
        lineChartOverview.setMaxHeight(150);
        lineChartOverview.setMinWidth(1390);
        lineChartOverview.setMaxWidth(1390);
        lineChartOverview.setAnimated(false);


    }

    private void addListeners(){

              hookLeft.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {

                ignoreSeriesChange = true;
                initialLeftHookPosition = mouseEvent.getSceneX();
            }
        });
        hookRight.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                ignoreSeriesChange = true;
                initialRightHookPosition = mouseEvent.getSceneX();
            }
        });

        trackXPosition.addListener(new Listeners(this, lineChart, displayAtPosition));

        trackXTargetPosition.addListener(new XTargetChangeListener());

        hookLeft.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                System.out.println(leftRect.getWidth() + " <<rectRight.getWidth()");
                System.out.println(hookRight.getLayoutX() + " <<hookRight.getLayoutX()");
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
                XYChart.Data<LocalDateTime, Float> lastData = (XYChart.Data<LocalDateTime, Float>) seriesHighRaw.getData().get(0);
                lastEarliestValue = String.valueOf(lineChartOverview.getXAxis().getValueForDisplay(mouseEvent.getSceneX() - 40));

//                updateIndicator();
                cutLeft(deltaLeft, lastData);
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
                    return;
                }
                ignoreSeriesChange = false;
                ////refreshGraphFromSolr();
//                updateIndicator();
            }
        });

        hookRight.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                System.out.println(rightRect.getLayoutX() + " rightRect layout");
                System.out.println(leftRect.getWidth() + " leftRect width");
                if (rightRect.getLayoutX() - 50 < leftRect.getWidth() || mouseEvent.getSceneX() - 55 < leftRect.getWidth()) {
                    rightHookPosition.set(leftRect.getWidth() + 60);
                    return;
                }
                if (mouseEvent.getSceneX() > 1370) {
                    rightHookPosition.setValue(1385);
                    return;
                }
                System.out.println(mouseEvent.getSceneX());
                double deltaRight = initialRightHookPosition - mouseEvent.getSceneX();
                rightHookPosition.set(hookRight.getLayoutX() - deltaRight/* + 75*/);

                XYChart.Data<LocalDateTime, Float> firstDate = (XYChart.Data<LocalDateTime, Float>) seriesHighRaw.getData().get(seriesHighRaw.getData().size() - 1);
                lastLatestValue = String.valueOf(lineChartOverview.getXAxis().getValueForDisplay(mouseEvent.getSceneX() - 45));
//                updateIndicator();
                cutRight(deltaRight, firstDate);

                initialRightHookPosition = mouseEvent.getSceneX();

            }
        });

        hookRight.setOnMouseReleased(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                String endDate = "*";
                String startDate = "*";
                if (rightRect.getLayoutX() - 50 < leftRect.getWidth() || mouseEvent.getSceneX() - 55 < leftRect.getWidth()) {
                    rightHookPosition.set(leftRect.getWidth() + 60);
                    return;
                }
                if (mouseEvent.getSceneX() > 1370) {
                    rightHookPosition.setValue(1385);
                    return;
                }
                ignoreSeriesChange = false;
                ////refreshGraphFromSolr();
//                updateIndicator();
            }
        });

        listOfMarks.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String s, String s2) {
                try {
                    seriesResults = SolrService.getSeries(s2);

                    ObservableList<String> observableList = FXCollections.observableList(seriesResults);
                    listOfSeries.setItems(observableList);
                    listOfSeries.getSelectionModel().clearSelection();
                    listOfSeries.getSelectionModel().selectFirst();
                } catch (SolrServerException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        listOfSeries.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        resetButton.setCursor(Cursor.HAND);
        resetTableButton.setCursor(Cursor.HAND);
        listOfSeries.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableDateValue, String previousSelection, String newSelection) {

                leftHookPosition.setValue(0);
                rightHookPosition.setValue(1380);
                resetButton.fire();
                orderOfSeriesPointsToId.clear();
                propertiesPane.setVisible(false);
                if (newSelection==null){
                    seriesAverageLow.getData().clear();
                    seriesHighRaw.getData().clear();
                    return;
                }

                if (listOfSeries.getSelectionModel().getSelectedItems().size() > 1){
                    seriesAverageLow.getData().clear();
                    try {
                        results = SolrService.getResultsOnInstrumentAndSeries(listOfMarks.getSelectionModel().getSelectedItem(), listOfSeries.getSelectionModel().getSelectedItem());
                        for (int c = 0; c <= results.size() - 1; c++) {
                            Item item = results.get(c);
                            System.out.println(toUtcDate(item.getPrice_date()) + " at the cost " + item.getPrice());
                            try{
                                seriesAverageLow.getData().add(new XYChart.Data(LocalDateTime.parse(toUtcDate(item.getPrice_date())), Float.valueOf(item.getPrice())));

                            }catch (Exception e){
                                seriesAverageLow.getData().add(new XYChart.Data(LocalDateTime.parse(toUtcDate(item.getPrice_date())) , Float.valueOf(item.getPrice())));
                            }
                        }
                    } catch (SolrServerException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }else{
                    try {
                        seriesHighRaw.getData().clear();
                        seriesTotal.getData().clear();
                        results = SolrService.getResultsOnInstrumentAndSeries(listOfMarks.getSelectionModel().getSelectedItem(), listOfSeries.getSelectionModel().getSelectedItem());
                        for (int c = 0; c <= results.size() - 1; c++) {
                            Item item = results.get(c);
                            System.out.println(toUtcDate(item.getPrice_date()) + " at the cost " + item.getPrice());
                            try{
                                seriesHighRaw.getData().add(new XYChart.Data(LocalDateTime.parse(toUtcDate(item.getPrice_date())) , Float.valueOf(item.getPrice())));
                                seriesTotal.getData().add(new XYChart.Data(LocalDateTime.parse(toUtcDate(item.getPrice_date())) , Float.valueOf(item.getPrice())));
                                orderOfSeriesPointsToId.put(c, item.getId());

                            }catch (Exception e){
                                seriesHighRaw.getData().add(new XYChart.Data(LocalDateTime.parse(toUtcDate(item.getPrice_date())) , Float.valueOf(item.getPrice())));
                                orderOfSeriesPointsToId.put(c, item.getId());

                                seriesTotal.getData().add(new XYChart.Data(LocalDateTime.parse(toUtcDate(item.getPrice_date())) , Float.valueOf(item.getPrice())));
                            }
                        }
                    } catch (SolrServerException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }); 
//        listOfMarks.getItems().addListener(new ListChangeListener<LocalDateTime>() {
//            @Override
//            public void onChanged(Change<? extends LocalDateTime> change) {
//                while (change.next()){
//                    if (listOfMarks.getItems().size() == 0){
//                        return;
//                    }
//                }
//            }
//        });

        lineChart.setOnMouseClicked(mouseHandler);
        lineChart.setOnMouseDragged(mouseHandler);
        lineChart.setOnMouseMoved(mouseHandler);
        lineChart.setOnMousePressed(mouseHandler);
        lineChart.setOnMouseReleased(mouseHandler);
        lineChart.setOnMouseEntered(mouseHandler);
        lineChart.setOnMouseExited(mouseHandler);
        lineChartOverview.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                positionXOverviewChart = mouseEvent.getSceneX();
            }
        });

        lineChartOverview.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                ignoreSeriesChange = true;
                double deltaX = positionXOverviewChart - mouseEvent.getSceneX();
                positionXOverviewChart = mouseEvent.getSceneX();
                if (positionXOverviewChart < 45 || positionXOverviewChart > 1390)return;
                if (rightHookPosition.getValue() - deltaX < 1390 && leftHookPosition.getValue() - deltaX >= 0){

                    leftHookPosition.set(leftHookPosition.getValue() - deltaX);
                    rightHookPosition.set(rightHookPosition.getValue() - deltaX);
                }
                lastLatestValue = String.valueOf(lineChartOverview.getXAxis().getValueForDisplay(rightHookPosition.getValue() - 45.0));
                lastEarliestValue = String.valueOf(lineChartOverview.getXAxis().getValueForDisplay(leftHookPosition.getValue()));
                try{

                    cutRight(deltaX, (XYChart.Data<LocalDateTime, Float>) seriesHighRaw.getData().get(seriesHighRaw.getData().size() - 1));
                    cutLeft(deltaX, (XYChart.Data<LocalDateTime, Float>) seriesHighRaw.getData().get(0));
                }catch (Exception e){}
                initialRightHookPosition = rightHookPosition.getValue();
                initialLeftHookPosition = leftHookPosition.getValue();
//                updateIndicator();
            }
        });

        lineChartOverview.setOnMouseReleased(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent MouseEvent) {
                ignoreSeriesChange = false;
                ////refreshGraphFromSolr();
//                updateIndicator();
            }
        });
    }

    private void cutLeft(double deltaLeft, XYChart.Data<LocalDateTime, Float> lastData) {
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

                    XYChart.Data<LocalDateTime, Float> data = (XYChart.Data<LocalDateTime, Float>) seriesHighRaw.getData().get(0);
                    dataRemovedFromBack.add(data);
                    seriesHighRaw.getData().remove(0);
                    chartLowerBound.setText("Chart lower bound: " + data.getXValue());
                    lastData = (XYChart.Data<LocalDateTime, Float>) seriesHighRaw.getData().get(0);
                    try {
                        currentDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm").parse((String.valueOf(lastData.getXValue())));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            } else if (deltaLeft > 0) {
                while (filterDate.getTime() < currentDate.getTime()) {

                    XYChart.Data<LocalDateTime, Float> backData = dataRemovedFromBack.get(dataRemovedFromBack.size() - 1);
                    dataRemovedFromBack.remove(backData);
                    chartLowerBound.setText("Chart lower bound: " + backData.getXValue());
                    String dateBack = String.valueOf(backData.getXValue());
                    Float valueBack = backData.getYValue();

                    seriesHighRaw.getData().add(0, new XYChart.Data(backData.getXValue(), valueBack));
                    lastData = (XYChart.Data<LocalDateTime, Float>) seriesHighRaw.getData().get(0);
                    try {
                        currentDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm").parse((String.valueOf(lastData.getXValue())));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    chartItemSize.setText("Chart item size: " + seriesHighRaw.getData().size());
                }
            }

        }catch (Exception e){

        }
    }

    private void cutRight(double deltaRight, XYChart.Data<LocalDateTime, Float> firstDate) {
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

                    XYChart.Data<LocalDateTime, Float> data = (XYChart.Data<LocalDateTime, Float>) seriesHighRaw.getData().get(seriesHighRaw.getData().size() - 1);
                    dataRemovedFromFront.add(data);
                    seriesHighRaw.getData().remove(data);
                    chartUpperBound.setText("Chart upper bound: " + data.getXValue());
                    firstDate = (XYChart.Data<LocalDateTime, Float>) seriesHighRaw.getData().get(seriesHighRaw.getData().size() - 1);
                    try {
                        currentDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm").parse((String.valueOf(firstDate.getXValue())));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            } else if (deltaRight < 0) {
                while (filterDate.getTime() > currentDate.getTime()) {

                    XYChart.Data<LocalDateTime, Float> frontData = dataRemovedFromFront.get(dataRemovedFromFront.size() - 1);
                    dataRemovedFromFront.remove(frontData);
                    chartUpperBound.setText("Chart upper bound: " + frontData.getXValue());
                    String dateFront = String.valueOf(frontData.getXValue());
                    Float valueFront = frontData.getYValue();
                    seriesHighRaw.getData().add(new XYChart.Data(frontData.getXValue(), valueFront));
                    try {
                        currentDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm").parse((String.valueOf(frontData.getXValue())));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    chartItemSize.setText("Chart item size: " + seriesHighRaw.getData().size());
                }
            }

        }catch (Exception e){

        }
    }

//    private void updateIndicator() {
//        try{
//
//            double newPosition = lineChart.getXAxis().getDisplayPosition(LocalDateTime.parse(selectedDate)) + 42.0;
//            if (newPosition < 1390 && newPosition > 45){
//                lineIndicator.setVisible(true);
//                lineIndicator.setLayoutX(newPosition);
//            }else{
//                lineIndicator.setVisible(false);
//            }
//
//        }catch (Exception e){
//        }
//    }

    private void initComponents(){
        aboveAverages = new ArrayList<LocalDateTime>();
        observableAboveAverages = FXCollections.observableArrayList(aboveAverages);
        valuesHighRaw = new ArrayList<>();
        tickValues = new ArrayList<>();
        dataRemovedFromFront = new ArrayList<XYChart.Data<LocalDateTime, Float>>();
        dataRemovedFromBack = new ArrayList<XYChart.Data<LocalDateTime, Float>>();
        changeOfvalues = new ArrayList<>();
        listOfMarks = new ListView<String>();

        lineChartPane = new Pane();
        lineChartPane.setFocusTraversable(true);

        yLineAxis = new NumberAxis();
        xCLineAxis = new DateAxis310();
        xCLineAxis.setTickLabelFormatter(STRING_CONVERTER);
        yAxis = new NumberAxis();
        xAxis = new DateAxis310();
        splitPane = new SplitPane();
        simplePane = new Pane();

        chartBox = new VBox();
        miniMapPane = new Pane();

        listOfMarks.setTranslateY(25);

        separator = new Separator(Orientation.HORIZONTAL);
        leftRect.setTranslateX(45);
        leftRect.setFill(Color.web("blue", 0.1));
        leftRect.setStroke(Color.DODGERBLUE);
        leftRect.widthProperty().bind(leftHookPosition);

        hookLeft.setFill(Color.web("gray", 0.6));
        hookLeft.setTranslateX(37.5);
        hookLeft.setTranslateY(55);
        hookLeft.setStroke(Color.DARKGRAY);
        hookLeft.layoutXProperty().bind(leftHookPosition);
        width = new SimpleDoubleProperty(1450);
        rightRect.setWidth(0);
        rightRect.setFill(Color.web("blue", 0.1));
        rightRect.setStroke(Color.DODGERBLUE);
        hookRight.setFill(Color.web("gray", 0.6));
        hookRight.setTranslateX(-7.5);
        hookRight.setLayoutX(1310);
        hookRight.setTranslateY(55);
        hookRight.setStroke(Color.DARKGRAY);
        rightRect.layoutXProperty().bind(rightHookPosition);
        rightHookPosition.set(1385);
        hookRight.layoutXProperty().bind(rightHookPosition);
        rightRect.widthProperty().bind(width.subtract(rightRect.layoutXProperty()));

        displayAtPosition.layoutYProperty().bind(trackYPosition);
        displayAtPosition.setMouseTransparent(true);
        displayAtPosition.setTranslateX(10);
        displayAtPosition.setTranslateY(-10);
        displayAtPosition.setEffect(new InnerShadow(2, Color.ORANGE));
        displayAtPosition.setFont(Font.font(null, FontWeight.BOLD, 10));

        displayAtTarget.layoutYProperty().bind(trackYPosition);
        displayAtTarget.setMouseTransparent(true);
        displayAtTarget.setTranslateX(10);
        displayAtTarget.setTranslateY(-10);
        displayAtTarget.setEffect(new InnerShadow(2, Color.ORANGE));
        displayAtTarget.setFont(Font.font(null, FontWeight.BOLD, 10));

        trackX.setMouseTransparent(true);
        trackX.layoutXProperty().bind(trackXPosition);
        trackX.setStroke(Color.DODGERBLUE);
        displayAtPosition.layoutXProperty().bind(trackXPosition);
        displayAtTarget.layoutXProperty().bind(trackXTargetPosition);

        zoomFactor = new Delta(1.0, 1.0);
        zoomBounds = new Rectangle();
        zoomBounds.setFill(Color.web("blue", 0.1));
        zoomBounds.setStroke(Color.DODGERBLUE);
        zoomBounds.setStrokeDashOffset(50);
        zoomBounds.setMouseTransparent(true);
        zoomBounds.widthProperty().bind(rectX.subtract(rectinitX));
        zoomBounds.heightProperty().bind(rectY.subtract(rectinitY));

        spikes = new Label("List of Instruments");
        spikes.setFont(new Font("Calibri", 22));
        spikes.setTranslateX(50);
        TextField textField = new TextField();
        textField.textProperty().addListener(new ChangeListener() {
            public void changed(ObservableValue observable, Object oldVal,
                                Object newVal) {
                try {
                    search((String) oldVal, (String) newVal);
                } catch (SolrServerException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        listBox = new VBox(spikes, resetButton, resetTableButton, textField, listOfMarks, listOfSeries);
        listBox.setPadding(new Insets(10,10,10,10));
        listBox.setSpacing(15);
        simplePane.getChildren().addAll(listBox);
        splitPane.setDividerPosition(0, 0.825);
        listOfSeries.setTranslateY(25);

        seriesTotal = new XYChart.Series();
        seriesAverageHigh = new XYChart.Series();
        seriesAverageLow = new XYChart.Series();
        seriesOpenRaw = new XYChart.Series();

        seriesHighRaw.setName("Raw data");
        seriesAverageHigh.setName("Top average");
        seriesAverageLow.setName("Bottom average");
        seriesOpenRaw.setName("Open");

        miniMapDetail = new Text();
        miniMapDetail.setFill(Color.WHITE);
        miniMapDetail.setEffect(new InnerShadow(2, Color.ORANGE));
        miniMapDetail.setFont(Font.font(null, FontWeight.BOLD, 12));
        miniMapDetail.setVisible(false);

        detail = new Text();
        detail.setCache(true);
        detail.setFill(Color.BLACK);
        detail.setEffect(new InnerShadow(2, Color.ORANGE));
        detail.setFont(Font.font(null, FontWeight.BOLD, 13));
        detail.setVisible(false);

        containingPane = new Pane();
        yLineAxis.setOnMousePressed((mouseEvent) -> {
            yLineAxis.setAutoRanging(false);
            lowerBoundForYMain.set(yLineAxis.getLowerBound());
            upperBoundForYMain.set(yLineAxis.getUpperBound());
            yLineAxis.lowerBoundProperty().bind(lowerBoundForYMain);
            yLineAxis.upperBoundProperty().bind(upperBoundForYMain);
            initYpos = mouseEvent.getSceneY();
            if (mouseEvent.getSceneY() > 379 ){
                dragArea="bottom";
            }else if (mouseEvent.getSceneY() > 213){
                dragArea="middle";
            }else{
                dragArea="top";
            }
        });


        yLineAxis.setOnMouseDragged((mouseEvent) -> {
            yLineAxis.setAutoRanging(false);
            double lowerBound = yLineAxis.getLowerBound();
            double upperBound = yLineAxis.getUpperBound();

            double range = upperBound - lowerBound;
            double dragStrength = range / 425;
            switch (dragArea){
                case "top"    :
                    upperBoundForYMain.set(upperBound - dragStrength*(initYpos - mouseEvent.getSceneY()));
                    break;
                case "middle" :
                    upperBoundForYMain.set(upperBound - dragStrength*(initYpos - mouseEvent.getSceneY()));
                    lowerBoundForYMain.set(lowerBound - dragStrength*(initYpos - mouseEvent.getSceneY()));
                    break;
                case "bottom" :
                    lowerBoundForYMain.set(lowerBound - dragStrength*(initYpos - mouseEvent.getSceneY()));
                    break;
            }
            initYpos = mouseEvent.getSceneY();
            mouseEvent.consume();
        });

        resetButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                yLineAxis.lowerBoundProperty().unbind();
                yLineAxis.upperBoundProperty().unbind();
                yLineAxis.setAutoRanging(true);
            }
        });
        resetTableButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                itemResults.clear();
                observableListItems = FXCollections.observableArrayList(itemResults);
                tableOfProperties.setItems(observableListItems);
                selectedMarkSize.setValue(1);
                propertiesPane.setVisible(false);
                for (StackPane mark : marks) {
                    mark.scaleXProperty().unbind();
                    mark.scaleYProperty().unbind();
                }
                marks.clear();
            }
        });
        lineIndicator.setStroke(Color.DODGERBLUE);
        yAxis.setForceZeroInRange(false);
        separator.prefWidthProperty().bind(miniMapPane.widthProperty());
        listOfMarks.setMaxWidth(250);
        listOfSeries.setMaxWidth(250);
        listOfMarks.setMaxHeight(300);
        listOfSeries.setMaxHeight(300);
//        listOfMarks.prefHeightProperty().bind(simplePane.heightProperty().divide(2));
//        listOfSeries.prefHeightProperty().bind(simplePane.heightProperty().divide(2));
//        listOfSeries.setTranslateY(450);
        placeHolder = new Text();
        placeHolder.setFill(Color.WHITE);
        placeHolder.setEffect(new InnerShadow(2, Color.ORANGE));
        placeHolder.setFont(Font.font(null, FontWeight.BOLD, 13));
        placeHolder.setTranslateY(-100);
        placeHolder.setText("No Spikes");
        listOfMarks.setPlaceholder(placeHolder);
//        listOfSeries = new ListView<>();
    }

    private class XTargetChangeListener implements ChangeListener<Number> {
        @Override
        public void changed(ObservableValue<? extends Number> observableValue, Number number, Number number2) {
            String displayValueAtLocation = (String.valueOf(lineChart.getXAxis().getValueForDisplay((Double) number2)));

            displayAtTarget.setText(displayValueAtLocation.substring(0, 10));
        }
    }

    public void search(String oldVal, String newVal) throws SolrServerException, IOException {

            String value = newVal;

            List<String> instrumentsFromName = SolrService.getInstrumentsFromName(value);
            observableListInstruments = FXCollections.observableArrayList(instrumentsFromName);
            listOfMarks.setItems(observableListInstruments);


    }
}
