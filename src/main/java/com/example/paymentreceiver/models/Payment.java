package com.example.paymentreceiver.models;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "payments")
public class Payment {

    @Id
    @Column(name = "id")
    private int invoice;

    @Column(name = "amount")
    private int amount;

    @Column(name = "currency")
    private String currency;

    @ManyToOne(cascade = {CascadeType.DETACH, CascadeType.PERSIST
            , CascadeType.REFRESH, CascadeType.MERGE})
    @JoinColumn(name = "cardHolder_id",
            referencedColumnName = "id")
    private CardHolder cardHolder;

    @ManyToOne(cascade = {CascadeType.DETACH, CascadeType.PERSIST
            , CascadeType.REFRESH, CascadeType.MERGE})
    @JoinColumn(name = "card_id",
            referencedColumnName = "id")
    private Card card;
}

