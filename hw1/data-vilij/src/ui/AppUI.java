package ui;

import actions.AppActions;
import dataprocessors.AppData;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Dimension2D;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import static settings.AppPropertyTypes.*;
import vilij.propertymanager.PropertyManager;
import vilij.templates.ApplicationTemplate;
import vilij.templates.UITemplate;

import javax.swing.*;
import java.awt.*;
import java.awt.TextField;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;

import static vilij.settings.PropertyTypes.*;

/**
 * This is the application's user interface implementation.
 *
 * @author Ritwik Banerjee
 */
public final class AppUI extends UITemplate {

    /**
     * The application to which this class of actions belongs.
     */
    ApplicationTemplate applicationTemplate;

    @SuppressWarnings("FieldCanBeLocal")
    private Button scrnshotButton; // toolbar button to take a screenshot of the data
    private LineChart<Number, Number> chart;         // the chart where data will be displayed
    private Button displayButton = new Button();  // workspace button to display data on the chart
    private TextArea textArea = new TextArea();       // text area for new data input
    private boolean hasNewText;     // whether or not the text area has any new data since last display
    private String scrnshotPath;
    private final static String SEPARATOR = "/";
    private HBox mainBox = new HBox();
    private VBox splitBox = new VBox();
    private VBox splitBox2 = new VBox();
    private VBox algoBox = new VBox();
    private VBox stringBox = new VBox();
    private Button classification = new Button("Classification");
    private Button clustering = new Button("Clustering");
    private RadioButton clusteringAlg1 = new RadioButton("Algorithm 1");
    private RadioButton clusteringAlg2 = new RadioButton("Algorithm 1");
    private Button settingButton;
    private Button settingButton2;
    private String settingPath;
    private boolean textAreaBoolean = false;
    private static String[] config1 = {"1", "1", "false", "1"};
    private VBox playBox = new VBox();

    public LineChart<Number, Number> getChart() {
        return chart;
    }

    public AppUI(Stage primaryStage, ApplicationTemplate applicationTemplate) {
        super(primaryStage, applicationTemplate);
        this.applicationTemplate = applicationTemplate;
    }

    @Override
    protected void setResourcePaths(ApplicationTemplate applicationTemplate) {
        PropertyManager manager = applicationTemplate.manager;

        String iconsPath = SEPARATOR + String.join(SEPARATOR,
                manager.getPropertyValue(GUI_RESOURCE_PATH.name()),
                manager.getPropertyValue(ICONS_RESOURCE_PATH.name()));

        super.setResourcePaths(applicationTemplate);
        scrnshotPath = String.join(SEPARATOR, iconsPath, manager.getPropertyValue(SCREENSHOT_ICON.name()));
        settingPath = String.join(SEPARATOR, iconsPath, manager.getPropertyValue(SETTING_ICON.name()));
        settingButton = new Button(null, new ImageView(new Image(getClass().getResourceAsStream(settingPath))));
        settingButton2 = new Button(null, new ImageView(new Image(getClass().getResourceAsStream(settingPath))));


    }

    @Override
    protected void setToolBar(ApplicationTemplate applicationTemplate) {
        PropertyManager manager = applicationTemplate.manager;
        super.setToolBar(applicationTemplate);
        scrnshotButton = setToolbarButton(scrnshotPath, manager.getPropertyValue(SCREENSHOT_TOOLTIP.name()), true);
        toolBar.getItems().add(scrnshotButton);
    }

    @Override
    protected void setToolbarHandlers(ApplicationTemplate applicationTemplate) {
        applicationTemplate.setActionComponent(new AppActions(applicationTemplate));
        newButton.setOnAction(e -> applicationTemplate.getActionComponent().handleNewRequest());
        saveButton.setOnAction(e -> applicationTemplate.getActionComponent().handleSaveRequest());
        loadButton.setOnAction(e -> applicationTemplate.getActionComponent().handleLoadRequest());
        exitButton.setOnAction(e -> applicationTemplate.getActionComponent().handleExitRequest());
        printButton.setOnAction(e -> applicationTemplate.getActionComponent().handlePrintRequest());
        scrnshotButton.setOnAction(e -> {
            try {
                ((AppActions) applicationTemplate.getActionComponent()).handleScreenshotRequest();
            } catch (IOException ex) {

            }
        });
    }

    @Override
    public void initialize() {
        layout();
        setWorkspaceActions();
    }

    @Override
    public void clear() {
        if (!chart.getData().isEmpty()) {
            chart.getData().clear();
            textArea.setText("");
        }
    }

    public void clearChart() {
        if (!chart.getData().isEmpty()) {
            chart.getData().clear();
        }
    }

