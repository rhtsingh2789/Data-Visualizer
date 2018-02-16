package actions;

import javafx.stage.FileChooser;
import ui.AppUI;
import vilij.components.ActionComponent;
import vilij.components.ConfirmationDialog;
import vilij.templates.ApplicationTemplate;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;

/**
 * This is the concrete implementation of the action handlers required by the application.
 *
 * @author Ritwik Banerjee
 */
public final class AppActions implements ActionComponent {

    /** The application to which this class of actions belongs. */
    private ApplicationTemplate applicationTemplate;

    /** Path to the data file currently active. */
    Path dataFilePath;

    public AppActions(ApplicationTemplate applicationTemplate) {
        this.applicationTemplate = applicationTemplate;
    }

    @Override
    public void handleNewRequest() {
        try {
            this.promptToSave();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // TODO for homework 1
    }

    @Override
    public void handleSaveRequest() {
        // TODO: NOT A PART OF HW 1
    }

    @Override
    public void handleLoadRequest() {
        // TODO: NOT A PART OF HW 1
    }

    @Override
    public void handleExitRequest() {
        System.exit(0);
        // TODO for homework 1
    }

    @Override
    public void handlePrintRequest() {
        // TODO: NOT A PART OF HW 1
    }

    public void handleScreenshotRequest() throws IOException {
        // TODO: NOT A PART OF HW 1
    }

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
        ConfirmationDialog.getDialog().show("Work is not Saved", "Would you like to save current work?");
        try{
            if(ConfirmationDialog.getDialog().getSelectedOption().equals(ConfirmationDialog.Option.YES)) {
                FileChooser fileChooser = new FileChooser();
                fileChooser.getExtensionFilters().addAll(
                        new FileChooser.ExtensionFilter("Tab Seperated Data", "*.tsd"));
                File selectedFile = fileChooser.showSaveDialog(ConfirmationDialog.getDialog());

                if (selectedFile != null) {
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



        // TODO for homework 1
        // TODO remove the placeholder line below after you have implemented this method
        return false;
    }
}
