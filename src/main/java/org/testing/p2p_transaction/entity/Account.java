package org.testing.p2p_transaction.entity;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(schema = "p2p", name = "account")
public class Account {
    @Id
    private UUID id;
    @Size(max = 16, message = "Номер счёта не должен превышать 16 символов")
    @NotBlank(message = "Номер счёта обязателен")
    private String accountNumber;
    private UUID userId;
    @PositiveOrZero(message = "Баланс не может быть отрицательным")
    private double balance;
    private LocalDateTime createdAt;
}
