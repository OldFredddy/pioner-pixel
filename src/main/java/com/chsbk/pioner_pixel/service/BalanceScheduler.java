package com.chsbk.pioner_pixel.service;

import com.chsbk.pioner_pixel.entities.Account;
import com.chsbk.pioner_pixel.repository.AccountRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Component
public class BalanceScheduler {
    private static final BigDecimal RATE = BigDecimal.valueOf(0.10);
    private static final BigDecimal CAP  = BigDecimal.valueOf(2.07);
    private final AccountRepository accounts;
    public BalanceScheduler(AccountRepository accounts) {
        this.accounts = accounts;
    }

    @Scheduled(fixedRate = 30_000)
    @Transactional
    public void increaseBalance() {
        List<Account> allAccounts = accounts.findAll();
        for (Account account : allAccounts) {
            BigDecimal updated = nextBalance(account);
            if (updated.compareTo(account.getBalance()) > 0) {
                account.setBalance(updated);
            }
        }
    }

    private BigDecimal nextBalance(Account account) {
        BigDecimal current = account.getBalance();
        BigDecimal maximum = account.getInitialBalance()
                .multiply(CAP);
        if (current.compareTo(maximum) >= 0) {
            return current;
        }

        BigDecimal grown = current.multiply(BigDecimal.ONE.add(RATE));
        if (grown.compareTo(maximum) > 0) {
            return maximum;
        }

        return grown;
    }
}
