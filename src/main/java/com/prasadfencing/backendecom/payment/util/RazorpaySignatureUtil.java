package com.prasadfencing.backendecom.payment.util;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class RazorpaySignatureUtil {

    public static boolean verifySignature(
            String orderId,
            String paymentId,
            String signature,
            String secret) {

        try {
            String payload = orderId + "|" + paymentId;

            Mac sha256Hmac = Mac.getInstance("HmacSHA256");

            SecretKeySpec secretKey =
                    new SecretKeySpec(secret.getBytes(), "HmacSHA256");

            sha256Hmac.init(secretKey);

            byte[] hash = sha256Hmac.doFinal(payload.getBytes());

            String generatedSignature = bytesToHex(hash);

            return generatedSignature.equals(signature);

        } catch (Exception e) {
            throw new RuntimeException("Signature verification failed");
        }
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder result = new StringBuilder();

        for (byte b : bytes) {
            result.append(String.format("%02x", b));
        }

        return result.toString();
    }
    public static boolean verifyWebhookSignature(
            String payload,
            String signature,
            String secret) {

        try {
            javax.crypto.Mac mac =
                    javax.crypto.Mac.getInstance("HmacSHA256");

            javax.crypto.spec.SecretKeySpec secretKey =
                    new javax.crypto.spec.SecretKeySpec(
                            secret.getBytes(), "HmacSHA256");

            mac.init(secretKey);

            byte[] hash = mac.doFinal(payload.getBytes());

            String generated = bytesToHex(hash);

            return generated.equals(signature);

        } catch (Exception e) {
            return false;
        }
    }
}