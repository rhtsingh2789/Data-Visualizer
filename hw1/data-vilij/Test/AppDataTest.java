
import actions.AppActions;
import javafx.stage.FileChooser;
import org.junit.Assert;
import org.junit.Test;
import dataprocessors.TSDProcessor;
import dataprocessors.AppData;
import ui.AppUI;
import vilij.components.ConfirmationDialog;
import vilij.propertymanager.PropertyManager;

import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

import static settings.AppPropertyTypes.DATA_FILE_EXT;
import static settings.AppPropertyTypes.DATA_FILE_EXT_DESC;

public class AppDataTest {
    TSDProcessor processor = new TSDProcessor();


    public void test2ing(Path dataFilePath, String string){

        File selectedFile = new File(dataFilePath.toString());
        FileWriter writer;
        try {
            writer = new FileWriter(selectedFile);
            writer.write(string);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test1() throws Exception {
        TSDProcessor processor = new TSDProcessor();
            String test1    = "@a\t\t-2,3";
            String test2    = "@b\tLabel2\t4,5";
            processor.processString(test1);
            processor.processString(test2);
        Assert.assertTrue(processor.getLabels()!=null);

    }

    @Test
    public void test2(){
        String textAreaS= "@a\tLabel1\t2,3";
        File file = new File("Checking.tsd");
        test2ing(file.toPath(), textAreaS);
    }

    public void test3(){
    }


}