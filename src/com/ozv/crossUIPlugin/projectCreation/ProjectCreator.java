package com.ozv.crossUIPlugin.projectCreation;

import com.android.utils.FileUtils;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.util.ArrayUtil;
import com.ozv.crossUIPlugin.ClassCreator;
import org.jdom.JDOMException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Created by ozvairon on 20.05.17.
 */
public class ProjectCreator {

    private String projectDirectory;
    private String projectName;
    private String packageName;

    private String resources;

    public ProjectCreator(String projectDirectory, String projectName, String packageName) {
        this.packageName = packageName;
        this.projectDirectory = projectDirectory;
        this.projectName = projectName;

        if (this.projectDirectory.lastIndexOf('/') != this.projectDirectory.length()-1) {
            this.projectDirectory += '/';
        }

        resources = ProjectCreator.class.getResource("ProjectCreator.class").getFile();
        resources = resources.substring(0, resources.lastIndexOf('/'));
        resources = resources.substring(0, resources.lastIndexOf('/')) + "/templates/";
    }

    public static void createProject(String projectDirectory, String projectName, String packageName) {
        ProjectCreator creator = new ProjectCreator(projectDirectory, projectName, packageName);
        creator.start();
    }

    public void start() {
        try {
            System.out.println("Start creating project \"" + projectName + "\"");

            FileUtils.cleanOutputDir(new File(projectDirectory));
            System.out.println("Copy project files into directory: " + projectDirectory);

            if (unpackProject()) {
                prepareFiles();
                openProject();
            } else {
                System.out.println("Fail to unpack project");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean unpackProject() throws IOException {
        boolean result = false;
        String path = resources + "crossUIProject.zip";

        try {
            // Open the zip file
            ZipFile zipFile = new ZipFile(path);
            Enumeration<?> enu = zipFile.entries();
            while (enu.hasMoreElements()) {
                ZipEntry zipEntry = (ZipEntry) enu.nextElement();

                String name = zipEntry.getName();
                long size = zipEntry.getSize();
                long compressedSize = zipEntry.getCompressedSize();

                // Do we need to create a directory ?
                File file = new File(projectDirectory + name);
                if (name.endsWith("/")) {
                    file.mkdirs();
                    continue;
                }

                File parent = file.getParentFile();
                if (parent != null) {
                    parent.mkdirs();
                }

                // Extract the file
                InputStream is = zipFile.getInputStream(zipEntry);
                FileOutputStream fos = new FileOutputStream(file);
                byte[] bytes = new byte[1024];
                int length;
                while ((length = is.read(bytes)) >= 0) {
                    fos.write(bytes, 0, length);
                }
                is.close();
                fos.close();

            }
            zipFile.close();
            result = true;
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("unpacking finished successful");
        return result;
    }

    private void prepareFiles() throws IOException {

        renameProjectFolder();

        projectDirectory += projectName + "/";
        try {
            File macosdir = new File(projectDirectory + "__MACOSX");
            for (File s : macosdir.listFiles()) {
                s.delete();
            }
            macosdir.delete();
        } catch(Exception ex) {}



        //prepare modules
        String[] modules = {"core", "android", "desktop", "ios"};
        for (String module : modules) {
            createSourceFiles(module);
            integrateFramework(module);
        }

        fixConfigFiles();
    }

    private void renameProjectFolder() throws IOException {
        File project = new File(projectDirectory + "crossUI");
        File newProject = new File(projectDirectory + projectName);

        if (!project.isDirectory()) {
            project.mkdir();
        }
        FileUtils.copyDirectory(project, newProject);
        FileUtils.cleanOutputDir(project);
        FileUtils.delete(project);
    }

    private void createSourceFiles(String module) {
        String[] packageFolders = packageName.split("\\.");
        String moduleDir = projectDirectory + "";
        String[] mf = {module, "src"};

        String[] dirArr = (String[]) ArrayUtil.mergeArrays(mf, packageFolders);

        for (String p : dirArr) {
            moduleDir += p + "/";
            File f = new File(moduleDir);
            f.mkdir();
        }

        String className = "";
        if (module.equals("core")) {
            className = projectName;
        } else if (module.equals("ios")) {
            className = "IOSLauncher";
        } else {
            className = module.substring(0, 1).toUpperCase() + module.substring(1) + "Launcher";
        }

        ClassCreator.createNewClass(moduleDir, packageName, className, projectName, module + "Template");
    }

    private void fixConfigFiles()  {
        String[] files = {
                "android/build.gradle",
                "android/AndroidManifest.xml",
                "android/assets/appconfig.xml",
                "android/res/values/strings.xml",
                "desktop/build.gradle",
                "ios/robovm.properties",
                "build.gradle"

        };

        for (String filepath : files) {
            File file = new File(projectDirectory + filepath);
            String src = null;
            try {
                src = new String(Files.readAllBytes(file.toPath()));
                src = src.replaceAll("\\$<PACKAGE_NAME>", packageName);
                src = src.replaceAll("\\$<APP_NAME>", projectName);

                Files.write(file.toPath(), src.getBytes(), StandardOpenOption.WRITE);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    private void integrateFramework(String module) {
        try {
            File frameworkJar = new File(resources + "crossUIlib.jar");
            File moduleLib = new File(projectDirectory + module + "/libs");

            if (!moduleLib.exists()) moduleLib.mkdir();

            FileUtils.copyFileToDirectory(frameworkJar, moduleLib);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }




    private void openProject() {
        System.out.println("Open new project");
        try {
            ProjectManager.getInstance().loadAndOpenProject(projectDirectory + "build.gradle");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JDOMException e) {
            e.printStackTrace();
        } catch (InvalidDataException e) {
            e.printStackTrace();
        }

        System.out.println("Creating project is done");
    }
}
