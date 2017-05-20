package com.ozv.crossUIPlugin.screenCreation;

import com.ozv.crossUIPlugin.ClassCreator;

import javax.swing.*;
import java.awt.event.*;

public class ScreenDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField classField;
    private JTextField screenField;
    private JLabel pathLabel;

    public ScreenDialog() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);
        if (path.length() > 50) {
            pathLabel.setText("..." + path.substring(path.length() - 50));
        } else {
            pathLabel.setText(path);
        }

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
    }

    private void onOK() {
        //todo: add Fields check;
        createNewClassFile();
        dispose();
    }

    private void onCancel() {
        dispose();
    }

    public static void showDialog(String p, String pn) {

        path = p;
        packageName = pn;
        ScreenDialog dialog = new ScreenDialog();
        dialog.setTitle("Screen Wizard");
        dialog.pack();
        dialog.setLocationRelativeTo(dialog.getParent());
        dialog.setVisible(true);
    }

    private void createNewClassFile() {
        String className = classField.getText();
        String screenName = screenField.getText();

        ClassCreator.createNewClass(path, packageName, className, screenName, "ScreenTemplate");
    }

    private static String path = "";
    private static String packageName = "";




}