    private void layout() {
        chart = new LineChart<Number, Number>(new NumberAxis(), new NumberAxis());
        chart.setTitle("Plot");
        chart.setPrefWidth(600);

        chart.setHorizontalGridLinesVisible(false);
        chart.setVerticalGridLinesVisible(false);

        PropertyManager manager = applicationTemplate.manager;
        String cssPath1 = "/" + String.join(SEPARATOR,
                manager.getPropertyValue(GUI_RESOURCE_PATHS.name()),
                manager.getPropertyValue(CSS_RESOURCE_PATHS.name()),
                manager.getPropertyValue(CSS_RESOURCE_FILENAMES.name()));
        chart.getStylesheets().add(cssPath1);

        VBox chartholder = new VBox();
        chartholder.setAlignment(Pos.BASELINE_RIGHT);
        splitBox.getChildren().add(chart);
        splitBox.setAlignment(Pos.TOP_RIGHT);
        splitBox.setPrefWidth(600);
        splitBox2.setPrefWidth(400);
        mainBox.getChildren().addAll(splitBox2, splitBox);
        appPane.getChildren().add(mainBox);
        // TODO for homework 1
    }

    public void startingTextArea() {
        splitBox2.getChildren().clear();
        VBox labelBox = new VBox();
        labelBox.getChildren().clear();
        textArea.setPrefWidth(400);
        textArea.setPrefRowCount(10);
        labelBox.setMaxWidth(400);
        displayButton.setText("Done Editing");
        labelBox.getChildren().addAll(new Label("Data File"), textArea, displayButton);
        splitBox2.getChildren().add(labelBox);
    }


    public void addAlgos() {
        StringBuilder labelString = new StringBuilder();
        algoBox.getChildren().clear();
        stringBox.getChildren().clear();
        labelString = new StringBuilder("Total instances: " + ((AppData) applicationTemplate.getDataComponent()).getInstance()
                + "\nTotal Labels: " + ((AppData) applicationTemplate.getDataComponent()).getallLabels().size() + "\nLabels: \n");
        for (String s : ((AppData) applicationTemplate.getDataComponent()).getallLabels()) {
            labelString.append("\t- ").append(s).append("\n");
        }
        labelString.append("File path- ").append(((AppActions) applicationTemplate.getActionComponent()).getDataFilePath());
        Label instanceBox = new Label(labelString.toString());
        stringBox.getChildren().addAll(instanceBox);
        classification.setPrefSize(150, 20);
        clustering.setPrefSize(150, 20);

        if (((AppData) applicationTemplate.getDataComponent()).getallLabels().size() == 2)
            algoBox.getChildren().addAll(classification, clustering);
        else
            algoBox.getChildren().addAll(clustering);
        splitBox2.getChildren().addAll(stringBox, algoBox, playBox);
        setAlgorithmActions();
    }

    public void setAlgorithmActions() {
        classification.setOnAction(event -> ((AppActions) applicationTemplate.getActionComponent()).handleClassificationRequest());
        clustering.setOnAction(event -> ((AppActions) applicationTemplate.getActionComponent()).handleClusteringRequest());
        settingButton.setOnAction(event -> {
            createDialog();
        });
        settingButton2.setOnAction(event -> {
            createDialog1();
        });
        setAlgorithmSelection();
    }


    public void showClusterAlgorithms() {
        algoBox.getChildren().clear();
        HBox algorithmBox = new HBox();
        settingButton.setPrefSize(5, 5);
        algorithmBox.getChildren().addAll(clusteringAlg1, settingButton);
        algoBox.getChildren().addAll(algorithmBox);
    }

    public void showClassificationAlgorithms() {
        algoBox.getChildren().clear();
        HBox algorithmBox = new HBox();
        settingButton.setPrefSize(5, 5);
        algorithmBox.getChildren().addAll(clusteringAlg2, settingButton2);
        algoBox.getChildren().addAll(algorithmBox);

    }


    public void setAlgorithmSelection(){
        clusteringAlg1.setOnMouseReleased(event -> {
            if(clusteringAlg1.isSelected()){
                playBox.getChildren().clear();
                playBox.getChildren().addAll(new Button(("Run")));
            }
            else
                playBox.getChildren().clear();
        });
        clusteringAlg2.setOnMouseReleased(event -> {
            if(clusteringAlg1.isSelected()){
                playBox.getChildren().clear();
                playBox.getChildren().addAll(new Button(("Run")));
            }
            else
                playBox.getChildren().clear();
        });


    }

    public void setTextAreaActions() {
        textArea.textProperty().addListener(observable -> {
            if (!((AppData) applicationTemplate.getDataComponent()).checkTen(textArea.getText())) {
                ((AppData) applicationTemplate.getDataComponent()).makeTen(textArea.getText());
            }
        });
    }

