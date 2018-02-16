package ui;

import actions.AppActions;
import dataprocessors.AppData;
import dataprocessors.TSDProcessor;
import javafx.geometry.Pos;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import static settings.AppPropertyTypes.*;
import vilij.components.ErrorDialog;
import vilij.propertymanager.PropertyManager;
import vilij.templates.ApplicationTemplate;
import vilij.templates.UITemplate;

import java.nio.file.Path;

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
    private ScatterChart<Number, Number> chart;          // the chart where data will be displayed
    private Button                       displayButton;  // workspace button to display data on the chart
    private TextArea                     textArea;       // text area for new data input
    private boolean                      hasNewText;     // whether or not the text area has any new data since last display
    private String                         scrnshotPath;
    private final static String SEPARATOR = "/";

    public ScatterChart<Number, Number> getChart() { return chart; }

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

    private void layout() {
        chart = new ScatterChart<Number, Number>(new NumberAxis(),new NumberAxis());
        chart.setTitle("Data Visualization");
        textArea = new TextArea();
        VBox labelBox = new VBox();
        labelBox.getChildren().add(new Label("Data File"));
        labelBox.setAlignment(Pos.CENTER);
        labelBox.setMaxWidth(400);
        textArea.setPrefWidth(400);
        chart.setPrefWidth(600);
        displayButton= new Button("Display");
        HBox chartholder=new HBox();
        VBox textholder= new VBox();
        chartholder.getChildren().addAll(textholder);

        textholder.getChildren().addAll(labelBox, textArea, displayButton);
        chartholder.getChildren().add(chart);
        appPane.getChildren().add(chartholder);
        // TODO for homework 1
    }

    private void setWorkspaceActions() {
        textArea.setOnKeyPressed(event -> {
            newButton.setDisable(false);
            saveButton.setDisable(false);
        });
        textArea.setOnKeyTyped(event -> {
            if(textArea.getText().equals("")){
             newButton.setDisable(true);
             saveButton.setDisable(true);
         }
        });
        displayButton.setOnAction(e -> {
            ((AppData) applicationTemplate.getDataComponent()).loadData(textArea.getText());
        });
        // TODO for homework 1
    }

    public String getTextArea() {
        return textArea.getText();
    }
}
