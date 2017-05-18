package com.ozv.crossUI.ClassCreator;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by ozvairon on 19.05.17.
 */
public class ClassCreator {
    public static void main(String[] args) {
        ClassCreator cc = new ClassCreator();
        cc.createNewClass("/Users/ozvairon/Projects/CrossUIPlugin/ClassCreator", "com.ozv.crossUI.ClassCreator", "NewScreen");
    }

    void createNewClass(String dir, String packageName, String name) {

        String template = "";
        try(FileReader reader = new FileReader("ScreenTemplate"))
        {
            int c;
            while((c=reader.read())!=-1){
                template += (char)c;
            }
        }
        catch(IOException ex){
            System.out.println(ex.getMessage());
        }

        template = template.replaceAll("\\$<CLASS_NAME>", name);
        template = template.replaceAll("\\$<PACKAGE_NAME>", packageName);
        template = template.replaceAll("\\$<SCREEN_NAME>", name);

        try(FileWriter writer = new FileWriter(dir + "/" + name +".java", false))
        {
            // запись всей строки

            writer.write(template);
            writer.flush();
        }
        catch(IOException ex){

            System.out.println(ex.getMessage());
        }



    }
}
