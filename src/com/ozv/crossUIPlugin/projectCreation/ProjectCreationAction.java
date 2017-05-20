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

//        try {
//
//
//
//            //ProjectManager.getInstance().loadAndOpenProject("/Users/ozvairon/Projects/crossUI/CrossUIapp/build.gradle");
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (JDOMException e) {
//            e.printStackTrace();
//        } catch (InvalidDataException e) {
//            e.printStackTrace();
//        }
    }

    public void execute() {
        System.out.println("PROJECT CREATED");

        ProjectDialog.showDialog();
    }
}
