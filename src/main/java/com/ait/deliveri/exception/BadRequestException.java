package com.ait.deliveri.exception;

public class BadRequestException extends RuntimeException {
	
    public BadRequestException(String message) {
        super(message);
    }
}
