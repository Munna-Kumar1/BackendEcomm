package com.prasadfencing.backendecom.billing.controller;

import com.prasadfencing.backendecom.billing.dto.B2BInvoiceRequest;
import com.prasadfencing.backendecom.billing.entity.Invoice;
import com.prasadfencing.backendecom.billing.service.InvoiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/invoices")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminInvoiceController {

    private final InvoiceService invoiceService;

    @PostMapping("/order/{orderId}")
    public Invoice generateFromOrder(@PathVariable Long orderId) {
        return invoiceService.generateInvoiceFromOrder(orderId);
    }

    @PostMapping("/b2b")
    public Invoice createB2B(@RequestBody B2BInvoiceRequest request) {
        return invoiceService.createB2BInvoice(
                request.getAmount(),
                request.getGstNumber()
        );
    }

    @GetMapping("/all")
    public List<Invoice> getAllInvoices() {
        return invoiceService.getAllInvoices();
    }
}