package ui;

import actions.AppActions;
import dataprocessors.AppData;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import static settings.AppPropertyTypes.*;
import vilij.propertymanager.PropertyManager;
import vilij.templates.ApplicationTemplate;
import vilij.templates.UITemplate;

import java.awt.event.MouseEvent;
import java.io.IOException;

import static vilij.settings.PropertyTypes.*;

/**
 * This is the application's user interface implementation.
 *
 * @author Ritwik Banerjee
 */
public final class AppUI extends UITemplate {

    /** The application to which this class of actions belongs. */
    ApplicationTemplate applicationTemplate;

    @SuppressWarnings("FieldCanBeLocal")
    private Button                       scrnshotButton; // toolbar button to take a screenshot of the data
    private LineChart<Number, Number> chart;         // the chart where data will be displayed
    private Button                       displayButton;  // workspace button to display data on the chart
    private TextArea                     textArea;       // text area for new data input
    private boolean                      hasNewText;     // whether or not the text area has any new data since last display
    private String                       scrnshotPath;
    private final static String          SEPARATOR = "/";
    private CheckBox                     readOnly;

    public LineChart<Number, Number> getChart() { return chart; }

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
    }

    @Override
    protected void setToolBar(ApplicationTemplate applicationTemplate) {
        PropertyManager manager = applicationTemplate.manager;
        super.setToolBar(applicationTemplate);
        scrnshotButton = setToolbarButton(scrnshotPath, manager.getPropertyValue(SCREENSHOT_TOOLTIP.name()), true);
        toolBar.getItems().add(scrnshotButton);
        // TODO for homework 1
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
            try{
                ((AppActions) applicationTemplate.getActionComponent()).handleScreenshotRequest();
            }
            catch (IOException ex){

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
        if(!chart.getData().isEmpty()){
            chart.getData().clear();
            textArea.setText("");
        }
        // TODO for homework 1
    }

    public void clearChart() {
        if(!chart.getData().isEmpty()){
            chart.getData().clear();
        }
        // TODO for homework 1
    }

    private void layout() {
        chart = new LineChart<Number, Number>(new NumberAxis(),new NumberAxis());
        chart.setTitle("Data Visualization");
        textArea = new TextArea();
        VBox labelBox = new VBox();
        labelBox.getChildren().add(new Label("Data File"));

        labelBox.setAlignment(Pos.CENTER);
        labelBox.setMaxWidth(400);
        textArea.setPrefWidth(400);
        textArea.setPrefRowCount(10);
        chart.setPrefWidth(600);

        chart.setHorizontalGridLinesVisible(false);
        chart.setVerticalGridLinesVisible(false);

        PropertyManager manager = applicationTemplate.manager;
        String cssPath1 = "/" + String.join(SEPARATOR,
                manager.getPropertyValue(GUI_RESOURCE_PATHS.name()),
                manager.getPropertyValue(CSS_RESOURCE_PATHS.name()),
                manager.getPropertyValue(CSS_RESOURCE_FILENAMES.name()));
        chart.getStylesheets().add(cssPath1);

        displayButton= new Button("Display");
        HBox chartholder=new HBox();
        VBox textholder= new VBox();
        readOnly = new CheckBox("Read Only");

        chartholder.getChildren().addAll(textholder);
        textholder.getChildren().addAll(labelBox, textArea, displayButton, readOnly);
        chartholder.getChildren().add(chart);
        appPane.getChildren().add(chartholder);
        // TODO for homework 1
    }


    public void setChartAreaActions(){
        final Tooltip tooltip = new Tooltip();
        tooltip.setText(
                "\nYour password must be\n" +
                        "at least 8 characters in length\n"
        );
        displayButton.setTooltip(tooltip);

    }

    public void setTextAreaActions() {
        textArea.textProperty().addListener(observable -> {
            if(((AppData) applicationTemplate.getDataComponent()).checkTen(textArea.getText())){
                ((AppData) applicationTemplate.getDataComponent()).makeTen(textArea.getText());
            }
        });
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
                scrnshotButton.setDisable(false);
            } catch (IndexOutOfBoundsException e) {
                System.err.println(newValue);
            }
        });
    }

    public void setSavebutton(){
        saveButton.setDisable(true);
    }


    private void setWorkspaceActions() {
        readOnly.setOnAction(e -> {
            if(readOnly.isSelected()) {
                textArea.setDisable(true);
            }
            else{
                textArea.setDisable(false);
            }

        });
        setTextAreaActions();
        displayButton.setOnAction(e -> {
            setChartAreaActions();
            clearChart();
            ((AppData) applicationTemplate.getDataComponent()).loadData(textArea.getText());
        });
        // TODO for homework 1
    }

    public String getTextArea() {
        return textArea.getText();
    }
    public void setTextArea(String fileString) {
        textArea.setText(fileString);
    }
}
