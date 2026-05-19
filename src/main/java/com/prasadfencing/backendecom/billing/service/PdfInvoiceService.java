package com.prasadfencing.backendecom.billing.service;

import com.prasadfencing.backendecom.billing.entity.Invoice;
import com.itextpdf.kernel.pdf.*;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;

@Service
@RequiredArgsConstructor
public class PdfInvoiceService {

    private final InvoiceService invoiceService;

    public byte[] generatePdf(Long invoiceId) {

        Invoice invoice = invoiceService.getById(invoiceId);

        try {

            ByteArrayOutputStream out = new ByteArrayOutputStream();

            PdfWriter writer = new PdfWriter(out);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            document.add(new Paragraph("PRASAD FENCING ENTERPRISES"));
            document.add(new Paragraph("-----------------------------"));
            document.add(new Paragraph("Invoice No: " + invoice.getInvoiceNumber()));
            document.add(new Paragraph("Type: " + invoice.getType()));
            document.add(new Paragraph("Amount: " + invoice.getTotalAmount()));

            if (invoice.getGstNumber() != null) {
                document.add(new Paragraph("GST: " + invoice.getGstNumber()));
            }

            document.add(new Paragraph("-----------------------------"));
            document.add(new Paragraph("Thank you for your business!"));

            document.close();

            return out.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("PDF generation failed");
        }
    }
}