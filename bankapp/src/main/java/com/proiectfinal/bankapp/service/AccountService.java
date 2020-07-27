package com.proiectfinal.bankapp.service;

import com.proiectfinal.bankapp.domain.Account;
import com.proiectfinal.bankapp.repository.AccountRepository;
import com.proiectfinal.bankapp.repository.BranchRepository;
import lombok.AllArgsConstructor;
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

    public Account createAccount(long userId,Account account){
        account.setUserCnp(userService.findCnpById(userId));
        account.setLastUpdated(LocalDateTime.now());
        account.setIban(branchRepository.generateIbanForAccount());
        return accountRepository.save(account);
    }
    public List<Account> findAllAccounts(){
        return accountRepository.findAll();
    }

    public Account findAccountByIban(String iban){
      List<Account> ibanAccount=   findAllAccounts().stream()
                .filter(account -> account.getIban().equals(iban))
                .collect(Collectors.toList());
        return ibanAccount.get(0);
    }

    public BigDecimal checkBalanceByIban(String iban){
        return findAccountByIban(iban).getBalance();
    }

    @Transactional
    public BigDecimal depositInBank (String iban, double amountToDeposit ){
        Account accountToDeposit = findAccountByIban(iban);
        accountToDeposit.setBalance(accountToDeposit.getBalance().add(BigDecimal.valueOf(amountToDeposit)));
        return accountToDeposit.getBalance();
}

    /* @Transactional
    public void withdrawInBank (String iban, double amountToWithdraw){
        if (checkBalanceByIban(iban).compareTo(BigDecimal.valueOf(amountToWithdraw))>0){
        Account accountToWithdraw = findAccountByIban(iban);
        accountToWithdraw.setBalance(accountToWithdraw.getBalance().subtract(BigDecimal.valueOf(amountToWithdraw)));
        } else {
            System.out.println("Insufficient money \nWithdraw aborted");
        }
     }
            */

    @Transactional
    public void withdrawInBank (String iban, double amountToWithdraw){
        if (checkBalanceByIban(iban).compareTo(BigDecimal.valueOf(amountToWithdraw))>0){
            Account accountToWithdraw = findAccountByIban(iban);
            accountToWithdraw.setBalance(depositInBank(iban, -amountToWithdraw));
        } else {
            System.out.println("Insufficient funds \nWithdraw operation aborted");
        }

    }

}
