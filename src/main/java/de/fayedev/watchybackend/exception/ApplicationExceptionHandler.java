package de.fayedev.watchybackend.exception;

import de.fayedev.watchybackend.utils.LogMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.validation.*;
import java.util.HashSet;
import java.util.Set;

@ControllerAdvice
@Slf4j
public class ApplicationExceptionHandler {

    @ExceptionHandler(ApplicationException.class)
    protected ResponseEntity<Object> handleApplicationException(ApplicationException e) {
        return ResponseEntity.status(e.getHttpStatus()).body(e);
    }

    @ExceptionHandler(AccessDeniedException.class)
    protected ResponseEntity<Object> handleAccessDeniedException(AccessDeniedException e) {
        ApplicationException applicationException = new ApplicationException(HttpStatus.FORBIDDEN,
                ApplicationExceptionCode.FORBIDDEN, LogMessage.FORBIDDEN);
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(applicationException);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    protected ResponseEntity<Object> handleMessageNotReadableException(HttpMessageNotReadableException e) {
        ApplicationException applicationException = new ApplicationException(HttpStatus.BAD_REQUEST,
                ApplicationExceptionCode.BAD_REQUEST, LogMessage.BAD_REQUEST);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(applicationException);
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    protected ResponseEntity<Object> handleHttpMediaTypeNotSupportedException(HttpMediaTypeNotSupportedException e) {
        ApplicationException applicationException = new ApplicationException(HttpStatus.BAD_REQUEST,
                ApplicationExceptionCode.BAD_REQUEST, LogMessage.BAD_REQUEST);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(applicationException);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<Object> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        if (!e.getBindingResult().getFieldErrors().isEmpty()) {
            Set<ApplicationException> applicationExceptions = new HashSet<>();

            for (FieldError fieldError : e.getBindingResult().getFieldErrors()) {
                ApplicationException applicationException = new ApplicationException(HttpStatus.BAD_REQUEST,
                        ApplicationExceptionCode.BAD_REQUEST, fieldError.getField() + ", " + fieldError.getDefaultMessage());
                applicationExceptions.add(applicationException);
            }

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).contentType(MediaType.APPLICATION_JSON)
                    .body(applicationExceptions);
        } else {
            ApplicationException applicationException = new ApplicationException(HttpStatus.BAD_REQUEST,
                    ApplicationExceptionCode.BAD_REQUEST, LogMessage.BAD_REQUEST);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(applicationException);
        }
    }

    @ExceptionHandler(ValidationException.class)
    protected ResponseEntity<Object> handleConflict(ValidationException exception) {
        Set<ApplicationException> applicationExceptions = new HashSet<>();

        if (exception instanceof ConstraintViolationException constraintViolationException) {
            for (final ConstraintViolation<?> violation : constraintViolationException.getConstraintViolations()) {
                ApplicationException applicationException = new ApplicationException(HttpStatus.BAD_REQUEST,
                        ApplicationExceptionCode.BAD_REQUEST, violation.getMessage(), getAttributeName(violation));
                applicationExceptions.add(applicationException);
            }
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).contentType(MediaType.APPLICATION_JSON)
                .body(applicationExceptions);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    protected ResponseEntity<Object> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        ApplicationException applicationException = new ApplicationException(HttpStatus.BAD_REQUEST,
                ApplicationExceptionCode.BAD_REQUEST, LogMessage.BAD_REQUEST, e.getParameter().getParameterName());
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(applicationException);
    }

    @ExceptionHandler(MissingRequestHeaderException.class)
    protected ResponseEntity<Object> handleMissingRequestHeaderExceptionException(MissingRequestHeaderException e) {
        ApplicationException applicationException = new ApplicationException(HttpStatus.BAD_REQUEST,
                ApplicationExceptionCode.BAD_REQUEST, LogMessage.BAD_REQUEST, e.getHeaderName());
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(applicationException);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    protected ResponseEntity<Object> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        ApplicationException applicationException = new ApplicationException(HttpStatus.METHOD_NOT_ALLOWED,
                ApplicationExceptionCode.METHOD_NOT_ALLOWED, LogMessage.METHOD_NOT_ALLOWED);
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(applicationException);
    }

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<Object> handleException(Exception e) {
        log.error(LogMessage.UNHANDLED_EXCEPTION, e);
        ApplicationException applicationException = new ApplicationException(HttpStatus.INTERNAL_SERVER_ERROR,
                ApplicationExceptionCode.UNHANDLED_EXCEPTION, LogMessage.UNHANDLED_EXCEPTION);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(applicationException);
    }

    private String getAttributeName(final ConstraintViolation<?> violation) {
        StringBuilder attributeName = new StringBuilder();
        for (Path.Node node : violation.getPropertyPath()) {
            if (node.getKind() == ElementKind.PROPERTY) {
                attributeName.append(".").append(node.getName());
            } else if (node.getKind() == ElementKind.PARAMETER && node.getName().contains("\"")) {
                attributeName.append(".").append(getParameterName(node.getName()));
            } else if (node.getKind() == ElementKind.PARAMETER) {
                attributeName.append(".").append(node.getName().toLowerCase());
            }
        }
        if (attributeName.length() == 0) {
            for (Path.Node node : violation.getPropertyPath()) {
                if (node.getKind() == ElementKind.METHOD) {
                    attributeName.append("." + "id");
                }
            }
        }
        return attributeName.substring(1);
    }

    private String getParameterName(String name) {
        String[] parameter = name.split("\"");
        return parameter[1];
    }
}
