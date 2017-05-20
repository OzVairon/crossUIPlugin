package com.ozv.crossUIPlugin.projectCreation;

import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;

import javax.swing.*;
import java.awt.event.*;
import java.io.File;

public class ProjectDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField projectNameField;
    private TextFieldWithBrowseButton folderChooser;
    private JTextField packageNameField;
    private JProgressBar progressBar;

    public ProjectDialog() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        progressBar.setVisible(false);

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

        folderChooser.setText(System.getProperty("user.home") + "/Projects/CrossUIDemo/MyProj");

    }

    private void onOK() {
        //todo: Field check
        try {

            File projectDir = new File(folderChooser.getText());

            if (projectDir.isDirectory()) {
                lockEdit();
                progressBar.setVisible(true);
                progressBar.setIndeterminate(true);

                Thread tr = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            ProjectCreator.createProject(projectDir.getAbsolutePath(), projectNameField.getText(), packageNameField.getText());
                            dispose();
                        } catch (Exception ex) {
                            unlockEdit();
                            progressBar.setVisible(false);
                        }
                    }
                });
                tr.run();


            } else {
                System.out.println(projectDir.getAbsolutePath());
                System.out.println("it is not directory");
            }
        } catch (Exception ex) {ex.printStackTrace();}

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

    private void lockEdit() {
        projectNameField.setEnabled(false);
        packageNameField.setEnabled(false);
        folderChooser.setEnabled(false);
        buttonOK.setEnabled(false);
        buttonCancel.setEnabled(false);
    }

    private void unlockEdit() {
        projectNameField.setEnabled(true);
        packageNameField.setEnabled(true);
        folderChooser.setEnabled(true);
        buttonOK.setEnabled(true);
        buttonCancel.setEnabled(true);
    }

}
