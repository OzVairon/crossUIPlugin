package com.ozv.crossUIPlugin;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;

import javax.swing.*;
import java.awt.*;

/**
 * Created by ozvairon on 18.05.17.
 */
public class SimpleAction extends AnAction {
    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {

        ScreenDialog.showDialog();

    }
}
