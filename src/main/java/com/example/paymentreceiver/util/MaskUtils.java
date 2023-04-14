package com.example.paymentreceiver.util;

import lombok.experimental.UtilityClass;

@UtilityClass
// TODO should be refactored
public class MaskUtils {

    public static String maskName(String input) {
        var g = input.length();
        return input.replaceAll("\\w+\\s\\w+|\\w+", "*".repeat(input.length()));
    }

    public static String maskPan(String input) {
        String beginPan = input.substring(0, 12);
        String endPan = input.substring(12, 16);
        beginPan = beginPan.replaceAll("\\d{12}", "*".repeat(beginPan.length()));
        return beginPan + endPan;
    }

    public static String maskExpiryDate(String inputDate) {
        return inputDate.replaceAll("^\\d{4}$", "*".repeat(4));
    }

    public static String maskCvv(String cvv) {
        return cvv.replaceAll("\\d{3}", "*".repeat(3));
    }
}