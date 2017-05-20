package com.ozv.crossUIPlugin;

import com.android.utils.FileUtils;

import java.io.*;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

/**
 * Created by ozvairon on 20.05.17.
 */
public class JarPacker {

    private String inputDir = "/Users/ozvairon/Projects/crossUI/CrossUIapp/core/src/com/";

    private String temporaryDir = "/Users/ozvairon/Projects/CrossUIPlugin/src/com/ozv/crossUIPlugin/temp";

    private String outputJar = "/Users/ozvairon/Projects/CrossUIPlugin/src/com/ozv/crossUIPlugin/templates/crossUIlib.jar";

    public static void main(String[] args) {
        try {
            new JarPacker().run();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean prepareFiles() {
        try {
            File inp = new File(inputDir);
            File temp = new File(temporaryDir);


            FileUtils.cleanOutputDir(temp);

            temp = new File(temporaryDir + "/com");
            temp.mkdir();
            FileUtils.copyDirectory(inp, temp);

            File jar = new File(outputJar);
            if (jar.exists()) FileUtils.delete(new File(outputJar));
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void run() throws IOException
    {
        if (!prepareFiles()) return;

        Manifest manifest = new Manifest();
        manifest.getMainAttributes().put(Attributes.Name.MANIFEST_VERSION, "1.0");
        JarOutputStream target = new JarOutputStream(new FileOutputStream(outputJar));
        add(new File(temporaryDir + "/com"), target);
        target.close();
    }

    private void add(File source, JarOutputStream target) throws IOException
    {
        BufferedInputStream in = null;
        try
        {
            if (source.isDirectory())
            {
                String name = source.getPath().replace("\\", "/");
                if (!name.isEmpty())
                {
                    if (!name.endsWith("/"))
                        name += "/";
                    JarEntry entry = new JarEntry(name);
                    entry.setTime(source.lastModified());
                    target.putNextEntry(entry);
                    target.closeEntry();
                }
                for (File nestedFile: source.listFiles())
                    add(nestedFile, target);
                return;
            }

            JarEntry entry = new JarEntry(source.getPath().replace("\\", "/"));
            entry.setTime(source.lastModified());
            target.putNextEntry(entry);
            in = new BufferedInputStream(new FileInputStream(source));

            byte[] buffer = new byte[1024];
            while (true)
            {
                int count = in.read(buffer);
                if (count == -1)
                    break;
                target.write(buffer, 0, count);
            }
            target.closeEntry();
        }
        finally
        {
            if (in != null)
                in.close();
        }
    }
}
