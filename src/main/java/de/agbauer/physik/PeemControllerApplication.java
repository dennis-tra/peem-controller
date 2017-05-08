package de.agbauer.physik;

import de.agbauer.physik.Generic.PeemControllerWindowListener;
import de.agbauer.physik.Observers.SampleNameChangeListener;
import de.agbauer.physik.GeneralInformation.GeneralInformationController;
import de.agbauer.physik.Generic.Constants;
import de.agbauer.physik.Logging.LabelLogger;
import de.agbauer.physik.Logging.LogInitialiser;
import de.agbauer.physik.Observers.SampleNameObserver;
import de.agbauer.physik.Observers.DataSaveListeners;
import de.agbauer.physik.Observers.NewDataSavedObserver;
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
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Logger;

@SpringBootApplication
@Plugin(type = MenuPlugin.class)
public class PeemControllerApplication implements MenuPlugin, SciJavaPlugin {
    private Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);;
    private Studio studio;

	private MainWindow mainWindow;

    private PEEMCommunicator peemCommunicator;
    private QuickAcquisitionController quickAcquisitionController;
    private OptimisationSeriesController optimisationSeriesController;
    private GeneralInformationController generalInformationController;
    private PEEMHistoryController peemHistoryController;
    private PEEMStateController peemStateController;
    private RxTxConnectionHandler rxTxConnectionHandler;

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
        initWindowListener();
    }

    private void initPeemHistory() {
        peemHistoryController = new PEEMHistoryController(mainWindow.peemHistoryForm);
    }

    private void initGeneralInformation() {
        generalInformationController = new GeneralInformationController(mainWindow.generalInformationForm);
    }

    private void initObservers() {

        SampleNameObserver sampleNameObserver = new SampleNameObserver(new SampleNameChangeListener[]{
                peemHistoryController, optimisationSeriesController, quickAcquisitionController, peemStateController
        });

        generalInformationController.addObserver(sampleNameObserver);
        generalInformationController.notifyObservers();


        NewDataSavedObserver singleAcquisitionObserver = new NewDataSavedObserver(new DataSaveListeners[]{
                peemHistoryController
        });

        quickAcquisitionController.addObserver(singleAcquisitionObserver);
        peemStateController.addObserver(singleAcquisitionObserver);
    }

    private void initLogManager() {
        LabelLogger labelLogHandler = new LabelLogger(mainWindow.statusBarLabel);
        new LogInitialiser(labelLogHandler);
    }

    private void initPEEMConnection() {
        if (!Constants.peemConnected) {
            peemCommunicator = new PEEMCommunicatorDummy();
            return;
        }

        rxTxConnectionHandler = new RxTxConnectionHandler();

        try {
            rxTxConnectionHandler.connectTo(Constants.defaultPort);

            peemCommunicator = getPeemCommunicatorFromSerialConnection(rxTxConnectionHandler);
        } catch (IOException e) {
            String errorMessage = "Could not connect to default port '" + Constants.defaultPort + "': " + e.getMessage();

            logger.severe(errorMessage);
            JOptionPane.showMessageDialog(null, errorMessage, "Port connection error", JOptionPane.OK_OPTION);
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
        peemStateController = new PEEMStateController(peemCommunicator, mainWindow.peemStateForm);
    }

    private PEEMCommunicator getPeemCommunicatorFromSerialConnection(SerialConnectionHandler connectionHandler) throws IOException {
        InputStream inputStream = connectionHandler.getInputStream();
        OutputStream outputStream = connectionHandler.getOutputStream();
        return new PEEMCommunicator(inputStream, outputStream);
    }

    private void initWindowListener() {
        mainWindow.addWindowListener(new PeemControllerWindowListener(rxTxConnectionHandler));
    }

	@Override
	public void setContext(Studio studio) {
		this.studio = studio;
        System.setProperty("java.util.logging.SimpleFormatter.format", "%1$tF %1$tT %4$s %2$s %5$s%6$s%n");
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
