package de.agbauer.physik.AreaMeanInspectorPlugin;

import org.micromanager.Studio;
import org.micromanager.display.InspectorPanel;
import org.micromanager.display.InspectorPlugin;
import org.scijava.plugin.Plugin;
import org.scijava.plugin.SciJavaPlugin;

@Plugin(type = InspectorPlugin.class)
public class AreaMeanPlugin implements InspectorPlugin, SciJavaPlugin {

    private Studio studio;

    @Override
    public InspectorPanel createPanel() {
        return new AreaMeanInspectorPanel(studio);
    }

    @Override
    public void setContext(Studio studio) {
        this.studio = studio;
    }

    @Override
    public String getName() {
        return "Area Mean";
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
}
