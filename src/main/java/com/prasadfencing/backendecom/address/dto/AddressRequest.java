package com.prasadfencing.backendecom.address.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddressRequest {

    private String fullName;
    private String phone;
    private String street;
    private String city;
    private String state;
    private String pincode;
    private String country;
    private boolean defaultAddress;
}