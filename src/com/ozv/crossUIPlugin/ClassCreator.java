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

        String templatePath = ClassCreator.class.getResource("ClassCreator.class").getFile();
        templatePath = templatePath.substring(0, templatePath.lastIndexOf('/')) + "/templates/" + templateName;
        checkDir(dir);

//
//        try (FileReader reader = new FileReader(path))
//        {
//            int c;
//            while((c=reader.read())!=-1){
//                template += (char)c;
//            }
//        }
//        catch(IOException ex){
//            System.out.println("CANNOT FIND TEMPLATE: " + path);
//        }

        try {
            template = new String(Files.readAllBytes(new File(templatePath).toPath()));
            template = template.replaceAll("\\$<CLASS_NAME>", className);
            template = template.replaceAll("\\$<PACKAGE_NAME>", packageName);
            template = template.replaceAll("\\$<SCREEN_NAME>", screenName);
            File outFile = new File(dir +"/"+ className +".java");
            Files.write(outFile.toPath(), template.getBytes(), StandardOpenOption.CREATE_NEW);
        } catch (IOException e) {
            e.printStackTrace();
        }


//        File srcDir = new File(dir);
//        if (!srcDir.exists()) srcDir.mkdir();
//
//        try(FileWriter writer = new FileWriter( dir +"/"+ className +".java", true))
//        {
//            writer.write(template);
//            writer.flush();
//
//        }
//        catch(IOException ex){
//            System.out.println(ex.getMessage());
//        }
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
