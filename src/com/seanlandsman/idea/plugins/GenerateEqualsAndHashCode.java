package com.seanlandsman.idea.plugins;

import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiField;

import java.util.List;

public class GenerateEqualsAndHashCode extends GuavaUtilityGeneration {
    public GenerateEqualsAndHashCode() {
        super("Generate Guava equals and hashCode", "Select fields for equals and hashCode");
    }

    public void generate(final PsiClass psiClass, final List<PsiField> fields) {
        new WriteCommandAction.Simple(psiClass.getProject(), psiClass.getContainingFile()) {
            @Override
            protected void run() throws Throwable {
                generateEquals(psiClass, fields);
                generateHashCode(psiClass, fields);
            }
        }.execute();
    }

    private void generateEquals(PsiClass psiClass, List<PsiField> fields) {
        String method;

        if (hasJavaUtilObjects)
            method = JAVA_UTIL_OBJECTS + ".equals";
        else
            method = COM_GOOGLE_COMMON_BASE_OBJECTS + ".equal";

        StringBuilder builder = new StringBuilder("@Override\n");
        builder.append("public boolean equals(Object o) { \n");
        builder.append("if (this == o) return true;\n");
        builder.append("if (o == null || getClass() != o.getClass()) return false;\n\n");
        builder.append(psiClass.getName()).append(" that = (").append(psiClass.getName()).append(") o;\n\n");

        builder.append("return ");
        for (int i = 0; i < fields.size(); i++) {
            PsiField field = fields.get(i);
            builder.append(method)
                    .append("(this.")
                    .append(field.getName())
                    .append(", that.")
                    .append(field.getName())
                    .append(")");
            if (i < fields.size() - 1) {
                builder.append(" &&\n");
            }
        }
        builder.append(";\n}");
        setNewMethod(psiClass, builder.toString(), "equals");
    }

    private void generateHashCode(PsiClass psiClass, List<PsiField> fields) {
        StringBuilder builder = new StringBuilder("@Override\n");
        builder.append("public int hashCode() { \n");
        builder.append("return ");

        if (hasJavaUtilObjects)
            builder.append(JAVA_UTIL_OBJECTS).append(".hash");
        else
            builder.append(COM_GOOGLE_COMMON_BASE_OBJECTS).append(".hashCode");

        builder.append("(");
        for (int i = 0; i < fields.size(); i++) {
            PsiField field = fields.get(i);
            builder.append(field.getName());
            if (i < fields.size() - 1) {
                builder.append(",");
                if (fields.size() > 5 && i > 0 && i % 5 == 0) {
                    builder.append("\n");
                }
            }
        }
        builder.append(");\n}");
        setNewMethod(psiClass, builder.toString(), "hashCode");
    }
}
