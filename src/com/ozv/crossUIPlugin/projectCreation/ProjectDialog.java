package com.ozv.crossUIPlugin.projectCreation;

import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;

public class ProjectDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField projectNameField;
    private TextFieldWithBrowseButton folderChooser;
    private JTextField packageNameField;
    private JProgressBar progressBar;
    private JLabel alertLabel;

    public ProjectDialog() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);
        alertLabel.setText(" ");
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


        DocumentListener fieldChecker = new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                checkFields();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                checkFields();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                checkFields();
            }
        };

        projectNameField.getDocument().addDocumentListener(fieldChecker);
        packageNameField.getDocument().addDocumentListener(fieldChecker);

        projectNameField.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (
                        ((c < 'A') || (c > 'Z')) && ((c < 'a') || (c > 'z')) &&
                        (c != KeyEvent.VK_BACK_SPACE) && (c != KeyEvent.VK_SPACE)
                        ) {
                    e.consume();  // ignore event
                }
            }
        });

        packageNameField.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (
                        ((c < 'A') || (c > 'Z')) && ((c < 'a') || (c > 'z')) &&
                        (c != KeyEvent.VK_BACK_SPACE) && (c != '.')
                        ) {
                    e.consume();  // ignore event
                }
            }
        });
    }

    private void onOK() {
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

                            ProjectCreator creator = new ProjectCreator(projectDir.getAbsolutePath(), projectNameField.getText(), packageNameField.getText());
                            creator.start();
                            dispose();
                            creator.createScreenByWizard();
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        } catch (Exception ex2) {
                            unlockEdit();
                            progressBar.setVisible(false);
                        }
                    }
                });
                tr.run();
            } else {
//                System.out.println(projectDir.getAbsolutePath());
//                System.out.println("it is not directory");
                alertLabel.setText("Invalid path to folder");
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

    private void checkFields() {
        String project = projectNameField.getText().trim();
        String pcg = packageNameField.getText().trim();

        if (project.length() == 0 && pcg.length() == 0) {
            alertLabel.setText("Enter the project information");
            buttonOK.setEnabled(false);
            return;
        }

        if (project.length() == 0) {
            alertLabel.setText("Enter the project name");
            buttonOK.setEnabled(false);
            return;
        }

        if (pcg.length() == 0) {
            alertLabel.setText("Enter the package of project");
            buttonOK.setEnabled(false);
            return;
        }

        buttonOK.setEnabled(true);
        alertLabel.setText(" ");
    }

}
