package com.prasadfencing.backendecom.delivery.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddPincodeRequestDTO {
    private String state;
    private String pincode;
}
