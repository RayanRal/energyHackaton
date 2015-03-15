package com.gmail.ui;

import com.gmail.arduino.InputEvent;
import com.gmail.arduino.SerialTest;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventType;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;


public class Main extends Application {

    private static ImageView batteryImage = getBatteryImage();
    private static Text currentCounterTextField = getCurrentCounterText();
    private static Text plannedCostsText = getPlannedCostsText();
    private static Text leftOnAccountUahText = getLeftOnAccountUahText();

    @Override
    public void start(Stage primaryStage) throws Exception {
        Group root = new Group();
        TabPane tabPane = new TabPane();
        BorderPane borderPane = new BorderPane();
        Scene scene = new Scene(root, 500, 300);
        borderPane.setCenter(tabPane);
        borderPane.prefHeightProperty().bind(scene.heightProperty());
        borderPane.prefWidthProperty().bind(scene.widthProperty());

        Tab mainTab = new Tab("Main");
        Tab secondaryTableTab = createCheckboxTab();
        Tab thirdPictureTab = createThirdPictureTab();

        tabPane.getTabs().addAll(mainTab, secondaryTableTab, thirdPictureTab);
        root.getChildren().add(borderPane);

        primaryStage.setTitle("MoneyMonitor");
        primaryStage.setScene(scene);

        HBox mainHorizontalBox = new HBox();
        mainHorizontalBox.setSpacing(30);

        VBox textVerticalBox = new VBox();
        textVerticalBox.getChildren().addAll(currentCounterTextField, plannedCostsText, leftOnAccountUahText);

        mainHorizontalBox.getChildren().addAll(batteryImage, textVerticalBox);
        mainTab.setContent(mainHorizontalBox);

        scene.setRoot(root);
        primaryStage.setScene(scene);

        primaryStage.show();
    }

    private Tab createThirdPictureTab() {
        Tab imageTab = new Tab("Table");

        ImageView imageView1 = new ImageView(new Image(Main.class.getResourceAsStream("/0.png")));
        imageView1.setFitWidth(150);
        imageView1.setPreserveRatio(true);
        imageView1.setSmooth(true);

        HBox imageBox = new HBox(5);

        imageBox.setAlignment(Pos.CENTER);
        imageBox.getChildren().add(imageView1);
        imageTab.setContent(imageBox);

        return imageTab;
    }

    private Tab createCheckboxTab() {
        Tab secondaryTableTab = new Tab("Checkbox");
        HBox horizontalCheckboxBox = new HBox(5);
        ImageView imageView1 = new ImageView(new Image(Main.class.getResourceAsStream("/25.png")));
        imageView1.setFitWidth(150);
        imageView1.setPreserveRatio(true);
        imageView1.setSmooth(true);
        imageView1.setRotate(270);

        ImageView imageView2 = new ImageView(new Image(Main.class.getResourceAsStream("/75.png")));
        imageView2.setFitWidth(150);
        imageView2.setPreserveRatio(true);
        imageView2.setSmooth(true);
        imageView2.setRotate(270);

        horizontalCheckboxBox.getChildren().addAll(imageView1, imageView2);
        horizontalCheckboxBox.setAlignment(Pos.CENTER);

        horizontalCheckboxBox.getChildren().addAll(new CheckBox("Lower radiator\n temperature by 1°\n (6% economy)"));
        secondaryTableTab.setContent(horizontalCheckboxBox);
        return secondaryTableTab;
    }

    private static Text getCurrentCounterText() {
        Text textField = new Text();
        textField.setText("\n\nCurrent counter: ");
        textField.addEventHandler(EventType.ROOT, event -> Platform.runLater(() -> {
            if (event instanceof InputEvent) {
                textField.setText("\n\nCurrent counter: " + ((InputEvent) event).getValue().toString() + " KWt");
                event.consume();
            }
        }));
        return textField;
    }

    private static Text getPlannedCostsText() {
        Text textField = new Text();
        textField.setText("\n\nPlanned costs: ");
        textField.addEventHandler(EventType.ROOT, event -> Platform.runLater(() -> {
            if (event instanceof InputEvent) {
                textField.setText("\n\nPlanned costs: " + ((InputEvent) event).getValue().toString() + " UAH");
                event.consume();
            }
        }));
        return textField;
    }

    private static Text getLeftOnAccountUahText() {
        Text textField = new Text();
        textField.setText("\n\nLeft on account: 5.0 UAH");
        textField.addEventHandler(EventType.ROOT, event -> Platform.runLater(() -> {
            if (event instanceof InputEvent) {
                textField.setText("\n\nLeft on account: " + ((InputEvent) event).getValue().toString() + " UAH");
                event.consume();
            }
        }));
        return textField;
    }

    private static ImageView getBatteryImage() {
        ImageView iv1 = new ImageView();
        iv1.setImage(new Image(Main.class.getResourceAsStream("/100.png")));
        iv1.setFitWidth(170);
        iv1.setPreserveRatio(true);
        iv1.setSmooth(true);
        iv1.addEventHandler(EventType.ROOT, event -> Platform.runLater(() -> {
            if (event instanceof InputEvent) {
                iv1.setImage(getImage(((InputEvent) event).getValue()));
                event.consume();
            }
        }));
        return iv1;
    }

    private static Image getImage(Float new_val) {
        if (new_val.intValue() <= 0) return new Image(Main.class.getResourceAsStream("/0.png"));
        if (new_val.intValue() <= 25) return new Image(Main.class.getResourceAsStream("/25.png"));
        if (new_val.intValue() > 25 && new_val.intValue() <= 50)
            return new Image(Main.class.getResourceAsStream("/50.png"));
        if (new_val.intValue() > 50 && new_val.intValue() <= 75)
            return new Image(Main.class.getResourceAsStream("/75.png"));
        if (new_val.intValue() > 75) return new Image(Main.class.getResourceAsStream("/100.png"));
        return null;
    }

    public static void main(String[] args) throws InterruptedException {
        SerialTest main = new SerialTest(batteryImage, currentCounterTextField, plannedCostsText, leftOnAccountUahText);
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
            for (int i = 0; i < 4; i++) {
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