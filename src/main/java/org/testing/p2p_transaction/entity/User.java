package org.testing.p2p_transaction.entity;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor

@Table(schema = "p2p", name = "user")
public class User {
    @Id
    private UUID id;
    @Pattern(
            regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$",
            message = "Email должен быть валидным, содержать '@' и домен"
    )
    @NotBlank(message = "Email не должен быть пустым")
    private String mail;
    @NotBlank(message = "Имя пользователя не должно быть пустым")
    @Size(min = 3, max = 100, message = "Имя пользователя должно быть от 3 до 20 символов")
    private String userName;
    @NotBlank(message = "Пароль не должен быть пустым")
    @Size(min = 6, message = "Пароль должен содержать минимум 6 символов")
    private String password;
    @NotBlank(message = "ФИО не должно быть пустым")
    private String fullName;
    private LocalDateTime createdAt;
}
