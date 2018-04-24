package dataprocessors;

import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Tooltip;
import vilij.components.ErrorDialog;

import java.awt.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;

/**
 * The data files used by this data visualization applications follow a tab-separated format, where each data point is
 * named, labeled, and has a specific location in the 2-dimensional X-Y plane. This class handles the parsing and
 * processing of such data. It also handles exporting the data to a 2-D plot.
 * <p>
 * A sample file in this format has been provided in the application's <code>resources/data</code> folder.
 *
 * @author Rohit Singh
 * @see XYChart
 */
public final class TSDProcessor {

    public static class InvalidDataNameException extends Exception {

        private static final String NAME_ERROR_MSG = "All data instance names must start with the @ character.";

        public InvalidDataNameException(String name) {
            super(String.format("Invalid name '%s'." + NAME_ERROR_MSG, name));
        }
    }

    private  static Map<String, String>  dataLabels;
    private  static Map<String, Point2D> dataPoints;
    private HashSet<String> names = new HashSet<>();
    static int counter=0;
    static int labelCounter, instanceCounter;
    private static HashSet<String> labels;

    static double Yvalues;

    static double minX=0;
    static double maxX;
    static double average;

    public  double getMaxX() {
        return maxX;
    }
    public  double getMinX() {
        return minX;
    }
    public TSDProcessor() {
        dataLabels = new HashMap<>();
        dataPoints = new HashMap<>();
    }

    /**
     * Processes the data and populated two {@link Map} objects with the data.
     *
     * @param tsdString the input data provided as a single {@link String}
     * @throws Exception if the input string does not follow the <code>.tsd</code> data format
     */
    public void processString(String tsdString) throws Exception {
        labels = new HashSet<String>();
        counter = 0;
        labelCounter = 0;
        instanceCounter = 0;
        Yvalues = 0;
        AtomicBoolean hadAnError   = new AtomicBoolean(false);
        StringBuilder errorMessage = new StringBuilder();
        Stream.of(tsdString.split("\n"))
              .map(line -> Arrays.asList(line.split("\t")))
              .forEach(list -> {
                  try {
                      String   name  = checkedname(list.get(0));
                      String   label = list.get(1);
                      String[] pair  = list.get(2).split(",");
                      Point2D  point = new Point2D(Double.parseDouble(pair[0]), Double.parseDouble(pair[1]));
                      if(labels.contains(label) == false && !label.equals("null")){
                          labels.add(label);
                          labelCounter++;
                      }
                      dataLabels.put(name, label);
                      dataPoints.put(name, point);
                      counter++;
                  } catch (Exception e) {
                      errorMessage.setLength(0);
                      errorMessage.append(e.getClass().getSimpleName()).append(": ").append(e.getMessage());
                      hadAnError.set(true);
                  }
              });
        if (errorMessage.length() > 0) {
            ErrorDialog.getDialog().show("BAD INPUT", "Please make sure that the input is correct at line " + (counter+1));
            throw new Exception(errorMessage.toString());
        }
    }

//    public void textAreaLines(String tsdString){
//        counter = 0;
//       String[] strings =  tsdString.split("\n");
//       for(String s: strings){
//           counter++;
//           if(counter == 10){
//               break;
//           }
//           else{
//               textAreaString=s+"\n";
//           }
//       }
//    }

    /**
     * Exports the data to the specified 2-D chart.
     *
     * @param chart the specified chart
     */
    void toChartData(XYChart<Number, Number> chart) {
        Set<String> labels = new HashSet<>(dataLabels.values());
        Yvalues = 0;
        minX=0;
        maxX=0;
        for (String label : labels) {
            XYChart.Series<Number, Number> series = new XYChart.Series<>();
            series.setName(label);
            dataLabels.entrySet().stream().filter(entry -> entry.getValue().equals(label)).forEach(entry -> {
                Point2D point = dataPoints.get(entry.getKey());
                XYChart.Data<Number, Number> points = new XYChart.Data<>(point.getX(), point.getY());
                series.getData().add(points);
                Yvalues+= point.getY();
                counter++;
                if(minX==0) {
                    minX = point.getX();
                }
                if(point.getX()>maxX) {
                    maxX = point.getX();
                }
                if(point.getX()<minX) {
                    minX = point.getX();
                }
            });
            chart.getData().add(series);
            series.getNode().setStyle("-fx-stroke: transparent");
            for (XYChart.Series<Number, Number> s : chart.getData()) {
                for (XYChart.Data<Number, Number> d : s.getData()) {
                    Tooltip tooltip = new Tooltip();
                    Point2D point = new Point2D((double) d.getXValue(), (double) d.getYValue());
                    Tooltip.install(d.getNode(), tooltip);
                    tooltip.setText(getKeyFromValue(dataPoints,point));
                    d.getNode().setCursor(Cursor.CROSSHAIR);
                }
            }
        }
//        average=(Yvalues/instanceCounter);
//        XYChart.Series<Number, Number> series1 = new XYChart.Series<>();
//        series1.setName("Average");
//        //Rectangle rectangle = new Rectangle();
//        series1.getData().add(new XYChart.Data<>(minX, average));
//        series1.getData().add(new XYChart.Data<>(maxX, average));
//
//        chart.getData().add(series1);
    }

    private String getKeyFromValue(Map<String, Point2D> dataPoints, Point2D point) {
        for (Object o : dataPoints.keySet()) {
            if (dataPoints.get(o).equals(point)) {
                return o.toString();
            }
        }
        return null;
    }

    void clear() {
        dataPoints.clear();
        dataLabels.clear();
    }

    private String checkedname(String name) throws InvalidDataNameException {
        if (!name.startsWith("@") || names.contains(name)) {
            throw new InvalidDataNameException(name);
        }
        else {
            names.add(name);
        }
        instanceCounter++;
        return name;
    }

    public int getLabelCounter(){
        return labelCounter;
    }
    public int getInstanceCounter(){
        return instanceCounter;
    }

    public HashSet<String> getLabels() {
        return labels;
    }
}
