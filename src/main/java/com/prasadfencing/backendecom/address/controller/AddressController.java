package com.prasadfencing.backendecom.address.controller;

import com.prasadfencing.backendecom.address.dto.AddressRequest;
import com.prasadfencing.backendecom.address.dto.AddressResponse;
import com.prasadfencing.backendecom.address.service.AddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/address")
@RequiredArgsConstructor
public class AddressController {

    private final AddressService addressService;

    @PreAuthorize("isAuthenticated()")
    @PostMapping
    public ResponseEntity<AddressResponse> addAddress(
            @RequestBody AddressRequest request
    ) {
        return ResponseEntity.ok(addressService.addAddress(request));
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping
    public ResponseEntity<List<AddressResponse>> getMyAddresses() {
        return ResponseEntity.ok(addressService.getMyAddresses());
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteAddress(@PathVariable Long id) {
        return ResponseEntity.ok(addressService.deleteAddress(id));
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping("/{id}")
    public ResponseEntity<AddressResponse> updateAddress(
            @PathVariable Long id,
            @RequestBody AddressRequest request
    ) {
        return ResponseEntity.ok(addressService.updateAddress(id, request));
    }
}