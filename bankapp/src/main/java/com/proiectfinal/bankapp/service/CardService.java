package com.proiectfinal.bankapp.service;

import com.proiectfinal.bankapp.domain.Account;
import com.proiectfinal.bankapp.domain.Card;
import com.proiectfinal.bankapp.domain.Status;
import com.proiectfinal.bankapp.domain.User;
import com.proiectfinal.bankapp.repository.AccountRepository;
import com.proiectfinal.bankapp.repository.BranchRepository;
import com.proiectfinal.bankapp.repository.CardRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CardService {
    private final CardRepository cardRepository;
    private final AccountRepository accountRepository;
    private final AccountService accountService;
    private final BranchRepository branchRepository;

    public List<Card> findAllCards (){ return cardRepository.findAll();
    }

    public Card createNewCard (long id){
        Account account = accountRepository.findById(id)
                .orElseThrow(()->new RuntimeException("The account with the id " + id + " desn't exists"));
        Card newCard = new Card();
        newCard.setIban(account.getIban());
        newCard.setCardNumber(branchRepository.passNewCardNumberToNewCard());
        newCard.setPin(1234);
        newCard.setStatus(Status.ACTIVE);
        newCard.setLastUpdated(LocalDateTime.now());
        return cardRepository.save(newCard);
    }
    public Status findStatusById (long id){
        Card card = cardRepository.findById(id)
                .orElseThrow(()->new RuntimeException("The account with the id " + id + " does not exists"));
        return card.getStatus();
    }
    public long findCardIdByCarNumber (long cardNumber){
        return findAllCards()
                .stream()
                .filter(card -> card.getCardNumber()==cardNumber)
                .mapToLong(card -> card.getId())
                .min()
                .getAsLong();

    }
    public boolean checkIfCardIsActive (long cardNumber){
        if (findStatusById(findCardIdByCarNumber(cardNumber)).equals(Status.ACTIVE)){
            return true;
        }
       else {
           return false;
        }
    }

}
