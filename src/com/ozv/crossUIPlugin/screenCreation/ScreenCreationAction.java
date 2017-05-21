package com.ozv.crossUIPlugin.screenCreation;

import com.intellij.ide.IdeView;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaDirectoryService;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiPackage;

import javax.swing.*;
import java.io.File;

/**
 * Created by ozvairon on 18.05.17.
 */
public class ScreenCreationAction extends AnAction {

    Icon icon;


    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {

        String filePath = anActionEvent.getData(CommonDataKeys.VIRTUAL_FILE).getCanonicalPath();
        File file = new File(filePath);

        String path = file.getPath();

        if (file.isFile()) {
            path = file.getParent();
        }


        PsiPackage packagePsi = findPackage(anActionEvent);
        String packageFullName = packagePsi.getName();
        System.out.println(packageFullName);


        while (packagePsi.getParent() != null) {
            packagePsi = (PsiPackage) packagePsi.getParent();
            packageFullName = packagePsi.getName() + "." + packageFullName ;
        }
        packageFullName = packageFullName.substring(5);
        ScreenDialog.showDialog(path, packageFullName);
    }

    public PsiPackage findPackage(AnActionEvent e) {
        DataContext context = e.getDataContext();
        IdeView view = LangDataKeys.IDE_VIEW.getData(context);
        if(view != null) {
            Project project = CommonDataKeys.PROJECT.getData(context);
            if(project != null) {
                final PsiDirectory directory = view.getOrChooseDirectory();
                if(directory != null) {
                    return JavaDirectoryService.getInstance().getPackage(directory);
                }
            }
        }
        return null;
    }


    @Override
    public void update(AnActionEvent e) {
        super.update(e);

        if (findPackage(e) == null) {
            e.getPresentation().setVisible(false);
            return;
        }
//        try {
//            String projectPath = e.getProject().getBaseDir().getPath();
//            String filePath = e.getData(CommonDataKeys.VIRTUAL_FILE).getCanonicalPath();
//
//            if (projectPath != null && filePath != null)
//                e.getPresentation().setVisible(filePath.startsWith(projectPath));
//        } catch (NullPointerException ex) {
//            e.getPresentation().setVisible(false);
//        }

    }
}
