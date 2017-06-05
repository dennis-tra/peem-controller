package de.agbauer.physik.FieldOfViewInspectorPlugin;

import de.agbauer.physik.Observers.AcquisitionParamsLoadListener;
import de.agbauer.physik.QuickAcquisition.AcquisitionParameters.PeemVoltages;
import org.micromanager.Studio;
import org.micromanager.display.InspectorPanel;
import org.micromanager.display.InspectorPlugin;
import org.scijava.plugin.Plugin;
import org.scijava.plugin.SciJavaPlugin;

import java.util.Observable;

@Plugin(type = InspectorPlugin.class)
public class FieldOfViewPlugin implements InspectorPlugin, SciJavaPlugin, AcquisitionParamsLoadListener {

    private FieldOfViewInspectorPanel fieldOfViewInspectorPanel;

    @Override
    public InspectorPanel createPanel() {
        fieldOfViewInspectorPanel = new FieldOfViewInspectorPanel();
        return fieldOfViewInspectorPanel;
    }

    @Override
    public void setContext(Studio studio) {

    }

    @Override
    public String getName() {
        return "Field Of View";
    }

    @Override
    public String getHelpText() {
        return null;
    }

    @Override
    public String getVersion() {
        return "v0.0.1";
    }

    @Override
    public String getCopyright() {
        return "Christian-Albrechts-Universität zu Kiel, Germany, 2017. Author: Dennis Trautwein";
    }

    @Override
    public void peemVoltagesUpdated(Observable sender, PeemVoltages peemVoltages) {
        fieldOfViewInspectorPanel.peemVoltagesUpdated(peemVoltages);
    }
}
