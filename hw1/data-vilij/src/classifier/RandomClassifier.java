package classifier;
import algorithms.Classifier;
import data.DataSet;
import dataprocessors.AppData;
import dataprocessors.TSDProcessor;
import javafx.application.Platform;
import javafx.scene.chart.XYChart;
import javafx.scene.shape.Rectangle;
import ui.AppUI;
import vilij.templates.ApplicationTemplate;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Ritwik Banerjee
 */
public class RandomClassifier extends Classifier {
    private static final Random RAND = new Random();

    @SuppressWarnings("FieldCanBeLocal")
    // this mock classifier doesn't actually use the data, but a real classifier will
    //private DataSet dataset;
    private static XYChart.Series<Number, Number> randomSeries = new XYChart.Series<Number, Number>();
    private ApplicationTemplate applicationTemplate;
    private final int maxIterations;
    private final int updateInterval;
    private double minY;
    private double maxY;
    private XYChart.Data dataA;
    private XYChart.Data dataB;
    private DataSet dataset;
    //  private Object lock = new Object();

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
                            boolean tocontinue,
                            ApplicationTemplate applicationTemplate) {
        this.dataset = dataset;
        this.maxIterations = maxIterations;
        this.updateInterval = updateInterval;
        this.tocontinue = new AtomicBoolean(tocontinue);
        this.applicationTemplate = applicationTemplate;
    }

    @Override
    public void run() {
        Platform.runLater(this::setChartLine);
        for (int i = 1; i <= maxIterations && tocontinue(); i++) {
            ((AppUI)applicationTemplate.getUIComponent()).getScrnshotButton().setDisable(true);
            int xCoefficient =  new Long(-1 * Math.round((2 * RAND.nextDouble() - 1) * 10)).intValue();
            int yCoefficient = 10;
            int constant     = RAND.nextInt(11);

            // this is the real output of the classifier
            output = Arrays.asList(xCoefficient, yCoefficient, constant);
            // everything below is just for internal viewing of how the output is changing
            // in the final project, such changes will be dynamically visible in the UI
            if (i % updateInterval == 0) {
                System.out.printf("Iteration number %d: ", i);
                Platform.runLater(this::setYValues);
                flush();
            }
            if (i > maxIterations * .6 && RAND.nextDouble() < 0.05) {
                System.out.printf("Iteration number %d: ", i);
                flush();
                Platform.runLater(this::setYValues);
                ((AppUI)applicationTemplate.getUIComponent()).getScrnshotButton().setDisable(false);
                ((AppUI)applicationTemplate.getUIComponent()).setAlgoRunning(false);
                ((AppUI)applicationTemplate.getUIComponent()).setStartThread(false);
                break;
            }
            try {
                Thread.sleep(500);
            } catch (Exception ex) {

            }
        }
        if (!tocontinue()) {
            ((AppUI)applicationTemplate.getUIComponent()).getScrnshotButton().setDisable(true);
            for (int i = 1; i <= maxIterations; i++) {
                int xCoefficient =  new Long(-1 * Math.round((2 * RAND.nextDouble() - 1) * 10)).intValue();
                int yCoefficient = 10;
                int constant     = RAND.nextInt(11);

                output = Arrays.asList(xCoefficient, yCoefficient, constant);
                if (i % updateInterval == 0) {
                    System.out.printf("Iteration number %d: ", i);
                    Platform.runLater(this::setYValues);
                    flush();
                    synchronized (this) {
                        try {
                            ((AppUI)applicationTemplate.getUIComponent()).getScrnshotButton().setDisable(false);
                            ((AppUI)applicationTemplate.getUIComponent()).getRun().setDisable(false);
                            this.wait();
                        } catch (InterruptedException e) {
                        }
                    }
                }
                if (i > maxIterations * .6 && RAND.nextDouble() < 0.05) {
                    System.out.printf("Iteration number %d: ", i);
                    Platform.runLater(this::setYValues);
                    flush();
                    break;
                }
              /*  synchronized (lock) {
                    try {
                        lock.wait(); // Will block until lock.notify() is called on another thread.
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } */

            }
        }
        ((AppUI)applicationTemplate.getUIComponent()).setStartThread(false);
        ((AppUI)applicationTemplate.getUIComponent()).getScrnshotButton().setDisable(false);
        ((AppUI)applicationTemplate.getUIComponent()).getRun().setDisable(false);
    }

    // for internal viewing only
    protected void flush() {
        System.out.printf("%d\t%d\t%d%n", output.get(0), output.get(1), output.get(2));
    }

    /**
     * A placeholder main method to just make sure this code runs smoothly
     */
   /* public static void main(String... args) throws IOException {
        DataSet dataset = DataSet.fromTSDFile(Paths.get("/path/to/some-data.tsd"));
        RandomClassifier classifier = new RandomClassifier(dataset, 100, 5, true);
        classifier.run(); // no multithreading yet
    } */

    public void setChartLine() {
        TSDProcessor processor = ((AppData)applicationTemplate.getDataComponent()).getProcessor();
        randomSeries = new XYChart.Series<Number, Number>();
        randomSeries.setName("Random Line");
        dataA = new XYChart.Data(processor.getMinX(),minY);
        dataB = new XYChart.Data(processor.getMaxX(), maxY);
        Rectangle rect = new Rectangle(0, 0);
        rect.setVisible(false);
        Rectangle rectangle = new Rectangle(0, 0);
        rectangle.setVisible(false);
        dataA.setNode(rect);
        dataB.setNode(rectangle);
        randomSeries.getData().addAll(dataA, dataB);
        ((AppUI)applicationTemplate.getUIComponent()).getChart().getData().add(randomSeries);
    }

    public void setYValues(){
        TSDProcessor processor = ((AppData) applicationTemplate.getDataComponent()).getProcessor();;
         minY = (-output.get(2)-(processor.getMinX()*output.get(0)))/output.get(1);
         maxY = (-output.get(2)-(processor.getMaxX()*output.get(0)))/output.get(1);
         dataA.setYValue(minY);
         dataB.setYValue(maxY);

    }

    public static void clearSeries(){
        randomSeries.getData().clear();
    }
}
//package classifier;
//
//import algorithms.Classifier;
//import data.DataSet;
//import dataprocessors.AppData;
//import dataprocessors.TSDProcessor;
//import javafx.application.Platform;
//import javafx.scene.chart.XYChart;
//import ui.AppUI;
//import vilij.templates.ApplicationTemplate;
//
//import java.io.IOException;
//import java.lang.management.PlatformLoggingMXBean;
//import java.nio.file.Paths;
//import java.util.Arrays;
//import java.util.Random;
//import java.util.concurrent.atomic.AtomicBoolean;
//
///**
// * @author Ritwik Banerjee
// */
//public class RandomClassifier extends Classifier {
//
//    private static final Random RAND = new Random();
//    private ApplicationTemplate applicationTemplate;
//
//    @SuppressWarnings("FieldCanBeLocal")
//    // this mock classifier doesn't actually use the data, but a real classifier will
//    private DataSet dataset;
//
//    private final int maxIterations;
//    private final int updateInterval;
//    private double minY;
//    private double maxY;
//    private XYChart.Data dataA;
//    private XYChart.Data dataB;
//    private static int counter=0;
//
//    // currently, this value does not change after instantiation
//    private final AtomicBoolean tocontinue;
//
//    @Override
//    public int getMaxIterations() {
//        return maxIterations;
//    }
//
//    @Override
//    public int getUpdateInterval() {
//        return updateInterval;
//    }
//
//    @Override
//    public boolean tocontinue() {
//        return tocontinue.get();
//    }
//
//    public RandomClassifier(DataSet dataset,
//                            int maxIterations,
//                            int updateInterval,
//                            boolean tocontinue, ApplicationTemplate template) {
//        this.dataset = dataset;
//        this.maxIterations = maxIterations;
//        this.updateInterval = updateInterval;
//        this.tocontinue = new AtomicBoolean(tocontinue);
//        this.applicationTemplate = template;
//    }
//
//    @Override
//    public void run() {
//        Platform.runLater(this::setChart);
//        for (int i = 1; i <= maxIterations && tocontinue(); i++) {
//            int xCoefficient =  new Long(-1 * Math.round((2 * RAND.nextDouble() - 1) * 10)).intValue();
//            int yCoefficient = 10;
//            int constant     = RAND.nextInt(11);
//
//            // this is the real output of the classifier
//            output = Arrays.asList(xCoefficient, yCoefficient, constant);
//
//            // everything below is just for internal viewing of how the output is changing
//            // in the final project, such changes will be dynamically visible in the UI
//            if (i % updateInterval == 0) {
//                System.out.printf("Iteration number %d: ", i);
//                Platform.runLater(this::setYvalues);
//                flush();
//            }
//            if (i > maxIterations * .6 && RAND.nextDouble() < 0.05) {
//                System.out.printf("Iteration number %d: ", i);
//                flush();
//                break;
//            }
//            try {
//                Thread.sleep(750);
//            } catch (InterruptedException e) {
//            }
//        }
//
//        if(!tocontinue()){
//            int xCoefficient =  new Long(-1 * Math.round((2 * RAND.nextDouble() - 1) * 10)).intValue();
//            int yCoefficient = 10;
//            int constant     = RAND.nextInt(11);
//
//            // this is the real output of the classifier
//            output = Arrays.asList(xCoefficient, yCoefficient, constant);
//
//            if (counter % updateInterval == 0 && counter<=maxIterations) {
//                System.out.printf("Iteration number %d: ", counter);
//                Platform.runLater(this::setYvalues);
//                flush();
//            }
//            if (counter > maxIterations) {
//                System.out.printf("Iteration number %d: ", counter);
//                flush();
//                ((AppUI) applicationTemplate.getUIComponent()).getRun().setDisable(true);
//            }
//            counter+=updateInterval;
//        }
//    }
//
//    // for internal viewing only
//    protected void flush() {
//        System.out.printf("%d\t%d\t%d%n", output.get(0), output.get(1), output.get(2));
//    }
//
//    public void setYvalues(){
//        TSDProcessor processor = ((AppData) applicationTemplate.getDataComponent()).getProcessor();;
//         minY = (-output.get(2)-(processor.getMinX()*output.get(0)))/output.get(1);
//         maxY = (-output.get(2)-(processor.getMaxX()*output.get(0)))/output.get(1);
//         dataA.setYValue(minY);
//         dataB.setYValue(maxY);
//
//    }
//
//    public void setChart(){
//        TSDProcessor processor = ((AppData) applicationTemplate.getDataComponent()).getProcessor();
//        XYChart.Series<Number, Number> series1 = new XYChart.Series<>();
//        series1.setName("Line");
//        dataA = new XYChart.Data(processor.getMinX(),minY);
//        dataB = new XYChart.Data(processor.getMaxX(), maxY);
//        series1.getData().addAll(dataA,dataB);
//
//        ((AppUI) applicationTemplate.getUIComponent()).getChart().getData().add(series1);
//    }
//
//
//    /** A placeholder main method to just make sure this code runs smoothly */
//    public static void main(String... args) throws IOException {
//        DataSet          dataset    = DataSet.fromTSDFile(Paths.get("/path/to/some-data.tsd"));
//        //RandomClassifier classifier = new RandomClassifier(dataset, 100, 5, true);
//        //classifier.run(); // no multithreading yet
//    }
//}
