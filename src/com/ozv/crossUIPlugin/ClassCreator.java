package com.ozv.crossUIPlugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

/**
 * Created by ozvairon on 19.05.17.
 */
public class ClassCreator {

    public static void createNewClass(String dir, String packageName, String className, String screenName, String templateName) {

        String template = "";

        if (!ResourceLoader.isReady()) {
            ResourceLoader.setTempDir(dir);
        }
        String templatePath = ClassCreator.class.getResource("ClassCreator.class").getPath();
        templatePath = templatePath.substring(0, templatePath.lastIndexOf('/')) + "/templates/" + templateName;
        checkDir(dir);

        templatePath = ResourceLoader.loadFile(templateName).getPath();

        try {
            //template = new String(Files.readAllBytes(new File(templatePath).toPath()));
            template = new String(Files.readAllBytes(ResourceLoader.loadFile(templateName).toPath()));
            template = template.replaceAll("\\$<CLASS_NAME>", className);
            template = template.replaceAll("\\$<PACKAGE_NAME>", packageName);
            template = template.replaceAll("\\$<SCREEN_NAME>", screenName);
            File outFile = new File(dir +"/"+ className +".java");
            Files.write(outFile.toPath(), template.getBytes(), StandardOpenOption.CREATE_NEW);
        } catch (IOException e) {
            ErrorNotifier.push(ErrorNotifier.ERROR, "cannot find template", templateName);
            ErrorNotifier.push(ErrorNotifier.ERROR, e.getMessage(), e.getStackTrace());
            e.printStackTrace();
        }

        ResourceLoader.clear();

    }

    private static void checkDir(String dir) {
        File f = new File(dir);

        if (!f.exists()) {
            String parenDir = dir.substring(0, dir.lastIndexOf('/'));
            checkDir(parenDir);
            f.mkdir();
        }

    }


}
