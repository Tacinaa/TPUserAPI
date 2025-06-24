package org.example.tpuserapi.exception;

//DataIntegrityViolationException
public class EmailAlreadyExistsException extends RuntimeException {
    public EmailAlreadyExistsException(String message) {
        super(message);
    }
}