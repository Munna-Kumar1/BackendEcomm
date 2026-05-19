package com.prasadfencing.backendecom.delivery.service;

import com.prasadfencing.backendecom.delivery.dto.AddPincodeRequestDTO;
import com.prasadfencing.backendecom.delivery.dto.DeliveryCheckResponseDTO;
import com.prasadfencing.backendecom.delivery.entity.ServiceablePincode;
import com.prasadfencing.backendecom.delivery.repository.ServiceablePincodeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeliveryService {

    private final ServiceablePincodeRepository repository;

    // CHECK DELIVERY
    public DeliveryCheckResponseDTO checkDelivery(String pincode) {

        boolean exists = repository.existsByPincode(pincode);

        if (exists) {
            return DeliveryCheckResponseDTO.builder()
                    .available(true)
                    .message("Delivery available in your area")
                    .build();
        }

        return DeliveryCheckResponseDTO.builder()
                .available(false)
                .message("Sorry, delivery not available")
                .build();
    }

    // ADD PINCODE (ADMIN ONLY)
    public String addPincode(AddPincodeRequestDTO request) {

        if (repository.existsByPincode(request.getPincode())) {
            return "Pincode already exists";
        }

        ServiceablePincode pin = ServiceablePincode.builder()
                .state(request.getState())
                .pincode(request.getPincode())
                .build();

        repository.save(pin);

        return "Pincode added successfully";
    }
}