package com.prasadfencing.backendecom.inquiry.dto;

import com.prasadfencing.backendecom.inquiry.entity.InquiryStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateInquiryStatusDTO {
    private InquiryStatus status;
}
