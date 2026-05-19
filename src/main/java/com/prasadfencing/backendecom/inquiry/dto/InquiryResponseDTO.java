package com.prasadfencing.backendecom.inquiry.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class InquiryResponseDTO {
    private Long id;
    private String subject;
    private String message;
    private String status;
    private LocalDateTime createdAt;

}
