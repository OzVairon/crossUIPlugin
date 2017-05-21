package com.ozv.crossUIPlugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

/**
 * Created by ozvairon on 21.05.17.
 */
public class ErrorNotifier {

    private static String outDir = "";

    public static final String MESSAGE = "message";
    public static final String ERROR = "error";
    public static final String WARNING = "warning";


    public static void push(String type, String title, StackTraceElement[] st) {
        String name = type + System.currentTimeMillis();

        String content = title + "\n\n";
        for (StackTraceElement ste : st) {
            content += ste.toString() + "\n";
        }

        write(name, content);
    }

    public static void push(String type, String title, String src) {
        String name = type + System.currentTimeMillis();

        String content = title + "\n\n" + src;

        write(name, content);
    }

    private static void write(String name, String content) {
        try {
            Files.write(new File(outDir + "/" + name).toPath(), content.getBytes(), StandardOpenOption.CREATE_NEW);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void setOutDir(String path) {
        outDir = path;
    }
}
