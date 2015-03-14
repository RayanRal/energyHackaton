package com.gmail.ui;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.Arrays;

/**
 * Created by rayanral on 14/03/15.
 */
public class Main extends Application {

    private static ProgressIndicator pb;

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("Energy Saver");
        Scene scene = new Scene(root, 300, 275);
        primaryStage.setScene(scene);

        pb = new ProgressBar();
        pb.setProgress(0);

        VBox vb = new VBox();
        vb.setSpacing(5);
        final Slider slider = new Slider();
        ImageView iv1 = getImageView();
        vb.getChildren().addAll(pb, slider, iv1);

        scene.setRoot(vb);
        primaryStage.setScene(scene);

        slider.setMin(0);
        slider.setMax(100);

        slider.valueProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue<? extends Number> ov,
                                Number old_val, Number new_val) {
                pb.setProgress(new_val.doubleValue() / 100);
                imageProgress(new_val);
            }

            private void imageProgress(Number new_val) {
                if(new_val.intValue() < 25) iv1.setImage(new Image(getClass().getResourceAsStream("/0.png")));
                if(new_val.intValue() > 25 && new_val.intValue() < 50) iv1.setImage(new Image(getClass().getResourceAsStream("/25.png")));
                if(new_val.intValue() > 50 && new_val.intValue() < 75) iv1.setImage(new Image(getClass().getResourceAsStream("/50.png")));
                if(new_val.intValue() > 75 && new_val.intValue() < 100) iv1.setImage(new Image(getClass().getResourceAsStream("/75.png")));
                if(new_val.intValue() == 100) iv1.setImage(new Image(getClass().getResourceAsStream("/100.png")));
            }
        });

        primaryStage.show();

    }

    private ImageView getImageView() {
        ImageView iv1 = new ImageView();
        iv1.setImage(new Image(getClass().getResourceAsStream("/0.png")));
        iv1.setFitWidth(170);
        iv1.setPreserveRatio(true);
        iv1.setSmooth(true);
        return iv1;
    }

    public static void main(String[] args) throws InterruptedException {
        launch(args);
    }
}