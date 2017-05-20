package com.ozv.crossUIPlugin;

import com.ozv.crossUIPlugin.projectCreation.ProjectCreator;

import java.util.concurrent.TimeUnit;

/**
 * Created by ozvairon on 20.05.17.
 */
public class TestProjectCreation {
    public static void main(String[] args) {

        String dir = "/Users/ozvairon/Projects/CrossUIDemo/MyProj/";
        String pcg = "com.user.crossui";
        String name = "NewCrossUI";

        boolean pack = false;
        //pack = true;
        if (pack) {
            FrameworkPacker.main(null);
            System.out.println("framework was packed");
            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("project creation");
        ProjectCreator.createProject(dir, name, pcg);
    }
}
