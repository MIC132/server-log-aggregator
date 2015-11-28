package GUI;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import jfxtras.scene.control.LocalTimeTextField;

import java.util.ArrayList;

public class UserInterface extends Application {
    Tab createConfigurationTab(){
        ArrayList<Node> labels = new ArrayList<>();
        ArrayList<Node> fields = new ArrayList<>();

        /**
         * This part creates all needed labels and fields, as well as sets theis properties
         */
        Label urlLabel = new Label("URL:");
        TextField urlTextField = new TextField();

        Label regexpLabel = new Label("Regexp:");
        TextField regexpTextField = new TextField();

        Label loginLabel = new Label("Login:");
        TextField loginTextField = new TextField();

        Label passwordLabel = new Label("Password:");
        TextField passwordTextField = new TextField();

        Label sourceTypeLabel = new Label("Source:");
        ComboBox<SourceType> sourceTypeComboBox = new ComboBox<>();
        sourceTypeComboBox.getItems().addAll(SourceType.values());

        Label startDateLabel = new Label("Start date:");
        DatePicker startDatePicker = new DatePicker();

        Label startTimeLabel = new Label("Start time:");
        LocalTimeTextField startTimeTextField = new LocalTimeTextField();   //still not working properly

        Label endDateLabel = new Label("End date:");
        DatePicker endDatePicker = new DatePicker();

        Label endTimeLabel = new Label("End time:");
        LocalTimeTextField endTimeTextField = new LocalTimeTextField();     //still not working properly

        /**
         * This part allows us to add created labels to columns, managing their order
         */
        labels.add(urlLabel);
        labels.add(regexpLabel);
        labels.add(sourceTypeLabel);
        labels.add(loginLabel);
        labels.add(passwordLabel);
        labels.add(startDateLabel);
        labels.add(startTimeLabel);
        labels.add(endDateLabel);
        labels.add(endTimeLabel);

        fields.add(urlTextField);
        fields.add(regexpTextField);
        fields.add(sourceTypeComboBox);
        fields.add(loginTextField);
        fields.add(passwordTextField);
        fields.add(startDatePicker);
        fields.add(startTimeTextField);
        fields.add(endDatePicker);
        fields.add(endTimeTextField);

        /**
         * Padding of columns, their order and name of tab
         */
        VBox leftColumn = new VBox();
        leftColumn.getChildren().addAll(labels);
        leftColumn.setPadding(new Insets(12,10,10,10));
        leftColumn.setSpacing(8);

        VBox rightColumn = new VBox();
        rightColumn.getChildren().addAll(fields);
        rightColumn.setPadding(new Insets(10,10,10,10));

        HBox configurationTabContent = new HBox();
        configurationTabContent.getChildren().addAll(leftColumn, rightColumn);

        Tab configurationTab = new Tab();
        configurationTab.setText("Configuration");
        configurationTab.setClosable(false);
        configurationTab.setContent(configurationTabContent);

        return configurationTab;
    }

    Tab createBrowsingTab(){

        TableView table = new TableView();

        HBox browsingButtons = new HBox();

        VBox browsingTabContent = new VBox();

        Tab browsingTab = new Tab();
        browsingTab.setText("Browse");
        browsingTab.setClosable(false);
        browsingTab.setContent(browsingTabContent);
        return browsingTab;
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Log Aggregator");
        TabPane root = new TabPane();

        Tab configurationTab = createConfigurationTab();

        root.getTabs().add(configurationTab);

        primaryStage.setScene(new Scene(root, 400, 400));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}