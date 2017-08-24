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
