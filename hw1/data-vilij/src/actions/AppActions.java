package actions;

import dataprocessors.AppData;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.WritableImage;
import javafx.stage.FileChooser;
import ui.AppUI;
import vilij.components.ActionComponent;
import vilij.components.ConfirmationDialog;
import vilij.components.Dialog;
import vilij.propertymanager.PropertyManager;
import vilij.templates.ApplicationTemplate;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import javafx.scene.chart.Chart;

import javafx.beans.property.SimpleBooleanProperty;

import javax.imageio.ImageIO;

import static settings.AppPropertyTypes.*;

/**
 * This is the concrete implementation of the action handlers required by the application.
 *
 * @author Rohit Singh
 */
public final class AppActions implements ActionComponent {

    /** The application to which this class of actions belongs. */
    private ApplicationTemplate applicationTemplate;

    /** Path to the data file currently active. */
    Path  dataFilePath;
    boolean isUnsaved;


    public void setdataFilePath(Path dataFilePath){
        this.dataFilePath = dataFilePath;
    }
    public AppActions(ApplicationTemplate applicationTemplate) {
        this.applicationTemplate = applicationTemplate;
        this.isUnsaved = false;
    }



    @Override
    public void handleNewRequest() {
        try {
            if(((AppUI) applicationTemplate.getUIComponent()).getTextAreas().getText().isEmpty() == false)
                this.promptToSave();

            ((AppUI) applicationTemplate.getUIComponent()).startingTextArea();
            ((AppUI) (applicationTemplate.getUIComponent())).getTextAreas().clear();
            ((AppUI) (applicationTemplate.getUIComponent())).getTextAreas().setDisable(false);
            ((AppUI) (applicationTemplate.getUIComponent())).setTextAreaActions2();
            ((AppUI) (applicationTemplate.getUIComponent())).enableDone();
        } catch (IOException e) {
        }

        // TODO for homework 1
    }

    public void handleSaveRequest() {
        applicationTemplate.getDataComponent().saveData(dataFilePath);
        // TODO: NOT A PART OF HW 1
    }

    @Override
    public void handleLoadRequest() {
        PropertyManager manager= applicationTemplate.manager;
        applicationTemplate.getUIComponent().clear();
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter(manager.getPropertyValue(DATA_FILE_EXT_DESC.name()),
                        manager.getPropertyValue(DATA_FILE_EXT.name())));
        File selectedFile = fileChooser.showOpenDialog(ConfirmationDialog.getDialog());
        if (selectedFile != null) {
            dataFilePath = Paths.get(selectedFile.getAbsolutePath());
            ((AppData) applicationTemplate.getDataComponent()).loadData(dataFilePath);
        }        // TODO: NOT A PART OF HW 1
        ((AppUI) applicationTemplate.getUIComponent()).startingTextArea();
        ((AppUI) applicationTemplate.getUIComponent()).addAlgos();
        ((AppUI) applicationTemplate.getUIComponent()).setTextAreaBoolean(true);
        ((AppUI) applicationTemplate.getUIComponent()).disableDone();
    }

    @Override
    public void handleExitRequest() {
        if(!((AppUI) applicationTemplate.getUIComponent()).isAlgoRunning() && !isUnsaved)
            System.exit(0);
        else if(!((AppUI) applicationTemplate.getUIComponent()).isAlgoRunning() && isUnsaved){
            try {
                promptToSave();
                System.exit(0);
            } catch (IOException e) {

            }
        }
        else{
            PropertyManager manager= applicationTemplate.manager;
            ConfirmationDialog.getDialog().show(manager.getPropertyValue(ALGORITHM_RUNNING_TITLE.name()),
                    manager.getPropertyValue(ALGORITHM_END.name()));

            try {
                if (ConfirmationDialog.getDialog().getSelectedOption().equals(ConfirmationDialog.Option.YES)) {
                    System.exit(0);

                } else if (ConfirmationDialog.getDialog().getSelectedOption() == ConfirmationDialog.Option.NO) {
                }
            }catch (Exception e){}
        }

        // TODO for homework 1
    }

    @Override
    public void handlePrintRequest() {
        // TODO: NOT A PART OF HW 1
    }

    public void handleScreenshotRequest() throws IOException {
        PropertyManager manager= applicationTemplate.manager;
        Chart chart = ((AppUI)applicationTemplate.getUIComponent()).getChart();
        try{
            WritableImage scrnimage = chart.snapshot(new SnapshotParameters(),null);
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Image", "*.png"));
            File selectedFile = fileChooser.showSaveDialog(ConfirmationDialog.getDialog());
            ImageIO.write(SwingFXUtils.fromFXImage(scrnimage,null), "png",selectedFile);
        }
        catch (Exception ignored){
        }
        // TODO: NOT A PART OF HW 1
    }
    public void setIsUnsavedProperty(boolean property) { isUnsaved= property; }

    /**
     * This helper method verifies that the user really wants to save their unsaved work, which they might not want to
     * do. The user will be presented with three options:
     * <ol>
     * <li><code>yes</code>, indicating that the user wants to save the work and continue with the action,</li>
     * <li><code>no</code>, indicating that the user wants to continue with the action without saving the work, and</li>
     * <li><code>cancel</code>, to indicate that the user does not want to continue with the action, but also does not
     * want to save the work at this point.</li>
     * </ol>
     *
     * @return <code>false</code> if the user presses the <i>cancel</i>, and <code>true</code> otherwise.
     */
    private boolean promptToSave() throws IOException {
        PropertyManager manager= applicationTemplate.manager;
        ConfirmationDialog.getDialog().show(manager.getPropertyValue(SAVE_UNSAVED_WORK_TITLE.name()),
                manager.getPropertyValue(SAVE_UNSAVED_WORK.name()));

        try{
            if(ConfirmationDialog.getDialog().getSelectedOption().equals(ConfirmationDialog.Option.YES)) {
                FileChooser fileChooser = new FileChooser();
                fileChooser.getExtensionFilters().addAll(
                        new FileChooser.ExtensionFilter(manager.getPropertyValue(DATA_FILE_EXT_DESC.name()),
                                manager.getPropertyValue(DATA_FILE_EXT.name())));
                File selectedFile = fileChooser.showSaveDialog(ConfirmationDialog.getDialog());
                if (selectedFile != null) {
                    dataFilePath = selectedFile.toPath();
                    FileWriter writer = new FileWriter(selectedFile);
                    writer.write(((AppUI) (applicationTemplate.getUIComponent())).getTextArea());
                    writer.close();
                }
                applicationTemplate.getUIComponent().clear();
            }
            else if(ConfirmationDialog.getDialog().getSelectedOption()== ConfirmationDialog.Option.NO) {
                ((AppUI) (applicationTemplate.getUIComponent())).getTextArea();
                applicationTemplate.getUIComponent().clear();
            }
        }catch (Exception e){
            System.out.println();
        }
        return false;
    }

    public void handleClassificationRequest() {
        ((AppUI) applicationTemplate.getUIComponent()).showClassificationAlgorithms();
    }

    public void handleClusteringRequest() {
        ((AppUI) applicationTemplate.getUIComponent()).showClusterAlgorithms();
    }

    public Path getDataFilePath(){
        return dataFilePath;
    }
}
