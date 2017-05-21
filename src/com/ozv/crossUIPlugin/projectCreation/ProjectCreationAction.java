package com.ozv.crossUIPlugin.projectCreation;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;

/**
 * Created by ozvairon on 20.05.17.
 */
public class ProjectCreationAction extends AnAction{
    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        execute();
    }

    public void execute() {
        ProjectDialog.showDialog();
    }
}
