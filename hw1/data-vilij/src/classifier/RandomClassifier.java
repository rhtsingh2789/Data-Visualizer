package classifier;
import algorithms.Classifier;
import data.DataSet;
import dataprocessors.AppData;
import dataprocessors.TSDProcessor;
import javafx.application.Platform;
import javafx.scene.chart.XYChart;
import javafx.scene.shape.Rectangle;
import ui.AppUI;
import vilij.components.ErrorDialog;
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
    private static XYChart.Series<Number, Number> series = new XYChart.Series<Number, Number>();
    private ApplicationTemplate applicationTemplate;
    private final int maxIterations;
    private final int updateInterval;
    private double minY;
    private double maxY;
    private XYChart.Data dataA;
    private XYChart.Data dataB;
    private DataSet dataset;
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
            int xCoefficient =  new Long(-1 * Math.round((2 * RAND.nextDouble() - 1) * 10)).intValue();
            int yCoefficient = 10;
            int constant     = RAND.nextInt(11);

            output = Arrays.asList(xCoefficient, yCoefficient, constant);
            if (i % updateInterval == 0) {
                System.out.printf("Iteration number %d: ", i);
                Platform.runLater(this::setYValues);
                flush();
            }
            if (i > maxIterations * .6 && RAND.nextDouble() < 0.05) {
                System.out.printf("Iteration number %d: ", i);
                Platform.runLater(this::setYValues);
                flush();
                ((AppUI)applicationTemplate.getUIComponent()).getScrnshotButton().setDisable(false);
                ((AppUI)applicationTemplate.getUIComponent()).setAlgoRunning(false);
                ((AppUI)applicationTemplate.getUIComponent()).setStartThread(false);
                break;
            }
            try {
                Thread.sleep(650);
            } catch (Exception ex) {

            }
        }
        if (!tocontinue()) {
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

            }
        }
        ((AppUI)applicationTemplate.getUIComponent()).getScrnshotButton().setDisable(false);
        ((AppUI)applicationTemplate.getUIComponent()).setAlgoRunning(false);
        ((AppUI)applicationTemplate.getUIComponent()).setStartThread(false);
        ((AppUI)applicationTemplate.getUIComponent()).getRun().setDisable(false);
    }

    protected void flush() {
        System.out.printf("%d\t%d\t%d%n", output.get(0), output.get(1), output.get(2));
    }

    public void setChartLine() {
        TSDProcessor processor = ((AppData)applicationTemplate.getDataComponent()).getProcessor();
        series = new XYChart.Series<Number, Number>();
        series.setName("Algo Line");
        dataA = new XYChart.Data(processor.getMinX(),minY);
        dataB = new XYChart.Data(processor.getMaxX(), maxY);
        Rectangle square = new Rectangle(0, 0);
        Rectangle wrekkttt = new Rectangle(0, 0);
        dataA.setNode(square);
        dataB.setNode(wrekkttt);
        series.getData().addAll(dataA, dataB);
        ((AppUI)applicationTemplate.getUIComponent()).getChart().getData().add(series);
    }

    public void setYValues(){
        TSDProcessor processor = ((AppData) applicationTemplate.getDataComponent()).getProcessor();;
         minY = (-output.get(2)-(processor.getMinX()*output.get(0)))/output.get(1);
         maxY = (-output.get(2)-(processor.getMaxX()*output.get(0)))/output.get(1);
         dataA.setYValue(minY);
         dataB.setYValue(maxY);

    }

    public static void clearSeries(){
        series.getData().clear();
    }
}
