package org.vincent.devops.system.handling.mapping;

import org.springframework.http.HttpStatus;
import org.vincent.devops.system.handling.dto.ErrorDTO;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

/**
 * Status : Content
 *  400   : If logic is incorrect
 *  404   : If resource not found
 *  409   : If resource is duplicate
 *  500   : If system error
 */
public class BasicExceptionMapper {

    public Response buildResponse(Status status, Throwable e){
        return buildResponse(status, e.getMessage());
    }

    public Response buildResponse(Status status, String errorMessage){
        return buildResponse(status.getStatusCode(), errorMessage);
    }

    public Response buildResponse(HttpStatus status, String errorMessage){
        return buildResponse(status.value(), errorMessage);
    }

    private Response buildResponse(int status, String errorMessage){
        ErrorDTO dto = buildErrorDTO(status, errorMessage);
        return Response.status(status).entity(dto).type(MediaType.APPLICATION_JSON_TYPE).build();
    }

    private ErrorDTO buildErrorDTO(int status, String errorMessage){
        ErrorDTO dto = new ErrorDTO();
        dto.setHttpCode(status);
        dto.setMessage(errorMessage);
        return dto;
    }

}