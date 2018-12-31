package org.vincent.devops.system.handling.exceptions;

import javax.ws.rs.core.Response;

@SuppressWarnings("serial")
public class DevCustomException extends RuntimeException{

    private String messageTemplate;

    private Object[] parameters;

    private Response.Status errorStatus;

    public DevCustomException(Response.Status errorStatus, String messageTemplate, Object[] _parameters){
        this.errorStatus = errorStatus;
        this.messageTemplate = messageTemplate;
        this.parameters = _parameters;
    }

    public String getMessageTemplate() {
        return messageTemplate;
    }

    public Object[] getParameters() {
        return parameters;
    }

    public Response.Status getErrorStatus() {
        return errorStatus;
    }
}
