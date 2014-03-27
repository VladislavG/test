package solrTest;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import org.apache.solr.client.solrj.SolrServerException;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by vladislav on 27.03.14.
 */
public class SeriesActions {

    public static final String YYYY_MM_DD_T_HH_MM = "yyyy-MM-dd'T'HH:mm";

    public static void cutLeft(double deltaLeft,
                               XYChart.Data<LocalDateTime, Float> lastData,
                               XYChart.Series series, List dataPointRemovedFromBack,
                               String lastEarliestValue) {

        Date filterDate = new Date();
        Date currentDate = new Date();
        try {
            filterDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS").parse(lastEarliestValue);
            currentDate = new SimpleDateFormat(YYYY_MM_DD_T_HH_MM).parse((String.valueOf(lastData.getXValue())));
        } catch (ParseException e) {
            try {
                filterDate = new SimpleDateFormat(YYYY_MM_DD_T_HH_MM).parse(lastEarliestValue);
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
                            currentDate = new SimpleDateFormat(YYYY_MM_DD_T_HH_MM).parse((String.valueOf(lastData.getXValue())));
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
                        currentDate = new SimpleDateFormat(YYYY_MM_DD_T_HH_MM).parse((String.valueOf(lastData.getXValue())));
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
            currentDate = new SimpleDateFormat(YYYY_MM_DD_T_HH_MM).parse((String.valueOf(firstDate.getXValue())));
        } catch (ParseException e) {
            try {
                filterDate = new SimpleDateFormat(YYYY_MM_DD_T_HH_MM).parse((lastLatestValue));
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
                            currentDate = new SimpleDateFormat(YYYY_MM_DD_T_HH_MM).parse((String.valueOf(firstDate.getXValue())));
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
                        currentDate = new SimpleDateFormat(YYYY_MM_DD_T_HH_MM).parse((String.valueOf(frontData.getXValue())));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }

        }catch (Exception e){
        }
    }

    public static void completeSeriesFromRemovedPoints(ArrayList pointsRemovedFromBack, ArrayList pointsRemovedFromFront, XYChart.Series series) {
        int backPointsListSize = pointsRemovedFromBack.size() - 1;
        for (int f = backPointsListSize; f >= 0; f--){
            Object backPoint = pointsRemovedFromBack.get(f);
            series.getData().add(0, backPoint);
            pointsRemovedFromBack.remove(backPoint);
        }
        int frontPointsListSize = pointsRemovedFromFront.size() - 1;
        for (int f = frontPointsListSize; f >= 0; f--){
            Object frontPoint = pointsRemovedFromFront.get(f);
            series.getData().add(frontPoint);
            pointsRemovedFromFront.remove(frontPoint);
        }
    }

    public static void updateSeriesListAndSetButtonText(String s2, List<String> results, ListView seriesList, Button openInstrumentList, XYChart.Series series) {
        if (s2==null)return;
        try {
            results = SolrService.getSeries(s2);

            ObservableList<String> observableList = FXCollections.observableList(results);
            seriesList.setItems(observableList);
            seriesList.getSelectionModel().selectFirst();
        } catch (SolrServerException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            String shortName = SolrService.getShortName(s2);
            openInstrumentList.setText("Instrument: " + shortName);
            series.setName(shortName);
        } catch (SolrServerException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
