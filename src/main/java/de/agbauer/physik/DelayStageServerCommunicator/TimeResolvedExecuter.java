package de.agbauer.physik.DelayStageServerCommunicator;

import de.agbauer.physik.Constants;
import ij.ImagePlus;
import ij.process.ImageProcessor;
import mmcorej.CMMCore;
import org.micromanager.PropertyMap;
import org.micromanager.Studio;
import org.micromanager.data.Coords;
import org.micromanager.data.Datastore;
import org.micromanager.data.Image;
import org.micromanager.data.Metadata;
import org.micromanager.display.DisplayWindow;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;


class TimeResolvedExecuter {
    private Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    private boolean shouldStop = false;

    private final Studio studio;
    private final CMMCore mmCore;
    private final DelayStageServerCommunicator dssCommunicator;

    TimeResolvedExecuter(Studio studio, DelayStageServerCommunicator delayStageServerCommunicator) {
        this.studio = studio;
        this.mmCore = studio.getCMMCore();
        this.dssCommunicator = delayStageServerCommunicator;
    }

    List<ImagePlus> startSeries(TimeResolvedParameters timeResolvedParameters) throws Exception {
        String deviceLabel = Constants.cameraDevice;

        double exposureTimeInSeconds = timeResolvedParameters.exposureTimeInSeconds;
        ArrayList<Double> values = timeResolvedParameters.values;

        logger.info("Slack: Starting time resolved measurement - " + timeResolvedParameters.toString());

        if (mmCore.deviceBusy(deviceLabel)) {
            throw new IOException("Can't start time resolved measurement, camera is busy.");
        }

        if (studio.getSnapLiveManager().getIsLiveModeOn()) {
            studio.getSnapLiveManager().setLiveMode(false);
        }

        final String currentBinning = setCameraBinningReturnCurrentBinning(1);
        final double currentExposureTimeInSeconds = setExposureAndReturnCurrentExposure(exposureTimeInSeconds);

        Datastore store = studio.data().createRAMDatastore();
        DisplayWindow window = studio.displays().createDisplay(store);
        window.setCustomTitle("Time resolved measurement");

        List<ImagePlus> images = new ArrayList<>();

        for (int i = 0; i < values.size(); i++) {
            if (shouldStop) {
                throw new IOException("User stopped time resolved measurement");
            }

            Double value = values.get(i);

            float position = this.moveTo(value);

            String msg = String.format("Slack: Acquiring image %d/%d. Position %.4f fs",
                    i + 1,
                    values.size(),
                    position);

            logger.info(msg);

            Image image = studio.getSnapLiveManager().snap(false).get(0);

            PropertyMap.PropertyMapBuilder propertyMapBuilder = studio.data().getPropertyMapBuilder();
            Metadata.MetadataBuilder metadataBuilder = studio.data().getMetadataBuilder();
            metadataBuilder.positionName(String.format("Position %.4f fs", value));
            metadataBuilder.userData(propertyMapBuilder.build());

            Coords.CoordsBuilder coordsBuilder = studio.data().getCoordsBuilder();
            coordsBuilder.z(i);

            image = image.copyAtCoords(coordsBuilder.build());
            image = image.copyWithMetadata(metadataBuilder.build());

            store.putImage(image);

            //Processes the image into an ImagePlus Object and adds it to
            //the images list
            String imageTitle = "tr" + "_" + value;
            ImageProcessor ip = studio.data().getImageJConverter().createProcessor(image);
            ImagePlus imagePlus = new ImagePlus(imageTitle, ip);
            images.add(imagePlus);
        }

        logger.info("Reset camera exposure to " + currentExposureTimeInSeconds * 1000 + " s");
        mmCore.setExposure(currentExposureTimeInSeconds * 1000);

        logger.info("Reset camera binning to " + currentBinning);
        mmCore.setProperty(deviceLabel, "Binning", currentBinning);

        if (timeResolvedParameters.sendNotification) {
            logger.info("Slack: @channel Successfully finished time resolved measurement!");
        }

        return images;
    }

    private float moveTo(double femtoSeconds) throws Exception {
        for (int i = 1; i <= 5; i++) {
            try {
                return dssCommunicator.moveTo(femtoSeconds);
            } catch (Exception exception) {
                logger.warning(String.format("Couldn't move to position %.4f fs: %s. Trying again %d/%d", femtoSeconds, exception.getMessage(), i, 5));
            }
        }
        throw new Exception("Couldn't communicate with delay stage... stopping time resolved measurement.");
    }

    private String setCameraBinningReturnCurrentBinning(int binning) throws Exception {

        final String currentBinning = mmCore.getProperty(Constants.cameraDevice, "Binning");
        logger.info("Save current binning: " + currentBinning);

        logger.info("Set camera binning to " + binning);
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
