package de.agbauer.physik.DelayStageServerCommunicator;

import de.agbauer.physik.FileSystem.DataFiler;
import de.agbauer.physik.FileSystem.ImageSaver;
import de.agbauer.physik.OptimisationSeries.OptimisationSeriesParameters;
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
import java.util.logging.Logger;

public class TimeResolvedSaver {
    private final AcquisitionParametersCollector apc;
    private PeemCommunicator peemCommunicator;
    private DataFiler filer;
    private Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    private AcquisitionParameters finalAp;
    private File directory;

    public TimeResolvedSaver(PeemCommunicator peemCommunicator, DataFiler filer) {
        this.peemCommunicator = peemCommunicator;
        this.filer = filer;
        this.apc = new AcquisitionParametersCollector(this.peemCommunicator);
    }

    public void getAcquisitionParameters(String sampleName, TimeResolvedParameters trParameters) throws IOException {
        CameraData dummyData = new CameraData(null, trParameters.exposureTimeInSeconds * 1000,1);
        finalAp = apc.collect(sampleName, dummyData);


        int trNumber = 1;
        directory = new File(filer.getWorkingDirectoryFor(sampleName) + "tr" + trNumber + File.separator);

        while(directory.exists()){
            trNumber++;
            directory = new File(filer.getWorkingDirectoryFor(sampleName) + "tr" + trNumber + File.separator);
        }


        directory.mkdirs();

        TimeResolvedSummarySaver.startSummary(directory.getAbsolutePath() + File.separator + "summary.txt", finalAp);
    }

    public AcquisitionParameters save(TimeResolvedParameters trParameters, ImagePlus image, int idx) throws Exception {
        logger.info("Saving time resolved measurement...");

        CameraData cameraData = new CameraData(image, trParameters.exposureTimeInSeconds * 1000,1);
        AcquisitionParameters ap = apc.collectSilently(cameraData, finalAp.generalData);
        ap.imageNumber = idx + 1;
        ap.timeOffsetInFs = trParameters.values.get(idx);

        embedAcquisitonParameters(ap.cameraData, ap);

        String fileString = directory.getAbsolutePath() + File.separator
                + filer.generateScopeName(ap.generalData.sampleName) + "_" + String.format("%d_", idx + 1)
                + String.format("%.0f", trParameters.values.get(idx) * 1000)  + "as";

        ImageSaver imageSaver = new ImageSaver();
        imageSaver.save(ap, ap.cameraData.imagePlus,fileString + ".tif");

        AcquisitionParametersFormatter apFormatter = new AcquisitionParametersPowershellFormatter();
        AcquisitionParametersSaver apSaver = new AcquisitionParametersSaver(apFormatter);
        apSaver.save(ap,fileString + "_PARAMS.txt");

        TimeResolvedSummarySaver.append(directory.getAbsolutePath() + File.separator + "summary.txt", trParameters, idx);
        logger.info("Finished saving time resolved measurement number " + idx + "...");

        return ap;
    }

    private void embedAcquisitonParameters(CameraData cameraData, AcquisitionParameters ap) {

        cameraData.imagePlus.setProperty("Sample Name", ap.generalData.sampleName);
        cameraData.imagePlus.setProperty("Excitation", ap.generalData.excitation);
        cameraData.imagePlus.setProperty("Aperture", ap.generalData.aperture);
        cameraData.imagePlus.setProperty("Note", ap.generalData.note);
        cameraData.imagePlus.setProperty("Binning", ap.cameraData.binning);
        cameraData.imagePlus.setProperty("Exposure[ms]", ap.cameraData.exposureInMs);
        cameraData.imagePlus.setProperty("Time offset [fs]", ap.timeOffsetInFs);

        for (PeemProperty peemProperty: PeemProperty.values()) {
            cameraData.imagePlus.setProperty(peemProperty.displayName() + " U", ap.peemVoltages.get(peemProperty));
            cameraData.imagePlus.setProperty(peemProperty.displayName() + " I", ap.peemCurrents.get(peemProperty));
        }
    }
}
