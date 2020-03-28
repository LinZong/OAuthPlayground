package com.nemesiss.dev.oauthplayground.Exception;

import com.nemesiss.dev.oauthplayground.Model.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@Slf4j
public class AOPExceptionHandler {

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public static ErrorResponse exceptionGet(Exception e) {
        if (e instanceof PlaygroundException) {
            PlaygroundException MyException = (PlaygroundException) e;
            return new ErrorResponse(MyException.getCode(), MyException.getMessage());
        }

        log.error(String.format("%s - %s", e.getClass().getSimpleName(), e.getMessage()));
        return new ErrorResponse(-1, e.getMessage());
    }
}
