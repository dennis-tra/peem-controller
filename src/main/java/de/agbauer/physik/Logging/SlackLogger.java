package de.agbauer.physik.Logging;

import de.agbauer.physik.Generic.Constants;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.json.JSONObject;

import javax.swing.*;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

/**
 * Created by dennis on 17/02/2017.
 */
public class SlackLogger extends Handler {
    @Override
    public synchronized void publish(LogRecord record) {
        if (!record.getMessage().startsWith("Slack")) {
            return;
        }

        OkHttpClient client = new OkHttpClient();

        JSONObject payload = new JSONObject();

        try {
            payload.put("username", "PEEM");
            payload.put("link_names", 1);

            payload.put("text", "@channel " + record.getMessage());
            payload.put("icon_emoji", ":camera:");

            RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), payload.toString());

            Request request = new Request.Builder()
                    .url("https://hooks.slack.com/services/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")
                    .post(body)
                    .build();

            client.newCall(request).execute();
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
