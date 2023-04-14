package com.example.paymentreceiver.service;

import com.example.paymentreceiver.models.Card;
import com.example.paymentreceiver.models.CardHolder;
import com.example.paymentreceiver.models.Payment;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.Base64;
import java.util.Base64.Decoder;
import java.util.Base64.Encoder;

@Service
public class CryptoService implements Serializable {

    public Payment encode(Payment payment) {
        final Encoder encoder = Base64.getEncoder();
        final CardHolder cardHolder = payment.getCardHolder();
        final Card card = payment.getCard();
        cardHolder.setName(encoder.encodeToString(cardHolder.getName().getBytes()));
        card.setPan(encoder.encodeToString(card.getPan().getBytes()));
        card.setExpiryDate(encoder.encodeToString(card.getExpiryDate().getBytes()));
        card.setCvv(encoder.encodeToString(card.getCvv().getBytes()));
        return payment;
    }

    public Payment decode(Payment payment) {
        final Decoder decoder = Base64.getDecoder();
        final CardHolder cardHolder = payment.getCardHolder();
        final Card card = payment.getCard();
        cardHolder.setName(new String(decoder.decode(cardHolder.getName().getBytes())));
        card.setPan(new String(decoder.decode(card.getPan().getBytes())));
        card.setExpiryDate(new String(decoder.decode(card.getExpiryDate().getBytes())));
        card.setCvv(new String(decoder.decode(card.getCvv().getBytes())));
        return payment;
    }
}
