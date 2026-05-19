package com.prasadfencing.backendecom.exception.custom;

public class TooManyRequestsException extends RuntimeException {
    public TooManyRequestsException() {
        super("Too many requests. Please try again later.");
    }
}