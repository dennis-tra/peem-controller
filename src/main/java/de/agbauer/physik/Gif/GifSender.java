package de.agbauer.physik.Gif;

import de.agbauer.physik.FileSystem.TmpJpegSaver;
import de.agbauer.physik.Logging.SlackFileUploader;
import de.agbauer.physik.PeemCommunicator.PeemProperty;
import org.micromanager.data.*;

import org.micromanager.data.Image;

import javax.imageio.ImageIO;
import javax.imageio.stream.FileImageOutputStream;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

public class GifSender {
    private Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    private ImageJConverter imageJConverter;
    private List<File> tmpImages = new ArrayList<>();

    public GifSender(ImageJConverter imageJConverter) {
        this.imageJConverter = imageJConverter;
    }

    public void addImage(Image image) {
        try {

            File jpegFile = new TmpJpegSaver(image, imageJConverter).save();

            tmpImages.add(jpegFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void sendGifAsync(ArrayList<Double> values, PeemProperty property) {
        CompletableFuture.runAsync(() -> {
            try {
                File gifFile = createGifFromTmpFiles(tmpImages);

                Double firstVal = values.get(0);
                Double lastVal = values.get(values.size() - 1);
                String title = "Optimisation series " + property.displayName() + " (from " + firstVal.toString() + " V to " + lastVal.toString() + " V).";

                SlackFileUploader sfu = new SlackFileUploader(title, gifFile, SlackFileUploader.MediaType.GIF);
                sfu.send();

            } catch (Exception e) {
                logger.warning("Could not save or send gif to slack channel " + e.getMessage());
            }
        });
    }

    private File createGifFromTmpFiles(List<File> tmpImages) throws IOException {
        logger.info("Generating gif from temporary jpeg images");
        BufferedImage firstImage = ImageIO.read(tmpImages.get(0));


        File gifFile = File.createTempFile("gifFile", Long.toString(System.nanoTime()));
        gifFile.deleteOnExit();

        ImageOutputStream output = new FileImageOutputStream(gifFile);

        GifSequenceWriter writer = new GifSequenceWriter(output, firstImage.getType(), 2, true);

        writer.writeToSequence(firstImage);

        for (File image : tmpImages) {
            BufferedImage nextImage = ImageIO.read(image);
            writer.writeToSequence(nextImage);
        }

        writer.close();
        output.close();
        return gifFile;
    }
}
