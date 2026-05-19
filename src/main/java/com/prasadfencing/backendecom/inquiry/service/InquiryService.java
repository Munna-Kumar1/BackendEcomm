package com.prasadfencing.backendecom.inquiry.service;

import com.prasadfencing.backendecom.auth.entity.User;
import com.prasadfencing.backendecom.auth.repository.UserRepository;
import com.prasadfencing.backendecom.inquiry.dto.CreateInquiryRequestDTO;
import com.prasadfencing.backendecom.inquiry.dto.InquiryResponseDTO;
import com.prasadfencing.backendecom.inquiry.entity.Inquiry;
import com.prasadfencing.backendecom.inquiry.entity.InquiryStatus;
import com.prasadfencing.backendecom.inquiry.repository.InquiryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InquiryService {

    private final InquiryRepository inquiryRepository;
    private final UserRepository userRepository;

    private User getCurrentUser() {

        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    // CREATE INQUIRY
    public String createInquiry(CreateInquiryRequestDTO request) {

        User user = getCurrentUser();

        Inquiry inquiry = Inquiry.builder()
                .subject(request.getSubject())
                .message(request.getMessage())
                .status(InquiryStatus.NEW)
                .createdAt(LocalDateTime.now())
                .user(user)
                .build();

        inquiryRepository.save(inquiry);

        return "Inquiry submitted successfully";
    }

    // GET MY INQUIRIES
    public List<InquiryResponseDTO> getMyInquiries() {

        User user = getCurrentUser();

        return inquiryRepository.findByUserId(user.getId())
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    // ADMIN UPDATE STATUS
    public String updateStatus(Long id, InquiryStatus status) {

        Inquiry inquiry = inquiryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Inquiry not found"));

        inquiry.setStatus(status);

        inquiryRepository.save(inquiry);

        return "Status updated";
    }

    // MAPPER
    private InquiryResponseDTO mapToDTO(Inquiry inquiry) {

        return InquiryResponseDTO.builder()
                .id(inquiry.getId())
                .subject(inquiry.getSubject())
                .message(inquiry.getMessage())
                .status(inquiry.getStatus().name())
                .createdAt(inquiry.getCreatedAt())
                .build();
    }
}