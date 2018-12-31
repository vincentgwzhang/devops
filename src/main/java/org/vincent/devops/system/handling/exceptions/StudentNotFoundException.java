package org.vincent.devops.system.handling.exceptions;

import javax.ws.rs.core.Response;

public class StudentNotFoundException extends DevCustomException {

    private static final Response.Status errorStatus = Response.Status.NOT_FOUND;
    private static final String idNotFoundTemplate = "student.id.notfound.errormessage";
    private static final String nameNotFoundTemplate = "student.name.notfound.errormessage";

    public StudentNotFoundException(int id) {
        super(errorStatus, idNotFoundTemplate, new Object[]{id});
    }

    public StudentNotFoundException(String name) {
        super(errorStatus, nameNotFoundTemplate, new Object[]{name});
    }
}
