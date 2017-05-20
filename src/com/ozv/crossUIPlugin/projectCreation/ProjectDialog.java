package com.ozv.crossUIPlugin.projectCreation;

import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;

import javax.swing.*;
import java.awt.event.*;
import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ProjectDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField classField;
    private TextFieldWithBrowseButton folderChooser;
    private JTextField comUserMycrossuiTextField;
    private JTextField debug;
    private JButton button1;

    public ProjectDialog() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

// call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

// call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        FileChooserDescriptor descriptor = new FileChooserDescriptor(false, true, false, false,false, false);

        folderChooser.addBrowseFolderListener(
                "Choose project path",
                "",
                null,
                descriptor
        );

        folderChooser.setText(System.getProperty("user.home"));

        buttonOK.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                File projectDir = new File(folderChooser.getText());
                if (!projectDir.exists()) {
                    projectDir.mkdir();
                }
                if (projectDir.isDirectory()) {

                    String path = ProjectDialog.class.getResource("ProjectDialog.class").getFile();
                    path = path.substring(0, path.lastIndexOf('/'));
                    path = path.substring(0, path.lastIndexOf('/')) + "/templates/picture.zip";

                    try {
                        File zipFile = new File(path);

                        ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile));
                        ZipEntry ze = zis.getNextEntry();
                        byte[] buffer = new byte[1024];

                        while(ze!=null){

                            String fileName = ze.getName();
                            File newFile = new File(projectDir + File.separator + fileName);

                            System.out.println("file unzip : "+ newFile.getAbsoluteFile());
                            new File(newFile.getParent()).mkdirs();

                            FileOutputStream fos = new FileOutputStream(newFile);

                            int len;
                            while ((len = zis.read(buffer)) > 0) {
                                fos.write(buffer, 0, len);
                            }

                            fos.close();
                            ze = zis.getNextEntry();
                        }

                        zis.closeEntry();
                        zis.close();

                        System.out.println("Done");


                    } catch (FileNotFoundException e2) {

                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }

                }
            }
        });

    }

    private void onOK() {
// add your code here
        dispose();
    }

    private void onCancel() {
// add your code here if necessary
        dispose();
    }

    public static void showDialog() {
        ProjectDialog dialog = new ProjectDialog();
        dialog.setTitle("Project Wizard");
        dialog.pack();
        dialog.setLocationRelativeTo(dialog.getParent());
        dialog.setVisible(true);
    }
}
