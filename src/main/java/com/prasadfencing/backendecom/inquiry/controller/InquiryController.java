package com.prasadfencing.backendecom.inquiry.controller;

import com.prasadfencing.backendecom.inquiry.dto.CreateInquiryRequestDTO;
import com.prasadfencing.backendecom.inquiry.dto.InquiryResponseDTO;
import com.prasadfencing.backendecom.inquiry.dto.UpdateInquiryStatusDTO;
import com.prasadfencing.backendecom.inquiry.entity.Inquiry;
import com.prasadfencing.backendecom.inquiry.service.InquiryService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inquiry")
@RequiredArgsConstructor
public class InquiryController {

    private final InquiryService inquiryService;

    // USER CREATE INQUIRY
    @PostMapping
    public String create(@RequestBody CreateInquiryRequestDTO request) {
        return inquiryService.createInquiry(request);
    }

    // USER GET OWN INQUIRIES
    @GetMapping
    public List<InquiryResponseDTO> getMyInquiries() {
        return inquiryService.getMyInquiries();
    }

    // ADMIN UPDATE STATUS
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/status")
    public String updateStatus(
            @PathVariable Long id,
            @RequestBody UpdateInquiryStatusDTO request
    ) {
        return inquiryService.updateStatus(id, request.getStatus());
    }
}