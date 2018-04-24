package classifier;

import algorithms.Classifier;
import data.DataSet;
import dataprocessors.AppData;
import dataprocessors.TSDProcessor;
import javafx.application.Platform;
import javafx.scene.chart.XYChart;
import ui.AppUI;
import vilij.templates.ApplicationTemplate;

import java.io.IOException;
import java.lang.management.PlatformLoggingMXBean;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Ritwik Banerjee
 */
public class RandomClassifier extends Classifier {

    private static final Random RAND = new Random();
    private ApplicationTemplate applicationTemplate;

    @SuppressWarnings("FieldCanBeLocal")
    // this mock classifier doesn't actually use the data, but a real classifier will
    private DataSet dataset;

    private final int maxIterations;
    private final int updateInterval;
    private double minY;
    private double maxY;
    private XYChart.Data dataA;
    private XYChart.Data dataB;

    // currently, this value does not change after instantiation
    private final AtomicBoolean tocontinue;

    @Override
    public int getMaxIterations() {
        return maxIterations;
    }

    @Override
    public int getUpdateInterval() {
        return updateInterval;
    }

    @Override
    public boolean tocontinue() {
        return tocontinue.get();
    }

    public RandomClassifier(DataSet dataset,
                            int maxIterations,
                            int updateInterval,
                            boolean tocontinue, ApplicationTemplate template) {
        this.dataset = dataset;
        this.maxIterations = maxIterations;
        this.updateInterval = updateInterval;
        this.tocontinue = new AtomicBoolean(tocontinue);
        this.applicationTemplate = template;
    }

    @Override
    public void run() {
        Platform.runLater(this::setChart);
        for (int i = 1; i <= maxIterations && tocontinue(); i++) {
            int xCoefficient = new Double(RAND.nextDouble() * 100).intValue();
            int yCoefficient = new Double(RAND.nextDouble() * 100).intValue();
            int constant     = new Double(RAND.nextDouble() * 100).intValue();

            // this is the real output of the classifier
            output = Arrays.asList(xCoefficient, yCoefficient, constant);

            // everything below is just for internal viewing of how the output is changing
            // in the final project, such changes will be dynamically visible in the UI
            if (i % updateInterval == 0) {
                System.out.printf("Iteration number %d: ", i);
                Platform.runLater(this::setYvalues);
                flush();
            }
            if (i > maxIterations * .6 && RAND.nextDouble() < 0.05) {
                System.out.printf("Iteration number %d: ", i);
                flush();
                break;
            }
            try {
                Thread.sleep(750);
            } catch (InterruptedException e) {
            }
        }

        if(!tocontinue()){
            int xCoefficient = new Double(RAND.nextDouble() * 100).intValue();
            int yCoefficient = new Double(RAND.nextDouble() * 100).intValue();
            int constant     = new Double(RAND.nextDouble() * 100).intValue();

            // this is the real output of the classifier
            output = Arrays.asList(xCoefficient, yCoefficient, constant);

            // everything below is just for internal viewing of how the output is changing
            // in the final project, such changes will be dynamically visible in the UI
        }
    }

    // for internal viewing only
    protected void flush() {
        System.out.printf("%d\t%d\t%d%n", output.get(0), output.get(1), output.get(2));
    }

    public void setYvalues(){
        TSDProcessor processor = ((AppData) applicationTemplate.getDataComponent()).getProcessor();;
         minY = (-output.get(2)-(processor.getMinX()*output.get(0)))/output.get(1);
         maxY = (-output.get(2)-(processor.getMaxX()*output.get(0)))/output.get(1);
         dataA.setYValue(minY);
         dataB.setYValue(maxY);

    }

    public void setChart(){
        TSDProcessor processor = ((AppData) applicationTemplate.getDataComponent()).getProcessor();
        XYChart.Series<Number, Number> series1 = new XYChart.Series<>();
        series1.setName("Line");
        dataA = new XYChart.Data(processor.getMinX(),minY);
        dataB = new XYChart.Data(processor.getMaxX(), maxY);
        series1.getData().addAll(dataA,dataB);

        ((AppUI) applicationTemplate.getUIComponent()).getChart().getData().add(series1);
    }


    /** A placeholder main method to just make sure this code runs smoothly */
    public static void main(String... args) throws IOException {
        DataSet          dataset    = DataSet.fromTSDFile(Paths.get("/path/to/some-data.tsd"));
        //RandomClassifier classifier = new RandomClassifier(dataset, 100, 5, true);
        //classifier.run(); // no multithreading yet
    }
}
