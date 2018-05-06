package ui;

import actions.AppActions;
import algorithms.*;
import data.DataSet;
import dataprocessors.AppData;
import javafx.geometry.Pos;
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

import vilij.components.ErrorDialog;
import vilij.propertymanager.PropertyManager;
import vilij.templates.ApplicationTemplate;
import vilij.templates.UITemplate;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Scanner;

import static vilij.settings.PropertyTypes.*;

/**
 * This is the application's user interface implementation.
 *
 * @author Rohit Singh
 */
public final class AppUI extends UITemplate {

    /**
     * The application to which this class of actions belongs.
     */
    ApplicationTemplate applicationTemplate;

    @SuppressWarnings("FieldCanBeLocal")
    private Button scrnshotButton; // toolbar button to take a screenshot of the data
    private static LineChart<Number, Number> chart;         // the chart where data will be displayed
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
    private RadioButton clusteringAlg1 = new RadioButton();
    private RadioButton clusteringAlg2= new RadioButton();
    private RadioButton clusteringAlg3= new RadioButton();
    private RadioButton clusteringAlg4= new RadioButton();
    private RadioButton classAlg1= new RadioButton();
    private RadioButton classAlg2= new RadioButton();
    private RadioButton classAlg3= new RadioButton();
    private RadioButton classAlg4 =new RadioButton();
    private boolean algoRunning= false;
    Classifier classifier;
    Clusterer clusterer;




