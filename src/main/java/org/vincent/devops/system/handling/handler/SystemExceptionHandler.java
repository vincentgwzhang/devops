package org.vincent.devops.system.handling.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.vincent.devops.system.handling.dto.ErrorDTO;

import javax.ws.rs.core.Response;

@RestControllerAdvice
public class SystemExceptionHandler {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(value= HttpStatus.BAD_REQUEST)
    public ErrorDTO handlerRuntimeException(RuntimeException ex) {
        ErrorDTO errorDTO = new ErrorDTO();
        errorDTO.setHttpCode(Response.Status.BAD_REQUEST.getStatusCode());
        errorDTO.setMessage(ex.getMessage());
        logger.error(ex.getMessage(), ex);
        return errorDTO;
    }

}
