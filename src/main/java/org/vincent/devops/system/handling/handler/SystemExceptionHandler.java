package org.vincent.devops.system.handling.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.vincent.devops.system.handling.dto.ErrorDTO;

import javax.ws.rs.core.Response;

@RestControllerAdvice
public class SystemExceptionHandler {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(value= HttpStatus.CONFLICT)
    public ErrorDTO handlerDataIntegrityViolationException(DataIntegrityViolationException ex) {
        logger.error(ex.getMessage(), ex);
        return newErrorDTO(Response.Status.CONFLICT.getStatusCode(), ex.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(value= HttpStatus.BAD_REQUEST)
    public ErrorDTO handlerRuntimeException(RuntimeException ex) {
        logger.error(ex.getMessage(), ex);
        return newErrorDTO(Response.Status.BAD_REQUEST.getStatusCode(), ex.getMessage());
    }

    private ErrorDTO newErrorDTO(int httpCode, String message) {
        ErrorDTO errorDTO = new ErrorDTO();
        errorDTO.setHttpCode(httpCode);
        errorDTO.setMessage(message);
        return errorDTO;
    }

}
