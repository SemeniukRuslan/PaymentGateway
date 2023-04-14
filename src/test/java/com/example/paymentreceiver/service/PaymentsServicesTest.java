package com.example.paymentreceiver.service;

import com.example.paymentreceiver.models.Card;
import com.example.paymentreceiver.models.CardHolder;
import com.example.paymentreceiver.models.Payment;
import com.example.paymentreceiver.repositories.PaymentsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentsServicesTest {

    @Mock
    private PaymentsRepository paymentsRepository;

    @Mock
    private CryptoService cryptoService;

    @Mock
    private AuditService auditService;

    @InjectMocks
    private PaymentsService paymentsService;

    private List<Payment> paymentsList;

    @BeforeEach
    public void setUp() {
        paymentsList = new ArrayList<>();

        Payment payment1 = Payment.builder()
                .amount(452)
                .invoice(5555)
                .currency("USD")
                .cardHolder(new CardHolder(
                        "Ivan Hoper",
                        "pre@gmail.com"))
                .card(new Card(
                        "4974968999862909",
                        "0629",
                        "654"))
                .build();

        Payment payment2 = Payment.builder()
                .amount(1252)
                .invoice(11)
                .currency("EUR")
                .cardHolder(new CardHolder(
                        "John Duglas",
                        "first@gmail.com"))
                .card(new Card(
                        "4731121841687736",
                        "1227",
                        "291"))
                .build();

        Payment payment3 = Payment.builder()
                .amount(11400)
                .invoice(9)
                .currency("UAH")
                .cardHolder(new CardHolder(
                        "Kost Dikni",
                        "second@gmail.com"))
                .card(new Card(
                        "4183758050232929",
                        "1123",
                        "792"))
                .build();


        paymentsList.add(payment1);
        paymentsList.add(payment2);
        paymentsList.add(payment3);
    }

    @Test
    void findById() throws Exception {
        when(paymentsRepository.findById(9)).thenReturn(Optional.of(paymentsList.get(2)));
        when(cryptoService.decode(any(Payment.class))).thenReturn(paymentsList.get(2));

        Optional<Payment> id = paymentsService.findById(9);

        assertAll(() -> {
            final Payment payment = paymentsList.get(2);
            final Payment payment1 = id.get();
            assertEquals(payment.getAmount(), payment1.getAmount());
            assertEquals(payment.getCurrency(), id.get().getCurrency());
        });

        verify(paymentsRepository).findById(9);
    }

    @Test
    void findByIncorrectId() {
        Payment payment = paymentsList.get(0);

        when(paymentsRepository.findById(9)).thenReturn(Optional.of(payment));

        org.junit.jupiter.api.Assertions.assertThrows(RuntimeException.class, () -> {
            paymentsService.findById(payment.getInvoice());
        });

        verify(paymentsRepository, times(1)).findById(payment.getInvoice());
    }

    @Test
    void findAll() {
        given(paymentsRepository.findAll()).willReturn(paymentsList);

        List<Payment> paymentsList2 = paymentsService.findAll();

        assertThat(paymentsList2).isNotNull();
        assertThat(paymentsList2.size()).isEqualTo(3);
        verify(paymentsRepository, times(1)).findAll();
    }

    @Test
    void savePayment() throws Exception {
        when(paymentsRepository.save(any(Payment.class))).thenReturn(paymentsList.get(0));
        when(cryptoService.encode(any(Payment.class))).thenReturn(paymentsList.get(0));

        Payment savedPayments = paymentsService.savePayments(paymentsList.get(0));

        assertThat(savedPayments).isNotNull();
        verify(paymentsRepository).save(argThat(argument -> argument.getInvoice() == 5555));

        verify(auditService).saveToAuditLog(any());
    }

    @Test
    void exceptionSavePayment() throws Exception {
        Payment payment = paymentsList.get(0);

        when(paymentsRepository.save(payment)).thenThrow(RuntimeException.class);

        org.junit.jupiter.api.Assertions.assertThrows(Exception.class, () -> {
            paymentsService.savePayments(payment);
        });

        verify(paymentsRepository, never()).save(any(Payment.class));
    }
}