    private Button run = new Button("Run");
    private String settingPath;
    private boolean textAreaBoolean = false;
    private static String[] config1 = {"1", "1", "false", "1"};
    private static String[] config2 = {"1", "1", "false", "1"};
    private VBox playBox = new VBox();
    private VBox algorithmsDrop = new VBox();
    private CheckBox cRun = new CheckBox("Continuous Run?");
    private DataSet dataSet = new DataSet();
    private boolean startThread = false;
    private Thread thread;
    private String[] strings;
    private String[] strings1;


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
//        settingButton1 = new Button(null, new ImageView(new Image(getClass().getResourceAsStream(settingPath))));
//        settingButton2 = new Button(null, new ImageView(new Image(getClass().getResourceAsStream(settingPath))));


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
        chart.setVerticalZeroLineVisible(false);
        chart.setHorizontalZeroLineVisible(false);
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
        try {
            labelString = new StringBuilder("Total instances: " + ((AppData) applicationTemplate.getDataComponent()).getInstance()
                    + "\nTotal Labels: " + ((AppData) applicationTemplate.getDataComponent()).getallLabels().size() + "\nLabels: \n");
        }
        catch (Exception e){

        }
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
        splitBox2.getChildren().addAll(stringBox, algoBox, algorithmsDrop, playBox);
        setAlgorithmActions();
    }

    public void setAlgorithmActions() {
        classification.setOnAction(event -> ((AppActions) applicationTemplate.getActionComponent()).handleClassificationRequest());
        clustering.setOnAction(event -> ((AppActions) applicationTemplate.getActionComponent()).handleClusteringRequest());
    }


    public void showClusterAlgorithms() {
        clustloadTextFile();
        algorithmsDrop.getChildren().clear();
        Label topAlgoLabel = new Label("CLUSTERING ALGORITHMS:-");
        algorithmsDrop.getChildren().add(topAlgoLabel);
        for(int mc =0; mc<strings1.length; mc++) {
            Button settings1 = new Button("",new ImageView(new Image(getClass().getResourceAsStream(settingPath))));
            HBox eachAlgo = new HBox();
            if(mc==0){
                clusteringAlg1 = new RadioButton(strings1[0]);
                eachAlgo.getChildren().addAll(clusteringAlg1,settings1);
            }
            if(mc==1){
                clusteringAlg2 = new RadioButton(strings1[1]);
                eachAlgo.getChildren().addAll(clusteringAlg2, settings1);
            }
            if(mc==2){
                clusteringAlg3 = new RadioButton(strings1[2]);
                eachAlgo.getChildren().addAll(clusteringAlg3, settings1);
            }
            if(mc==3) {
                clusteringAlg4 = new RadioButton(strings1[3]);
                eachAlgo.getChildren().addAll(clusteringAlg4, settings1);
            }

            int algoIndex = mc;

            algorithmsDrop.getChildren().addAll(eachAlgo);

            settings1.setOnAction(e -> {
                try {
                    createDialog();
                    Class c = Class.forName("algorithms."+ strings1[algoIndex]);   //TEST
                    Constructor constructor = c.getDeclaredConstructor(DataSet.class, int.class, int.class, int.class, boolean.class,ApplicationTemplate.class);

                        clusterer = (Clusterer) constructor.newInstance(dataSet.fromTSDFile(((AppActions) applicationTemplate.getActionComponent()).getDataFilePath()), Integer.parseInt(config1[0]),
                                Integer.parseInt(config1[1]), Integer.parseInt(config1[3]), cRun.isSelected(), applicationTemplate);
                        run.setOnAction(even  -> {
                            if(!cRun.isSelected() && !startThread){
                                ErrorDialog.getDialog().show(
                                        "Click continuously", "Click run over and over until max iterations");
                            }

                            if(!startThread) {
                                run.setDisable(true);
                                startThread = true;
                                scrnshotButton.setDisable(true);
                                algoRunning = true;
                                thread = new Thread(clusterer);
                                thread.start();
                            }
                            else{
                                try {
                                    scrnshotButton.setDisable(true);
                                    thread.interrupt();
                                }
                                catch (Exception exe) {
                                }
                            }
                        });


                        run.setOnAction(even  -> {
                            if(!cRun.isSelected() && !startThread){
                                ErrorDialog.getDialog().show(
                                        "Click continuously", "Click run over and over until max iterations");
                            }

                            if(!startThread) {
                                run.setDisable(true);
                                startThread = true;
                                thread = new Thread(clusterer);
                                scrnshotButton.setDisable(true);
                                algoRunning = true;
                                thread.start();
                            }
                            else{
                                try {
                                    scrnshotButton.setDisable(true);
                                    thread.interrupt();
                                }
                                catch (Exception exe) {
                                }
                            }
                        });

                } catch (Exception exe) {
                    ErrorDialog.getDialog().show("OOPS", "The class does not exist or there is no run method");
                }
            });
        }
        setAlgorithmSelection();

    }

    public void showClassificationAlgorithms() {
        classloadTextFile();
        algorithmsDrop.getChildren().clear();
        Label topAlgoLabel = new Label("CLASSIFICATION ALGORITHMS:-");
        VBox algorithmBox = new VBox();
        algorithmsDrop.getChildren().add(topAlgoLabel);
        for(int mc =0; mc<strings.length; mc++) {
            Button settings = new Button("",new ImageView(new Image(getClass().getResourceAsStream(settingPath))));
            HBox eachAlgo = new HBox();
            if(mc==0){
                classAlg1 = new RadioButton(strings[0]);
                eachAlgo.getChildren().addAll(classAlg1,settings);
            }
            if(mc==1){
                classAlg2 = new RadioButton(strings[1]);
                eachAlgo.getChildren().addAll(classAlg2, settings);
            }
            if(mc==2){
                classAlg3 = new RadioButton(strings[2]);
                eachAlgo.getChildren().addAll(classAlg3, settings);
            }
            if(mc==3) {
                classAlg4 = new RadioButton(strings[3]);
                eachAlgo.getChildren().addAll(classAlg4, settings);
            }
            //label.setLayoutX(60);
            //radiobutton.setLayoutX(25);
            int name = mc;

            algorithmsDrop.getChildren().addAll(eachAlgo);

            settings.setOnAction(e -> {
                try {
                    createDialog1();
                    Class c = Class.forName("algorithms."+ strings[name]);   //TEST
                    Constructor constructor = c.getDeclaredConstructor(DataSet.class, int.class, int.class, boolean.class,ApplicationTemplate.class);
                        classifier = (Classifier) constructor.newInstance(dataSet, Integer.parseInt(config2[0]),
                                Integer.parseInt(config2[1]), cRun.isSelected(), applicationTemplate);


                        run.setOnAction(even  -> {
                            if(!cRun.isSelected() && !startThread){
                                ErrorDialog.getDialog().show(
                                        "Click continuously", "Click run over and over until max iterations");
                            }

                            if(!startThread) {
                                try {
                                    Method clearMethod = c.getMethod("clearSeries");
                                    clearMethod.invoke(classifier);
                                } catch (NoSuchMethodException e1) {
                                } catch (IllegalAccessException e1) {
                                } catch (InvocationTargetException e1) {
                                }
                                run.setDisable(true);
                                scrnshotButton.setDisable(true);
                                algoRunning = true;
                                startThread = true;
                                thread = new Thread(classifier);
                                thread.start();
                            }
                            else{
                                try {
                                    scrnshotButton.setDisable(true);
                                    thread.interrupt();
                                }
                                catch (Exception exe) {
                                }
                            }
                        });


                } catch (Exception exe) {
                    ErrorDialog.getDialog().show("OOPS", "The class does not exist or there is no run method");
                }
            });
        }
        setAlgorithmSelection();
    }


    public void setAlgorithmSelection(){
        classAlg1.setOnMouseReleased(event -> {
            if(classAlg1.isSelected()){
                classAlg2.setSelected(false);
                classAlg3.setSelected(false);
                classAlg4.setSelected(false);
                clusteringAlg1.setSelected(false);
                clusteringAlg2.setSelected(false);
                clusteringAlg3.setSelected(false);
                clusteringAlg4.setSelected(false);
                playBox.getChildren().clear();
                playBox.getChildren().addAll(run);
            }
            else
                playBox.getChildren().clear();
        });
        classAlg2.setOnMouseReleased(event -> {
            if(classAlg2.isSelected()){
                classAlg1.setSelected(false);
                classAlg3.setSelected(false);
                classAlg4.setSelected(false);
                clusteringAlg1.setSelected(false);
                clusteringAlg2.setSelected(false);
                clusteringAlg3.setSelected(false);
                clusteringAlg4.setSelected(false);
                playBox.getChildren().clear();
                playBox.getChildren().addAll(run);
            }
            else
                playBox.getChildren().clear();
        });
        classAlg3.setOnMouseReleased(event -> {
            if(classAlg3.isSelected()){
                classAlg1.setSelected(false);
                classAlg2.setSelected(false);
                classAlg4.setSelected(false);
                clusteringAlg1.setSelected(false);
                clusteringAlg2.setSelected(false);
                clusteringAlg3.setSelected(false);
                clusteringAlg4.setSelected(false);
                playBox.getChildren().clear();
                playBox.getChildren().addAll(run);
            }
            else
                playBox.getChildren().clear();
        });
        classAlg4.setOnMouseReleased(event -> {
            if(classAlg4.isSelected()){
                classAlg1.setSelected(false);
                classAlg3.setSelected(false);
                classAlg2.setSelected(false);
                clusteringAlg1.setSelected(false);
                clusteringAlg2.setSelected(false);
                clusteringAlg3.setSelected(false);
                clusteringAlg4.setSelected(false);
                playBox.getChildren().clear();
                playBox.getChildren().addAll(run);
            }

            else
                playBox.getChildren().clear();
        });
        clusteringAlg1.setOnMouseReleased(event -> {
            if(clusteringAlg1.isSelected()){
                classAlg1.setSelected(false);
                classAlg2.setSelected(false);
                classAlg3.setSelected(false);
                classAlg4.setSelected(false);
                clusteringAlg4.setSelected(false);
                clusteringAlg2.setSelected(false);
                clusteringAlg3.setSelected(false);
                playBox.getChildren().clear();
                playBox.getChildren().addAll(run);
            }

            else
                playBox.getChildren().clear();
        });
        clusteringAlg2.setOnMouseReleased(event -> {
            if(clusteringAlg2.isSelected()){
                classAlg1.setSelected(false);
                classAlg2.setSelected(false);
                classAlg3.setSelected(false);
                classAlg4.setSelected(false);
                clusteringAlg1.setSelected(false);
                clusteringAlg4.setSelected(false);
                clusteringAlg3.setSelected(false);
                playBox.getChildren().clear();
                playBox.getChildren().addAll(run);
            }

            else
                playBox.getChildren().clear();
        });
        clusteringAlg3.setOnMouseReleased(event -> {
            if(clusteringAlg3.isSelected()){
                classAlg1.setSelected(false);
                classAlg2.setSelected(false);
                classAlg3.setSelected(false);
                classAlg4.setSelected(false);
                clusteringAlg1.setSelected(false);
                clusteringAlg2.setSelected(false);
                clusteringAlg4.setSelected(false);
                playBox.getChildren().clear();
                playBox.getChildren().addAll(run);
            }

            else
                playBox.getChildren().clear();
        });
        clusteringAlg4.setOnMouseReleased(event -> {
            if(clusteringAlg4.isSelected()){
                classAlg1.setSelected(false);
                classAlg2.setSelected(false);
                classAlg3.setSelected(false);
                classAlg4.setSelected(false);
                clusteringAlg1.setSelected(false);
                clusteringAlg2.setSelected(false);
                clusteringAlg3.setSelected(false);
                playBox.getChildren().clear();
                playBox.getChildren().addAll(run);
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

    public Button getRun() {
        return run;
    }
    private void setWorkspaceActions() {
        setTextAreaActions();
        displayButton.setOnAction(e -> {
            clearChart();
            if (!textAreaBoolean) {
                textArea.setDisable(true);
                textAreaBoolean = true;
            } else {
                textArea.setDisable(false);
                textAreaBoolean = false;
            }
            ((AppData) applicationTemplate.getDataComponent()).loadData(textArea.getText());
            ((AppData) applicationTemplate.getDataComponent()).displayData();
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
    public void enableDone() {
        displayButton.setDisable(false);
    }
    public LineChart<Number, Number> getChart(){
        return chart;
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
            if(iteration.getText().charAt(0)>='1' && iteration.getText().charAt(0)<='9')
                config1[0] = iteration.getText();
            else
                config1[0] = "1";
            if(updateInterval.getText().charAt(0)>='1' && updateInterval.getText().charAt(0)<='9')
                config1[1] = updateInterval.getText();
            else
                config1[1] = "1";
            if(labelsCounting.getText().charAt(0)>='1' && labelsCounting.getText().charAt(0)<='9')
                config1[3] = labelsCounting.getText();
            else
                config1[3] = "1";
            if (cRun.isSelected()) {
                config1[2] = "true";
            } else
                config1[2] = "false";
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
        Label it = new Label("Iterations: \t");
        Label ut = new Label("Update Interval: \t");


        Label secondLabel = new Label("Configuration");
        secondLabel.setFont(new Font(20));
        Pane secondaryLayout = new VBox(10);

        HBox hBox1 = new HBox();

        iteration.setText(config2[0]);


        hBox1.getChildren().addAll(it, iteration);

        HBox hBox2 = new HBox();

        updateInterval.setText(config2[1]);


        hBox2.getChildren().addAll(ut, updateInterval);


        HBox hBox3 = new HBox();

        if (config2[2].equals("true"))
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
            if(iteration.getText().charAt(0)>='1' && iteration.getText().charAt(0)<='9')
                config2[0] = iteration.getText();
            else
                config2[0] = "1";
            if(updateInterval.getText().charAt(0)>='1' && updateInterval.getText().charAt(0)<='9')
                config2[1] = updateInterval.getText();
            else
                config2[1] = "1";
            if (cRun.isSelected()) {
                config2[2] = "true";
            } else
                config2[2] = "false";
            newWindow.close();
        });
        newWindow.setScene(secondScene);
        newWindow.showAndWait();

    }

    public void classloadTextFile(){
        File file = new File("classificationAlgos.txt");
        try {
            String fullString = new Scanner(file).useDelimiter("//A").next();
            strings =  fullString.split("\n");
        } catch (FileNotFoundException e) {
        }
    }
    public void clustloadTextFile(){
        File file = new File("clusteringAlgos.txt");
        try {
            String fullString = new Scanner(file).useDelimiter("//A").next();
            strings1 =  fullString.split("\n");
        } catch (FileNotFoundException e) {
        }
    }

    public Button getScrnshotButton() {
        return scrnshotButton;
    }

    public void setAlgoRunning(boolean algoRunning) {
        this.algoRunning = algoRunning;
    }

    public void setStartThread(boolean startThread) {
        this.startThread = startThread;
    }
    public boolean isAlgoRunning() {
        return algoRunning;
    }
    public static void showDone(){
            ErrorDialog.getDialog().show("Algorithm Ended", "Algorithm Ended");
    }
    
}



