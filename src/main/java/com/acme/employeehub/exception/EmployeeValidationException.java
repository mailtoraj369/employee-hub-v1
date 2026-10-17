package com.acme.employeehub.exception;

public class EmployeeValidationException extends RuntimeException {
    private final String errorCode;

    public EmployeeValidationException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}