package com.db.awmd.challenge.domain;

import java.math.BigDecimal;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;

@Data
public class Account {

  @NotNull
  @NotEmpty
  private final String accountId;

  @NotNull
  @Min(value = 0, message = "Initial balance must be positive.")
  private BigDecimal balance;

  private final ReadWriteLock accountLock;

  public Account(String accountId) {
    this.accountId = accountId;
    this.balance = BigDecimal.ZERO;
    this.accountLock = new ReentrantReadWriteLock();
  }

  @JsonCreator
  public Account(@JsonProperty("accountId") String accountId,
    @JsonProperty("balance") BigDecimal balance) {
    this.accountId = accountId;
    this.balance = balance;
    this.accountLock = new ReentrantReadWriteLock();
  }

  public BigDecimal getBalance() {
    this.accountLock.readLock().lock();
    try {
      return this.balance;
    } finally {
      this.accountLock.readLock().unlock();
    }
  }

  public void addAmount(BigDecimal amount) {
    this.accountLock.writeLock().lock();
    try {
      this.balance = this.balance.add(amount);
    } finally {
      this.accountLock.writeLock().unlock();
    }
  }

  public void withdrawAmount(BigDecimal amount) {
    this.accountLock.writeLock().lock();
    try {
      this.balance = this.balance.subtract(amount);
    } finally {
      this.accountLock.writeLock().unlock();
    }
  }
}
