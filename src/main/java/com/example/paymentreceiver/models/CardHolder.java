package com.example.paymentreceiver.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "cardHolder")
@Getter
@Setter
@NoArgsConstructor
public class CardHolder {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "name")
    private String name;

    @Column(name = "email")
    private String email;

    @OneToMany(cascade = {CascadeType.ALL},
            mappedBy = "cardHolder")
    private List<Payment> payments;

    public CardHolder(String name, String email) {
        this.name = name;
        this.email = email;
    }
}
