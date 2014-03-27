package solrTest;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ListView;
import javafx.util.Duration;

/**
 * Created by vladislav on 27.03.14.
 */
public class Animation {


    public static void playOpeningAnimation(ListView listView, Double openPos, Double opacity) {
        if (openPos > 0){
            listView.setManaged(true);
        }
        final Timeline timeline = new Timeline();
        timeline.setCycleCount(1);
        timeline.setAutoReverse(false);
        final KeyValue kv2 = new KeyValue(listView.maxHeightProperty(), openPos);
        final KeyFrame kf2 = new KeyFrame(Duration.millis(300), kv2);
        timeline.getKeyFrames().add(kf2);
        timeline.play();
        timeline.setOnFinished(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                if (openPos == 0.0){
                    listView.setManaged(false);
                }
            }
        });
        final Timeline timelineOpacity = new Timeline();
        timelineOpacity.setCycleCount(1);
        timelineOpacity.setAutoReverse(false);
        final KeyValue kv = new KeyValue(listView.opacityProperty(), opacity);
        final KeyFrame kf = new KeyFrame(Duration.millis(500), kv);
        timelineOpacity.getKeyFrames().add(kf);
        timelineOpacity.play();
    }

    public static void playOpenCloseAnimationOnMenu(ListView listViewGrow, ListView closingListView, ListView secondClosingListView, ListView thirdClosingListView) {

        playOpeningAnimation(listViewGrow, 300.0, 1.0);
        playOpeningAnimation(closingListView, 0.0, 0.0);
        playOpeningAnimation(secondClosingListView, 0.0, 0.0);
        playOpeningAnimation(thirdClosingListView, 0.0, 0.0);
    }

}
