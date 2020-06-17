package com.db.awmd.challenge.web;

import javax.validation.Valid;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.domain.AccountTransfer;
import com.db.awmd.challenge.domain.AccountsForNotification;
import com.db.awmd.challenge.exception.AccountNotFoundException;
import com.db.awmd.challenge.exception.DuplicateAccountIdException;
import com.db.awmd.challenge.exception.NegativeBalanceException;
import com.db.awmd.challenge.service.AccountsService;
import com.db.awmd.challenge.service.NotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/accounts")
@Slf4j
public class AccountsController {

    private final AccountsService accountsService;
    private final NotificationService notificationService;

    @Autowired
    public AccountsController(AccountsService accountsService, NotificationService notificationService) {
        this.accountsService = accountsService;
        this.notificationService = notificationService;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> createAccount(@RequestBody @Valid Account account) {
        log.info("Creating account {}", account);

        try {
            accountsService.createAccount(account);
        } catch (DuplicateAccountIdException daie) {
            return new ResponseEntity<>(daie.getMessage(), HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>("Account created successfully!", HttpStatus.CREATED);
    }

    @GetMapping(path = "/{accountId}")
    public Account getAccount(@PathVariable String accountId) {
        log.info("Retrieving account for id {}", accountId);
        return accountsService.getAccount(accountId);
    }

    //Endpoint for a transfer of money between accounts
    @PostMapping(path = "/wire-transfer", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> transferMoney(@RequestBody @Valid AccountTransfer accountTransfer) {
        log.info("Performing money transfer from {} to {}", accountTransfer.getFromAccountId(), accountTransfer.getToAccountId());

        try {
            AccountsForNotification accountsForNotification = accountsService.transferMoney(accountTransfer);
            notificationService.notifyAboutTransfer(accountsForNotification.getFromAccount(),
                    "Amount " + accountTransfer.getAmountToTransfer() + " is transferred successfully to " + accountsForNotification.getToAccount());
            notificationService.notifyAboutTransfer(accountsForNotification.getToAccount(),
                    "Amount " + accountTransfer.getAmountToTransfer() + " is received from " + accountsForNotification.getFromAccount());
        } catch (AccountNotFoundException | NegativeBalanceException ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>("Money transfer was successful!", HttpStatus.OK);
    }

}
