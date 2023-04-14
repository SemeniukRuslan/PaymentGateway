package com.example.paymentreceiver.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "card")
@Getter
@Setter
@NoArgsConstructor
public class Card {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "pan")
    private String pan;

    @Column(name = "expiryDate")
    private String expiryDate;

    @Column(name = "cvv")
    private String cvv;

    @OneToMany(cascade = {CascadeType.ALL},
            mappedBy = "card")
    private List<Payment> payments;

    public Card(String pan, String expiryDate, String cvv) {
        this.pan = pan;
        this.expiryDate = expiryDate;
        this.cvv = cvv;
    }
}
