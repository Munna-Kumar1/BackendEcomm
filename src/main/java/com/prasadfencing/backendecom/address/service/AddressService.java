package com.prasadfencing.backendecom.address.service;

import com.prasadfencing.backendecom.address.dto.AddressRequest;
import com.prasadfencing.backendecom.address.dto.AddressResponse;
import com.prasadfencing.backendecom.address.entity.Address;
import com.prasadfencing.backendecom.address.repository.AddressRepository;
import com.prasadfencing.backendecom.auth.entity.User;
import com.prasadfencing.backendecom.auth.repository.UserRepository;
import com.prasadfencing.backendecom.delivery.repository.ServiceablePincodeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AddressService {

    private final AddressRepository addressRepository;
    private final UserRepository userRepository;
    private final ServiceablePincodeRepository serviceablePincodeRepository;

    // GET CURRENT USER EMAIL
    private User getCurrentUser() {

        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    // ADD ADDRESS
    public AddressResponse addAddress(AddressRequest request) {

        boolean isServiceable = serviceablePincodeRepository
                .existsByPincode(request.getPincode());

        if (!isServiceable) {
            throw new RuntimeException(
                    "Sorry, our service is not available at pincode: " + request.getPincode()
            );
        }


        User user = getCurrentUser();

        Address address = Address.builder()
                .fullName(request.getFullName())
                .phone(request.getPhone())
                .street(request.getStreet())
                .city(request.getCity())
                .state(request.getState())
                .pincode(request.getPincode())
                .country(request.getCountry())
                .defaultAddress(request.isDefaultAddress())
                .user(user)
                .build();

        addressRepository.save(address);

        return map(address);
    }

    // GET MY ADDRESSES
    public List<AddressResponse> getMyAddresses() {

        User user = getCurrentUser();

        return addressRepository.findByUserId(user.getId())
                .stream()
                .map(this::map)
                .toList();
    }

    // DELETE ADDRESS
    public String deleteAddress(Long id) {

        User user = getCurrentUser();

        Address address = addressRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Address not found"));

        if (!address.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Not allowed");
        }

        addressRepository.delete(address);

        return "Address deleted";
    }

    // MAPPER
    private AddressResponse map(Address a) {

        return AddressResponse.builder()
                .id(a.getId())
                .fullName(a.getFullName())
                .phone(a.getPhone())
                .street(a.getStreet())
                .city(a.getCity())
                .state(a.getState())
                .pincode(a.getPincode())
                .country(a.getCountry())
                .defaultAddress(a.isDefaultAddress())
                .build();
    }

    // Update Address
    public AddressResponse updateAddress(Long id, AddressRequest request) {

        User user = getCurrentUser();

        Address address = addressRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Address not found"));

        // 🔐 OWNERSHIP CHECK (VERY IMPORTANT)
        if (!address.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Not allowed to update this address");
        }

        // 📝 UPDATE FIELDS
        address.setFullName(request.getFullName());
        address.setPhone(request.getPhone());
        address.setStreet(request.getStreet());
        address.setCity(request.getCity());
        address.setState(request.getState());
        address.setPincode(request.getPincode());
        address.setCountry(request.getCountry());
        address.setDefaultAddress(request.isDefaultAddress());

        // 💡 OPTIONAL: handle default address logic
        if (request.isDefaultAddress()) {
            addressRepository.resetDefaultByUserId(user.getId());
        }

        Address updated = addressRepository.save(address);

        return map(updated);
    }
}