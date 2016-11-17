package com.codyy.erpsportal.urlbuilder.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by gujiajia on 2016/4/12.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.SOURCE)
public @interface UrlSuffix {
    String CLASS_NAME = "Url$$Builder";

    String PACKAGE_NAME = "com.codyy.url";

    String METHOD_NAME = "updateUrls";

    String value() default "";
}
