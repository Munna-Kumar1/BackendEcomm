package com.prasadfencing.backendecom.address.entity;

import com.prasadfencing.backendecom.auth.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "addresses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fullName;
    private String phone;
    private String street;
    private String city;
    private String state;
    private String pincode;
    private String country;

    private boolean defaultAddress;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}