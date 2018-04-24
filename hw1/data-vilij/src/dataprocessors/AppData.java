package dataprocessors;

import actions.AppActions;
import javafx.stage.FileChooser;
import ui.AppUI;
import vilij.components.ConfirmationDialog;
import vilij.components.DataComponent;
import vilij.components.ErrorDialog;
import vilij.propertymanager.PropertyManager;
import vilij.templates.ApplicationTemplate;
import vilij.templates.UITemplate;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Scanner;

import static settings.AppPropertyTypes.DATA_FILE_EXT;
import static settings.AppPropertyTypes.DATA_FILE_EXT_DESC;

/**
 * This is the concrete application-specific implementation of the data component defined by the Vilij framework.
 *
 * @author Rohit Singh
 * @see DataComponent
 */
public class AppData implements DataComponent {

    public TSDProcessor getProcessor() {
        return processor;
    }

    private TSDProcessor        processor;
    private ApplicationTemplate applicationTemplate;
    private int counter;
    String textAreaString = "";
    String fullString = "";
    static int lengthOfArray = 10;

    public AppData(ApplicationTemplate applicationTemplate) {
        this.processor = new TSDProcessor();
        this.applicationTemplate = applicationTemplate;
    }

    @Override
    public void loadData(Path dataFilePath) {
        fullString = "";
        textAreaString = "";
        ((AppUI) applicationTemplate.getUIComponent()).getTextAreas().clear();
        File loadedFile = new File(dataFilePath.toString());
        try{
            fullString = new Scanner(loadedFile).useDelimiter("//A").next();
            ((AppUI) (applicationTemplate.getUIComponent())).setTextArea(textAreaLines(fullString));
            loadData1(fullString);
            ((AppUI) (applicationTemplate.getUIComponent())).getTextAreas().setDisable(true);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        // TODO: NOT A PART OF HW 1
    }

    public String textAreaLines(String tsdString){
        counter = 0;
        String[] strings =  tsdString.split("\n");
        for(String s: strings){
            counter++;
            if(counter > 10){
            }
            else{
                textAreaString += s+"\n";
            }
        }
        ErrorDialog.getDialog().show("File Loaded", "The file loaded has " + (counter) + " (First 10 shown)" );

        return textAreaString;
    }

    public boolean checkTen(String textString){
        if(textString.split("\n").length<10 && lengthOfArray<=fullString.split("\n").length){
            return true;
        }
        else return false;
    }

    public void makeTen(String textString){
        String[] strings =  fullString.split("\n");
        if(lengthOfArray<strings.length) {
            textString += strings[lengthOfArray];
            lengthOfArray++;
            ((AppUI) (applicationTemplate.getUIComponent())).setTextArea(textString);
        }
    }
    public void loadData1(String dataString) {
        TSDProcessor processor = new TSDProcessor();
        try {
            processor.processString(dataString);
            displayData();
        } catch (Exception e) {
        }
        // TODO for homework 1
    }
    public void loadData(String dataString) {
        TSDProcessor processor = new TSDProcessor();
        try {
            processor.processString(dataString);
            displayData();
            ((AppUI) applicationTemplate.getUIComponent()).addAlgos();
        } catch (Exception e) {
        }
        // TODO for homework 1
    }

    public boolean saveDataHelper() {
        try {
            processor.processString(((AppUI) (applicationTemplate.getUIComponent())).getTextArea());
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public void saveData(Path dataFilePath) {
        if (dataFilePath != null && saveDataHelper()) {
            FileWriter writer;
            try {
                writer = new FileWriter(dataFilePath.toFile());
                writer.write(((AppUI) (applicationTemplate.getUIComponent())).getTextArea());
                writer.close();
                ((AppUI) (applicationTemplate.getUIComponent())).setSavebutton();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        else if(saveDataHelper()) {
            try {
                PropertyManager manager = applicationTemplate.manager;
                FileChooser fileChooser = new FileChooser();
                fileChooser.getExtensionFilters().addAll(
                        new FileChooser.ExtensionFilter(manager.getPropertyValue(DATA_FILE_EXT_DESC.name()),
                                manager.getPropertyValue(DATA_FILE_EXT.name())));
                File selectedFile = fileChooser.showSaveDialog(ConfirmationDialog.getDialog());
                FileWriter writer;
                writer = new FileWriter(selectedFile);
                writer.write(((AppUI) (applicationTemplate.getUIComponent())).getTextArea());
                writer.close();
                ((AppActions) applicationTemplate.getActionComponent()).setdataFilePath(selectedFile.toPath());
                ((AppUI) (applicationTemplate.getUIComponent())).setSavebutton();
            } catch (Exception e) {

            }
        }

        // TODO: NOT A PART OF HW 1
    }

    @Override
    public void clear() {
        processor.clear();
    }

    public void displayData() {
        processor.toChartData(((AppUI) applicationTemplate.getUIComponent()).getChart());
    }

    public int getInstance(){
        return processor.getInstanceCounter();
    }
    public int getLabels(){
        return processor.getLabelCounter();
    }
    public HashSet<String> getallLabels(){
        return processor.getLabels();
    }
}
