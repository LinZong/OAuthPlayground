package com.nemesiss.dev.oauthplayground.ParamValidator;

import com.nemesiss.dev.oauthplayground.Utils.AnnotationGetter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ControllerAdvice
public class BindExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleBindException(BindException ex,
                                                         HttpHeaders headers,
                                                         HttpStatus status,
                                                         WebRequest request) {
        return new ResponseEntity<>(ErrorsFormatter(
                ex.getBindingResult().getTarget(),
                ex.getBindingResult().getAllErrors()),
                headers,
                HttpStatus.BAD_REQUEST);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers,
                                                                  HttpStatus status,
                                                                  WebRequest request) {
        return new ResponseEntity<>(ErrorsFormatter(
                ex.getBindingResult().getTarget(),
                ex.getBindingResult().getAllErrors()),
                headers, HttpStatus.BAD_REQUEST);
    }

    private Map<String, String> ErrorsFormatter(Object targetObject,
                                                List<ObjectError> Errors) {
        Map<String, String> errors = new HashMap<>();
        Errors.forEach((err) ->
                errors.put(err instanceof FieldError ?
                                AnnotationGetter.GetJsonPropertyValue(
                                        targetObject.getClass(),
                                        ((FieldError) err).getField(),
                                        ((FieldError) err).getField()) :
                                err.getObjectName(),
                        err.getDefaultMessage()));
        return errors;
    }
}
