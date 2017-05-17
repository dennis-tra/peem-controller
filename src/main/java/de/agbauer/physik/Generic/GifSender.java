package de.agbauer.physik.Generic;

import de.agbauer.physik.PEEMCommunicator.PEEMProperty;
import ij.io.FileSaver;
import ij.ImagePlus;
import ij.process.ImageProcessor;
import okhttp3.*;
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
import java.util.stream.IntStream;

public class GifSender {
    private Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    private ImageJConverter imageJConverter;
    private List<File> tmpImages = new ArrayList<>();

    public GifSender(ImageJConverter imageJConverter) {
        this.imageJConverter = imageJConverter;
    }

    public void addImage(Image image) {
        try {
            ImageProcessor ip = this.imageJConverter.createProcessor(image);
            int[] histogram = ip.getHistogram();
            int sum = IntStream.of(histogram).sum();
            int tenPercent = Math.round(sum * 0.1f);

            int tmpSum = 0;
            int upperLimit = 250;

            for (int i = histogram.length - 1; i >= 0; i--) {
                tmpSum += histogram[i];

                if (tmpSum > tenPercent) {
                    upperLimit = i;
                    break;
                }
            }

            ip.setMinAndMax(40, upperLimit);
            ImagePlus imagePlus = new ImagePlus("", ip);

            FileSaver.setJpegQuality(40);
            FileSaver fileSaver = new FileSaver(imagePlus);

            File jpegFile = File.createTempFile("jpegFile", Long.toString(System.nanoTime()));
            jpegFile.deleteOnExit();

            fileSaver.saveAsJpeg(jpegFile.getAbsolutePath());
            tmpImages.add(jpegFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void sendGifAsync(ArrayList<Float> values, PEEMProperty property) {
        CompletableFuture.runAsync(() -> {
            try {
                File gifFile = createGifFromTmpFiles(tmpImages);

                Float firstVal = values.get(0);
                Float lastVal = values.get(values.size() - 1);
                String title = "Optimisation series " + property.displayName() + " (from " + firstVal.toString() + " V to " + lastVal.toString() + " V).";

                sendImageToSlack(title, gifFile);
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

    private void sendImageToSlack(String title, File imageFile) throws Exception {
        logger.info("Sending gif to slack channel #peem-lab");
        OkHttpClient client = new OkHttpClient();

        RequestBody requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("file", title, RequestBody.create(MediaType.parse("image/gif"), imageFile))
                .addFormDataPart("token", Constants.slackBotToken)
                .addFormDataPart("filename", title)
                .addFormDataPart("channels", "#peem-lab")
                .build();

        Request request = new Request.Builder().url("https://slack.com/api/files.upload")
                .post(requestBody).build();

        Response response = client.newCall(request).execute();
        response.body().close();

    }
}
