package de.agbauer.physik;

import de.agbauer.physik.FieldOfViewInspectorPlugin.FieldOfViewPlugin;
import de.agbauer.physik.FileSystem.DataFiler;
import de.agbauer.physik.FileSystem.DataFilerPeemLab;
import de.agbauer.physik.Logging.*;
import de.agbauer.physik.MaxCountInspectorPlugin.MaxCountPlugin;
import de.agbauer.physik.Observers.*;
import de.agbauer.physik.Observers.SampleNameChangeListener;
import de.agbauer.physik.GeneralInformation.GeneralInformationController;
import de.agbauer.physik.OptimisationSeries.OptimisationSeriesController;
import de.agbauer.physik.PeemCommunicator.*;
import de.agbauer.physik.PeemHistory.PEEMHistoryController;
import de.agbauer.physik.PeemHistoryBrowser.PeemHistoryBrowserController;
import de.agbauer.physik.PeemState.PEEMStateController;
import de.agbauer.physik.Presets.PresetController;
import de.agbauer.physik.QuickAcquisition.AcquisitionSaver;
import de.agbauer.physik.QuickAcquisition.QuickAcquisitionController;
import de.agbauer.physik.QuickAcquisition.QuickAcquisitionSaver;
import org.micromanager.MenuPlugin;
import org.micromanager.Studio;

import org.scijava.plugin.Plugin;
import org.scijava.plugin.SciJavaPlugin;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

@SpringBootApplication
@Plugin(type = MenuPlugin.class)
public class PeemControllerApplication implements MenuPlugin, SciJavaPlugin {
    private Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    private Studio studio;
    private MaxCountPlugin maxCountPlugin;
    private FieldOfViewPlugin fieldOfViewPlugin;


	private MainWindow mainWindow;

    private PeemCommunicator peemCommunicator;
    private QuickAcquisitionController quickAcquisitionController;
    private OptimisationSeriesController optimisationSeriesController;
    private GeneralInformationController generalInformationController;
    private PEEMHistoryController peemHistoryController;
    private PEEMStateController peemStateController;
    private PresetController presetController;
    private SerialConnectionHandler serialConnectionHandler = new RxTxConnectionHandler();

    private DataFiler dataFiler = new DataFilerPeemLab();

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
        initButtonListener();
    }

    private void initButtonListener() {
        mainWindow.browseButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                new PeemHistoryBrowserController(dataFiler);
            }
        });
    }

    private void initPresetController(){
        presetController = new PresetController(peemCommunicator, mainWindow.presetForm);
    }

    private void initPeemHistory() {
        peemHistoryController = new PEEMHistoryController(mainWindow.peemHistoryForm, dataFiler);
    }

    private void initGeneralInformation() {
        generalInformationController = new GeneralInformationController(mainWindow.generalInformationForm);
    }

    private void initObservers() {

        SampleNameChangeObserver sampleNameChangeObserver = new SampleNameChangeObserver(new SampleNameChangeListener[] {
                peemHistoryController, optimisationSeriesController, quickAcquisitionController
        });

        generalInformationController.addObserver(sampleNameChangeObserver);
        generalInformationController.notifyObservers();



        NewDataSavedObserver singleAcquisitionObserver = new NewDataSavedObserver(new DataSaveListeners[]{
                peemHistoryController
        });
        quickAcquisitionController.addObserver(singleAcquisitionObserver);



        AcquisitionParamsLoadObserver paramsLoadObserver = new AcquisitionParamsLoadObserver(new AcquisitionParamsLoadListener[]{
                peemStateController, presetController, fieldOfViewPlugin
        });
        peemStateController.addObserver(paramsLoadObserver);
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

        try {
            FileHandler fileLogHandler = new FileHandler(dataFiler.logLocation(), true);
            fileLogHandler.setFormatter(new ConsoleLogFormatter());
            logger.addHandler(fileLogHandler);
        } catch (IOException e) {
            logger.warning("Could not initialize file logger: " + e.getMessage());
        }
    }

    private void initPEEMConnection() {

        try {
            serialConnectionHandler.connectTo(Constants.defaultPort);

            InputStream inputStream = serialConnectionHandler.getInputStream();
            OutputStream outputStream = serialConnectionHandler.getOutputStream();

            peemCommunicator =  new FocusPeemCommunicator(inputStream, outputStream);


        } catch (IOException e) {

            String errorMessage = "Could not connect to default port '" + Constants.defaultPort + "': " + e.getMessage();
            logger.severe(errorMessage);

            if (!Constants.peemConnected) {
                logger.warning("PEEM is not connected: Using dummy interface instead.");
                peemCommunicator = new DummyPeemCommunicator();
                return;
            }

            JOptionPane.showMessageDialog(null, errorMessage, "Port connection error", JOptionPane.OK_OPTION);
            System.exit(1);
        }

    }

    private void initOptimisationSeries() {
        optimisationSeriesController = new OptimisationSeriesController(studio, peemCommunicator, mainWindow.optimisationSeriesForm);
    }

    private void initQuickAcquisition() {
        AcquisitionSaver fileSaver = new QuickAcquisitionSaver(peemCommunicator, dataFiler);
        quickAcquisitionController = new QuickAcquisitionController(studio, fileSaver, mainWindow.quickAcquisitionForm);
    }

    private void initPeemState() {
        peemStateController = new PEEMStateController(peemCommunicator, mainWindow.peemStateForm);
    }

    private void initWindowListener() {
        mainWindow.addWindowListener(new PeemControllerWindowListener(serialConnectionHandler));
    }

	@Override
	public void setContext(Studio studio) {
		this.studio = studio;

        this.maxCountPlugin = (MaxCountPlugin) this.studio.plugins().getInspectorPlugins().get(MaxCountPlugin.class.getName());
        this.fieldOfViewPlugin = (FieldOfViewPlugin) this.studio.plugins().getInspectorPlugins().get(FieldOfViewPlugin.class.getName());

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
