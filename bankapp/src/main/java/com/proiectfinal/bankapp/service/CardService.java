package com.proiectfinal.bankapp.service;

import com.proiectfinal.bankapp.domain.Card;
import com.proiectfinal.bankapp.repository.BranchRepository;
import com.proiectfinal.bankapp.repository.CardRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class CardService {
    private final CardRepository cardRepository;
    private final BranchRepository branchRepository;

    public List<Card> findAllCards (){ return cardRepository.findAll();
    }
}
