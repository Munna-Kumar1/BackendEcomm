package com.prasadfencing.backendecom.inquiry.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateInquiryRequestDTO {
    private String subject;
    private String message;
}
