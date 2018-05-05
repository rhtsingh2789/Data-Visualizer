package dataprocessors;

import actions.AppActions;
import javafx.stage.FileChooser;
import org.junit.Test;
import ui.AppUI;
import vilij.components.ConfirmationDialog;
import vilij.propertymanager.PropertyManager;
import vilij.templates.ApplicationTemplate;
import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.*;
import static settings.AppPropertyTypes.DATA_FILE_EXT;
import static settings.AppPropertyTypes.DATA_FILE_EXT_DESC;

public class AppDataTest {
    ApplicationTemplate applicationTemplate = new ApplicationTemplate();
    TSDProcessor processor = new TSDProcessor();

    public boolean saveDataHelper() {
        try {
            processor.processString(((AppUI) (applicationTemplate.getUIComponent())).getTextArea());
            return true;
        } catch (Exception e) {
            return false;
        }
    }

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

        } else if (saveDataHelper()) {
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
    }

    @Test
    public void test1() throws Exception {
        TSDProcessor processor = new TSDProcessor();
        File file = new File("OneLineTest.tsd");
        processor.processString(file.toString());
    }

    @Test
    public void test2(){
        TextArea area = new TextArea();
        File file = new File("OneLineTest.tsd");
        Path path = Paths.get("OneLineTest.tsd");
        area.setText(file.toString());
        saveData(path);
    }
}