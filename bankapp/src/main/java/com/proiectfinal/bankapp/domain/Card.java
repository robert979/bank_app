package com.proiectfinal.bankapp.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "card")
@Data
@AllArgsConstructor
@NoArgsConstructor

public class Card {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String iban;
    private long cardNumber;
    private int pin;

    @Enumerated(EnumType.STRING)
    private Status status;
    private LocalDateTime creationDate;
    private LocalDateTime lastUpdated;


}
