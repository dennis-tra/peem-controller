package de.agbauer.physik;

import de.agbauer.physik.Generic.Constants;
import de.agbauer.physik.Generic.LogManager;
import de.agbauer.physik.OptimisationSeries.OptimisationSeriesController;
import de.agbauer.physik.PEEMCommunicator.PEEMCommunicator;
import de.agbauer.physik.PEEMCommunicator.PEEMCommunicatorDummy;
import de.agbauer.physik.PEEMCommunicator.RxTxConnectionHandler;
import de.agbauer.physik.PEEMCommunicator.SerialConnectionHandler;
import org.micromanager.MenuPlugin;
import org.micromanager.Studio;

import org.scijava.plugin.Plugin;
import org.scijava.plugin.SciJavaPlugin;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

@SpringBootApplication
@Plugin(type = MenuPlugin.class)
public class PeemControllerApplication implements MenuPlugin, SciJavaPlugin {

    private Studio studio;

	private MainWindow mainWindow;
    private LogManager logManager;

    private PEEMCommunicator peemCommunicator;
    private OptimisationSeriesController optimisationSeriesController;

	public static void main(String[] args) {
		SpringApplication.run(PeemControllerApplication.class, args);
	}

    @Override
    public String getSubMenu() {
        return "";
    }

    @Override
    public void onPluginSelected() {
		mainWindow = new MainWindow();

        initLogManager();
        initPEEMConnection();
        initOptimisationSeries();

    }

    private void initLogManager() {
        logManager = new LogManager(studio.getLogManager(), mainWindow.statusBarLabel);
        logManager.inform("Start PEEM controller plugin", false, true);
    }

    private void initPEEMConnection() {
        if (Constants.peemConnected) {
            SerialConnectionHandler rxTxConnectionHandler = new RxTxConnectionHandler(logManager);
            try {
                rxTxConnectionHandler.connectTo(Constants.defaultPort);

                InputStream inputStream = rxTxConnectionHandler.getInputStream();
                OutputStream outputStream = rxTxConnectionHandler.getOutputStream();
                peemCommunicator = new PEEMCommunicatorDummy(inputStream, outputStream, logManager);
            } catch (IOException e) {
                logManager.showDialog("Could not connect to default port '" + Constants.defaultPort + "': " + e.getMessage());
                e.printStackTrace();
                System.exit(1);
            }
        } else {
            peemCommunicator = new PEEMCommunicatorDummy(null, null, logManager);
        }

    }

    private void initOptimisationSeries() {
        optimisationSeriesController = new OptimisationSeriesController(studio, peemCommunicator, logManager, mainWindow.optimisationSeriesForm);

    }

	@Override
	public void setContext(Studio studio) {
		this.studio = studio;

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
		return "Christian-Albrechts-Universität zu Kiel, Germany, 2017. Author: Dennis Trautwein";
	}
}
