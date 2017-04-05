package de.agbauer.physik.Logging;

import de.agbauer.physik.Generic.Constants;
import okhttp3.*;
import org.apache.tomcat.util.bcel.Const;
import org.json.JSONObject;

import java.util.logging.Handler;
import java.util.logging.LogRecord;

public class SlackLogger extends Handler {
    @Override
    public synchronized void publish(LogRecord record) {
        if (!record.getMessage().startsWith("Slack") && isLoggable(record)) {
            return;
        }

        String logMessage = record.getMessage().substring("Slack: ".length());


        try {
            OkHttpClient client = new OkHttpClient();

            RequestBody requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                    .addFormDataPart("token", Constants.slackBotToken)
                    .addFormDataPart("channel", "#peem-lab")
                    .addFormDataPart("text", logMessage)
                    .addFormDataPart("link_names", "true")
                    .addFormDataPart("as_user", "true")
                    .build();

            Request request = new Request.Builder().url("https://slack.com/api/chat.postMessage")
                    .post(requestBody).build();

            Response response = client.newCall(request).execute();
            response.body().close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void flush() {

    }

    @Override
    public void close() throws SecurityException {

    }
}