    public void setTextAreaActions2() {
        textArea.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                if (!newValue.equals(oldValue)) {
                    if (!newValue.isEmpty()) {
                        ((AppActions) applicationTemplate.getActionComponent()).setIsUnsavedProperty(true);
                        if (newValue.charAt(newValue.length() - 1) == '\n')
                            hasNewText = true;
                        newButton.setDisable(false);
                        saveButton.setDisable(false);
                    } else {
                        hasNewText = true;
                        newButton.setDisable(true);
                        saveButton.setDisable(true);
                        scrnshotButton.setDisable(true);
                    }
                }
                //scrnshotButton.setDisable(false);
            } catch (IndexOutOfBoundsException e) {
                System.err.println(newValue);
            }
        });
    }

    public void setSavebutton() {
        saveButton.setDisable(true);
    }


    private void setWorkspaceActions() {
        setTextAreaActions();
        displayButton.setOnAction(e -> {
            if (!textAreaBoolean) {
                textArea.setDisable(true);
                textAreaBoolean = true;
            } else {
                textArea.setDisable(false);
                textAreaBoolean = false;
            }
            ((AppData) applicationTemplate.getDataComponent()).loadData(textArea.getText());
        });
    }

    public String getTextArea() {
        return textArea.getText();
    }

    public TextArea getTextAreas() {
        return textArea;
    }

    public void setTextArea(String fileString) {
        textArea.setText(fileString);
    }

    public void setTextAreaBoolean(boolean b) {
        textAreaBoolean = b;
    }

    public void disableDone() {
        displayButton.setDisable(true);
    }

    public void createDialog() {
        TextArea iteration = new TextArea();
        TextArea updateInterval = new TextArea();
        Button okButton = new Button("OK");

        iteration.setPrefRowCount(1);
        updateInterval.setPrefRowCount(1);
        iteration.setPrefWidth(30);
        updateInterval.setPrefWidth(30);
        iteration.setText(config1[0]);
        CheckBox cRun = new CheckBox("Continuous Run?");
        Label it = new Label("Iterations: \t");
        Label ut = new Label("Update Interval: \t");
        Label labelCount = new Label("Labels desired: ");
        TextArea labelsCounting = new TextArea();
        labelsCounting.setPrefWidth(30);
        labelsCounting.setPrefRowCount(1);
        labelsCounting.setText(config1[3]);
        HBox hBox5 = new HBox();
        hBox5.getChildren().addAll(labelCount, labelsCounting);


        Label secondLabel = new Label("Configuration");
        secondLabel.setFont(new Font(20));
        Pane secondaryLayout = new VBox(10);

        HBox hBox1 = new HBox();

        iteration.setText(config1[0]);


        hBox1.getChildren().addAll(it, iteration);

        HBox hBox2 = new HBox();

        updateInterval.setText(config1[1]);


        hBox2.getChildren().addAll(ut, updateInterval);


        HBox hBox3 = new HBox();

        if (config1[2].equals("1"))
            cRun.setSelected(true);


        hBox3.getChildren().addAll(cRun);

        HBox hBox4 = new HBox();
        hBox4.getChildren().addAll(okButton);


        secondaryLayout.getChildren().addAll(secondLabel, hBox1, hBox2, hBox5, hBox3, hBox4);
        Scene secondScene = new Scene(secondaryLayout, 350, 300);

        Stage newWindow = new Stage();
        newWindow.setMaxHeight(400);
        newWindow.setMaxWidth(500);
        newWindow.setTitle("Configuration");
        okButton.setOnAction(event -> {
            config1[0] = iteration.getText();
            config1[1] = updateInterval.getText();
            config1[3] = labelsCounting.getText();
            if (cRun.isSelected()) {
                config1[2] = "1";
            } else
                config1[2] = "0";
            newWindow.close();
        });
        newWindow.setScene(secondScene);
        newWindow.showAndWait();

    }

    public void createDialog1() {
        TextArea iteration = new TextArea();
        TextArea updateInterval = new TextArea();
        Button okButton = new Button("OK");

        iteration.setPrefRowCount(1);
        updateInterval.setPrefRowCount(1);
        iteration.setPrefWidth(30);
        updateInterval.setPrefWidth(30);
        iteration.setText(config1[0]);
        CheckBox cRun = new CheckBox("Continuous Run?");
        Label it = new Label("Iterations: \t");
        Label ut = new Label("Update Interval: \t");


        Label secondLabel = new Label("Configuration");
        secondLabel.setFont(new Font(20));
        Pane secondaryLayout = new VBox(10);

        HBox hBox1 = new HBox();

        iteration.setText(config1[0]);


        hBox1.getChildren().addAll(it, iteration);

        HBox hBox2 = new HBox();

        updateInterval.setText(config1[1]);


        hBox2.getChildren().addAll(ut, updateInterval);


        HBox hBox3 = new HBox();

        if (config1[2].equals("1"))
            cRun.setSelected(true);


        hBox3.getChildren().addAll(cRun);

        HBox hBox4 = new HBox();
        hBox4.getChildren().addAll(okButton);


        secondaryLayout.getChildren().addAll(secondLabel, hBox1, hBox2, hBox3, hBox4);
        Scene secondScene = new Scene(secondaryLayout, 350, 300);

        Stage newWindow = new Stage();
        newWindow.setMaxHeight(400);
        newWindow.setMaxWidth(500);
        newWindow.setTitle("Configuration");
        okButton.setOnAction(event -> {
            config1[0] = iteration.getText();
            config1[1] = updateInterval.getText();
            if (cRun.isSelected()) {
                config1[2] = "1";
            } else
                config1[2] = "0";
            newWindow.close();
        });
        newWindow.setScene(secondScene);
        newWindow.showAndWait();

    }
}



