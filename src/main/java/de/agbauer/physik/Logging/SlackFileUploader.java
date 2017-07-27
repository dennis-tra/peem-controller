package de.agbauer.physik.Logging;

import de.agbauer.physik.Constants;
import okhttp3.*;

import java.io.File;
import java.util.logging.Logger;

public class SlackFileUploader {
    private Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    private String title;
    private File file;
    private MediaType mediaType;

    public enum MediaType {
        GIF, JPEG;

        public okhttp3.MediaType parse() throws Exception {
            switch (this) {
                case GIF:
                    return okhttp3.MediaType.parse("image/gif");
                case JPEG:
                    return okhttp3.MediaType.parse("image/jpeg");
                default:
                    throw new Exception("Unsupported Mediatype");
            }
        }
    }

    public SlackFileUploader(String title, File file, MediaType mediaType) {
        this.title = title;
        this.file = file;
        this.mediaType = mediaType;
    }

    public void send() throws Exception {
        logger.info("Sending file to slack channel #peem-lab");
        OkHttpClient client = new OkHttpClient();

        RequestBody requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("file", this.title, RequestBody.create(this.mediaType.parse(), this.file))
                .addFormDataPart("token", Constants.slackBotToken)
                .addFormDataPart("filename", this.title)
                .addFormDataPart("channels", "#peem-lab")
                .build();

        Request request = new Request.Builder().url("https://slack.com/api/files.upload")
                .post(requestBody).build();

        Response response = client.newCall(request).execute();
        response.body().close();

    }
}
