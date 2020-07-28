package com.proiectfinal.bankapp.domain;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "card")

public class Card {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String iban;
    private long cardNumber;
    private int pin;
    private Status status;
    private LocalDateTime lastUpdated;


}
