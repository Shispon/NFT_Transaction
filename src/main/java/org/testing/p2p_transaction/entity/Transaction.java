package org.testing.p2p_transaction.entity;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor

@Table(schema = "p2p", name = "transactions")
public class Transaction {
    @Id
    private UUID id;
    @NotNull(message = "ID пользователя не может быть null")
    private UUID userId;

    @NotBlank(message = "Номер отправителя не может быть пустым")
    private String fromAccountNumber;

    @NotBlank(message = "Номер получателя не может быть пустым")
    private String toAccountNumber;

    @NotNull(message = "Сумма перевода не может быть null")
    @Positive(message = "Сумма перевода должна быть положительной")
    private Double amount;
    private LocalDateTime timestamp;
}
