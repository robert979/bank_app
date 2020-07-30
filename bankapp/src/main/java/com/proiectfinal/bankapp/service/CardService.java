package com.proiectfinal.bankapp.service;

import com.proiectfinal.bankapp.domain.Account;
import com.proiectfinal.bankapp.domain.Card;
import com.proiectfinal.bankapp.domain.Status;
import com.proiectfinal.bankapp.repository.AccountRepository;
import com.proiectfinal.bankapp.repository.BranchRepository;
import com.proiectfinal.bankapp.repository.CardRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;


@Service
@AllArgsConstructor
public class CardService {
    private final CardRepository cardRepository;
    private final AccountRepository accountRepository;
    private final BranchRepository branchRepository;
    private static int flag = 0;
    private static long tempCard = 0;

    public List<Card> findAllCards() {
        return cardRepository.findAll();
    }

    public Card createNewCard(long id) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("The account with the id " + id + " doesn't exists"));
        Card newCard = new Card();
        newCard.setIban(account.getIban());
        newCard.setCardNumber(branchRepository.passNewCardNumberToNewCard());
        newCard.setPin(1234);
        newCard.setStatus(Status.ACTIVE);
        newCard.setLastUpdated(LocalDateTime.now());
        newCard.setExpirationDate(LocalDateTime.now().plusYears(2));
        return cardRepository.save(newCard);
    }

    public Status findStatusById(long id) {
        Card card = cardRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("The account with the id " + id + " does not exists"));
        return card.getStatus();
    }

    public long findCardIdByCardNumber(long cardNumber) {
        long id= branchRepository.findIdByCardNumber(cardNumber);
        if (id!=0){
            return id;
        } else {
            System.out.println("The card number you have provided is not valid\n" +
                    "Please try again");
            return 99999999999999L;
        }
    }


    @Transactional
    public void blockCard(long cardNumber) {
        Card cardToBeBlocked = cardRepository.findById(findCardIdByCardNumber(cardNumber))
                .orElseThrow(() -> new RuntimeException("The Card with the Card Number" + cardNumber + " is not valid"));
        if (checkIfCardIsActive(cardNumber)) {
            cardToBeBlocked.setStatus(Status.BLOCKED);
            cardToBeBlocked.setLastUpdated(LocalDateTime.now());
        } else {
            System.out.println("The Card with the Card Number + " + cardNumber + " is already Blocked " );

        }
    }

    @Transactional
    public void unblockCard(long cardNumber) {

        Card cardToBeUnblocked = cardRepository.findById(findCardIdByCardNumber(cardNumber))
                .orElseThrow(() -> new RuntimeException("The Card with the Card Number" + cardNumber + " is not valid"));
        if (!checkIfCardIsActive(cardNumber)) {
            cardToBeUnblocked.setStatus(Status.ACTIVE);
            cardToBeUnblocked.setLastUpdated(LocalDateTime.now());

        } else {
            System.out.println("The Card with the Card Number + " + cardNumber + " is already Active " );

        }
    }
    @Transactional
    public void changePin (long cardNumber, int initialPin,int newPin, int newPinAgain){
        Card cardToChangePin = findCardByCardNumber(cardNumber);
        if (cardToChangePin.getPin() == initialPin && initialPin !=newPin){
            if (newPin == newPinAgain){
                cardToChangePin.setPin(newPin);
                cardToChangePin.setLastUpdated(LocalDateTime.now());
                System.out.println("The Pin was successfully changed \n" +
                        "The new pin number is " + cardToChangePin.getPin());
            } else {
                System.out.println("Your new pin does not match with the retyped new pin\n" +
                        "Please try again");
            }
        }else {
            System.out.println("You must provide the right existing pin, and this must be different from the new pin\n" +
                    "Please try again");
        }
    }
    public boolean checkIfCardIsActive(long cardNumber) {
        if (findStatusById(findCardIdByCardNumber(cardNumber)).equals(Status.ACTIVE)) {
            return true;
        }else {
          return false;}

    }

    public Card findCardByCardNumber(long cardNumber) {

       return cardRepository.findById(findCardIdByCardNumber(cardNumber))
               .orElseThrow(()->new RuntimeException("The card Number you have provided is not valid\n" +
                       "Please try again"));
    }

    public boolean checkIfOkForWithdraw(long cardNumber, int pin) {
        if (checkIfCardIsActive(cardNumber)) {

            if (findCardByCardNumber(cardNumber).getPin() == pin) {
                return true;
          } else if (cardNumber != tempCard) {
              tempCard = cardNumber;
              flag = 0;
              System.out.println("The pin it's incorrect, please try again \n" +
                      "You have 2 more attempts, before your card will be blocked");
              flag=2;
              return false;
          } else if (flag!=3) {
              flag += 1;
              System.out.println("The pin it's incorrect, please try again\n" +
                      "You have one more attempt to provide the right pin, otherwise your card will be blocked");
              return false;
            } else {
                System.out.println("You have provided 3 times an incorrect pin, therefore your card is blocked\n" +
                        "Please contact our bank helpDesk");
                flag =0;
                branchRepository.blockCard(findCardIdByCardNumber(cardNumber));
               // branchRepository.setLastUpdate(Date.valueOf(LocalDateTime.now().toLocalDate()), findCardIdByCardNumber(cardNumber));
                findCardByCardNumber(cardNumber).setLastUpdated(LocalDateTime.now());
            }


        }  else {
                System.out.println("Your card with the card number " + cardNumber + " is blocked\n" +
                        "The withdraw operation was aborted\n" +
                        "Please contact our bank helpDesk");
                return false;
            }
                   return false;
    }

    public String findIbanByCardNumber(long cardNumber) {
        return findCardByCardNumber(cardNumber).getIban();
    }

    public void deleteCardByCardNumber(long cardNumber) {
        Card cardToBeDeleted = cardRepository.findById(findCardIdByCardNumber(cardNumber))
                .orElseThrow(() -> new RuntimeException("The card with the Card Number " + cardNumber + " does not txists"));
        branchRepository.addDeletedCardToTable(cardToBeDeleted.getIban(), cardNumber);
        cardRepository.deleteById(findCardIdByCardNumber(cardNumber));
        System.out.println("The card with the Card Number " + cardNumber + " was successfully deleted");

    }

    public boolean checkExpirationDate (long cardNumber) {
            if (LocalDateTime.now().isBefore(findCardByCardNumber(cardNumber).getExpirationDate().minusMonths(6))) {
                    return true;
            } else if((LocalDateTime.now().isBefore(findCardByCardNumber(cardNumber).getExpirationDate()))) {
                    System.out.println("The card will expire within the next 6 months");
                    return true;
            }else {
                System.out.println("Your card is expired\n" +
                        "Your card will be blocked");
                branchRepository.blockCard(findCardIdByCardNumber(cardNumber));
                return false;}
            }






}
