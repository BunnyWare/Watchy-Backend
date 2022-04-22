package de.fayedev.watchybackend.exception;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@JsonIgnoreProperties({"cause", "stackTrace", "localizedMessage", "suppressed", "applicationExceptionCode", "httpStatus"})
public class ApplicationException extends RuntimeException {

    private final HttpStatus httpStatus;
    private final ApplicationExceptionCode applicationExceptionCode;

    private final int code;
    private final String message;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final String field;

    public ApplicationException(HttpStatus httpStatus, ApplicationExceptionCode applicationExceptionCode, String message, String field) {
        this(httpStatus, applicationExceptionCode, message, field, null);
    }

    public ApplicationException(HttpStatus httpStatus, ApplicationExceptionCode applicationExceptionCode, String message) {
        this(httpStatus, applicationExceptionCode, message, null, null);
    }

    public ApplicationException(HttpStatus httpStatus, ApplicationExceptionCode applicationExceptionCode, String message, String field, Throwable throwable) {
        super(message, throwable);
        this.httpStatus = httpStatus;
        this.applicationExceptionCode = applicationExceptionCode;
        this.code = applicationExceptionCode.getCode();
        this.message = message;
        this.field = field;
    }
}
