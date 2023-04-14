package com.example.paymentreceiver.repositories;

import com.example.paymentreceiver.models.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentsRepository
        extends JpaRepository<Payment, Integer> {
}
