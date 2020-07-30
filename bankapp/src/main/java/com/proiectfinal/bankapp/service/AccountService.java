package com.proiectfinal.bankapp.service;

import com.proiectfinal.bankapp.domain.Account;
import com.proiectfinal.bankapp.repository.AccountRepository;
import com.proiectfinal.bankapp.repository.BranchRepository;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Repository
@AllArgsConstructor

public class AccountService {
    private final UserService userService;
    private final AccountRepository accountRepository;
    private final BranchRepository branchRepository;
    private final CardService cardService;


    public Account createAccount(long userId, Account account) {
        account.setUserCnp(userService.findCnpById(userId));
        account.setLastUpdated(LocalDateTime.now());
        account.setIban(branchRepository.generateIbanForAccount());
        return accountRepository.save(account);
    }

    public List<Account> findAllAccounts() {
        return accountRepository.findAll();
    }

    public Account findAccountByIban(String iban) {
        List<Account> ibanAccount = findAllAccounts().stream()
                .filter(account -> account.getIban().equals(iban))
                .collect(Collectors.toList());
        return ibanAccount.get(0);
    }

    public List<Account> findAccountsByCnp(long cnp) {
        return findAllAccounts().stream()
                .filter(account -> account.getUserCnp() == cnp)
                .collect(Collectors.toList());
    }

    public BigDecimal checkBalanceByIban(String iban) {
        return findAccountByIban(iban).getBalance();
    }

    @Transactional
    public BigDecimal depositInBank(String iban, double amountToDeposit) {
        Account accountToDeposit = findAccountByIban(iban);
        accountToDeposit.setBalance(accountToDeposit.getBalance().add(BigDecimal.valueOf(amountToDeposit)));
        accountToDeposit.setLastUpdated(LocalDateTime.now());

        return accountToDeposit.getBalance();
    }


    @Transactional
    public void withdrawInBank(String iban, double amountToWithdraw) {
        if (checkBalanceByIban(iban).compareTo(BigDecimal.valueOf(amountToWithdraw)) >= 0) {
            Account accountToWithdraw = findAccountByIban(iban);
            accountToWithdraw.setBalance(depositInBank(iban, -amountToWithdraw));
        } else {
            System.out.println("Insufficient funds \nWithdraw operation aborted");
        }
    }

    @Transactional
    public void transferMoney(String ibanToTransferFrom, String ibanToTransferTo, double amountToTransfer){
        Account accountToTransferFrom = findAccountByIban(ibanToTransferFrom);
                Account accountToTransferTo =findAccountByIban(ibanToTransferTo);
        if (checkBalanceByIban(ibanToTransferFrom).compareTo(BigDecimal.valueOf(amountToTransfer))>=0){
            accountToTransferFrom.setBalance(depositInBank(ibanToTransferFrom, -amountToTransfer));
            accountToTransferTo.setBalance(depositInBank(ibanToTransferTo, amountToTransfer));

        }
    }

    @Transactional
    public void withdrawAtPos(long cardNumber, int pin, long withdrawAmount) {
        if (cardService.checkExpirationDate(cardNumber)) {
            if (cardService.checkIfOkForWithdraw(cardNumber, pin)) {
                if (withdrawAmount % 10 == 0 && withdrawAmount > 0) {
                    withdrawInBank(cardService.findIbanByCardNumber(cardNumber), withdrawAmount);
                } else {
                    System.out.println("The withdraw amount must a positive value, multiple of 10\n" +
                            "Please try again");
                }
            }
        }else{
            System.out.println("The withdraw can't be processed");
        }
    }


    public void deleteAccountById(long id) {
        Account accountToBeDeleted = accountRepository.findById(id).orElseThrow(() -> new RuntimeException("The account with the ID " + id + " doesn't exists"));
        if ((accountToBeDeleted.getBalance().compareTo(BigDecimal.valueOf(0)) == 0)) {
            branchRepository.addDeletedAccountToTable(String.valueOf(accountToBeDeleted.getUserCnp()), accountToBeDeleted.getIban());
            accountRepository.deleteById(id);
        } else {
            System.out.println("Your actual amount of " + accountToBeDeleted.getBalance() + " $, prevents account to be deleted\n" +
                    "Please withdraw all the amount first");
        }
    }



}
