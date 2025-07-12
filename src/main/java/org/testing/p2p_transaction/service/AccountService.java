package org.testing.p2p_transaction.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.testing.p2p_transaction.dto.AccountRegistrationDto;
import org.testing.p2p_transaction.dto.AccountResponseDto;
import org.testing.p2p_transaction.entity.Account;
import org.testing.p2p_transaction.exception.AccountNotFoundException;
import org.testing.p2p_transaction.repository.AccountRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;


/**
 * Сервис для работы с банковскими счетами пользователей.
 */
@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;

    /**
     * Регистрирует новый счёт для пользователя.
     *
     * @param accountResponseDto данные для создания счета: ID пользователя и начальный баланс.
     * @return объект {@link AccountRegistrationDto} с информацией о созданном счёте.
     */
    public AccountRegistrationDto registerAccount(AccountResponseDto accountResponseDto) {

        String generatedAccountNumber = generateUniqueAccountNumber();

        Account account = new Account();
        account.setAccountNumber(generatedAccountNumber);
        account.setUserId(accountResponseDto.getUserId());
        account.setBalance(accountResponseDto.getBalance());
        account.setCreatedAt(LocalDateTime.now());

        accountRepository.save(account);

        return new AccountRegistrationDto(account.getAccountNumber(), account.getBalance());
    }

    /**
     * Генерирует уникальный 16-символьный номер счёта.
     *
     * @return уникальный номер счёта.
     * @throws RuntimeException если не удалось сгенерировать уникальный номер за 5 попыток.
     */
    private String generateUniqueAccountNumber() {
        String accountNumber;
        int attempts = 0;
        do {
            accountNumber = UUID.randomUUID().toString().replaceAll("-", "").substring(0, 16);
            attempts++;
            if (attempts > 5) {
                throw new RuntimeException("Не удалось сгенерировать уникальный номер счёта");
            }
        } while (accountRepository.existsByAccountNumber(accountNumber));
        return accountNumber;
    }

    /**
     * Удаляет счёт по номеру.
     *
     * @param accountNumber номер счёта для удаления.
     * @return строка с подтверждением удаления.
     * @throws AccountNotFoundException если счёт не найден.
     */
    public String deleteAccount(String accountNumber) {
        boolean accountExist = accountRepository.existsByAccountNumber(accountNumber);
        if (!accountExist) {
            throw new AccountNotFoundException(accountNumber);
        }
        accountRepository.deleteByAccountNumber(accountNumber);
        return "Счет успешно удален " + accountNumber;
    }

    /**
     * Возвращает список всех счетов, принадлежащих пользователю.
     *
     * @param userId ID пользователя.
     * @return список объектов {@link AccountRegistrationDto} с данными о счетах.
     */
    public List<AccountRegistrationDto> getAccounts(UUID userId) {
        List<Account> accounts = accountRepository.findByUserId(userId);

        return accounts.stream()
                .map(account -> new AccountRegistrationDto(
                        account.getAccountNumber(),
                        account.getBalance()
                ))
                .collect(Collectors.toList());
    }
}

