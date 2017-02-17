package de.agbauer.physik;

import com.google.common.eventbus.Subscribe;
import org.micromanager.Studio;
import org.micromanager.data.Datastore;
import org.micromanager.data.Image;
import org.micromanager.data.NewImageEvent;
import org.micromanager.display.DataViewer;
import org.micromanager.display.Inspector;
import org.micromanager.display.InspectorPanel;

import javax.swing.*;
import java.awt.*;

/**
 * Created by dennis on 17/02/2017.
 */
public class MaxCountInspectorPanel extends InspectorPanel {
    JLabel countLabel = new JLabel("0");
    private Datastore datastore;

    public MaxCountInspectorPanel(Studio studio) {
        Font font = countLabel.getFont();
        countLabel.setFont(new Font(font.getName(), font.getStyle(), 70));
        this.add(countLabel);
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
        if (viewer == null && this.datastore != null) {
            this.datastore.unregisterForEvents(this);
        } else {
            this.datastore = viewer.getDatastore();
            this.datastore.registerForEvents(this);
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
        Image newImage = event.getImage();
        short[] pixels = (short[]) newImage.getRawPixels();

        int max = 0;
        for (short pixel: pixels) {
            max = Math.max(pixel, max);
        }

        countLabel.setText("" + max);

    }
}
