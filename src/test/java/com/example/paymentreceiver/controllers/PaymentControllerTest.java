package com.example.paymentreceiver.controllers;

import com.example.paymentreceiver.dto.CardDTO;
import com.example.paymentreceiver.dto.CardHolderDTO;
import com.example.paymentreceiver.dto.PaymentDTO;
import com.example.paymentreceiver.models.Card;
import com.example.paymentreceiver.models.CardHolder;
import com.example.paymentreceiver.models.Payment;
import com.example.paymentreceiver.service.PaymentsService;
import com.example.paymentreceiver.util.MaskUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = PaymentController.class)
class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PaymentsService paymentsService;

    private ObjectMapper objectMapper;

    @Autowired
    private ModelMapper modelMapper;

    private List<Payment> paymentsList;

    @BeforeEach
    void setUp() {
        this.paymentsList = new ArrayList<>();

        Payment payment1 = Payment.builder()
                .amount(452)
                .invoice(5555)
                .currency("USD")
                .card(new Card(
                        "4974968999862909",
                        "0629",
                        "654"))
                .cardHolder(new CardHolder(
                        "Ivan Hoper",
                        "pre@gmail.com"))
                .build();

        Payment payment2 = Payment.builder()
                .amount(1252)
                .invoice(11)
                .currency("EUR")
                .card(new Card(
                        "4731121841687736",
                        "1227",
                        "291"))
                .cardHolder(new CardHolder(
                        "John Duglas",
                        "first@gmail.com"))
                .build();

        Payment payment3 = Payment.builder()
                .amount(11400)
                .invoice(9)
                .currency("UAH")
                .card(new Card(
                        "4183758050232929",
                        "1123",
                        "792"))
                .cardHolder(new CardHolder(
                        "Kost Dikni",
                        "second@gmail.com"))
                .build();

        paymentsList.add(payment1);
        paymentsList.add(payment2);
        paymentsList.add(payment3);

        modelMapper = new ModelMapper();
        objectMapper = new ObjectMapper();
    }

    @Test
    void savePayment() throws Exception {
        Payment payment = paymentsList.get(2);

        this.mockMvc.perform(post("/pay")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payment)))
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.approved").value("true"))
                .andDo(print());
    }

    @Test
    void findPayment() throws Exception {
        Payment payment = paymentsList.get(1);
        given(paymentsService.findById(11)).willReturn(Optional.ofNullable(paymentsList.get(1)));

        this.mockMvc.perform(get("/pay/11"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.invoice", is(payment.getInvoice())))
                .andExpect(jsonPath("$.amount", is(payment.getAmount())))
                .andExpect(jsonPath("$.currency", is(payment.getCurrency())))
                .andExpect(jsonPath("$.card.pan", is("************7736")))
                .andExpect(jsonPath("$.card.cvv", is("***")))
                .andExpect(jsonPath("$.cardHolder.name", is("***********")))
                .andExpect(jsonPath("$.cardHolder.email", is(payment.getCardHolder().getEmail())))
                .andDo(print());

        verify(paymentsService).findById(11);
    }

    @Test
    void findAllPayments() throws Exception {
        given(paymentsService.findAll()).willReturn(paymentsList);

        mockMvc.perform(get("/pay"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(paymentsList.size())))
                .andDo(print());

        verify(paymentsService).findAll();
    }

    @Test
    void inputPaymentAndConvertToMaskPaymentDTO() {
        Payment payment = paymentsList.get(2);
        PaymentDTO paymentDTO = modelMapper.map(payment, PaymentDTO.class);
        CardDTO card = paymentDTO.getCard();
        CardHolderDTO cardHolderDTO = paymentDTO.getCardHolder();

        cardHolderDTO.setName(MaskUtils.maskName(cardHolderDTO.getName()));
        card.setPan(MaskUtils.maskPan(card.getPan()));
        card.setExpiryDate(MaskUtils.maskExpiryDate(card.getExpiryDate()));
        card.setCvv(MaskUtils.maskCvv(card.getCvv()));

        assertEquals("**********", cardHolderDTO.getName());
        assertEquals("************2929", card.getPan());
        assertEquals("****", card.getExpiryDate());
        assertEquals("***", card.getCvv());
    }
}