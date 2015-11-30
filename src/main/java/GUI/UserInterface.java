package GUI;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import jfxtras.scene.control.LocalTimeTextField;

import java.util.ArrayList;

public class UserInterface extends Application {
    final TableView table = new TableView();
    TabPane root = new TabPane();

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
         * Button!
         */
        Button submitButton = new Button();
        submitButton.setText("Pobierz");
        submitButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                try {
                    root.getTabs().remove(1);
                }
                catch(IndexOutOfBoundsException a) {}
                table.getColumns().clear();
                root.getTabs().add(createBrowsingTab(3));
            }
        });

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
        fields.add(submitButton);

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

    Tab createBrowsingTab(int numberOfColumns){
        TableColumn firstColumn = new TableColumn("ID");
        table.getColumns().add(firstColumn);

        TableColumn secondColumn;
        for(int i = 0; i < numberOfColumns; i++) {
            secondColumn = new TableColumn("Data");
            table.getColumns().add(secondColumn);
        }

        Button first = new Button("|<");
        Button previous = new Button("<");
        Button next = new Button(">");
        Button last = new Button(">|");

        HBox browsingButtons = new HBox();
        browsingButtons.setAlignment(Pos.CENTER);
        browsingButtons.getChildren().addAll(first,previous, next, last);

        VBox browsingTabContent = new VBox();
        browsingTabContent.getChildren().addAll(table, browsingButtons);

        Tab browsingTab = new Tab();
        browsingTab.setText("Browse");
        browsingTab.setClosable(false);
        browsingTab.setContent(browsingTabContent);
        return browsingTab;
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Log Aggregator");

        Tab configurationTab = createConfigurationTab();
//        Tab browsingTab = createBrowsingTab(1);

        root.getTabs().add(configurationTab);
//        root.getTabs().add(browsingTab);

        primaryStage.setScene(new Scene(root, 400, 400));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}