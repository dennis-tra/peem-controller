package de.agbauer.physik.OptimisationSeries;

import de.agbauer.physik.Constants;
import de.agbauer.physik.FileSystem.DataFiler;
import de.agbauer.physik.FileSystem.DataFilerPeemLab;
import de.agbauer.physik.Gif.GifSender;
import de.agbauer.physik.PeemCommunicator.*;

import de.agbauer.physik.QuickAcquisition.AcquisitionParameters.AcquisitionParameters;
import de.agbauer.physik.QuickAcquisition.AcquisitionParameters.CameraData;
import de.agbauer.physik.QuickAcquisition.AcquisitionParameters.GeneralAcquisitionData;
import de.agbauer.physik.QuickAcquisition.AcquisitionParameters.PeemVoltages;
import de.agbauer.physik.QuickAcquisition.AcquisitionParametersCollector;
import de.agbauer.physik.QuickAcquisition.QuickAcquisitionSaver;
import ij.ImagePlus;
import ij.process.ImageProcessor;
import mmcorej.CMMCore;
import org.micromanager.PropertyMap;
import org.micromanager.Studio;
import org.micromanager.data.*;
import org.micromanager.data.Image;
import org.micromanager.display.DisplayWindow;

import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;

class OptimisationSeriesExecuter {
    private final PeemCommunicator peemCommunicator;
    private Studio studio;
    private CMMCore mmCore;
    private Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    private boolean shouldStop = false;
    private String sampleName;
    private AcquisitionParametersCollector apc;

    OptimisationSeriesExecuter(Studio studio, PeemCommunicator communicator, String sampleName) {
        this.studio = studio;
        this.mmCore = studio.getCMMCore();
        this.peemCommunicator = communicator;
        this.sampleName = sampleName;
        apc = new AcquisitionParametersCollector(peemCommunicator);
    }

    List<ImagePlus> startSeries(OptimisationSeriesParameters optimisationSeriesParameters) throws Exception {
        String deviceLabel = Constants.cameraDevice;

        double exposureTimeInSeconds = optimisationSeriesParameters.exposureTimeInSeconds;
        ArrayList<Double> values = optimisationSeriesParameters.values;
        PeemProperty property = optimisationSeriesParameters.property;

        logger.info("Slack: Starting optimisation series - " + optimisationSeriesParameters.toString());

        if (mmCore.deviceBusy(deviceLabel)) {
            throw new IOException("Can't start series, camera is busy.");
        }

        if (studio.getSnapLiveManager().getIsLiveModeOn()) {
            studio.getSnapLiveManager().setLiveMode(false);
        }

        final String currentBinning = setCameraBinningReturnCurrentBinning(1);
        final double currentExposureTimeInSeconds = setExposureAndReturnCurrentExposure(exposureTimeInSeconds);
        peemCommunicator.getProperty(property, PeemQuantity.VOLTAGE);

        Datastore store = studio.data().createRAMDatastore();
        DisplayWindow window = studio.displays().createDisplay(store);
        window.setCustomTitle("Optimisation series for " + optimisationSeriesParameters.property.displayName());

        List<ImagePlus> images = new ArrayList<>();
        //List<AcquisitionParameters> params = new ArrayList<>();

        GifSender gifSender = new GifSender(this.studio.data().getImageJConverter());

        for (int i = 0; i < values.size(); i++) {
            if (shouldStop) {
                throw new IOException("User stopped optimisation series");
            }

            Double value = values.get(i);

            peemCommunicator.setProperty(property, value);

            String msg = String.format("Slack: Acquiring image %d/%d. %s = %.4f V",
                    i + 1,
                    values.size(),
                    property.displayName(),
                    value);

            logger.info(msg);

            Image image = studio.getSnapLiveManager().snap(false).get(0);

            PropertyMap.PropertyMapBuilder propertyMapBuilder = studio.data().getPropertyMapBuilder();
            Metadata.MetadataBuilder metadataBuilder = studio.data().getMetadataBuilder();
            metadataBuilder.positionName(property.cmdString() + " = " + value);
            metadataBuilder.userData(propertyMapBuilder.build());

            Coords.CoordsBuilder coordsBuilder = studio.data().getCoordsBuilder();
            coordsBuilder.z(i);

            image = image.copyAtCoords(coordsBuilder.build());
            image = image.copyWithMetadata(metadataBuilder.build());

            store.putImage(image);

            gifSender.addImage(image);

            //Processes the image into an ImagePlus Object and adds it to
            //the images list
            String imageTitle = property.cmdString() + "_" + value;
            ImageProcessor ip = studio.data().getImageJConverter().createProcessor(image);
            ImagePlus imagePlus = new ImagePlus(imageTitle, ip);
            images.add(imagePlus);
        }

        logger.info("Reset camera exposure to " + currentExposureTimeInSeconds * 1000 + " s");
        mmCore.setExposure(currentExposureTimeInSeconds * 1000);

        logger.info("Reset camera binning to " + currentBinning);
        mmCore.setProperty(deviceLabel, "Binning", currentBinning);

        if (optimisationSeriesParameters.sendNotification) {
            logger.info("Slack: @channel Successfully finished optimisation series!");
        }

        gifSender.sendGifAsync(values, property);

        //Only full optimisation series are supposed to be saved
        if(!shouldStop) {
            saveImagesInList(images, currentBinning, currentExposureTimeInSeconds, values, property);
        }

        return images;
    }


