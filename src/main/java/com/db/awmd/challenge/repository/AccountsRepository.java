package com.db.awmd.challenge.repository;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.domain.AccountTransfer;
import com.db.awmd.challenge.domain.AccountsForNotification;
import com.db.awmd.challenge.exception.DuplicateAccountIdException;

public interface AccountsRepository {

  void createAccount(Account account) throws DuplicateAccountIdException;

  AccountsForNotification transferMoney(AccountTransfer accountTransfer) throws DuplicateAccountIdException;

  Account getAccount(String accountId);

  void clearAccounts();
}
