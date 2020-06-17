package com.db.awmd.challenge.domain;

import java.math.BigDecimal;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;

@Data
public class AccountTransfer {

    @NotNull
    @NotEmpty
    private final String fromAccountId;

    @NotNull
    @NotEmpty
    private final String toAccountId;

    @NotNull
    @Min(value = 1, message = "The amount to transfer should always be a positive number.")
    private BigDecimal amountToTransfer;

    @JsonCreator
    public AccountTransfer(@JsonProperty("fromAccountId") String fromAccountId, @JsonProperty("toAccountId") String toAccountId,
            @JsonProperty("amountToTransfer") BigDecimal amountToTransfer) {
        this.fromAccountId = fromAccountId;
        this.toAccountId = toAccountId;
        this.amountToTransfer = amountToTransfer;
    }
}
