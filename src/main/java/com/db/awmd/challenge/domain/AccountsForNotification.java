package com.db.awmd.challenge.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AccountsForNotification {
    Account fromAccount;
    Account toAccount;


}
