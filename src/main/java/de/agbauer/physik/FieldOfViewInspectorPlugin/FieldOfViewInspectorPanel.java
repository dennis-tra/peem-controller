package de.agbauer.physik.FieldOfViewInspectorPlugin;

import de.agbauer.physik.QuickAcquisition.AcquisitionParameters.PeemVoltages;
import org.micromanager.display.Inspector;
import org.micromanager.display.InspectorPanel;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

public class FieldOfViewInspectorPanel extends InspectorPanel {
    private JLabel fovLabel = new JLabel("-");
    private JLabel extLabel = new JLabel("ext: -");
    private JLabel p1Label = new JLabel("p1: -");
    private JLabel p2Label = new JLabel("p2: -");

    FieldOfViewInspectorPanel() {


        BorderLayout borderLayout = new BorderLayout();
        this.setLayout(borderLayout);

        Font font = fovLabel.getFont();
        Font smallFont = new Font(font.getName(), font.getStyle(), 12);

        GridLayout gridLayout = new GridLayout(3,1);
        JPanel verticalPanel = new JPanel(gridLayout);

        Border leftPadding = BorderFactory.createEmptyBorder(0, 20, 0, 0);
        Border rightPadding = BorderFactory.createEmptyBorder(10, 0, 10, 20);

        this.fovLabel.setBorder(leftPadding);

        this.extLabel.setBorder(rightPadding);
        this.p1Label.setBorder(rightPadding);
        this.p2Label.setBorder(rightPadding);

        this.extLabel.setForeground(Color.GRAY);
        this.p1Label.setForeground(Color.GRAY);
        this.p2Label.setForeground(Color.GRAY);

        this.extLabel.setFont(smallFont);
        this.p1Label.setFont(smallFont);
        this.p2Label.setFont(smallFont);

        verticalPanel.add(this.extLabel);
        verticalPanel.add(this.p1Label);
        verticalPanel.add(this.p2Label);


        this.add(verticalPanel, BorderLayout.LINE_END);

        fovLabel.setFont(new Font(font.getName(), font.getStyle(), 48));
        this.add(fovLabel, BorderLayout.LINE_START);

    }

    public JPopupMenu getGearMenu() {
        return null;
    }

    public void setInspector(Inspector inspector) {
    }


    public boolean getGrowsVertically() {
        return true;
    }

    @Override
    public void cleanup() {

    }

    void peemVoltagesUpdated(PeemVoltages peemVoltages) {

        if (peemVoltages == null) {
            fovLabel.setText("-");
            extLabel.setText("ext: -");
            p1Label.setText("p1: -");
            p2Label.setText("p2: -");
            return;
        }

        double p1 = peemVoltages.projective1;
        double p2 = peemVoltages.projective2;
        double ext= peemVoltages.extractor;

        double fov = FieldOfViewEstimator.estimateFieldOfView(p1, p2, ext);

        if (fov < 0 ) {
            fovLabel.setText("-");
        } else {
            fovLabel.setText(String.format("%.1f µm", fov));
        }

        extLabel.setText(String.format("ext: %.1f V", ext));
        p1Label.setText(String.format("p1: %.1f V", p1));
        p2Label.setText(String.format("p2: %.1f V", p2));

    }
}
