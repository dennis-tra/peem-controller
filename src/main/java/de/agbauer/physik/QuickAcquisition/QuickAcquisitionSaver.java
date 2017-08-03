package de.agbauer.physik.QuickAcquisition;

import de.agbauer.physik.FileSystem.DataFiler;
import de.agbauer.physik.FileSystem.FileLocations;
import de.agbauer.physik.FileSystem.ImageSaver;
import de.agbauer.physik.FileSystem.TmpJpegSaver;
import de.agbauer.physik.Logging.SlackFileUploader;
import de.agbauer.physik.PeemCommunicator.PeemCommunicator;
import de.agbauer.physik.PeemCommunicator.PeemProperty;
import de.agbauer.physik.QuickAcquisition.AcquisitionParameters.AcquisitionParameters;
import de.agbauer.physik.QuickAcquisition.AcquisitionParameters.CameraData;
import ij.ImagePlus;
import ij.process.ImageProcessor;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

public class QuickAcquisitionSaver implements AcquisitionSaver {
    private Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    private PeemCommunicator peemCommunicator;
    private DataFiler filer;

    public QuickAcquisitionSaver(PeemCommunicator peemCommunicator, DataFiler filer) {

        this.peemCommunicator = peemCommunicator;
        this.filer = filer;
    }

    @Override
    public AcquisitionParameters save(String sampleName, CameraData cameraData) throws IOException {

        AcquisitionParametersCollector apc = new AcquisitionParametersCollector(peemCommunicator);
        AcquisitionParameters ap = apc.collect(sampleName, cameraData);

        FileLocations fileLocations = filer.setAcquisitionParams(ap);


        embedAcquisitonParameters(cameraData, ap);

        ImageSaver imageSaver = new ImageSaver();
        imageSaver.save(ap, cameraData.imagePlus, fileLocations.tifImageFilePath);

        AcquisitionParametersFormatter apFormatter = new AcquisitionParametersPowershellFormatter();
        AcquisitionParametersSaver apSaver = new AcquisitionParametersSaver(apFormatter);
        apSaver.save(ap, fileLocations.peemParametersFilePath);

        sendImageToSlackAsync(ap, cameraData.imagePlus);

        return ap;
    }

    //Overloading for the OptimisationSeries (which already provides AcquisitionParameters)
    public ArrayList<AcquisitionParameters> save(ArrayList<AcquisitionParameters> ap, ArrayList<Double> propertyValues ) throws IOException {

        // The initial optSeries is number one, if the according folder already exists, it is checked,
        // if the second optSeries also exists and so on...
        // If (for example) there are already four optSeries recorded, the fifth one shouldn't exist yet.
        // The number five is then the number of the curent optSeries
        int optSeriesNumber = 1;
        File directory = new File(filer.getWorkingDirectoryFor(ap.get(0).generalData.sampleName)
                + "series_" + optSeriesNumber + File.separator);

        while(directory.exists()){
            optSeriesNumber++;
            directory = new File(filer.getWorkingDirectoryFor(ap.get(0).generalData.sampleName)
                    + "series_" + optSeriesNumber + File.separator);
        }

        directory.mkdirs();

        // This loop saves all images to the SAME subfolder. In order to do this, it is necessary for this
        // saving function to take all parameters and propertyValues as a list. Otherwise there would be
        // a new subfolder for every image.
        for(int i = 0; i < ap.size(); i++){
            embedAcquisitonParameters(ap.get(i).cameraData, ap.get(i));

            String fileString = filer.getWorkingDirectoryFor(ap.get(i).generalData.sampleName)
                                + "series_" + optSeriesNumber + File.separator
                                + filer.generateScopeName(ap.get(i).generalData.sampleName) + "_"
                                + propertyValues.get(i) + "_"
                                + ap.get(i).generalData.excitation;

            ImageSaver imageSaver = new ImageSaver();
            imageSaver.save(ap.get(i), ap.get(i).cameraData.imagePlus,fileString + ".tif");

            AcquisitionParametersFormatter apFormatter = new AcquisitionParametersPowershellFormatter();
            AcquisitionParametersSaver apSaver = new AcquisitionParametersSaver(apFormatter);
            apSaver.save(ap.get(i),fileString + "_PARAMS.txt");

            sendImageToSlackAsync(ap.get(i), ap.get(i).cameraData.imagePlus);
        }

        return ap;
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


    private void sendImageToSlackAsync(AcquisitionParameters ap, ImagePlus imagePlus) {
        CompletableFuture.runAsync(() -> {
            try {
                ImageProcessor imageProcessor = imagePlus.getProcessor();

                File jpegFile = new TmpJpegSaver(imageProcessor).save();

                String imageTitle = String.format("%s (%s %.0fms): ext %.1f foc %.1f; p1 %.1f p2 %.1f",
                        ap.generalData.sampleName,
                        ap.generalData.excitation,
                        ap.cameraData.exposureInMs,
                        ap.peemVoltages.extractor,
                        ap.peemVoltages.focus,
                        ap.peemVoltages.projective1,
                        ap.peemVoltages.projective2);
                SlackFileUploader sfu = new SlackFileUploader(imageTitle, jpegFile, SlackFileUploader.MediaType.JPEG);
                sfu.send();
                logger.info("Sent image to Slack");
            } catch (Exception e) {
                logger.warning("Failed sending image to Slack" + e.getMessage());
            }
        });
    }
}
