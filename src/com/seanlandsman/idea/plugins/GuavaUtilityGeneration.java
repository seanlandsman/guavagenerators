package com.seanlandsman.idea.plugins;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.*;
import com.intellij.psi.codeStyle.JavaCodeStyleManager;
import com.intellij.psi.util.PsiTreeUtil;

import java.util.List;

public abstract class GuavaUtilityGeneration extends AnAction {
    public static final String COM_GOOGLE_COMMON_BASE_OBJECTS = "com.google.common.base.Objects";
    private String dialogTitle;

    public GuavaUtilityGeneration(String text, String dialogTitle) {
        super(text);
        this.dialogTitle = dialogTitle;
    }

    public void actionPerformed(AnActionEvent e) {
        PsiClass psiClass = getPsiClassFromContext(e);
        GenerateDialog dlg = new GenerateDialog(psiClass, dialogTitle);
        dlg.show();
        if (dlg.isOK()) {
            generate(psiClass, dlg.getFields());
        }
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
