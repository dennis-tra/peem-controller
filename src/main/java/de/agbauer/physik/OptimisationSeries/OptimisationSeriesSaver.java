package de.agbauer.physik.OptimisationSeries;

import de.agbauer.physik.FileSystem.*;
import de.agbauer.physik.PeemCommunicator.PeemCommunicator;
import de.agbauer.physik.PeemCommunicator.PeemProperty;
import de.agbauer.physik.QuickAcquisition.AcquisitionParameters.AcquisitionParameters;
import de.agbauer.physik.QuickAcquisition.AcquisitionParameters.CameraData;
import de.agbauer.physik.QuickAcquisition.AcquisitionParameters.GeneralAcquisitionData;
import de.agbauer.physik.QuickAcquisition.AcquisitionParameters.PeemVoltages;
import de.agbauer.physik.QuickAcquisition.AcquisitionParametersCollector;
import de.agbauer.physik.QuickAcquisition.AcquisitionParametersFormatter;
import de.agbauer.physik.QuickAcquisition.AcquisitionParametersPowershellFormatter;
import de.agbauer.physik.QuickAcquisition.AcquisitionParametersSaver;
import ij.ImagePlus;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class OptimisationSeriesSaver {
    private PeemCommunicator peemCommunicator;
    private DataFiler filer;
    private Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    public OptimisationSeriesSaver(PeemCommunicator peemCommunicator, DataFiler filer) {

        this.peemCommunicator = peemCommunicator;
        this.filer = filer;
    }

    public ArrayList<AcquisitionParameters> save(String sampleName, OptimisationSeriesParameters osParameters, List<ImagePlus> images) throws Exception {
        logger.info("Saving optimisation series...");

        AcquisitionParametersCollector apc = new AcquisitionParametersCollector(this.peemCommunicator);


        //The dummyData are used to obtain the PeemCurrents and Voltages
        //at the end of the optimisation series
        CameraData dummyData = new CameraData(null, osParameters.exposureTimeInSeconds * 1000,1);
        AcquisitionParameters finalAp = apc.collect(sampleName, dummyData);

        // The images list should have the same amount of elements as the values list at this point.
        // This params list is supposed to hold the AcqusitionParameters associated to the values of
        // the optimisation series.
        ArrayList<AcquisitionParameters> acquisitionParameters = new ArrayList<>();

        // Since the PeemVoltages fields are private and we want to change only one property
        // (the one to be optimised), a new map is put together to be able to initialise a brand new
        // PeemProperty with the correct values for the respective image
        Map<PeemProperty, Double> voltageMap = new HashMap<>();
        for (PeemProperty prop : PeemProperty.values()) {
            voltageMap.put(prop, finalAp.peemVoltages.get(prop));
        }

        // If the dimensions differ from each other, there will be an ArrayOutOfBoundsException
        if (images.size() == osParameters.values.size()) {
            for (int i = 0; i < images.size(); i++) {
                // For every value in the optimisation value list the optimised property is changed in the
                // HashMap, then a new PeemVoltages object is created. Finally, the PeemVoltages are used
                // to create the AcqusitionParameters to the value.
                voltageMap.put(osParameters.property, osParameters.values.get(i));
                PeemVoltages apVoltages = new PeemVoltages(voltageMap);

                // The sample name is expanded by "OptSeries" and the optimised property.
                // This determines the saving folder
                GeneralAcquisitionData genData =
                        new GeneralAcquisitionData(
                                finalAp.generalData.sampleName,
                                finalAp.generalData.excitation,
                                finalAp.generalData.aperture,
                                finalAp.generalData.note);

                AcquisitionParameters ap = new AcquisitionParameters(genData, apVoltages,
                        finalAp.peemCurrents,
                        new CameraData(images.get(i), finalAp.cameraData.exposureInMs, finalAp.cameraData.binning));

                acquisitionParameters.add(ap);
            }
        }else{
            throw new IOException("Images and values must have the same dimension.");
        }


        // The initial optSeries is number one, if the according folder already exists, it is checked,
        // if the second optSeries also exists and so on...
        // If (for example) there are already four optSeries recorded, the fifth one shouldn't exist yet.
        // The number five is then the number of the current optSeries
        int optSeriesNumber = 1;
        File directory = new File(filer.getWorkingDirectoryFor(acquisitionParameters.get(0).generalData.sampleName)
                + "OptSeries_" + osParameters.property + "_" + optSeriesNumber + File.separator);

        while(directory.exists()){
            optSeriesNumber++;
            directory = new File(filer.getWorkingDirectoryFor(acquisitionParameters.get(0).generalData.sampleName)
                    + "OptSeries_" + osParameters.property + "_" + optSeriesNumber + File.separator);
        }

        directory.mkdirs();

        // This loop saves all images to the SAME subfolder. In order to do this, it is necessary for this
        // saving function to take all parameters and propertyValues as a list. Otherwise there would be
        // a new subfolder for every image.
        for(int i = 0; i < acquisitionParameters.size(); i++){
            embedAcquisitonParameters(acquisitionParameters.get(i).cameraData, acquisitionParameters.get(i));

            String fileString = filer.getWorkingDirectoryFor(acquisitionParameters.get(0).generalData.sampleName)
                    + "OptSeries_" + osParameters.property + "_" + optSeriesNumber + File.separator
                    + filer.generateScopeName(acquisitionParameters.get(i).generalData.sampleName) + "_"
                    + osParameters.values.get(i) + "_"
                    + acquisitionParameters.get(i).generalData.excitation;

            ImageSaver imageSaver = new ImageSaver();
            imageSaver.save(acquisitionParameters.get(i), acquisitionParameters.get(i).cameraData.imagePlus,fileString + ".tif");

            AcquisitionParametersFormatter apFormatter = new AcquisitionParametersPowershellFormatter();
            AcquisitionParametersSaver apSaver = new AcquisitionParametersSaver(apFormatter);
            apSaver.save(acquisitionParameters.get(i),fileString + "_PARAMS.txt");
        }

        logger.info("Finished saving optimisation series...");

        return acquisitionParameters;
    }

    private void embedAcquisitonParameters(CameraData cameraData, AcquisitionParameters ap) {

        cameraData.imagePlus.setProperty("Sample Name", ap.generalData.sampleName);
        cameraData.imagePlus.setProperty("Excitation", ap.generalData.excitation);
        cameraData.imagePlus.setProperty("Aperture", ap.generalData.aperture);
        cameraData.imagePlus.setProperty("Note", ap.generalData.note);
        cameraData.imagePlus.setProperty("Binning", ap.cameraData.binning);
        cameraData.imagePlus.setProperty("Exposure[ms]", ap.cameraData.exposureInMs);

        for (PeemProperty peemProperty: PeemProperty.values()) {
            cameraData.imagePlus.setProperty(peemProperty.displayName() + " U", ap.peemVoltages.get(peemProperty));
            cameraData.imagePlus.setProperty(peemProperty.displayName() + " I", ap.peemCurrents.get(peemProperty));
        }
    }
}
