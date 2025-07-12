package org.testing.p2p_transaction.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.testing.p2p_transaction.dto.*;
import org.testing.p2p_transaction.exception.AccountNotFoundException;
import org.testing.p2p_transaction.exception.InsufficientFundsException;
import org.testing.p2p_transaction.exception.UserNotFoundException;
import org.testing.p2p_transaction.service.AccountService;
import org.testing.p2p_transaction.service.TransactionService;
import org.testing.p2p_transaction.service.UserService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final AccountService accountService;
    private final TransactionService transactionService;

    // Регистрация пользователя
    @PostMapping("/register")
    public ResponseEntity<UserResponseDto> register(@RequestBody UserRegistrationDto request) {
        try {
            UserResponseDto userDto = userService.registerUser(request);
            return ResponseEntity.ok(userDto);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    // Регистрация счёта
    @PostMapping("/registerAccount")
    public ResponseEntity<?> registerAccount(@RequestBody AccountResponseDto request) {
        try {
            return ResponseEntity.ok(accountService.registerAccount(request));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Ошибка генерации номера счёта: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Внутренняя ошибка сервера");
        }
    }

    // Получить список счетов пользователя
    @GetMapping("/getAccounts")
    public ResponseEntity<?> getAccounts(@RequestParam UUID userId) {
        try {
            List<AccountRegistrationDto> accounts = accountService.getAccounts(userId);
            return ResponseEntity.ok(accounts);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Не удалось получить счета");
        }
    }

    // Удаление счёта
    @DeleteMapping("/deleteAccount")
    public ResponseEntity<?> deleteAccount(@RequestParam String accountNumber) {
        try {
            String result = accountService.deleteAccount(accountNumber);
            return ResponseEntity.ok(result);
        } catch (AccountNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Счёт не найден: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Ошибка при удалении счёта");
        }
    }

    // Создание транзакции
    @PostMapping("/createTransaction")
    public ResponseEntity<?> createTransaction(@RequestBody RequestTransactionDto requestTransactionDto) {
        try {
            CreateTransactionDto transaction = transactionService.createTransaction(requestTransactionDto);
            return ResponseEntity.ok(transaction);
        } catch (UserNotFoundException | AccountNotFoundException | InsufficientFundsException e) {
            return ResponseEntity.badRequest().body("Ошибка транзакции: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Внутренняя ошибка при создании транзакции");
        }
    }

    // Получение всех транзакций по номеру счёта
    @GetMapping("/getAllTransactions")
    public ResponseEntity<?> getAllTransactions(@RequestParam String accountNumber) {
        try {
            List<CreateTransactionDto> transactions = transactionService.getAllTransactionsByAccountNumber(accountNumber);
            return ResponseEntity.ok(transactions);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Ошибка при получении транзакций");
        }
    }
}

