package com.prasadfencing.backendecom.billing.service;

import com.prasadfencing.backendecom.billing.entity.Invoice;
import com.prasadfencing.backendecom.billing.repository.InvoiceRepository;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;

@Service
@RequiredArgsConstructor
public class PdfInvoiceService {

    private final InvoiceRepository invoiceRepository;

    public byte[] generatePdf(Long invoiceId) {

        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new RuntimeException("Invoice not found"));

        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();

            PdfWriter writer = new PdfWriter(out);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            // HEADER
            Paragraph company = new Paragraph("PRASAD FENCING ENTERPRISES")
                    .setBold()
                    .setFontSize(18)
                    .setTextAlignment(TextAlignment.CENTER);

            Paragraph address = new Paragraph(
                    "Industrial Area, Delhi, India | GSTIN: 07ABCDE1234F1Z5")
                    .setFontSize(10)
                    .setTextAlignment(TextAlignment.CENTER);

            document.add(company);
            document.add(address);
            document.add(new Paragraph("\n"));

            // INVOICE INFO
            document.add(new Paragraph("INVOICE")
                    .setBold()
                    .setFontSize(14));

            document.add(new Paragraph("Invoice No: " + invoice.getInvoiceNumber()));
            document.add(new Paragraph("Date: " + invoice.getCreatedAt()));
            document.add(new Paragraph("Status: " + invoice.getStatus()));

            document.add(new Paragraph("\n"));

            // CUSTOMER INFO
            document.add(new Paragraph("BILL TO:")
                    .setBold());

            document.add(new Paragraph("Customer ID: " + invoice.getUser().getId()));
            document.add(new Paragraph("Email: " + invoice.getUser().getEmail()));

            document.add(new Paragraph("\n"));

            // ORDER INFO
            document.add(new Paragraph("ORDER DETAILS")
                    .setBold());

            document.add(new Paragraph("Order ID: " + invoice.getOrder().getId()));

            document.add(new Paragraph("\n"));

            // ITEMS TABLE
            Table table = new Table(4);

            table.addHeaderCell("Product");
            table.addHeaderCell("Qty");
            table.addHeaderCell("Price");
            table.addHeaderCell("Total");

            invoice.getOrder().getItems().forEach(item -> {
                table.addCell(item.getProduct().getName());
                table.addCell(String.valueOf(item.getQuantity()));
                table.addCell(String.valueOf(item.getPrice()));
                table.addCell(String.valueOf(item.getPrice() * item.getQuantity()));
            });

            document.add(table);

            document.add(new Paragraph("\n"));

            // TOTAL
            Paragraph total = new Paragraph("TOTAL: ₹" + invoice.getTotalAmount())
                    .setBold()
                    .setFontSize(14)
                    .setTextAlignment(TextAlignment.RIGHT);

            document.add(total);

            // FOOTER
            document.add(new Paragraph("\nThank you for shopping with us!")
                    .setTextAlignment(TextAlignment.CENTER));

            document.close();

            return out.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("PDF generation failed", e);
        }
    }
}