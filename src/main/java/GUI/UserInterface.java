package GUI;
import downloading.*;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

public class UserInterface extends Application {
    TabPane root = new TabPane();
    API api = API.getInstance();
    ObservableList<ParsedData> dataList = FXCollections.observableArrayList();
    int numberOfColumns = 0;
    int offset = 0;
    int amount = 25;

    Tab createDatabaseTab(){
        ArrayList<Node> labels = new ArrayList<>();
        ArrayList<Node> fields = new ArrayList<>();

        Label urlLabel = new Label("URL:");
        TextField urlTextField = new TextField();
        Label loginLabel = new Label("Login:");
        TextField loginTextField = new TextField();
        Label passwordLabel = new Label("Password:");
        TextField passwordTextField = new TextField();

        Button submitButton = new Button();
        submitButton.setText("Podłącz");
        submitButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                api.connectToDatabase(urlTextField.getText(), loginTextField.getText(), passwordTextField.getText());
            }
        });

        labels.add(urlLabel);
        labels.add(loginLabel);
        labels.add(passwordLabel);

        fields.add(urlTextField);
        fields.add(loginTextField);
        fields.add(passwordTextField);
        fields.add(submitButton);


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

    Tab createConfigurationTab(){
        ArrayList<Node> labels = new ArrayList<>();
        ArrayList<Node> fields = new ArrayList<>();

        /**
         * This part creates all needed labels and fields, as well as sets theis properties
         */
        Label urlLabel = new Label("URL:");
        TextField urlTextField = new TextField();
        Label pathLabel = new Label("Path:");
        TextField pathTextField = new TextField();
        Label filenamePatternLabel = new Label("Filename Pattern:");
        TextField filenamePatternTextField = new TextField();
        Label regexpLabel = new Label("Regexp:");
        TextField regexpTextField = new TextField();
        Label timeStampingTypeLabel = new Label("Time Stamping:");
        ComboBox<ChronoUnit> timeStampingTypeComboBox = new ComboBox<>();
        timeStampingTypeComboBox.getItems().addAll(ChronoUnit.values());
        Label sourceTypeLabel = new Label("Source:");
        ComboBox<SourceType> sourceTypeComboBox = new ComboBox<>();
        sourceTypeComboBox.getItems().addAll(SourceType.values());
        Label stepAmountLabel = new Label("Step Amount:");
        TextField stepAmountTextField = new TextField();
        Label startDateLabel = new Label("Start date:");
        DatePicker startDatePicker = new DatePicker();
        Label loginLabel = new Label("Login:");
        TextField loginTextField = new TextField();
        Label passwordLabel = new Label("Password:");
        TextField passwordTextField = new TextField();


        Button submitButton = new Button();
        submitButton.setText("Podłącz");
        submitButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                Source source = null;
                switch(sourceTypeComboBox.getValue()){
                    case FTP:
                        source = new FtpSource(
                                "",
                                urlTextField.getText(),
                                pathTextField.getText(),
                                filenamePatternTextField.getText(),
                                regexpTextField.getText(),
                                timeStampingTypeComboBox.getValue(),
                                Integer.parseInt(stepAmountTextField.getText()),
                                loginTextField.getText(),
                                passwordTextField.getText()
                        );
                        break;
                    case SSH:
                        source = new SshSource(
                                "",
                                urlTextField.getText(),
                                pathTextField.getText(),
                                filenamePatternTextField.getText(),
                                regexpTextField.getText(),
                                timeStampingTypeComboBox.getValue(),
                                Integer.parseInt(stepAmountTextField.getText()),
                                loginTextField.getText(),
                                passwordTextField.getText()
                        );
                        break;
                    case HTTP:
                        source = new HttpSource(
                                "",
                                urlTextField.getText(),
                                pathTextField.getText(),
                                filenamePatternTextField.getText(),
                                regexpTextField.getText(),
                                timeStampingTypeComboBox.getValue(),
                                Integer.parseInt(stepAmountTextField.getText())
                        );
                        break;

                }
                api.connectToSource(source, startDatePicker.getValue());
            }
        });

        Button downloadButton = new Button();
        downloadButton.setText("Pobierz");
        downloadButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                dataList =  api.download(amount, offset);
                createBrowsingTab();
            }
        });

        /**
         * This part allows us to add created labels to columns, managing their order
         */
        labels.add(urlLabel);
        labels.add(pathLabel);
        labels.add(filenamePatternLabel);
        labels.add(regexpLabel);
        labels.add(sourceTypeLabel);
        labels.add(timeStampingTypeLabel);
        labels.add(stepAmountLabel);
        labels.add(startDateLabel);
        labels.add(loginLabel);
        labels.add(passwordLabel);
        labels.add(submitButton);

        fields.add(urlTextField);
        fields.add(pathTextField);
        fields.add(filenamePatternTextField);
        fields.add(regexpTextField);
        fields.add(sourceTypeComboBox);
        fields.add(timeStampingTypeComboBox);
        fields.add(stepAmountTextField);
        fields.add(startDatePicker);
        fields.add(loginTextField);
        fields.add(passwordTextField);
        fields.add(downloadButton);

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

    void addColumn(TableView tableView){
        int i = numberOfColumns;

        TableColumn<ParsedData, String> newColumn = new TableColumn<>("#" + String.valueOf(++numberOfColumns));
        newColumn.setCellValueFactory(cellData -> cellData.getValue().getCellValue(i));
        newColumn.setCellFactory(TextFieldTableCell.<ParsedData>forTableColumn());

        tableView.getColumns().add(newColumn);
    }

    void createBrowsingTab(){
        numberOfColumns = 0;
        TableView table = new TableView();
        while(root.getTabs().size() > 2) root.getTabs().remove(2);

        for(int i = 0; i < dataList.get(0).numberOfColumns; i++)
            addColumn(table);

        table.setItems(dataList);
        table.setPrefHeight(4096.00);
        Button first = new Button("|<");
        first.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                dataList.removeAll(dataList);
                offset = 0;
                dataList.addAll(api.download(amount, offset));
            }
        });
        Button previous = new Button("<");
        previous.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                dataList.removeAll(dataList);
                offset -= amount;
                dataList.addAll(api.download(amount, offset));
            }
        });
        Button next = new Button(">");
        next.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                dataList.removeAll(dataList);
                offset += amount;
                dataList.addAll(api.download(amount, offset));
            }
        });
        Button last = new Button(">|");
        last.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                dataList.removeAll(dataList);
                offset += amount*5;
                dataList.addAll(api.download(amount, offset));
            }
        });

        HBox browsingButtons = new HBox();
        browsingButtons.setAlignment(Pos.BOTTOM_CENTER);
        browsingButtons.getChildren().addAll(first, previous, next, last);

        VBox browsingTabContent = new VBox();
        browsingTabContent.getChildren().addAll(table, browsingButtons);

        Tab browsingTab = new Tab();
        browsingTab.setText("Browse");
        browsingTab.setClosable(false);
        browsingTab.setContent(browsingTabContent);
        root.getTabs().add(browsingTab);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Log Aggregator");

        root.getTabs().add(createDatabaseTab());
        root.getTabs().add(createConfigurationTab());

        primaryStage.setScene(new Scene(root, 400, 400));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}