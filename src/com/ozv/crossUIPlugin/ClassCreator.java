package com.ozv.crossUIPlugin;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by ozvairon on 19.05.17.
 */
public class ClassCreator {

    public static void createNewClass(String dir, String packageName, String className, String screenName, String templateName) {

        String template = "";

        //String path = ClassCreator.class.getProtectionDomain().getCodeSource().getLocation().getPath();

        String path = ClassCreator.class.getResource("ClassCreator.class").getFile();
        path = path.substring(0, path.lastIndexOf('/'));
        path = path.substring(0, path.lastIndexOf('/')) + "/templates/" + templateName;

        System.out.println(path);
        try (FileReader reader = new FileReader(path))
        {
            int c;
            while((c=reader.read())!=-1){
                template += (char)c;
            }
        }
        catch(IOException ex){
            System.out.println("CANNOT FIND TEMPLATE");
        }

        template = template.replaceAll("\\$<CLASS_NAME>", className);
        template = template.replaceAll("\\$<PACKAGE_NAME>", packageName);
        template = template.replaceAll("\\$<SCREEN_NAME>", screenName);

        try(FileWriter writer = new FileWriter( dir +"/"+ className +".java", false))
        {
            writer.write(template);
            writer.flush();

        }
        catch(IOException ex){
            System.out.println(ex.getMessage());
        }
    }
}
