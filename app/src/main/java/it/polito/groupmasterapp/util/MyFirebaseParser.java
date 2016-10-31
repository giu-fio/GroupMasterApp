package it.polito.groupmasterapp.util;

/**
 * Created by giuseppe on 30/07/16.
 */
public class MyFirebaseParser {
    public static Integer parseInt(Object val) {
        if (val == null) return null;
        return ((Long) val).intValue();
    }

    public static Double parseDouble(Object value) {
        if (value instanceof Long) return ((Long) value).doubleValue();
        return (Double) value;
    }
}
