package com.ozv.crossUIPlugin;

import com.ozv.crossUIPlugin.projectCreation.ProjectCreator;

/**
 * Created by ozvairon on 20.05.17.
 */
public class TestProjectCreation {
    public static void main(String[] args) {

        String dir = "/Users/ozvairon/Projects/CrossUIDemo/MyProj/";
        String pcg = "com.user.....crossui";
        String name = "NewCrossUI";

        boolean pPack = false;
        boolean fPack = false;
        //pPack = true;
        //fPack = true;

        if (fPack) {
            JarPacker.main(null);
        }


        if (pPack) {
            FrameworkPacker.main(null);
        }

        if (!(pPack || fPack)) {
            System.out.println("project creation");
            ProjectCreator.createProject(dir, name, pcg, false);
        }
    }
}
