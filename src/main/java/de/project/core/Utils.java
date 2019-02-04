package de.project.core;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Helper class for static methods.
 */
public class Utils {

    private static final String LOG_DIR = "logs/" + new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date()) + "/";
    public static final Logger MAIN_LOGGER = Logger.getLogger("collision_prevention");

    static {
        new File(LOG_DIR).mkdir();
        addFileHandler(MAIN_LOGGER, "main_log.log");
        de.pdbm.janki.core.Logger.switchLogTypeOn(de.pdbm.janki.core.LogType.values());
        MAIN_LOGGER.setLevel(Level.ALL);
    }

    public static void addFileHandler(Logger logger, String fileName) {
        try {
            LogFormatter formatter = new LogFormatter();
            FileHandler fh = new FileHandler(LOG_DIR + fileName);
            fh.setFormatter(formatter);
            logger.addHandler(fh);
        } catch (IOException e) {
            System.out.println("cannot create file logger. " + e);
        }
    }

    public static int getRandomValue(int min, int max){
        return (int) (Math.random() * ((max - min) + 1)) + min;
    }

    public static void sleep(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static double secToMs(double sec) {
        return sec * 1000.0;
    }

    public static double msToSec(double ms) {
        return ms / 1000.0;
    }
}
