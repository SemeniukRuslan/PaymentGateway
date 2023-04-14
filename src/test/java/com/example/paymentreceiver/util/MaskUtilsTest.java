package com.example.paymentreceiver.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MaskUtilsTest {
    @Test
    void maskName() {
        //when
        final String actual1 = MaskUtils.maskName("Darek Krolewski");
        final String actual2 = MaskUtils.maskName("Krolewski");
        final String expected1 = "***************";
        final String expected2 = "*********";
        //then
        assertEquals(expected1, actual1);
        assertEquals(expected2, actual2);
    }

    @Test
    void maskPan() {
        //when
        final String actual1 = MaskUtils.maskPan("1011567845663423");
        final String actual2 = MaskUtils.maskPan("2030303992923800");
        final String expected1 = "************3423";
        final String expected2 = "************3800";
        //then
        assertEquals(expected1, actual1);
        assertEquals(expected2, actual2);
    }

    @Test
    void maskExpiryDate() {
        //when
        final String actual1 = MaskUtils.maskExpiryDate("1224");
        final String actual2 = MaskUtils.maskExpiryDate("0625");
        final String expected1 = "****";
        System.out.println(actual2);
        //then
        assertEquals(expected1, actual1);
        assertEquals(expected1, actual2);
    }
}