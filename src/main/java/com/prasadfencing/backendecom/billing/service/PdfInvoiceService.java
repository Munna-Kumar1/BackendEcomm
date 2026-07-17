package com.prasadfencing.backendecom.billing.service;

import com.prasadfencing.backendecom.billing.entity.Invoice;
import com.prasadfencing.backendecom.billing.repository.InvoiceRepository;
import com.prasadfencing.backendecom.address.service.AddressService;

import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@RequiredArgsConstructor
public class PdfInvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final AddressService addressService;

    public byte[] generatePdf(Long invoiceId) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new RuntimeException("Invoice not found"));

        // Fetching the user's phone number dynamically using your AddressService
        Long userId = invoice.getUser().getId();
        String customerPhone;
        try {
            customerPhone = addressService.getPhoneByUserId(userId);
        } catch (Exception e) {
            customerPhone = "+91 XXXXXXXXXX"; // Fallback safeguard
        }

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(out);
            PdfDocument pdf = new PdfDocument(writer);

            Document document = new Document(pdf, PageSize.A4);
            document.setMargins(20, 20, 20, 20);

            DeviceRgb primaryTeal = new DeviceRgb(0, 140, 140);
            DeviceRgb slateText = new DeviceRgb(30, 41, 59);

            // ==========================================
            // 1. CORPORATE IDENTITY HEADER
            // ==========================================
            Table headerTable = new Table(UnitValue.createPercentArray(new float[]{65, 35})).useAllAvailableWidth();
            headerTable.setBorder(Border.NO_BORDER);

            Cell companyDetailsCell = new Cell().add(
                    new Paragraph("PRASAD FENCING ENTERPRISES")
                            .setBold().setFontSize(22).setFontColor(slateText)
            ).add(
                    new Paragraph("Manufacturing & Supply of High Quality Fencing Systems")
                            .setItalic().setFontSize(9.5f).setFontColor(primaryTeal)
            ).add(
                    new Paragraph("KAJUBAGAN PANCHOLI, Mesra,\nRanchi, Jharkhand - 835215")
                            .setFontSize(9).setMarginTop(4)
            ).setBorder(Border.NO_BORDER);

            Cell metaDetailsCell = new Cell().add(
                    new Paragraph("Tel : +91 73628 91311\nEmail : prasadfencingenterprises@gmail.com")
                            .setFontSize(9).setTextAlignment(TextAlignment.RIGHT).setMultipliedLeading(1.2f)
            ).setBorder(Border.NO_BORDER);

            headerTable.addCell(companyDetailsCell);
            headerTable.addCell(metaDetailsCell);
            document.add(headerTable);

            Table accentBar = new Table(1).useAllAvailableWidth();
            accentBar.addCell(new Cell().setBackgroundColor(primaryTeal).setHeight(4f).setBorder(Border.NO_BORDER));
            document.add(accentBar.setMarginTop(6).setMarginBottom(10));

            // ==========================================
            // 2. REGULATORY & TAX TITLES
            // ==========================================
            Table titleTable = new Table(UnitValue.createPercentArray(new float[]{35, 30, 35})).useAllAvailableWidth();
            titleTable.addCell(new Cell().add(new Paragraph("PAN : DNWPP5403C\nGSTIN : 20DNWPP5403C1Z5").setFontSize(8.5f)).setBorder(Border.NO_BORDER));
            titleTable.addCell(new Cell().add(new Paragraph("TAX INVOICE").setBold().setFontSize(14).setTextAlignment(TextAlignment.CENTER)).setBorder(Border.NO_BORDER));
            titleTable.addCell(new Cell().add(new Paragraph("ORIGINAL FOR RECIPIENT").setFontSize(8).setTextAlignment(TextAlignment.RIGHT)).setBorder(Border.NO_BORDER));
            document.add(titleTable.setMarginBottom(10));

            // ==========================================
            // 3. ENCAPSULATED METADATA BLOCK
            // ==========================================
            Table metaBlock = new Table(UnitValue.createPercentArray(new float[]{50, 50})).useAllAvailableWidth();
            metaBlock.setBorder(new SolidBorder(0.5f));

            Cell customerPane = new Cell()
                    .add(new Paragraph("Customer Detail").setBold().setFontSize(9.5f).setUnderline())
                    .add(new Paragraph("M/S : " + invoice.getUser().getEmail().split("@")[0].toUpperCase()).setFontSize(9).setBold())
                    .add(new Paragraph("Address : Customer Registered Address,\nDomestic Region").setFontSize(9))
                    .add(new Paragraph("Phone : " + customerPhone).setFontSize(9))
                    .add(new Paragraph("Email : " + invoice.getUser().getEmail()).setFontSize(9))
                    .setPadding(8);

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MMM-yyyy");
            Cell invoicePane = new Cell()
                    .add(new Paragraph("Invoice No :  " + invoice.getInvoiceNumber()).setFontSize(9).setBold())
                    .add(new Paragraph("Invoice Date : " + invoice.getCreatedAt().format(formatter)).setFontSize(9))
                    .setPadding(8);

            metaBlock.addCell(customerPane);
            metaBlock.addCell(invoicePane);
            document.add(metaBlock.setMarginBottom(12));

            // ==========================================
            // 4. TRANSACTION LINE ITEMS MATRIX
            // ==========================================
            float[] columnWidths = {5, 42, 8, 10, 11, 7, 7, 10};
            Table itemsTable = new Table(UnitValue.createPercentArray(columnWidths)).useAllAvailableWidth();

            String[] headers = {"Sr.", "Name of Product / Service", "Qty", "Rate", "Taxable Value", "GST %", "GST Amt", "Total"};
            for (String header : headers) {
                itemsTable.addHeaderCell(new Cell().add(new Paragraph(header).setBold().setFontSize(8.5f).setTextAlignment(TextAlignment.CENTER)));
            }

            AtomicInteger srNo = new AtomicInteger(1);
            invoice.getOrder().getItems().forEach(item -> {
                double qty = item.getQuantity();
                double rate = item.getPrice();
                double taxableVal = rate * qty;
                double gstRate = 18.0;
                double gstAmt = taxableVal * (gstRate / 100);
                double itemTotal = taxableVal + gstAmt;

                itemsTable.addCell(new Cell().add(new Paragraph(String.valueOf(srNo.getAndIncrement())).setFontSize(8.5f).setTextAlignment(TextAlignment.CENTER)));
                itemsTable.addCell(new Cell().add(new Paragraph(item.getProduct().getName()).setFontSize(8.5f)));
                itemsTable.addCell(new Cell().add(new Paragraph((int)qty + " NOS").setFontSize(8.5f).setTextAlignment(TextAlignment.CENTER)));
                itemsTable.addCell(new Cell().add(new Paragraph(String.format("%.2f", rate)).setFontSize(8.5f).setTextAlignment(TextAlignment.RIGHT)));
                itemsTable.addCell(new Cell().add(new Paragraph(String.format("%.2f", taxableVal)).setFontSize(8.5f).setTextAlignment(TextAlignment.RIGHT)));
                itemsTable.addCell(new Cell().add(new Paragraph(gstRate + "%").setFontSize(8.5f).setTextAlignment(TextAlignment.CENTER)));
                itemsTable.addCell(new Cell().add(new Paragraph(String.format("%.2f", gstAmt)).setFontSize(8.5f).setTextAlignment(TextAlignment.RIGHT)));
                itemsTable.addCell(new Cell().add(new Paragraph(String.format("%.2f", itemTotal)).setFontSize(8.5f).setTextAlignment(TextAlignment.RIGHT)));
            });

            document.add(itemsTable.setMarginBottom(12));

            // ==========================================
            // 5. REGULATORY FINANCIALS & BANK DETAILS
            // ==========================================
            Table summaryTable = new Table(UnitValue.createPercentArray(new float[]{55, 45})).useAllAvailableWidth();
            summaryTable.setBorder(new SolidBorder(0.5f));

            Cell bankingCell = new Cell().add(new Paragraph("Bank Details").setBold().setFontSize(9))
                    .add(new Paragraph("Bank Name : State Bank of India\nBranch : Mesra, Ranchi\nA/C Number : 39123456789\nIFSC Code : SBIN0001234").setFontSize(8).setMultipliedLeading(1.1f))
                    .add(new Paragraph("\nTerms and Conditions :").setBold().setFontSize(8))
                    .add(new Paragraph("1. Subject to Ranchi Jurisdiction.\n2. Our Responsibility Ceases as soon as goods leave our Premises.\n3. Goods once sold will not be taken back.").setFontSize(7).setMultipliedLeading(1.1f))
                    .setPadding(6);

            double orderTotal = invoice.getTotalAmount();
            double totalTaxable = orderTotal / 1.18;
            double calculatedTax = orderTotal - totalTaxable;

            Table totalsGrid = new Table(UnitValue.createPercentArray(new float[]{60, 40})).useAllAvailableWidth();
            totalsGrid.addCell(new Cell().add(new Paragraph("Taxable Amount:").setFontSize(8.5f)).setBorder(Border.NO_BORDER));
            totalsGrid.addCell(new Cell().add(new Paragraph(String.format("₹%.2f", totalTaxable)).setFontSize(8.5f).setTextAlignment(TextAlignment.RIGHT)).setBorder(Border.NO_BORDER));

            totalsGrid.addCell(new Cell().add(new Paragraph("Integrated GST (18%):").setFontSize(8.5f)).setBorder(Border.NO_BORDER));
            totalsGrid.addCell(new Cell().add(new Paragraph(String.format("₹%.2f", calculatedTax)).setFontSize(8.5f).setTextAlignment(TextAlignment.RIGHT)).setBorder(Border.NO_BORDER));

            totalsGrid.addCell(new Cell().add(new Paragraph("Total Tax:").setFontSize(8.5f)).setBorder(Border.NO_BORDER));
            totalsGrid.addCell(new Cell().add(new Paragraph(String.format("₹%.2f", calculatedTax)).setFontSize(8.5f).setTextAlignment(TextAlignment.RIGHT)).setBorder(Border.NO_BORDER));

            totalsGrid.addCell(new Cell().add(new Paragraph("Total Amount After Tax:").setBold().setFontSize(10.5f)).setBorder(Border.NO_BORDER));
            totalsGrid.addCell(new Cell().add(new Paragraph(String.format("₹%.2f", orderTotal)).setBold().setFontSize(10.5f).setTextAlignment(TextAlignment.RIGHT)).setBorder(Border.NO_BORDER));

            Cell totalsCell = new Cell().add(totalsGrid).setPadding(6);

            summaryTable.addCell(bankingCell);
            summaryTable.addCell(totalsCell);
            document.add(summaryTable.setMarginBottom(35));

            // ==========================================
            // 6. CLOSING & ACKNOWLEDGEMENT BLOCKS
            // ==========================================
            Table footerSignTable = new Table(UnitValue.createPercentArray(new float[]{50, 50})).useAllAvailableWidth();
            footerSignTable.setBorder(Border.NO_BORDER);

            footerSignTable.addCell(new Cell().add(new Paragraph("Customer Signature\n\n\n_______________________").setFontSize(8.5f).setTextAlignment(TextAlignment.LEFT)).setBorder(Border.NO_BORDER));

            Cell authSignCell = new Cell().add(
                    new Paragraph("For Prasad Fencing Enterprises").setBold().setFontSize(8.5f).setTextAlignment(TextAlignment.RIGHT)
            ).add(
                    new Paragraph("\n\nAuthorized Signatory").setFontSize(8.5f).setTextAlignment(TextAlignment.RIGHT)
            ).setBorder(Border.NO_BORDER);

            footerSignTable.addCell(authSignCell);
            document.add(footerSignTable);

            document.add(new Paragraph("\nThis is a computer generated invoice and requires no physical signature.").setFontSize(7.5f).setItalic().setTextAlignment(TextAlignment.CENTER).setMarginTop(15));

            document.close();
            return out.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Structured PDF layout execution failed", e);
        }
    }
}