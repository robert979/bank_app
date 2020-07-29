package com.proiectfinal.bankapp.controller;

import com.proiectfinal.bankapp.domain.Card;
import com.proiectfinal.bankapp.domain.Status;
import com.proiectfinal.bankapp.service.CardService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cards")
@AllArgsConstructor
public class CardController {

    private final CardService cardService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<Card> findAllCards() {
        return cardService.findAllCards();
    }
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Status checkStatusById (@PathVariable("id")long id){
        return cardService.findStatusById(id);
    }
    @GetMapping("/cardNumber/{cardNumber}")
    @ResponseStatus(HttpStatus.OK)
    public long findIdByCardNumber(@PathVariable("cardNumber")long cardNumber){
        return cardService.findCardIdByCarNumber(cardNumber);
    }
    @GetMapping("/status/{cardNumber}")
    @ResponseStatus(HttpStatus.OK)
    public Status checkCardStatus(@PathVariable("cardNumber") long cardNumber){
        if (cardService.checkIfCardIsActive(cardNumber)){
            return Status.ACTIVE;
        }else {
            return Status.BLOCKED;
        }
    }

    @PostMapping("/{id}")
    @ResponseStatus(HttpStatus.CREATED)
    public Card create(@PathVariable("id") long accountId, @RequestBody Card card) {
        return cardService.createNewCard(accountId);
    }
    @PutMapping("/block/{cardNumber}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void blockCardByCardNumber(@PathVariable("cardNumber") long cardNumber, @RequestBody Card card){
        cardService.blockCard(cardNumber);
    }
    @PutMapping("/unblock/{cardNumber}")
    @ResponseStatus(HttpStatus.CREATED)
    public  void unblockCardByCardNumber(@PathVariable("cardNumber") long cardNumber, @RequestBody Card card){
        cardService.unblockCard(cardNumber);
    }
    @DeleteMapping("/delete/{cardNumber}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCardByCardNumber (@PathVariable("cardNumber") long cardNumber){
        cardService.deleteCardByCardNumber(cardNumber);}

}
