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

    @PostMapping("/{id}")
    @ResponseStatus(HttpStatus.CREATED)
    public Card create(@PathVariable("id") long accountId, @RequestBody Card card) {
        return cardService.createNewCard(accountId);
    }

}
