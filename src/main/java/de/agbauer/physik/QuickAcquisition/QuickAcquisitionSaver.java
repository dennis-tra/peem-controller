package de.agbauer.physik.QuickAcquisition;

import de.agbauer.physik.FileSystem.DataFiler;
import de.agbauer.physik.FileSystem.ImageSaver;
import de.agbauer.physik.PeemCommunicator.PeemCommunicator;
import de.agbauer.physik.QuickAcquisition.AcquisitionParameters.AcquisitionParameters;
import de.agbauer.physik.QuickAcquisition.AcquisitionParameters.CameraData;

import java.io.IOException;

public class QuickAcquisitionSaver implements AcquisitionSaver {
    private PeemCommunicator peemCommunicator;
    private DataFiler filer;

    public QuickAcquisitionSaver(PeemCommunicator peemCommunicator, DataFiler filer) {

        this.peemCommunicator = peemCommunicator;
        this.filer = filer;
    }

    @Override
    public void save(String sampleName, CameraData cameraData) throws IOException {

        AcquisitionParametersCollector apc = new AcquisitionParametersCollector(peemCommunicator);
        AcquisitionParameters ap = apc.collect(sampleName, cameraData);

        filer.setAcquisitionParams(ap);

        ImageSaver imageSaver = new ImageSaver();
        imageSaver.save(cameraData.imagePlus, filer.getTifImageFilePath());

        AcquisitionParametersFormatter apFormatter = new AcquisitionParametersPowershellFormatter();
        AcquisitionParametersSaver apSaver = new AcquisitionParametersSaver(apFormatter);
        apSaver.save(ap, filer.getPeemParametersFilePath());
    }
}
