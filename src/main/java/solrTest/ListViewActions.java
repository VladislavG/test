package solrTest;

import javafx.collections.ObservableList;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;
import org.apache.solr.client.solrj.SolrServerException;

import java.io.IOException;

/**
 * Created by vladislav on 27.03.14.
 */
public class ListViewActions {

    public static void makeListViewDisappear(ListView listView) {
        listView.setMaxHeight(0);
        listView.setOpacity(0);
        listView.setManaged(false);
    }

    public static ListView<String> makeListView(ObservableList observableList) {
        ListView listView = new ListView();
        listView.setCellFactory(new Callback<ListView<String>,
                ListCell<String>>() {
            @Override
            public ListCell<String> call(ListView<String> list) {
                ListCell<String> cell = new ListCell<String>() {

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
                        } else {
                            setText("");
                        }
                    }
                };
                return cell;
            }
        });
        listView.getItems().setAll(observableList);
        return listView;
    }


}
