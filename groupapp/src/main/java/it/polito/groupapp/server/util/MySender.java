package it.polito.groupapp.server.util;

import com.google.api.client.http.*;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.http.json.JsonHttpContent;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.ExponentialBackOff;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.io.IOUtils;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by giuseppe on 29/05/16.
 */
public class MySender {

    public static void sendToTopic(String topic, Map<String, String> data, String keyId) {
        send("/topics/" + topic, data, keyId);
    }

    public static boolean send(String to, Map<String, String> data, String keyId, int maxTent, long time) {
        boolean res = false;
        while (!res && maxTent > 0) {
            res = send(to, data, keyId);
            maxTent--;
            try {
                Thread.sleep(time);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return res;
    }

    public static boolean send(String to, Map<String, String> data, String keyId) {
        if (to == null || data == null) {
            throw new NullPointerException("topic e data non possono essere null");
        }
       // System.out.println("Send  " + data);
        try {
            //preparazione  JSON contenente il FCM content. Cosa mandare e dove mandare
            JSONObject jFcmData = new JSONObject();
            JSONObject jData = new JSONObject();
            for (String key : data.keySet()) {
                jData.put(key, data.get(key));
            }
            jFcmData.put("to", to);
            jFcmData.put("data", jData);

            //creazione connessione per mandare FCM Message request
            URL url = new URL("https://fcm.googleapis.com/fcm/send");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("Authorization", "key=" + keyId);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);

            // Invio FCM message content.
            OutputStream outputStream = conn.getOutputStream();
            outputStream.write(jFcmData.toString().getBytes());

            // Lettura FCM response.
            InputStream inputStream = conn.getInputStream();
            String resp = IOUtils.toString(inputStream);
            Gson gson = new Gson();
            Type typeOfHashMap = new TypeToken<Map<String, Object>>() {
            }.getType();
            Map<String, Object> newMap = gson.fromJson(resp, typeOfHashMap);

            //String result = ((double) newMap.get("success") == 1) ? "SUCCESS" : "FAILURE";
            //System.out.println("Response: " + result);
        /*    if ((double) newMap.get("success") == 0) {
                System.out.println("Dettagli " + resp);
            }
            return ((double) newMap.get("success") == 1);
*/
        } catch (IOException e) {
            System.out.println("Unable to send GCM message.");
            System.out.println("Please ensure that API_KEY has been replaced by the server " +
                    "API key, and that the device's registration token is correct (if specified).");
            e.printStackTrace();
        }
        return true;
    }

    public static void sendToTopic2(String topic, Map<String, String> data, String keyId) {
        HttpTransport transport = new NetHttpTransport();
        HttpRequestFactory requestFactory = transport.createRequestFactory(request -> {
            HttpHeaders headers = new HttpHeaders();
            headers.setAuthorization("key=" + keyId);
            headers.setContentType("application/json");
            request.setHeaders(headers);
        });
        //preparazione  JSON contenente il FCM content. Cosa mandare e dove mandare
        Map<String, Object> datamap = new HashMap<>();
        datamap.put("to", "topics/" + topic);
        datamap.put("data", data);
        HttpContent content = new JsonHttpContent(new JacksonFactory(), datamap);
        try {
            content.writeTo(System.out);
        } catch (IOException e) {
            e.printStackTrace();
        }

        HttpRequest request = null;
        try {
            request = requestFactory.buildPostRequest(new GenericUrl("https://fcm.googleapis.com/fcm/send"), content);
            request.setUnsuccessfulResponseHandler(new HttpBackOffUnsuccessfulResponseHandler(new ExponentialBackOff()));
            System.out.println("Invio ...");
            com.google.api.client.http.HttpResponse response = request.execute();
            String resp = IOUtils.toString(response.getContent());
            System.out.println("Response " + resp);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
