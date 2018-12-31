package org.vincent.devops.system.handling.exceptions;

import javax.ws.rs.core.Response;

public class StudentDuplicateException extends DevCustomException {

    private static final Response.Status errorStatus = Response.Status.CONFLICT;
    private static final String nameDuplicateTemplate = "student.name.duplicate.errormessage";

    public StudentDuplicateException(String name) {
        super(errorStatus, nameDuplicateTemplate, new Object[]{name});
    }
}