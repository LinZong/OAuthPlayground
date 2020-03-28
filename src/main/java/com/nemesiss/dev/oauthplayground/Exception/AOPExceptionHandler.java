package com.nemesiss.dev.oauthplayground.Exception;

import com.nemesiss.dev.oauthplayground.Model.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.ConstraintViolationException;

@Slf4j
@ControllerAdvice
public class AOPExceptionHandler {

    @ExceptionHandler({PlaygroundException.class})
    @ResponseBody
    public static ErrorResponse PlaygroundExceptionGet(PlaygroundException e) {
        return new ErrorResponse(e.getCode(), e.getMessage());
    }

    @ExceptionHandler({ConstraintViolationException.class})
    @ResponseBody
    public static ErrorResponse ConstraintViolationExceptionGet(ConstraintViolationException e) {
        return new ErrorResponse(-2, e.getMessage());
    }
}