    private void saveImagesInList(List<ImagePlus> images, String currentBinning, double exposureTimeInSeconds,
                                  ArrayList<Double> values, PeemProperty property){
        try {
            //The dummyImage and dummyData are used to obtain the PeemCurrents and Voltages
            //at the end of the optimisation series
            ImagePlus dummyImage = images.get(0);
            CameraData dummyData = new CameraData(dummyImage, (float) (exposureTimeInSeconds * 1000),
                    Integer.parseInt(currentBinning));
            AcquisitionParameters finalAp = apc.collect(sampleName, dummyData);

            // The images list should have the same amount of elements as the values list at this point.
            // This params list is supposed to hold the AcqusitionParameters associated to the values of
            // the optimisation series.
            List<AcquisitionParameters> params = new ArrayList<>();

            // Since the PeemVoltages fields are private and we want to change only one property
            // (the one to be optimised), a new map is put together to be able to initialise a brand new
            // PeemProperty with the correct values for the respective image
            Map<PeemProperty, Double> voltageMap = new HashMap<>();
            for (PeemProperty prop : PeemProperty.values()){
              voltageMap.put(prop, finalAp.peemVoltages.get(prop));
            }

            // If the dimensions differ from each other, there will be an ArrayOutOfBoundsException
            if (images.size() == values.size()) {
                for (int i = 0; i < images.size(); i++) {
                    // For every value in the optimisation value list the optimised property is changed in the
                    // HashMap, then a new PeemVoltages object is created. Finally, the PeemVoltages are used
                    // to create the AcqusitionParameters to the value.
                    voltageMap.put(property, values.get(i));
                    PeemVoltages apVoltages = new PeemVoltages(voltageMap);

                    // The sample name is expanded by "OptSeries" and the optimised property.
                    // This determines the saving folder
                    GeneralAcquisitionData genData =
                            new GeneralAcquisitionData(
                                    finalAp.generalData.sampleName + "_OptSeries_" + property,
                                    finalAp.generalData.excitation,
                                    finalAp.generalData.aperture,
                                    finalAp.generalData.note);

                    AcquisitionParameters ap = new AcquisitionParameters(genData, apVoltages,
                            finalAp.peemCurrents,
                            new CameraData(images.get(i), finalAp.cameraData.exposureInMs, finalAp.cameraData.binning));

                    params.add(ap);
                }
            }else{
                throw new Exception("Images and values must have the same dimension.");
            }

            // The actual saving of the images and voltages embedded in the AcquisitionParameters
            QuickAcquisitionSaver saver = new QuickAcquisitionSaver(peemCommunicator, new DataFilerPeemLab());
            for(int i = 0; i < params.size(); i++){
                saver.save(params.get(i), values.get(i));
            }

        }catch (Exception e) {
            //If the user denies to enter any general data (or denies to save) during the collection
            //of the AcquisitionParameters
            return;
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
