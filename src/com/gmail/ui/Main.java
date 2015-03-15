package com.gmail.ui;

import com.gmail.arduino.InputEvent;
import com.gmail.arduino.SerialTest;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.event.EventType;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * Created by rayanral on 14/03/15.
 */
public class Main extends Application {

    static ImageView imageView = getImageView();
    static Text textField = getTextField();

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("Energy Saver");
        Scene scene = new Scene(root, 300, 275);
        primaryStage.setScene(scene);

        VBox vb = new VBox();
        vb.setSpacing(5);
        vb.getChildren().addAll(imageView, textField);

        scene.setRoot(vb);
        primaryStage.setScene(scene);

        primaryStage.show();
    }

    private static Text getTextField() {
        Text textField = new Text();
        textField.setText("Initial");
        textField.addEventHandler(EventType.ROOT, event -> {
            Platform.runLater(() -> {
                if(event instanceof InputEvent) {
                    textField.setText(((InputEvent) event).getValue().toString());
                    event.consume();
                }
            });
        });
        return textField;
    }

    private static ImageView getImageView() {
        ImageView iv1 = new ImageView();
        iv1.setImage(new Image(Main.class.getResourceAsStream("/0.png")));
        iv1.setFitWidth(170);
        iv1.setPreserveRatio(true);
        iv1.setSmooth(true);
        iv1.addEventHandler(EventType.ROOT, event -> {
            Platform.runLater(() -> {
                if(event instanceof InputEvent) {
                    iv1.setImage(getImage(((InputEvent) event).getValue()));
                    event.consume();
                }
            });
        });
        return iv1;
    }

    private static Image getImage(Float new_val) {
        if(new_val.intValue() < 25) return new Image(Main.class.getResourceAsStream("/0.png"));
        if(new_val.intValue() > 25 && new_val.intValue() < 50) return new Image(Main.class.getResourceAsStream("/25.png"));
        if(new_val.intValue() > 50 && new_val.intValue() < 75) return new Image(Main.class.getResourceAsStream("/50.png"));
        if(new_val.intValue() > 75 && new_val.intValue() < 100) return new Image(Main.class.getResourceAsStream("/75.png"));
        if(new_val.intValue() == 100) return new Image(Main.class.getResourceAsStream("/100.png"));
        return null;
    }

    public static void main(String[] args) throws InterruptedException {
        SerialTest main = new SerialTest(imageView, textField);
        main.initialize();
        Thread t = new Thread() {
            public void run() {
                //the following line will keep this app alive for 100 seconds,
                //waiting for events to occur and responding to them (printing incoming messages to console).
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException ie) {
                }
            }
        };
        t.start();

        testDergalka(main);
        main.close();

        launch(args);
    }

    private static void testDergalka(SerialTest main) throws InterruptedException {
        Thread t = new Thread(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            for(int i = 0; i < 10; i ++) {
                main.serialEvent();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        t.start();
    }
}