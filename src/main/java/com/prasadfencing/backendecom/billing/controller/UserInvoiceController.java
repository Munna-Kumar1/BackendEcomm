package com.prasadfencing.backendecom.billing.controller;

import com.prasadfencing.backendecom.billing.entity.Invoice;
import com.prasadfencing.backendecom.billing.service.InvoiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user/invoices")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
public class UserInvoiceController {

    private final InvoiceService invoiceService;

    @GetMapping
    public List<Invoice> getMyInvoices() {
        return invoiceService.getMyInvoices();
    }
}