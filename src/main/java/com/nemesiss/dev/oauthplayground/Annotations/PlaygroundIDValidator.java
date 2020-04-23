package com.nemesiss.dev.oauthplayground.Annotations;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface PlaygroundIDValidator {

    int order() default 0;
}
