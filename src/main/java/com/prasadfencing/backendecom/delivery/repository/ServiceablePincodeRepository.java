package com.prasadfencing.backendecom.delivery.repository;

import com.prasadfencing.backendecom.delivery.entity.ServiceablePincode;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServiceablePincodeRepository
        extends JpaRepository<ServiceablePincode, Long> {

    boolean existsByPincode(String pincode);
}