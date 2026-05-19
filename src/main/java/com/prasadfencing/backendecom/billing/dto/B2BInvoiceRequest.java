package com.prasadfencing.backendecom.billing.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class B2BInvoiceRequest {

    private Double amount;
    private String gstNumber;
}
