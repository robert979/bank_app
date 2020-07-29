package com.proiectfinal.bankapp.controller;

import com.proiectfinal.bankapp.domain.Account;
import com.proiectfinal.bankapp.domain.User;
import com.proiectfinal.bankapp.repository.BranchRepository;
import com.proiectfinal.bankapp.service.AccountService;
import com.proiectfinal.bankapp.service.CardService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/accounts")
@AllArgsConstructor


public class AccountController {
    private final AccountService accountService;
    private final CardService cardService;


    @PostMapping ("/{id}")
    @ResponseStatus(HttpStatus.CREATED)
    public Account createAccount (@PathVariable ("id") long userId, @RequestBody Account account){

                   return accountService.createAccount(userId,account);
    }
    @PutMapping ("/deposit/{iban}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deposit (@PathVariable("iban") String iban,@RequestParam("depositAmount") double depositAmount,  @RequestBody Account account ){

        accountService.depositInBank(iban,depositAmount);
        System.out.println("the new amount for account no " + iban + " is " + accountService.checkBalanceByIban(iban) + " $");
    }
    @PutMapping ("/withdraw/{iban}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void withdraw (@PathVariable("iban") String iban, @RequestParam("withdraw") double withdraw, @RequestBody Account account){
        accountService.withdrawInBank(iban, withdraw);
        System.out.println("The new amount for account no " + iban + " is " + accountService.checkBalanceByIban(iban) + " $");
    }
  @PutMapping("/withdraw/pos/{cardNumber}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void withdrawAtPos(@PathVariable("cardNumber")long cardNumber, @RequestParam("withdraw") long withdraw,
                              @RequestParam("pin") int pin, @RequestBody Account account){
        accountService.withdrawAtPos(cardNumber, pin, withdraw);
        System.out.println("The new amount for your account now is " + accountService.checkBalanceByIban(cardService.findIbanByCardNumber(cardNumber)) + " $");
    }

    @GetMapping("/balance/{iban}")
    @ResponseStatus(HttpStatus.OK)
    public BigDecimal checkBalanceByIban (@PathVariable("iban") String iban){
        return accountService.checkBalanceByIban(iban);
    }
    @GetMapping("/{user_cnp}")
    @ResponseStatus(HttpStatus.OK)
    public List<Account> findAllaccountsByCnp(@PathVariable("user_cnp") long cnp){
        return accountService.findAccountsByCnp(cnp);
    }
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<Account> findAllAccounts (){
        return accountService.findAllAccounts();
    }


    @DeleteMapping("/delete/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAccountById (@PathVariable ("id")long id){
        accountService.deleteAccountById(id);
    }


}
