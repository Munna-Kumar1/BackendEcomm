package com.prasadfencing.backendecom.delivery.controller;

import com.prasadfencing.backendecom.delivery.dto.AddPincodeRequestDTO;
import com.prasadfencing.backendecom.delivery.dto.DeliveryCheckResponseDTO;
import com.prasadfencing.backendecom.delivery.service.DeliveryService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/delivery")
@RequiredArgsConstructor
public class DeliveryController {

    private final DeliveryService deliveryService;

    // USER CHECK DELIVERY
    @GetMapping("/check/{pincode}")
    public DeliveryCheckResponseDTO checkDelivery(@PathVariable String pincode) {
        return deliveryService.checkDelivery(pincode);
    }

    // ADMIN ADD PINCODE
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/add")
    public String addPincode(@RequestBody AddPincodeRequestDTO request) {
        return deliveryService.addPincode(request);
    }
}