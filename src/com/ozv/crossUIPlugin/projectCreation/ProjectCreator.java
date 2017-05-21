package com.ozv.crossUIPlugin.projectCreation;

import com.android.utils.FileUtils;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.util.ArrayUtil;
import com.ozv.crossUIPlugin.ClassCreator;
import com.ozv.crossUIPlugin.screenCreation.ScreenDialog;
import org.jdom.JDOMException;

import java.io.*;
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
    private String packagepath;
    private boolean pluginMode = true;

    private String resources;

    public ProjectCreator(String projectDirectory, String projectName, String packageName) {
        this.projectDirectory = projectDirectory;

        this.packageName = packageName.replaceAll("[~\"#%&*:;<>?/{|} ,\\\\]" , "");

        String[] p = this.packageName.split("\\.");
        this.packageName = "";
        for (int i = 0; i < p.length; i++) {
            if (p[i].length() != 0) {
                this.packageName += p[i];
                if (i < p.length - 1) {
                    this.packageName +=".";
                }
            }
        }

        this.projectName = projectName.replaceAll("[~\"#%&*:;<>?/{|}. ,\\\\]" , "");
        this.projectName = this.projectName.trim();
        this.projectName = this.projectName.substring(0, 1).toUpperCase() + this.projectName.substring(1);
        this.packagepath = this.packageName.replaceAll("\\.","/");


        if (this.projectDirectory.lastIndexOf('/') != this.projectDirectory.length()-1) {
            this.projectDirectory += '/';
        }

        resources = ProjectCreator.class.getResource("ProjectCreator.class").getFile();
        resources = resources.substring(0, resources.lastIndexOf('/'));
        resources = resources.substring(0, resources.lastIndexOf('/')) + "/templates/";
    }

    public static void createProject(String projectDirectory, String projectName, String packageName) throws IOException {
        ProjectCreator creator = new ProjectCreator(projectDirectory, projectName, packageName);
        creator.start();
    }

    public static void createProject(String projectDirectory, String projectName, String packageName, boolean open) {
        ProjectCreator creator = new ProjectCreator(projectDirectory, projectName, packageName);
        creator.pluginMode = open;
        try {
            creator.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void start() throws IOException {
        System.out.println("Start creating project \"" + projectName + "\"");

//        FileUtils.cleanOutputDir(new File(projectDirectory));
        System.out.println("Copy project files into directory: " + projectDirectory);

        if (unpackProject()) {
            prepareFiles();
            if (!pluginMode) {
                createScreen();
            }
        } else {
            System.out.println("Fail to unpack project");
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

        if (newProject.exists()) {
            int i = 0;
            while (true) {
                i++;
                File temp = new File(projectDirectory + projectName + i);
                if (!temp.exists()) {
                    temp.mkdir();
                    newProject = temp;
                    break;
                }
            }
            projectDirectory += projectName + i + "/";
        } else {
            projectDirectory += projectName + "/";
        }
        if (!project.isDirectory()) {
            project.mkd
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

                Files.write(file.toPath(), src.getBytes(), StandardOpenOption.TRUNCATE_EXISTING);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void integrateFramework(String module) throws IOException {
        File frameworkJar = new File(resources + "crossUIlib.jar");
        File moduleLib = new File(projectDirectory + module + "/libs");

        if (!moduleLib.exists()) moduleLib.mkdir();

        FileUtils.copyFileToDirectory(frameworkJar, moduleLib);
    }

    public void createScreen() {
        if (pluginMode) {

        } else {
            ClassCreator.createNewClass(
                    projectDirectory + "core/src/" + packagepath + "/screens",
                    packageName + ".screens",
                    "MainScreen",
                    "MainScreen",
                    "ScreenTemplate"
            );
        }
    }

    public void createScreenByWizard() {
        System.out.println("Open first screen Wizard");

        ScreenDialog sd = new ScreenDialog(
                projectDirectory + "core/src/" + packagepath + "/screens",
                packageName + ".screens"
        ) {
            @Override
            public void createNewClassFile() {
                try {
                    super.createNewClassFile();
                    setScreenToConfig(this.classField.getText());
                    openProject();
                } catch (Exception ex) {}
            }

            @Override
            protected void onCancel() {
                super.onCancel();
                openProject();
            }
        };

        sd.setTitle("Screen Wizard");
        sd.pack();
        sd.setLocationRelativeTo(sd.getParent());
        sd.setVisible(true);
    }

    private void setScreenToConfig(String name) {
        File file = new File(projectDirectory + "android/assets/appconfig.xml");
        String src = null;
        try {
            src = new String(Files.readAllBytes(file.toPath()));
            src = src.replaceAll("\\$<SCREEN_NAME>", name);
            Files.write(file.toPath(), src.getBytes(), StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void openProject() {
        System.out.println("Open new project");
        try {
            ProjectManager.getInstance().loadAndOpenProject(projectDirectory + "build.gradle");
        } catch (IOException | JDOMException | InvalidDataException e) {}
        //System.out.println("Creating project is done");

    }
}
