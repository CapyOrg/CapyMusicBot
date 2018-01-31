package org.capy.musicbot;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;

public class Utils {

    public static String dateFormat(Date date) {
        return new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(date);
    }

    public static String dateFormat(Instant date) {
        return new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(date);
    }

}
