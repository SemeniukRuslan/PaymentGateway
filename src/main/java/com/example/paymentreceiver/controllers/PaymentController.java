package com.example.paymentreceiver.controllers;

import com.example.paymentreceiver.dto.CardDTO;
import com.example.paymentreceiver.dto.CardHolderDTO;
import com.example.paymentreceiver.dto.PaymentDTO;
import com.example.paymentreceiver.dto.ResponseHolder;
import com.example.paymentreceiver.models.Payment;
import com.example.paymentreceiver.service.PaymentsService;
import com.example.paymentreceiver.util.MaskUtils;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/pay")
public class PaymentController {

    private final PaymentsService paymentsServices;
    private final ModelMapper modelMapper;

    @Autowired
    public PaymentController(PaymentsService paymentsServices, ModelMapper modelMapper) {
        this.paymentsServices = paymentsServices;
        this.modelMapper = modelMapper;
    }

    @PostMapping
    public ResponseEntity<ResponseHolder> addPayment(@RequestBody @Valid PaymentDTO paymentDTO) throws Exception {
        paymentsServices.savePayments(convertToPayments(paymentDTO));
        return new ResponseEntity<>(ResponseHolder.builder().approved(true).build(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getPayment(@PathVariable("id") int id) throws Exception {
        Optional<Payment> byId = paymentsServices.findById(id);
        PaymentDTO paymentDTO = convertToPaymentsDTO(byId.orElse(null));
        return id != 0
                ? new ResponseEntity<>(paymentDTO, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping
    public ResponseEntity<List<PaymentDTO>> findAll() {
        var payAll = paymentsServices
                .findAll()
                .stream()
                .map(this::convertToPaymentsDTO)
                .toList();

        return payAll.size() != 0
                ? new ResponseEntity<>(payAll, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    public Payment convertToPayments(PaymentDTO paymentDTO) {
        return modelMapper.map(paymentDTO, Payment.class);
    }

    private PaymentDTO convertToPaymentsDTO(Payment payment) {
        PaymentDTO paymentDTO = modelMapper.map(payment, PaymentDTO.class);
        CardDTO card = paymentDTO.getCard();
        CardHolderDTO cardHolderDTO = paymentDTO.getCardHolder();
        cardHolderDTO.setName(MaskUtils.maskName(cardHolderDTO.getName()));

        card.setPan(MaskUtils.maskPan(card.getPan()));
        card.setExpiryDate(MaskUtils.maskExpiryDate(card.getExpiryDate()));
        card.setCvv(MaskUtils.maskCvv(card.getCvv()));

        return paymentDTO;
    }
}
