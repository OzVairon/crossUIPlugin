package com.ozv.crossUIPlugin;

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
public class SimpleAction extends AnAction {

    Icon icon;


    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {

        String filePath = anActionEvent.getData(CommonDataKeys.VIRTUAL_FILE).getCanonicalPath();
        File file = new File(filePath);

        String path = file.getPath();

        if (file.isFile()) {
            path = file.getParent();
        }



        DataContext context = anActionEvent.getDataContext();
        IdeView view = (IdeView)LangDataKeys.IDE_VIEW.getData(context);
        if(view != null) {
            Project project = (Project)CommonDataKeys.PROJECT.getData(context);
            if(project != null) {
                final PsiDirectory directory = view.getOrChooseDirectory();
                if(directory != null) {
                    System.out.println(directory.getVirtualFile().getCanonicalPath());

                    PsiPackage packagePsi = JavaDirectoryService.getInstance().getPackage(directory);
                    String packageFullName = packagePsi.getName();


                    while (packagePsi.getParent() != null) {
                        packagePsi = (PsiPackage) packagePsi.getParent();
                        packageFullName = packagePsi.getName() + "." + packageFullName ;
                    }
                    packageFullName = packageFullName.substring(5);
                    ScreenDialog.showDialog(path, packageFullName);
                }
            }
        }
    }


    @Override
    public void update(AnActionEvent e) {
        super.update(e);
        try {
            String projectPath = e.getProject().getBaseDir().getPath();
            String filePath = e.getData(CommonDataKeys.VIRTUAL_FILE).getCanonicalPath();

            if (projectPath != null && filePath != null)
                e.getPresentation().setVisible(filePath.startsWith(projectPath));
        } catch (NullPointerException ex) {
            e.getPresentation().setVisible(false);
        }

    }
}
