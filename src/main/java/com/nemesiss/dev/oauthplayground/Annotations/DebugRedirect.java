package com.nemesiss.dev.oauthplayground.Annotations;


import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface DebugRedirect {
}
