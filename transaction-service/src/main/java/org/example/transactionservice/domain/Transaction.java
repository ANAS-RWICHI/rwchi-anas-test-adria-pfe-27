package org.example.transactionservice.domain;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private UUID accountId;

    @Column(precision = 19, scale = 2)
    private BigDecimal amount;

    private String currency;

    private String merchantCategory;

    private String country;

    private Instant createdAt;

    /** DEBIT for a payment or the source of a transfer, CREDIT for the target of a transfer. */
    @Enumerated(EnumType.STRING)
    private TransactionType type;
}
