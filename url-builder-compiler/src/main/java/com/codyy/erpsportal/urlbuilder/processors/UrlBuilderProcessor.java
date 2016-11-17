package com.codyy.erpsportal.urlbuilder.processors;

import com.codyy.erpsportal.urlbuilder.annotations.UrlSuffix;
import com.google.auto.service.AutoService;

import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic.Kind;

@AutoService(Processor.class)
public class UrlBuilderProcessor extends AbstractProcessor {

    private Types typesUtils;

    private Elements elementUtils;

    private Filer filer;

    private Messager messager;

    private UrlStaticAttributes urlStaticAttributes = new UrlStaticAttributes();

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        typesUtils = processingEnv.getTypeUtils();
        elementUtils = processingEnv.getElementUtils();
        filer = processingEnv.getFiler();
        messager = processingEnv.getMessager();
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        //遍历所有有UrlSuffix注解的元素
        for (Element element : roundEnv.getElementsAnnotatedWith(UrlSuffix.class)) {
            if (!validateElement(element)){
                continue;
            }
            UrlSuffix urlSuffix = element.getAnnotation(UrlSuffix.class);
//            String message = "annotation found in " + element.getSimpleName()
//                    + " with url " + urlSuffix.value();
//            note(element, message);
            //获取属性名与注解中的相对url路径，创建UrlAttr并缓存好,到时生产赋值语句
            UrlAttr urlAttr = new UrlAttr(element.getSimpleName().toString(), urlSuffix.value());
            urlStaticAttributes.addAttr( urlAttr);
            //获取属性所在类，用于生成相关导入语句
            Element clazz = element.getEnclosingElement();
            TypeElement typeElement = (TypeElement) clazz;
            urlStaticAttributes.addClass( typeElement);
        }
        try {
            urlStaticAttributes.generateCode(elementUtils, filer);
        } catch (IOException e) {
            error(null, e.getMessage());
        }
        return true;
    }

    /**
     * 验证是否符合要求
     * @param element
     * @return
     */
    private boolean validateElement(Element element) {
        //元素是公共静态成员变量
        if (element.getKind().isField() && element.getModifiers().contains(Modifier.PUBLIC)
                && element.getModifiers().contains(Modifier.STATIC)){
            return true;
        } else {
            error(element, "Only public static field can be annotated with @%s",
                    UrlSuffix.class.getSimpleName());
            return false;
        }
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> types = new LinkedHashSet<>();
        types.add(UrlSuffix.class.getCanonicalName());
        return types;
    }

    private void error(Element e, String msg, Object... args) {
        messager.printMessage(Kind.ERROR, String.format(msg, args), e);
    }

    private void note(Element element ,String msg, Object... args) {
        messager.printMessage(Kind.NOTE, String.format(msg, args), element);
    }
}
