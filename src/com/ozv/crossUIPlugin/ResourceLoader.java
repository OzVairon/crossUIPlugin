package com.ozv.crossUIPlugin;

import java.io.*;

/**
 * Created by ozvairon on 21.05.17.
 */
public class ResourceLoader {

    private static String tempDir = "";
    private static boolean ready = false;

    private static String pcg = "/com/ozv/crossUIPlugin/templates/";
    public static File loadFile(String name) {

        if (!ready) {
            ErrorNotifier.push(ErrorNotifier.ERROR, "ResourceLoader doesn't ready", "");
        }
        try {

            InputStream is = ResourceLoader.class.getResourceAsStream(pcg + name);
            File tempFile = new File(tempDir + name);

            FileOutputStream fileOutputStream = new FileOutputStream(tempFile);
            byte[] buffer = new byte[32768];
            int length;
            while ((length = is.read(buffer)) > 0) {
                fileOutputStream.write(buffer, 0, length);
            }
            fileOutputStream.close();
            is.close();

            return tempFile;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            ErrorNotifier.push(ErrorNotifier.ERROR, e.getMessage(), e.getStackTrace());
        } catch (IOException e) {
            e.printStackTrace();
            ErrorNotifier.push(ErrorNotifier.ERROR, e.getMessage(), e.getStackTrace());
        }
        return null;
    }

    public static void setTempDir(String path) {
        tempDir = path + "temp/";
        if (new File(tempDir).mkdir()) ready = true;
    }

}
