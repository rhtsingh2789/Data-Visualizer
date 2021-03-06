import dataprocessors.AppData;
import dataprocessors.TSDProcessor;
import org.junit.Assert;
import org.junit.Test;
import ui.AppUI;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.text.ParseException;
import java.util.Scanner;

public class AppDataTest {
    TSDProcessor processor = new TSDProcessor();



    @Test
    public void OneLineTest() throws Exception {
        TSDProcessor processor = new TSDProcessor();
            String test1    = "@a\t\t-2,3";
            String test2    = "@b\tLabel2\t4,5";
            processor.processString(test1);
            processor.processString(test2);
        Assert.assertTrue(processor.getLabels()!=null);

    }

    @Test
    public void TextAreaSaveTest(){
        String fullString="";
        String textAreaS= "@a\tLabel1\t2,4";
        File file = new File("Checking.tsd");
        saveData(file.toPath(), textAreaS);
        try {
            fullString = new Scanner(file).useDelimiter("//A").next();
        } catch (FileNotFoundException e) {
        }
        Assert.assertTrue(textAreaS.equals(fullString));
    }

    @Test(expected = NumberFormatException.class)
    public void SetConfigurationTest() throws ParseException {
            AppUI.thirdTest("-1","z","3");
    }

    @Test
    public void testingWithWrongInput(){
        AppUI.testingWithWrongInput("-1","-2","-3");
    }




    public void saveData(Path dataFilePath, String string){

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


}