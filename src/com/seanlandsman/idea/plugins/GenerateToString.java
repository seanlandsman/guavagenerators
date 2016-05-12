package com.seanlandsman.idea.plugins;

import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiField;

import java.util.List;

public class GenerateToString extends GuavaUtilityGeneration {
    public GenerateToString() {
        super("Generate Guava toString", "Select fields for toString");
    }

    public void generate(final PsiClass psiClass, final List<PsiField> fields) {
        new WriteCommandAction.Simple(psiClass.getProject(), psiClass.getContainingFile()) {
            @Override
            protected void run() throws Throwable {
                generateToString(psiClass, fields);
            }
        }.execute();
    }

    private void generateToString(PsiClass psiClass, List<PsiField> fields) {
        StringBuilder builder = new StringBuilder("@Override\n");
        builder.append("public String toString() { \n");
        builder.append("return ").append(COM_GOOGLE_COMMON_BASE_MORE_OBJECTS).append(".toStringHelper(this)\n");
        for (PsiField field : fields) {
            builder.append(".add(\"")
                    .append(field.getName())
                    .append("\", ")
                    .append(field.getName())
                    .append(")\n");
        }
        builder.append(".toString();\n}");
        setNewMethod(psiClass, builder.toString(), "toString");
    }
}
