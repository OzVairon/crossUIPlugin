package com.ozv.crossUIPlugin.projectCreation;

import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.util.InvalidDataException;
import org.jdom.JDOMException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
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

        System.out.println("Start creating project \"" + projectName + "\"");
        System.out.println("Copy project files into directory: " + projectDirectory);
        try {
            if (unpackProject()) {
                prepareFiles();
                //openProject();
            } else {
                System.out.println("Fail to unpack project");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean unpackProject() throws IOException {
        boolean result = false;
        String path = resources + "crossUI.zip";

        try {
            // Open the zip file
            ZipFile zipFile = new ZipFile(path);
            Enumeration<?> enu = zipFile.entries();
            while (enu.hasMoreElements()) {
                ZipEntry zipEntry = (ZipEntry) enu.nextElement();

                String name = zipEntry.getName();
                long size = zipEntry.getSize();
                long compressedSize = zipEntry.getCompressedSize();
                System.out.printf("name: %-20s | size: %6d | compressed size: %6d\n",
                        name, size, compressedSize);

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

    private void prepareFiles() {

        File project = new File(projectDirectory + "crossUI");
        File newProject = new File(projectDirectory + projectName);
        //File newProject = new File(projectDirectory.substring(0, projectDirectory.length()-1));

        if (project.isDirectory()) {
            project.renameTo(newProject);
        } else {
            project.mkdir();
            project.renameTo(newProject);
        }

        projectDirectory += projectName + "/";
        try {
            File macosdir = new File(projectDirectory + "__MACOSX");
            for (File s : macosdir.listFiles()) {
                System.out.println(s.getCanonicalPath());
                s.delete();
            }
            macosdir.delete();
        } catch(Exception ex) {}


        String packageDir = packageName.replace('.','/') + "/";

        String[] modules = {"core", "android", "desktop", "ios"};





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
