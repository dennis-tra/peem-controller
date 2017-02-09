package de.agbauer.physik.OptimisationSeries;

import de.agbauer.physik.Generic.Constants;
import de.agbauer.physik.Generic.LogManager;
import de.agbauer.physik.PEEMCommunicator.PEEMCommunicator;
import de.agbauer.physik.PEEMCommunicator.PEEMProperty;

import mmcorej.CMMCore;
import org.joda.time.DateMidnight;
import org.micromanager.PropertyMap;
import org.micromanager.Studio;
import org.micromanager.data.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by dennis on 03/02/2017.
 */
public class OptimisationSeriesExecuter {
    private final PEEMCommunicator peemCommunicator;
    private Studio studio;
    private CMMCore mmCore;
    private LogManager logManager;
    private boolean shouldStop = false;

    OptimisationSeriesExecuter(Studio studio, PEEMCommunicator communicator, LogManager logManager) {
        this.studio = studio;
        this.mmCore = studio.getCMMCore();
        this.peemCommunicator = communicator;
        this.logManager = logManager;
    }

    void startSeries(OptimisationSeriesParameters optimisationSeriesParameters) throws Exception {
        String deviceLabel = Constants.cameraDevice;

        double exposureTimeInSeconds = optimisationSeriesParameters.exposureTimeInS;
        ArrayList<Float> values =optimisationSeriesParameters.values;
        PEEMProperty property = optimisationSeriesParameters.property;

        logManager.inform("Starting optimisation series with exposure "+ exposureTimeInSeconds +" s and the following values: " +values.toString(), false,true);

        if (mmCore.deviceBusy(deviceLabel)) {
            throw new IOException("Can't start series, camera is busy.");
        }

        final String currentBinning = setCameraBinningReturnCurrentBinning(1);
        final double currentExposureTimeInSeconds = setExposureAndReturnCurrentExposure(exposureTimeInSeconds);


        Datastore store = studio.data().createRAMDatastore();
        studio.displays().createDisplay(store);

        for (int i = 0; i < values.size(); i++) {
            if (shouldStop) {
                break;
            }

            Float value = values.get(i);

            final int imageNumber = i + 1;

            peemCommunicator.setProperty(property, value);

            logManager.inform("Acquiring image "+ imageNumber +"/" +values.size() + "...", true, true);

            List<Image> images = studio.getSnapLiveManager().snap(false);

            Image image = images.get(0);

            PropertyMap.PropertyMapBuilder propertyMapBuilder = studio.data().getPropertyMapBuilder();
            propertyMapBuilder.putDouble(property.cmdString(), Double.valueOf(value));

            Metadata.MetadataBuilder metadataBuilder = studio.data().getMetadataBuilder();
            metadataBuilder.positionName(property.cmdString() + " = " + value);
            metadataBuilder.userData(propertyMapBuilder.build());

            Coords.CoordsBuilder coordsBuilder = studio.data().getCoordsBuilder();
            coordsBuilder.z(i);

            image = image.copyAtCoords(coordsBuilder.build());
            image = image.copyWithMetadata(metadataBuilder.build());

            store.putImage(image);

        }

        if (optimisationSeriesParameters.saveImages) {
            String savePath = Constants.defaultFileSaveFolder;
            store.save(Datastore.SaveMode.SINGLEPLANE_TIFF_SERIES, savePath);
        }

        logManager.inform("Reset camera exposure to " + currentExposureTimeInSeconds * 1000 + " s", true, true);
        mmCore.setExposure(currentExposureTimeInSeconds * 1000);

        logManager.inform("Reset camera binning to " + currentBinning, true, true);
        mmCore.setProperty(deviceLabel, "Binning", currentBinning);
    }

    private String setCameraBinningReturnCurrentBinning(int binning) throws Exception {

        final String currentBinning = mmCore.getProperty(Constants.cameraDevice, "Binning");
        logManager.inform("Save current binning: " + currentBinning, true, true);

        logManager.inform("Set camera binning to 1", true, true);
        mmCore.setProperty(Constants.cameraDevice, "Binning", binning);

        return currentBinning;
    }

    private double setExposureAndReturnCurrentExposure(double exposureTimeInSeconds) throws Exception {

        final double currentExposureTimeInSeconds = mmCore.getExposure()/1000.0;
        logManager.inform("Save current exposure: " + currentExposureTimeInSeconds +" s", true, true);

        logManager.inform("Set camera exposure to " + exposureTimeInSeconds + " s", true, true);
        mmCore.setExposure(exposureTimeInSeconds * 1000);

        return currentExposureTimeInSeconds;
    }

    void cancelSeries() {
        shouldStop = true;
    }
}
