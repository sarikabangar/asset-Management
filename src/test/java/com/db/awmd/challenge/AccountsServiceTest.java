package com.db.awmd.challenge;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

import java.math.BigDecimal;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.domain.AccountTransfer;
import com.db.awmd.challenge.exception.AccountNotFoundException;
import com.db.awmd.challenge.exception.DuplicateAccountIdException;
import com.db.awmd.challenge.service.AccountsService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AccountsServiceTest {

  @Autowired
  private AccountsService accountsService;

  @Test
  public void addAccount() throws Exception {
    Account account = new Account("Id-123");
    account.setBalance(new BigDecimal(1000));
    this.accountsService.createAccount(account);

    assertThat(this.accountsService.getAccount("Id-123")).isEqualTo(account);
  }

  @Test
  public void addAccount_failsOnDuplicateId() throws Exception {
    String uniqueId = "Id-" + System.currentTimeMillis();
    Account account = new Account(uniqueId);
    this.accountsService.createAccount(account);

    try {
      this.accountsService.createAccount(account);
      fail("Should have failed when adding duplicate account");
    } catch (DuplicateAccountIdException ex) {
      assertThat(ex.getMessage()).isEqualTo("Account id " + uniqueId + " already exists!");
    }

  }
  
  @Test(expected=AccountNotFoundException.class)
  public void transferMoney() throws Exception {
	    String uniqueFromAccountId = "Id-" + System.currentTimeMillis();
	    String uniqueToAccountId = "Id-" + System.currentTimeMillis();
	    AccountTransfer accountTransfer = new AccountTransfer(uniqueFromAccountId, uniqueToAccountId, new BigDecimal("123.45"));
       this.accountsService.transferMoney(accountTransfer);

  }
  
  @Test
  public void transferMoney_failsOnAccountNotFound() throws Exception {
	    String uniqueFromAccountId = "Id-" + System.currentTimeMillis();
	    String uniqueToAccountId = "Id-" + System.currentTimeMillis();
	    AccountTransfer accountTransfer = new AccountTransfer(uniqueFromAccountId, uniqueToAccountId, new BigDecimal("123.45"));
       try {
    	      this.accountsService.transferMoney(accountTransfer);
    	      fail("Should have failed when if account does not exist");
    	    } catch (AccountNotFoundException ex) {
    	      assertThat(ex.getMessage()).isEqualTo("From Account id "+uniqueFromAccountId+" does not exists!");
    	    }

  }
}
