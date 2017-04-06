package de.agbauer.physik.OptimisationSeries;

import de.agbauer.physik.Generic.Constants;
import de.agbauer.physik.Generic.GifSender;
import de.agbauer.physik.PEEMCommunicator.PEEMBulkReader;
import de.agbauer.physik.PEEMCommunicator.PEEMCommunicator;
import de.agbauer.physik.PEEMCommunicator.PEEMProperty;

import de.agbauer.physik.PEEMCommunicator.PEEMQuantity;
import ij.ImagePlus;
import mmcorej.CMMCore;
import org.micromanager.PropertyMap;
import org.micromanager.Studio;
import org.micromanager.data.*;
import org.micromanager.data.Image;
import org.micromanager.display.DisplayWindow;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

class OptimisationSeriesExecuter {
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

    List<ImagePlus> startSeries(OptimisationSeriesParameters optimisationSeriesParameters) throws Exception {
        String deviceLabel = Constants.cameraDevice;

        double exposureTimeInSeconds = optimisationSeriesParameters.exposureTimeInSeconds;
        ArrayList<Float> values = optimisationSeriesParameters.values;
        PEEMProperty property = optimisationSeriesParameters.property;

        logger.info("Slack: Starting optimisation series - " + optimisationSeriesParameters.toString());

        if (mmCore.deviceBusy(deviceLabel)) {
            throw new IOException("Can't start series, camera is busy.");
        }

        if (studio.getSnapLiveManager().getIsLiveModeOn()) {
            studio.getSnapLiveManager().setLiveMode(false);
        }

        final String currentBinning = setCameraBinningReturnCurrentBinning(1);
        final double currentExposureTimeInSeconds = setExposureAndReturnCurrentExposure(exposureTimeInSeconds);
        peemCommunicator.getProperty(property, PEEMQuantity.VOLTAGE);

        PEEMBulkReader bulkReader = new PEEMBulkReader(peemCommunicator);
        Map<PEEMProperty, String> peemProperties = bulkReader.getAllVoltages();

        Datastore store = studio.data().createRAMDatastore();
        DisplayWindow window = studio.displays().createDisplay(store);
        window.setCustomTitle("Optimisation series for " + optimisationSeriesParameters.property.displayName());

        List<ImagePlus> images = new ArrayList<>();
        GifSender gifSender = new GifSender(this.studio.data().getImageJConverter());

        for (int i = 0; i < values.size(); i++) {
            if (shouldStop) {
                throw new IOException("User stopped optimisation series");
            }

            Float value = values.get(i);

            final int imageNumber = i + 1;

            peemCommunicator.setProperty(property, value);

            logger.info("Slack: Acquiring image " + imageNumber + "/" + values.size() + ". " + property.displayName() + " = " + value + " V");

            Image image = studio.getSnapLiveManager().snap(false).get(0);

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

            gifSender.addImage(image);

        }

        logger.info("Reset camera exposure to " + currentExposureTimeInSeconds * 1000 + " s");
        mmCore.setExposure(currentExposureTimeInSeconds * 1000);

        logger.info("Reset camera binning to " + currentBinning);
        mmCore.setProperty(deviceLabel, "Binning", currentBinning);

        if (optimisationSeriesParameters.sendNotification) {
            logger.info("Slack: @channel Successfully finished optimisation series!");
        }

        gifSender.sendGifAsync(values, property);

        return images;
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
