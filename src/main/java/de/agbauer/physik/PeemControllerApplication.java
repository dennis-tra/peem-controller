package de.agbauer.physik;

import de.agbauer.physik.Logging.*;
import de.agbauer.physik.Observers.*;
import de.agbauer.physik.Observers.SampleNameChangeListener;
import de.agbauer.physik.GeneralInformation.GeneralInformationController;
import de.agbauer.physik.OptimisationSeries.OptimisationSeriesController;
import de.agbauer.physik.PeemCommunicator.*;
import de.agbauer.physik.PeemHistory.PEEMHistoryController;
import de.agbauer.physik.PeemState.PEEMStateController;
import de.agbauer.physik.Presets.PresetController;
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
import java.util.logging.ConsoleHandler;
import java.util.logging.Logger;

@SpringBootApplication
@Plugin(type = MenuPlugin.class)
public class PeemControllerApplication implements MenuPlugin, SciJavaPlugin {
    private Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    private Studio studio;

	private MainWindow mainWindow;

    private PeemCommunicator peemCommunicator;
    private QuickAcquisitionController quickAcquisitionController;
    private OptimisationSeriesController optimisationSeriesController;
    private GeneralInformationController generalInformationController;
    private PEEMHistoryController peemHistoryController;
    private PEEMStateController peemStateController;
    private PresetController presetController;
    private SerialConnectionHandler serialConnectionHandler = new RxTxConnectionHandler();

    public static void main(String[] args) {
        PeemControllerApplication app = new PeemControllerApplication();
        app.onPluginSelected();
	}

    @Override
    public void onPluginSelected() {

		mainWindow = new MainWindow();

        initLogHandlers();
        initPEEMConnection();
        initGeneralInformation();
        initOptimisationSeries();
        initQuickAcquisition();
        initPeemState();
        initPeemHistory();
        initPresetController();
        initObservers();
        initWindowListener();
    }

    private void initPresetController(){
        presetController = new PresetController(mainWindow.presetForm);
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

        AcquisitionParamsLoadObserver paramsLoadObserver = new AcquisitionParamsLoadObserver(new AcquisitionParamsLoadListener[]{
                peemStateController
        });

        presetController.addObserver(paramsLoadObserver);
        peemHistoryController.addObserver(paramsLoadObserver);
    }

    private void initLogHandlers() {
        logger.setUseParentHandlers(false);

        LabelLogHandler labelLogHandler = new LabelLogHandler(mainWindow.statusBarLabel);
        ImageJLogHandler imageJLogHandler = new ImageJLogHandler();
        SlackLogHandler slackLogHandler = new SlackLogHandler();
        ConsoleHandler consoleLogHandler = new ConsoleHandler();

        imageJLogHandler.setFormatter(new ImageJLogFormatter());
        consoleLogHandler.setFormatter(new ConsoleLogFormatter());

        logger.addHandler(labelLogHandler);
        logger.addHandler(imageJLogHandler);
        logger.addHandler(slackLogHandler);
        logger.addHandler(consoleLogHandler);
    }

    private void initPEEMConnection() {
        if (!Constants.peemConnected) {
            peemCommunicator = new DummyPeemCommunicator();
            return;
        }

        try {
            serialConnectionHandler.connectTo(Constants.defaultPort);

            peemCommunicator = getPeemCommunicatorFromSerialConnection(serialConnectionHandler);
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

    private FocusPeemCommunicator getPeemCommunicatorFromSerialConnection(SerialConnectionHandler connectionHandler) throws IOException {
        InputStream inputStream = connectionHandler.getInputStream();
        OutputStream outputStream = connectionHandler.getOutputStream();
        return new FocusPeemCommunicator(inputStream, outputStream);
    }

    private void initWindowListener() {
        mainWindow.addWindowListener(new PeemControllerWindowListener(serialConnectionHandler));
    }

	@Override
	public void setContext(Studio studio) {
		this.studio = studio;
        System.setProperty("java.util.logging.SimpleFormatter.format", "%1$tF %1$tT %4$s %2$s %5$s%6$s%n");
    }

    @Override
    public String getSubMenu() {
        return "";
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
