package de.agbauer.physik;

import de.agbauer.physik.GeneralInformation.GeneralInformationChangeListener;
import de.agbauer.physik.GeneralInformation.GeneralInformationController;
import de.agbauer.physik.Generic.Constants;
import de.agbauer.physik.Logging.LabelLogHandler;
import de.agbauer.physik.Logging.LogInitialiser;
import de.agbauer.physik.Observers.GeneralInformationObserver;
import de.agbauer.physik.Observers.SingleAcquisition;
import de.agbauer.physik.Observers.SingleAcquisitionObserver;
import de.agbauer.physik.OptimisationSeries.OptimisationSeriesController;
import de.agbauer.physik.PEEMCommunicator.*;
import de.agbauer.physik.PEEMHistory.PEEMHistoryController;
import de.agbauer.physik.PEEMState.PEEMStateController;
import de.agbauer.physik.QuickAcquisition.FileSaver;
import de.agbauer.physik.QuickAcquisition.QuickAcquisitionController;
import org.micromanager.MenuPlugin;
import org.micromanager.Studio;

import org.scijava.plugin.Plugin;
import org.scijava.plugin.SciJavaPlugin;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.swing.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Logger;

@SpringBootApplication
@Plugin(type = MenuPlugin.class)
public class PeemControllerApplication implements MenuPlugin, SciJavaPlugin {
    private Logger logger;
    private Studio studio;

	private MainWindow mainWindow;

    private PEEMCommunicator peemCommunicator;
    private QuickAcquisitionController quickAcquisitionController;
    private OptimisationSeriesController optimisationSeriesController;
    private GeneralInformationController generalInformationController;
    private PEEMHistoryController peemHistoryController;

    public static void main(String[] args) {
        PeemControllerApplication app = new PeemControllerApplication();
        app.onPluginSelected();
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
        initGeneralInformation();
        initOptimisationSeries();
        initQuickAcquisition();
        initPeemState();
        initPeemHistory();
        initObservers();
    }

    private void initPeemHistory() {
        peemHistoryController = new PEEMHistoryController(mainWindow.peemHistoryForm);
    }

    private void initGeneralInformation() {
        generalInformationController = new GeneralInformationController(mainWindow.generalInformationForm);
    }

    private void initObservers() {

        GeneralInformationObserver generalInformationObserver = new GeneralInformationObserver(new GeneralInformationChangeListener[]{
                peemHistoryController, optimisationSeriesController, quickAcquisitionController
        });

        generalInformationController.addObserver(generalInformationObserver);
        generalInformationController.notifyObservers();

        SingleAcquisitionObserver singleAcquisitionObserver = new SingleAcquisitionObserver(new SingleAcquisition[]{
                peemHistoryController
        });

        quickAcquisitionController.addObserver(singleAcquisitionObserver);
    }

    private void initLogManager() {
        LabelLogHandler labelLogHandler = new LabelLogHandler(mainWindow.statusBarLabel);
        new LogInitialiser(labelLogHandler);

        logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    }

    private void initPEEMConnection() {
        if (!Constants.peemConnected) {
            peemCommunicator = new PEEMCommunicatorDummy();
            return;
        }

        SerialConnectionHandler rxTxConnectionHandler = new RxTxConnectionHandler();

        try {
            rxTxConnectionHandler.connectTo(Constants.defaultPort);

            peemCommunicator = getPeemCommunicatorFromSerialConnection(rxTxConnectionHandler);
        } catch (IOException e) {
            String errorMessage = "Could not connect to default port '" + Constants.defaultPort + "': " + e.getMessage();

            logger.severe(errorMessage);
            JOptionPane.showConfirmDialog(null, errorMessage, "Port connection error", JOptionPane.OK_OPTION);
            System.exit(1);
        }

    }

    private void initOptimisationSeries() {
        optimisationSeriesController = new OptimisationSeriesController(studio, peemCommunicator, mainWindow.optimisationSeriesForm);
    }

    private void initQuickAcquisition() {
        FileSaver fileSaver = new FileSaver(peemCommunicator);
        quickAcquisitionController = new QuickAcquisitionController(studio, fileSaver, mainWindow.quickAcquisitionForm);
    }

    private void initPeemState() {
        new PEEMStateController(peemCommunicator, mainWindow.peemStatePanel);
    }

    private PEEMCommunicator getPeemCommunicatorFromSerialConnection(SerialConnectionHandler connectionHandler) throws IOException {
        InputStream inputStream = connectionHandler.getInputStream();
        OutputStream outputStream = connectionHandler.getOutputStream();
        return new PEEMCommunicator(inputStream, outputStream);
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
		return Constants.version;
	}

	@Override
	public String getCopyright() {
		return "Christian-Albrechts-Universität zu Kiel, Germany, 2017. Author: Dennis Trautwein";
	}
}
