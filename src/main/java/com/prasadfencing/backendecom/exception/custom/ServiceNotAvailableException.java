package com.prasadfencing.backendecom.exception.custom;

public class ServiceNotAvailableException extends RuntimeException {
    public ServiceNotAvailableException(String pincode) {
        super("Sorry, our service is not available at pincode: " + pincode);
    }
}