package org.testing.p2p_transaction.ServiceTest;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;
import org.testing.p2p_transaction.dto.AccountRegistrationDto;
import org.testing.p2p_transaction.dto.AccountResponseDto;
import org.testing.p2p_transaction.entity.Account;
import org.testing.p2p_transaction.exception.AccountNotFoundException;
import org.testing.p2p_transaction.repository.AccountRepository;
import org.testing.p2p_transaction.service.AccountService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;
    private JdbcTemplate jdbcTemplate = mock(JdbcTemplate.class);
    @InjectMocks
    private AccountService accountService;

    @Test
    public void registerAccount_shouldCreateAccountSuccessfully() {
        UUID userId = UUID.randomUUID();
        double balance = 1000.0;
        AccountResponseDto requestDto = new AccountResponseDto();
        requestDto.setUserId(userId);
        requestDto.setBalance(balance);

        when(accountRepository.existsByAccountNumber(anyString())).thenReturn(false);

        AccountRegistrationDto result = accountService.registerAccount(requestDto);

        assertNotNull(result);
        assertEquals(balance, result.getBalance(), 0.0001);
        assertEquals(16, result.getAccountNumber().length());

        verify(accountRepository, times(1)).save(any(Account.class));
    }

    @Test
    public void registerAccount_shouldThrowIfAccountNumberIsNotUniqueAfter5Attempts() {
        AccountResponseDto requestDto = new AccountResponseDto();
        requestDto.setUserId(UUID.randomUUID());
        requestDto.setBalance(10.0);

        when(accountRepository.existsByAccountNumber(anyString())).thenReturn(true);

        assertThrows(RuntimeException.class, () -> accountService.registerAccount(requestDto));

        verify(accountRepository, never()).save(any(Account.class));
    }

    @Test
    public void deleteAccount_shouldThrowWhenNotFoundByRepository() {
        String accountNumber = "not_existing";

        // Счёт не существует по проверке репозитория
        when(accountRepository.existsByAccountNumber(accountNumber)).thenReturn(false);

        // Проверяем, что выбрасывается исключение
        assertThrows(AccountNotFoundException.class, () -> accountService.deleteAccount(accountNumber));

        // Проверяем, что delete через jdbcTemplate не вызывался
        verify(jdbcTemplate, never()).update(anyString(), anyString());
    }

    @Test
    public void deleteAccount_shouldThrowWhenNotDeletedByJdbcTemplate() {
        String accountNumber = "1234567890123456";

        // Счёт существует
        when(accountRepository.existsByAccountNumber(accountNumber)).thenReturn(true);

        // При попытке удаления update возвращает 0, значит удаление не произошло
        when(jdbcTemplate.update(anyString(), eq(accountNumber))).thenReturn(0);

        // Проверяем, что выбрасывается исключение
        assertThrows(AccountNotFoundException.class, () -> accountService.deleteAccount(accountNumber));

        // Проверяем, что update вызвался ровно 1 раз
        verify(jdbcTemplate, times(1)).update(anyString(), eq(accountNumber));
    }

    @Test
    public void getAccounts_shouldReturnUserAccounts() {
        UUID userId = UUID.randomUUID();

        List<Account> mockAccounts = List.of(
                new Account(UUID.randomUUID(), "acc1", userId, 100.0, LocalDateTime.now()),
                new Account(UUID.randomUUID(), "acc2", userId, 200.0, LocalDateTime.now())
        );

        when(accountRepository.findByUserId(userId)).thenReturn(mockAccounts);

        List<AccountRegistrationDto> result = accountService.getAccounts(userId);

        assertEquals(2, result.size());
        assertEquals("acc1", result.get(0).getAccountNumber());
        assertEquals(100.0, result.get(0).getBalance(), 0.0001);
        assertEquals("acc2", result.get(1).getAccountNumber());
        assertEquals(200.0, result.get(1).getBalance(), 0.0001);
    }
}
