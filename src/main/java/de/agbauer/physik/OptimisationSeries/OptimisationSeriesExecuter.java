package de.agbauer.physik.OptimisationSeries;

import de.agbauer.physik.Generic.Constants;
import de.agbauer.physik.Generic.WorkingDirectory;
import de.agbauer.physik.PEEMCommunicator.PEEMBulkReader;
import de.agbauer.physik.PEEMCommunicator.PEEMCommunicator;
import de.agbauer.physik.PEEMCommunicator.PEEMProperty;

import mmcorej.CMMCore;
import org.micromanager.PropertyMap;
import org.micromanager.Studio;
import org.micromanager.data.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Created by dennis on 03/02/2017.
 */
public class OptimisationSeriesExecuter {
    private final PEEMCommunicator peemCommunicator;
    private Studio studio;
    private CMMCore mmCore;
    private Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    private boolean shouldStop = false;

    OptimisationSeriesExecuter(Studio studio, PEEMCommunicator communicator) {
        this.studio = studio;
        this.mmCore = studio.getCMMCore();
        this.peemCommunicator = communicator;
    }

    void startSeries(OptimisationSeriesParameters optimisationSeriesParameters) throws Exception {
        String deviceLabel = Constants.cameraDevice;

        double exposureTimeInSeconds = optimisationSeriesParameters.exposureTimeInSeconds;
        ArrayList<Float> values = optimisationSeriesParameters.values;
        PEEMProperty property = optimisationSeriesParameters.property;

        logger.info("Slack: Starting optimisation series - " + optimisationSeriesParameters.toString());

        if (mmCore.deviceBusy(deviceLabel)) {
            throw new IOException("Can't start series, camera is busy.");
        }

        final String currentBinning = setCameraBinningReturnCurrentBinning(1);
        final double currentExposureTimeInSeconds = setExposureAndReturnCurrentExposure(exposureTimeInSeconds);

        PEEMBulkReader bulkReader = new PEEMBulkReader(peemCommunicator);
        Map<PEEMProperty, String> peemProperties = bulkReader.getAllVoltages();

        Datastore store = studio.data().createRAMDatastore();
        studio.displays().createDisplay(store);

        for (int i = 0; i < values.size(); i++) {
            if (shouldStop) {
                break;
            }

            Float value = values.get(i);

            final int imageNumber = i + 1;

            peemCommunicator.setProperty(property, value);

            logger.info("Slack: Acquiring image "+ imageNumber +"/" +values.size() + ". " + property.displayName() +" = " + value + " V");

            List<Image> images = studio.getSnapLiveManager().snap(false);

            Image image = images.get(0);

            PropertyMap.PropertyMapBuilder propertyMapBuilder = studio.data().getPropertyMapBuilder();
            propertyMapBuilder.putDouble(property.cmdString(), Double.valueOf(value));

            for (Map.Entry<PEEMProperty, String> entry : peemProperties.entrySet()) {
                propertyMapBuilder.putString(entry.getKey().displayName(), entry.getValue());
            }

            Metadata.MetadataBuilder metadataBuilder = studio.data().getMetadataBuilder();
            metadataBuilder.positionName(property.cmdString() + " = " + value);
            metadataBuilder.userData(propertyMapBuilder.build());

            Coords.CoordsBuilder coordsBuilder = studio.data().getCoordsBuilder();
            coordsBuilder.z(i);

            image = image.copyAtCoords(coordsBuilder.build());
            image = image.copyWithMetadata(metadataBuilder.build());

            store.putImage(image);

        }

        logger.info("Reset camera exposure to " + currentExposureTimeInSeconds * 1000 + " s");
        mmCore.setExposure(currentExposureTimeInSeconds * 1000);

        logger.info("Reset camera binning to " + currentBinning);
        mmCore.setProperty(deviceLabel, "Binning", currentBinning);

        if (optimisationSeriesParameters.sendNotification) {
            logger.info("Slack: @channel Successfully finished optimisation series!");
        }
    }

    private String setCameraBinningReturnCurrentBinning(int binning) throws Exception {

        final String currentBinning = mmCore.getProperty(Constants.cameraDevice, "Binning");
        logger.info("Save current binning: " + currentBinning);

        logger.info("Set camera binning to 1");
        mmCore.setProperty(Constants.cameraDevice, "Binning", binning);

        return currentBinning;
    }

    private double setExposureAndReturnCurrentExposure(double exposureTimeInSeconds) throws Exception {

        final double currentExposureTimeInSeconds = mmCore.getExposure()/1000.0;
        logger.info("Save current exposure: " + currentExposureTimeInSeconds +" s");

        logger.info("Set camera exposure to " + exposureTimeInSeconds + " s");
        mmCore.setExposure(exposureTimeInSeconds * 1000);

        return currentExposureTimeInSeconds;
    }

    void cancelSeries() {
        shouldStop = true;
    }
}
