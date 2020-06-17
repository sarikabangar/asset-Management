package com.db.awmd.challenge.repository;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.domain.AccountTransfer;
import com.db.awmd.challenge.domain.AccountsForNotification;
import com.db.awmd.challenge.exception.AccountNotFoundException;
import com.db.awmd.challenge.exception.DuplicateAccountIdException;
import com.db.awmd.challenge.exception.NegativeBalanceException;
import org.springframework.stereotype.Repository;

@Repository
public class AccountsRepositoryInMemory implements AccountsRepository {

    private final Map<String, Account> accounts = new ConcurrentHashMap<>();

    @Override
    public void createAccount(Account account) throws DuplicateAccountIdException {
        Account previousAccount = accounts.putIfAbsent(account.getAccountId(), account);
        if (previousAccount != null) {
            throw new DuplicateAccountIdException(
                    "Account id " + account.getAccountId() + " already exists!");
        }
    }

    @Override
    public AccountsForNotification transferMoney(AccountTransfer accountTransfer) throws AccountNotFoundException {
        String fromAccountId = accountTransfer.getFromAccountId();
        String toAccountId = accountTransfer.getToAccountId();
        BigDecimal transferAmount = accountTransfer.getAmountToTransfer();
        if (!accounts.containsKey(fromAccountId)) {
            throw new AccountNotFoundException("From Account id " + fromAccountId + " does not exists!");
        } else if (!accounts.containsKey(toAccountId)) {
            throw new AccountNotFoundException("To Account id " + toAccountId + " does not exists!");
        } else if (-1 == accounts.get(fromAccountId).getBalance().compareTo(accountTransfer.getAmountToTransfer())) {
            throw new NegativeBalanceException("Not enough balance in from account " + fromAccountId + ", transfer is rejected!");
        } else
            return transferMoney(fromAccountId, toAccountId, transferAmount);
    }

    private AccountsForNotification transferMoney(String fromAccountId, String toAccountId, BigDecimal amountToTransfer) {
        Account accountFrom = accounts.get(fromAccountId);
        Account accountTo = accounts.get(toAccountId);
        accountFrom.getAccountLock().writeLock().lock();
        accountTo.getAccountLock().writeLock().lock();
        try {
            accountFrom.withdrawAmount(amountToTransfer);
            accountTo.addAmount(amountToTransfer);
        } finally {
            accountFrom.getAccountLock().writeLock().unlock();
            accountTo.getAccountLock().writeLock().unlock();
        }
        return new AccountsForNotification(accountFrom, accountTo);
    }

    @Override
    public Account getAccount(String accountId) {
        return accounts.get(accountId);
    }

    @Override
    public void clearAccounts() {
        accounts.clear();
    }

}
