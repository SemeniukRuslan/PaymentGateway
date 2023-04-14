package com.example.paymentreceiver.service;

import com.example.paymentreceiver.exception.AlreadyExistsException;
import com.example.paymentreceiver.exception.NotFoundException;
import com.example.paymentreceiver.models.Payment;
import com.example.paymentreceiver.repositories.PaymentsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class PaymentsService {

    private final PaymentsRepository paymentsRepository;

    private final CryptoService cryptoService;

    private final AuditService auditService;

    @Autowired
    public PaymentsService(CryptoService cryptoService, PaymentsRepository paymentsRepository,
                           AuditService auditService) {
        this.cryptoService = cryptoService;
        this.paymentsRepository = paymentsRepository;
        this.auditService = auditService;
    }

    public Optional<Payment> findById(int id) throws Exception {
        return paymentsRepository
                .findById(id)
                .map(cryptoService::decode)
                .map(Optional::of)
                .orElseThrow(() -> new NotFoundException(String.valueOf(id)));
    }

    public List<Payment> findAll() {
        return paymentsRepository
                .findAll()
                .stream()
                .map(cryptoService::decode)
                .toList();
    }

    @Transactional
    public Payment savePayments(Payment payment) throws Exception {
        Payment saveToDB = cryptoService.encode(payment);

        if (paymentsRepository.findById(payment.getInvoice()).isPresent())
            throw new AlreadyExistsException("" + payment.getInvoice());
        paymentsRepository.save(saveToDB);
        auditService.saveToAuditLog(saveToDB);

        return payment;
    }
}


