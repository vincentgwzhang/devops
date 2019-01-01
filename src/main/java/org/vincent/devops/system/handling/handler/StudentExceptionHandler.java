package org.vincent.devops.system.handling.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.vincent.devops.system.handling.dto.ErrorDTO;
import org.vincent.devops.system.handling.exceptions.DevCustomException;
import org.vincent.devops.system.handling.exceptions.StudentDuplicateException;
import org.vincent.devops.system.handling.exceptions.StudentNotFoundException;

import java.util.Locale;

@RestControllerAdvice
public class StudentExceptionHandler {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private MessageSource messageSource;

    @Autowired
    public StudentExceptionHandler(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @ExceptionHandler(StudentNotFoundException.class)
    @ResponseStatus(value= HttpStatus.NOT_FOUND)
    public ErrorDTO handlerStudentNotFoundException(StudentNotFoundException ex) {
        return buildErrorDTO(ex);
    }

    @ExceptionHandler(StudentDuplicateException.class)
    @ResponseStatus(value= HttpStatus.CONFLICT)
    public ErrorDTO handlerStudentDuplicateException(StudentDuplicateException ex) {
        return buildErrorDTO(ex);
    }

    private ErrorDTO buildErrorDTO(DevCustomException exception) {
        String errorMessage = messageSource.getMessage(exception.getMessageTemplate(), exception.getParameters(), Locale.getDefault());
        ErrorDTO errorDTO = new ErrorDTO();
        errorDTO.setHttpCode(exception.getErrorStatus().getStatusCode());
        errorDTO.setMessage(errorMessage);
        logger.error(errorMessage, exception);
        return errorDTO;
    }

}
