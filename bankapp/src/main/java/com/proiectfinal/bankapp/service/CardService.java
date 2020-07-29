package com.proiectfinal.bankapp.service;

import com.proiectfinal.bankapp.domain.Account;
import com.proiectfinal.bankapp.domain.Card;
import com.proiectfinal.bankapp.domain.Status;
import com.proiectfinal.bankapp.domain.User;
import com.proiectfinal.bankapp.repository.AccountRepository;
import com.proiectfinal.bankapp.repository.BranchRepository;
import com.proiectfinal.bankapp.repository.CardRepository;
import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CardService {
    private final CardRepository cardRepository;
    private final AccountRepository accountRepository;
    //private final AccountService accountService;
    private final BranchRepository branchRepository;
    private static int flag=0;

    public List<Card> findAllCards (){ return cardRepository.findAll();
    }

    public Card createNewCard (long id){
        Account account = accountRepository.findById(id)
                .orElseThrow(()->new RuntimeException("The account with the id " + id + " doesn't exists"));
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

    public  long findCardIdByCarNumber (long cardNumber){
        return findAllCards().stream()
                .collect(Collectors.toMap(Card::getCardNumber, Card::getId))
                .get(cardNumber);
    }


    @Transactional
    public void  blockCard (long cardNumber){
        Card cardToBeBlocked = cardRepository.findById(findCardIdByCarNumber(cardNumber))
                .orElseThrow(() -> new RuntimeException("The Card with the Card Number" + cardNumber + " is not valid"));
        if(checkIfCardIsActive(cardNumber)) {
            cardToBeBlocked.setStatus(Status.BLOCKED);
            cardToBeBlocked.setLastUpdated(LocalDateTime.now());
        }else {
            System.out.println("The Card with the Card Number + " + cardNumber + " is already Blocked " + flag);
            flag=flag+1;
        }
    }

    @Transactional
    public void unblockCard (long cardNumber) {

        Card cardToBeUnblocked = cardRepository.findById(findCardIdByCarNumber(cardNumber))
                .orElseThrow(() -> new RuntimeException("The Card with the Card Number" + cardNumber + " is not valid"));
        if (!checkIfCardIsActive(cardNumber)){
            cardToBeUnblocked.setStatus(Status.ACTIVE);
            cardToBeUnblocked.setLastUpdated(LocalDateTime.now());

        }else {
            System.out.println ("The Card with the Card Number + " + cardNumber + " is already Active " + flag);
            flag=flag+1;
        }
    }

    public boolean checkIfCardIsActive (long cardNumber){
        if (findStatusById(findCardIdByCarNumber(cardNumber)).equals(Status.ACTIVE)){
            return true;
        }
       else {
           return false;
        }
    }
    public Card findCardByCardNumber(long cardNumber){

        List<Card> listCards = findAllCards()
                .stream()
                .filter(card -> card.getCardNumber()==cardNumber)
                .collect(Collectors.toList());

        return listCards.get(0);

    }
    public boolean checkIfOkForWithdraw(long cardNumber, int pin){
        if (checkIfCardIsActive(cardNumber)){
            if (findCardByCardNumber(cardNumber).getPin()==pin){
                return true;
            }else {
                System.out.println("The pin it's incorrect, please try again");
                return false;
            }
        }else {
            System.out.println("Your Card with the Card Number "+ cardNumber + " is blocked\n" +
                    "The withdraw operation was aborted");
            return false;
        }
    }
    public String findIbanByCardNumber (long cardNumber){
        return findCardByCardNumber(cardNumber).getIban();
    }

    public void deleteCardByCardNumber (long cardNumber){
        Card cardToBeDeleted = cardRepository.findById(findCardIdByCarNumber(cardNumber))
                .orElseThrow(()->new RuntimeException("The card with the Card Number " + cardNumber + " does not txists"));
        branchRepository.addDeletedCardToTable(cardToBeDeleted.getIban(), cardNumber);
        cardRepository.deleteById(findCardIdByCarNumber(cardNumber));
        System.out.println("The card with the Card Number " + cardNumber + " was successfully deleted");

    }

}
