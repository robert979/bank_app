package com.proiectfinal.bankapp.controller;

import com.proiectfinal.bankapp.domain.Account;
import com.proiectfinal.bankapp.domain.User;
import com.proiectfinal.bankapp.repository.BranchRepository;
import com.proiectfinal.bankapp.service.AccountService;
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
    @GetMapping("balance/{user_cnp}")
    @ResponseStatus(HttpStatus.OK)
    public BigDecimal findTotalAmountByCnp (@PathVariable("user_cnp") long cnp){
        return accountService.findTotalAmountByCnp(cnp);
    }

}
