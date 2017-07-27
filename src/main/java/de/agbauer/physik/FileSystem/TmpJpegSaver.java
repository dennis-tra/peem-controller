package de.agbauer.physik.FileSystem;

import ij.ImagePlus;
import ij.io.FileSaver;
import ij.process.ImageProcessor;
import ij.process.ImageStatistics;
import org.micromanager.data.Image;
import org.micromanager.data.ImageJConverter;

import java.io.File;
import java.io.IOException;

public class TmpJpegSaver {

    private ImageProcessor imageProcessor;

    public TmpJpegSaver(ImageProcessor imageProcessor) {
        this.imageProcessor = imageProcessor;
    }

    public TmpJpegSaver(Image image, ImageJConverter ijConverter) {
        this.imageProcessor =  ijConverter.createProcessor(image);
    }

    public File save(int jpegQuality) throws IOException {

        ImageStatistics imageStatistics = imageProcessor.getStatistics();

        // From here http://wiki.cmci.info/documents/120206pyip_cooking/python_imagej_cookbook#automatic_brightnesscontrast_button
        int totalPixelCount = imageStatistics.pixelCount;
        int[] histogram = imageProcessor.getHistogram();

        int limit = totalPixelCount/10;
        int threshold = totalPixelCount / 5000;

        int lowerLimit = 0;
        int upperLimit = 250;

        for (int i = 0; i < histogram.length; i++) {
            int count = histogram[i];

            if (count > threshold && count < limit) {
                lowerLimit = i;
                break;
            }
        }

        for (int i = histogram.length - 1; i >= 0; i--) {
            int count = histogram[i];

            if (count > threshold && count < limit) {
                upperLimit = i;
                break;
            }
        }

        imageProcessor.setMinAndMax(lowerLimit, upperLimit);
        ImagePlus imagePlus = new ImagePlus("", imageProcessor);

        FileSaver.setJpegQuality(jpegQuality);
        FileSaver fileSaver = new FileSaver(imagePlus);

        File jpegFile = File.createTempFile("jpegFile", Long.toString(System.nanoTime()));
        jpegFile.deleteOnExit();

        fileSaver.saveAsJpeg(jpegFile.getAbsolutePath());

        return jpegFile;
    }

    public File save() throws IOException {
        return this.save(40);
    }
}
