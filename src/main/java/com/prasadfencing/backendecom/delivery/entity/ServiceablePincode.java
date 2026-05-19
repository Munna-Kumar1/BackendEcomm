package com.prasadfencing.backendecom.delivery.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "serviceable_pincodes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServiceablePincode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String state;

    @Column(unique = true, nullable = false)
    private String pincode;
}