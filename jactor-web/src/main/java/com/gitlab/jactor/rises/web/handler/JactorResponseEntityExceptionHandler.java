package com.gitlab.jactor.rises.web.handler;

import com.gitlab.jactor.rises.commons.stack.StackResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class JactorResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger LOGG = LoggerFactory.getLogger(JactorResponseEntityExceptionHandler.class);

    @ExceptionHandler(value = RuntimeException.class)
    public ResponseEntity<Object> handleInternalServerError(RuntimeException rex, HttpHeaders headers, WebRequest webRequest) {
        StackResolver.logStack(LOGG::error, LOGG::error, rex);

        return handleExceptionInternal(rex, null, headers, HttpStatus.INTERNAL_SERVER_ERROR, webRequest);
    }
}
