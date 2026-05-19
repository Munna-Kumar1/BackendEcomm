package com.prasadfencing.backendecom.delivery.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class DeliveryCheckResponseDTO {
    private String message;
    private boolean available;
}
