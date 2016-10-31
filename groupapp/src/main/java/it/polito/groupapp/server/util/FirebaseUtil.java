package it.polito.groupapp.server.util;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.Map;

/**
 * Created by giuseppe on 19/10/16.
 */
public class FirebaseUtil {


    public static final String KEY_FILE = "GroupApp-72017076a5d3.json";
    public static final String KEY_FILE2 = "groupapp/GroupApp-c5a980c65f9c.json";
    public static final String KEY_ID_FILE = "groupapp/key_id.json";
    public static final String DB_URL = "https://groupapp-8174f.firebaseio.com/";

    public static void initFirebase() {

        FirebaseOptions options = null;
        try {
            options = new FirebaseOptions.Builder()
                    .setServiceAccount(new FileInputStream(KEY_FILE2))
                    .setDatabaseUrl(DB_URL)
                    .build();
        } catch (FileNotFoundException e) {
            System.out.println("File non trovato: " + KEY_FILE2);
            e.printStackTrace();

        }
        FirebaseApp.initializeApp(options);

        System.out.println("Firebase initialized");
    }

    public static String getId() {
        Gson gson = new GsonBuilder().create();
        Type typeOfHashMap = new TypeToken<Map<String, String>>() {
        }.getType();
        Map<String, String> map;
        try {
            map = gson.fromJson(new FileReader(KEY_ID_FILE), typeOfHashMap);
        } catch (FileNotFoundException e) {
            System.out.println("File non trovato");
            e.printStackTrace();
            return null;
        }
        System.out.println("ID: " + map.get("key_id"));
        return map.get("key_id");
    }
}
