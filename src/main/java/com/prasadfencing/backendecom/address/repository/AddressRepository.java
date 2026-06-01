package com.prasadfencing.backendecom.address.repository;

import com.prasadfencing.backendecom.address.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface AddressRepository extends JpaRepository<Address, Long> {

    List<Address> findByUserId(Long userId);

    // RESET DEFAULT ADDRESS
    @Modifying
    @Transactional
    @Query("UPDATE Address a SET a.defaultAddress = false WHERE a.user.id = :userId")
    void resetDefaultByUserId(Long userId);

    // SET DEFAULT ADDRESS
    @Modifying
    @Transactional
    @Query("UPDATE Address a SET a.defaultAddress = true WHERE a.id = :addressId")
    void setDefaultAddress(@Param("addressId") Long addressId);
}