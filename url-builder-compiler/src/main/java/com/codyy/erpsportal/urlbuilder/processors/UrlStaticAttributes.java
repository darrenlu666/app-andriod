package com.codyy.erpsportal.urlbuilder.processors;

import com.codyy.erpsportal.urlbuilder.annotations.UrlSuffix;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.annotation.processing.Filer;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;

/**
 * url静态属性
 * Created by gujiajia on 2016/4/13.
 */
public class UrlStaticAttributes {

    private Set<UrlAttr> attrSet = new LinkedHashSet<>();

    private Set<TypeElement> importSet = new LinkedHashSet<>();

    /**
     * 是否已经生成类文件
     */
    private boolean generated;

    /**
     * 创建生成类文件
     * @param elementUtils
     * @param filer
     * @throws IOException
     */
    public void generateCode(Elements elementUtils, Filer filer) throws IOException {
        if (!generated) {
            TypeSpec urlBuilder = TypeSpec.classBuilder(UrlSuffix.CLASS_NAME)
                    .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                    .addMethod(buildMethod())
                    .build();
            JavaFile.Builder javaFileBuilder = JavaFile.builder(UrlSuffix.PACKAGE_NAME, urlBuilder);
            for (TypeElement typeElement: importSet) {
                ClassName className = ClassName.get(typeElement);
                javaFileBuilder.addStaticImport(className, "*");
            }
            JavaFile javaFile = javaFileBuilder.build();
            javaFile.writeTo(filer);
            generated = true;
        }
    }

    public MethodSpec buildMethod() {
        MethodSpec.Builder builder = MethodSpec.methodBuilder(UrlSuffix.METHOD_NAME)
                .returns(void.class)
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC);
        for (UrlAttr attr: attrSet) {
            builder.addStatement("$N = BASE + $S", attr.getAttrName(), attr.getSuffix());
        }
        return builder.build();
    }

    public void addAttr(UrlAttr attr) {
        attrSet.add(attr);
    }

    public void addClass(TypeElement typeElement) {
        importSet.add(typeElement);
    }

    public void clear() {
        attrSet.clear();
        importSet.clear();
    }
}
