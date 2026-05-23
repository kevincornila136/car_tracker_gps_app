import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import java.util.List;

public class ChartWindow {

    public static void showChart(List<LocationData> route) {

        XYSeries series = new XYSeries("Speed");

        int index = 0;

        for (LocationData l : route) {
            series.add(index++, l.speed);
        }

        XYSeriesCollection dataset =
                new XYSeriesCollection(series);

        JFreeChart chart =
                ChartFactory.createXYLineChart(
                        "Speed over Time",
                        "Point",
                        "Speed",
                        dataset
                );

        JFrame frame = new JFrame("Speed Chart");

        frame.setSize(700, 400);

        frame.add(new ChartPanel(chart));

        frame.setVisible(true);
    }
}