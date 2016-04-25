package com.seanlandsman.idea.plugins;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.LanguageLevelModuleExtensionImpl;
import com.intellij.openapi.roots.LanguageLevelProjectExtension;
import com.intellij.pom.java.LanguageLevel;
import com.intellij.psi.*;
import com.intellij.psi.codeStyle.JavaCodeStyleManager;
import com.intellij.psi.util.PsiTreeUtil;

import java.util.List;

public abstract class GuavaUtilityGeneration extends AnAction {
    public static final String COM_GOOGLE_COMMON_BASE_OBJECTS = "com.google.common.base.Objects";
    public static final String COM_GOOGLE_COMMON_BASE_MORE_OBJECTS = "com.google.common.base.MoreObjects";
    public static final String JAVA_UTIL_OBJECTS = "java.util.Objects";
    public static final LanguageLevel JAVA_UTIL_OBJECTS_VERSION = LanguageLevel.JDK_1_7;
    protected boolean hasJavaUtilObjects = false;
    private String dialogTitle;

    public GuavaUtilityGeneration(String text, String dialogTitle) {
        super(text);
        this.dialogTitle = dialogTitle;
    }

    public void actionPerformed(AnActionEvent e) {
        hasJavaUtilObjects = findLanguageLevel(e).compareTo(JAVA_UTIL_OBJECTS_VERSION) >= 0;

        PsiClass psiClass = getPsiClassFromContext(e);
        GenerateDialog dlg = new GenerateDialog(psiClass, dialogTitle);
        dlg.show();
        if (dlg.isOK()) {
            generate(psiClass, dlg.getFields());
        }
    }

    private LanguageLevel findLanguageLevel(AnActionEvent e) {
        Module module = DataKeys.MODULE.getData(e.getDataContext());
        LanguageLevel languageLevel = LanguageLevel.JDK_1_6;

        Project project = e.getProject();
        if (project != null) {
            languageLevel = LanguageLevelProjectExtension.getInstance(project).getLanguageLevel();
        }

        if (module != null) {
            LanguageLevel moduleLanguageLevel = LanguageLevelModuleExtensionImpl.getInstance(module).getLanguageLevel();
            languageLevel = moduleLanguageLevel != null ? moduleLanguageLevel : languageLevel;
        }

        return languageLevel;
    }

    abstract public void generate(final PsiClass psiClass, final List<PsiField> fields);

    protected void setNewMethod(PsiClass psiClass, String newMethodBody, String methodName) {
        PsiElementFactory elementFactory = JavaPsiFacade.getElementFactory(psiClass.getProject());
        PsiMethod newEqualsMethod = elementFactory.createMethodFromText(newMethodBody, psiClass);
        PsiElement method = addOrReplaceMethod(psiClass, newEqualsMethod, methodName);
        JavaCodeStyleManager.getInstance(psiClass.getProject()).shortenClassReferences(method);
    }

    protected PsiElement addOrReplaceMethod(PsiClass psiClass, PsiMethod newEqualsMethod, String methodName) {
        PsiMethod existingEqualsMethod = findMethod(psiClass, methodName);

        PsiElement method;
        if (existingEqualsMethod != null) {
            method = existingEqualsMethod.replace(newEqualsMethod);
        }
        else {
            method = psiClass.add(newEqualsMethod);
        }
        return method;
    }

    protected PsiMethod findMethod(PsiClass psiClass, String methodName) {
        PsiMethod[] allMethods = psiClass.getAllMethods();
        for (PsiMethod method : allMethods) {
            if (psiClass.getName().equals(method.getContainingClass().getName()) && methodName.equals(method.getName())) {
                return method;
            }
        }
        return null;
    }

    @Override
    public void update(AnActionEvent e) {
        PsiClass psiClass = getPsiClassFromContext(e);
        e.getPresentation().setEnabled(psiClass != null);
    }

    private PsiClass getPsiClassFromContext(AnActionEvent e) {
        PsiFile psiFile = e.getData(LangDataKeys.PSI_FILE);
        Editor editor = e.getData(PlatformDataKeys.EDITOR);
        if (psiFile == null || editor == null) {
            return null;
        }
        int offset = editor.getCaretModel().getOffset();
        PsiElement elementAt = psiFile.findElementAt(offset);
        return PsiTreeUtil.getParentOfType(elementAt, PsiClass.class);
    }
}
