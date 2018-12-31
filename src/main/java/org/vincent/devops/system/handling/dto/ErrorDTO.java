package org.vincent.devops.system.handling.dto;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="errorDTO",propOrder={"httpCode","message"})
@XmlRootElement(name="errorDTO")
public class ErrorDTO {

    @NotNull
    private int httpCode;

    private String message;

    public int getHttpCode() {
        return httpCode;
    }

    public void setHttpCode(int httpCode) {
        this.httpCode = httpCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ErrorDTO(int httpCode, String message) {
        super();
        this.httpCode = httpCode;
        this.message = message;
    }

    public ErrorDTO() {
        super();
    }
}