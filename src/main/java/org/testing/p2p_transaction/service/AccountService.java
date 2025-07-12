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


@Service
@RequiredArgsConstructor
public class AccountService {
    private final AccountRepository accountRepository;

    public AccountRegistrationDto registerAccount(AccountResponseDto accountRegistrationDto) {

        // Генерируем уникальный номер
        String generatedAccountNumber = generateUniqueAccountNumber();

        Account account = new Account();
        account.setId(UUID.randomUUID());
        account.setAccountNumber(generatedAccountNumber);
        account.setUserId(accountRegistrationDto.getUserId());
        account.setBalance(accountRegistrationDto.getBalance());
        account.setCreatedAt(LocalDateTime.now());

        accountRepository.save(account);

        // Возвращаем DTO с балансом (если хочешь можно добавить и номер)
        return new AccountRegistrationDto(account.getAccountNumber(),account.getBalance());
    }

    private String generateUniqueAccountNumber() {
        String accountNumber;
        int attempts = 0;
        do {
            accountNumber = UUID.randomUUID().toString().replaceAll("-", "").substring(0, 16); // 16 символов
            attempts++;
            if (attempts > 5) {
                throw new RuntimeException("Не удалось сгенерировать уникальный номер счёта");
            }
        } while (accountRepository.existsByAccountNumber(accountNumber));
        return accountNumber;
    }

    public String deleteAccount(String accountNumber) {
        boolean accountExist = accountRepository.existsByAccountNumber(accountNumber);
        if(!accountExist) {
            throw new AccountNotFoundException(accountNumber);
        }
        accountRepository.deleteByAccountNumber(accountNumber);
       return "Счет успешно удален" + accountNumber;
    }

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
