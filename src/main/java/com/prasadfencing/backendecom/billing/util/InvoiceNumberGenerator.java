package com.prasadfencing.backendecom.billing.util;

import java.util.UUID;

public class InvoiceNumberGenerator {

    public static String generate() {
        return "INV-" + UUID.randomUUID().toString().substring(0, 8);
    }
}