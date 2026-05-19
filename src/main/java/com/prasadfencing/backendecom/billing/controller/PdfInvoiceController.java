package com.prasadfencing.backendecom.billing.controller;

import com.prasadfencing.backendecom.billing.service.PdfInvoiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/invoices")
@RequiredArgsConstructor
public class PdfInvoiceController {

    private final PdfInvoiceService pdfInvoiceService;

    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> download(@PathVariable Long id) {

        byte[] pdf = pdfInvoiceService.generatePdf(id);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }
}