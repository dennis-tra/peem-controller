package de.agbauer.physik;

import org.micromanager.MenuPlugin;
import org.micromanager.Studio;

import org.scijava.plugin.Plugin;
import org.scijava.plugin.SciJavaPlugin;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Plugin(type = MenuPlugin.class)
public class PeemControllerApplication implements MenuPlugin, SciJavaPlugin {

    @Override
    public String getSubMenu() {
        return "";
    }

    @Override
    public void onPluginSelected() {

    }

	@Override
	public void setContext(Studio studio) {
        System.out.println("Hello");

	}

	@Override
	public String getName() {
		return "PEEM Controller";
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
		return null;
	}
}
