package de.agbauer.physik.AreaMeanInspectorPlugin;

import com.google.common.eventbus.Subscribe;
import de.agbauer.physik.Constants;
import ij.ImagePlus;
import ij.process.ImageProcessor;
import ij.process.ImageStatistics;
import org.jdesktop.swingx.HorizontalLayout;
import org.jdesktop.swingx.VerticalLayout;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataItem;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.micromanager.Studio;
import org.micromanager.data.Datastore;
import org.micromanager.data.NewImageEvent;
import org.micromanager.display.DataViewer;
import org.micromanager.display.DisplayWindow;
import org.micromanager.display.Inspector;
import org.micromanager.display.InspectorPanel;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

import static ij.measure.Measurements.MEAN;

public class AreaMeanInspectorPanel extends InspectorPanel {
    private Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    private final Studio studio;
    private XYSeriesCollection dataset;
    private XYSeries series;
    private boolean active = false;
    private JButton toggleButton;
    private JLabel currentMeanLabel;

    AreaMeanInspectorPanel(Studio studio) {
        this.studio = studio;

        series = new XYSeries("mean");

        dataset = new XYSeriesCollection();
        dataset.addSeries(series);

        JFreeChart chart = createChart(dataset);
        ChartPanel chartPanel = new ChartPanel(chart){
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(300, 240);
            }
        };
        chartPanel.setDomainZoomable(true);
        chartPanel.setBackground(Color.white);

        this.setLayout(new VerticalLayout(1));
        add(chartPanel);
        add(initControls());
    }

    private JPanel initControls() {

        JPanel buttonPanel = new JPanel(new HorizontalLayout(1));

        toggleButton = new JButton("Start");
        JButton resetButton = new JButton("Reset");
        JButton saveButton = new JButton("Save");
        currentMeanLabel = new JLabel("Current mean: -");

        buttonPanel.add(toggleButton);
        buttonPanel.add(resetButton);
        buttonPanel.add(saveButton);
        buttonPanel.add(currentMeanLabel);

        toggleButton.addActionListener(this::toggleChart);
        resetButton.addActionListener(this::resetData);
        saveButton.addActionListener(this::saveData);

        return buttonPanel;
    }

    private void saveData(ActionEvent actionEvent) {

        final JFileChooser fc = new JFileChooser();
        int returnVal = fc.showSaveDialog(this);

        if (returnVal != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File file = fc.getSelectedFile();

        try {
            BufferedWriter br = new BufferedWriter(new FileWriter(file));

            StringBuilder sb = new StringBuilder();

            br.write("# Generated by PEEM-Controller " + Constants.version + "\n");

            for (Object element : this.series.getItems()) {
                XYDataItem dataItem = (XYDataItem) element;
                String row = String.format("%d\t%.2f\n", dataItem.getX().intValue(), dataItem.getYValue());
                sb.append(row);
            }

            br.write(sb.toString());
            br.close();

            logger.info("Saved area mean data to: " + file.getAbsolutePath());

        } catch (IOException e) {
            logger.severe("Failed saving area mean data: " + e.getLocalizedMessage());
        }
    }

    private void toggleChart(ActionEvent actionEvent) {
        active = !active;

        if (active) {
            toggleButton.setText("Stop");
        } else {
            toggleButton.setText("Start");
        }
    }


    private void resetData(ActionEvent actionEvent) {
        dataset.removeSeries(this.series);
        this.series = new XYSeries("mean");
        dataset.addSeries(series);
        currentMeanLabel.setText("Current mean: -");
    }

    private JFreeChart createChart(XYDataset dataset) {

        JFreeChart chart = ChartFactory.createXYLineChart(
                "",
                "Image #",
                "Count",
                dataset,
                PlotOrientation.VERTICAL,
                false,
                true,
                false
        );

        XYPlot plot = chart.getXYPlot();

        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();

        renderer.setSeriesPaint(0, new Color(28, 168, 220));
        renderer.setSeriesStroke(0, new BasicStroke(1.0f));

        plot.setRenderer(renderer);
        plot.setBackgroundPaint(Color.white);

        plot.setRangeGridlinesVisible(true);
        plot.setRangeGridlinePaint(Color.BLACK);

        plot.setDomainGridlinesVisible(true);
        plot.setDomainGridlinePaint(Color.BLACK);

        return chart;

    }

    public JPopupMenu getGearMenu() {
        return null;
    }

    public void setInspector(Inspector inspector) {
    }

    public boolean getIsValid(DataViewer viewer) {
        return true;
    }

    public void setDataViewer(DataViewer viewer) {
        if (viewer != null) {
            viewer.registerForEvents(this);
            viewer.getDatastore().registerForEvents(this);
        }
    }

    public boolean getGrowsVertically() {
        return true;
    }

    @Override
    public void cleanup() {

    }

    @Subscribe
    public void onNewImage(NewImageEvent event) {

        try {

            ImagePlus imagePlus = studio
                    .getSnapLiveManager()
                    .getDisplay()
                    .getImagePlus();

            ImageStatistics statistics = imagePlus.getStatistics(MEAN);
            int x = series.getItemCount();
            double y = statistics.mean;
            currentMeanLabel.setText(String.format("Current mean: %.2f", y));

            if (active) {
                series.add(x, y);
            }

        } catch (Exception e) {
            logger.warning("Area mean plugin error: " + e.getLocalizedMessage());
        }
    }
}
