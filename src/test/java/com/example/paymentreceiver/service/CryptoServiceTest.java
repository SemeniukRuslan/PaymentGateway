package com.example.paymentreceiver.service;

import com.example.paymentreceiver.models.Card;
import com.example.paymentreceiver.models.CardHolder;
import com.example.paymentreceiver.models.Payment;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CryptoServiceTest {

    private static CryptoService cryptoService;
    private static Payment payment1;
    private static Payment payment2;

    @BeforeEach
    public void before() {
        cryptoService = new CryptoService();

        payment1 = Payment.builder()
                .amount(1252)
                .invoice(11)
                .currency("EUR")
                .cardHolder(new CardHolder(
                        "John Duglas",
                        "first@gmail.com"))
                .card(new Card(
                        "1011567845663423",
                        "1126",
                        "291"))
                .build();

        payment2 = Payment.builder()
                .amount(9999999)
                .invoice(98326032)
                .currency("CHR")
                .cardHolder(new CardHolder(
                        "Boris Jonson",
                        "second@gmail.com"))
                .card(new Card(
                        "1534951357905511",
                        "1223",
                        "050"))
                .build();
    }

    @Test
    void encode() {
        final Payment encoded1 = cryptoService.encode(payment1);
        final Payment encoded2 = cryptoService.encode(payment2);

        Assertions.assertEquals("Sm9obiBEdWdsYXM=", encoded1.getCardHolder().getName());
        Assertions.assertEquals("MTAxMTU2Nzg0NTY2MzQyMw==", encoded1.getCard().getPan());
        Assertions.assertEquals("MTEyNg==", encoded1.getCard().getExpiryDate());
        Assertions.assertEquals("Mjkx", encoded1.getCard().getCvv());

        Assertions.assertEquals("Qm9yaXMgSm9uc29u", encoded2.getCardHolder().getName());
        Assertions.assertEquals("MTUzNDk1MTM1NzkwNTUxMQ==", encoded2.getCard().getPan());
        Assertions.assertEquals("MTIyMw==", encoded2.getCard().getExpiryDate());
        Assertions.assertEquals("MDUw", encoded2.getCard().getCvv());
    }

    @Test
    void decode() {
        Payment encode1 = cryptoService.encode(payment1);
        Payment decode1 = cryptoService.decode(encode1);
        Payment encode2 = cryptoService.encode(payment2);
        Payment decode2 = cryptoService.decode(encode2);

        Assertions.assertEquals("John Duglas", decode1.getCardHolder().getName());
        Assertions.assertEquals("1011567845663423", decode1.getCard().getPan());
        Assertions.assertEquals("1126", decode1.getCard().getExpiryDate());
        Assertions.assertEquals("291", decode1.getCard().getCvv());

        Assertions.assertEquals("Boris Jonson", decode2.getCardHolder().getName());
        Assertions.assertEquals("1534951357905511", decode2.getCard().getPan());
        Assertions.assertEquals("1223", decode2.getCard().getExpiryDate());
        Assertions.assertEquals("050", decode2.getCard().getCvv());
    }
}