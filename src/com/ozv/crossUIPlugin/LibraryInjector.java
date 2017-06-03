package com.ozv.crossUIPlugin;

import com.android.utils.FileUtils;

import java.io.File;
import java.io.IOException;

/**
 * Created by ozvairon on 04.06.17.
 */
public class LibraryInjector {

    public String path;
    public String packageName;


    public static void main(String[] args) {
        LibraryInjector l = new LibraryInjector();
        l.path = "/Users/ozvairon/Projects/CrossUIDemo/TestProj/MyProject/";
        l.packageName = "com.user.myproject";

        l.execute();

    }

    public void execute() {
        String[] modules = {"core", "android", "desktop", "ios"};
        for (String module : modules) {
            try {
                integrateFramework(module);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void integrateFramework(String module) throws IOException {
        //File frameworkJar = new File(resources + "crossUIlib.jar");
        File frameworkJar = new File("/Users/ozvairon/Projects/CrossUIPlugin/out/production/CrossUIPlugin/com/ozv/crossUIPlugin/templates/crossUIlib.jar");

        File moduleLib = new File(path + module + "/libs");

        if (!moduleLib.exists()) moduleLib.mkdir();

        File existedJar = new File (path + module + "/libs/crossUIlib.jar");

        if (existedJar.exists()) {
            existedJar.delete();
        }
        FileUtils.copyFileToDirectory(frameworkJar, moduleLib);
    }
}
