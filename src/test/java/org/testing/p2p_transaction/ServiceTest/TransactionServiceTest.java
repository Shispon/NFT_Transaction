package org.testing.p2p_transaction.ServiceTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testing.p2p_transaction.dto.CreateTransactionDto;
import org.testing.p2p_transaction.dto.RequestTransactionDto;
import org.testing.p2p_transaction.entity.Account;
import org.testing.p2p_transaction.entity.Transaction;
import org.testing.p2p_transaction.exception.AccountNotFoundException;
import org.testing.p2p_transaction.exception.InsufficientFundsException;
import org.testing.p2p_transaction.exception.UserNotFoundException;
import org.testing.p2p_transaction.repository.AccountRepository;
import org.testing.p2p_transaction.repository.TransactionRepository;
import org.testing.p2p_transaction.repository.UserRepository;
import org.testing.p2p_transaction.service.TransactionService;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private TransactionService transactionService;

    private UUID userId;
    private RequestTransactionDto requestDto;

    @BeforeEach
    public void setUp() {
        userId = UUID.randomUUID();
        requestDto = new RequestTransactionDto();
        requestDto.setUserId(userId);
        requestDto.setFromAccountNumber("1111222233334444");
        requestDto.setToAccountNumber("5555666677778888");
        requestDto.setAmount(100.0);
    }

    @Test
    public void createTransaction_shouldSucceed() {
        Account sender = new Account(UUID.randomUUID(), requestDto.getFromAccountNumber(), userId, 200.0, LocalDateTime.now());
        Account receiver = new Account(UUID.randomUUID(), requestDto.getToAccountNumber(), UUID.randomUUID(), 50.0, LocalDateTime.now());

        when(userRepository.existsById(userId)).thenReturn(true);
        when(accountRepository.existsByAccountNumber(requestDto.getFromAccountNumber())).thenReturn(true);
        when(accountRepository.existsByAccountNumber(requestDto.getToAccountNumber())).thenReturn(true);
        when(accountRepository.findByAccountNumber(requestDto.getFromAccountNumber())).thenReturn(Optional.of(sender));
        when(accountRepository.findByAccountNumber(requestDto.getToAccountNumber())).thenReturn(Optional.of(receiver));

        CreateTransactionDto result = transactionService.createTransaction(requestDto);

        assertEquals(requestDto.getFromAccountNumber(), result.getFromAccountNumber());
        assertEquals(requestDto.getToAccountNumber(), result.getToAccountNumber());
        assertEquals(requestDto.getAmount(), result.getAmount());

        verify(accountRepository, times(1)).save(sender);
        verify(accountRepository, times(1)).save(receiver);
        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }

    @Test
    public void createTransaction_shouldThrowIfUserNotFound() {
        when(userRepository.existsById(userId)).thenReturn(false);

        assertThrows(UserNotFoundException.class, () -> transactionService.createTransaction(requestDto));
        verifyNoInteractions(accountRepository, transactionRepository);
    }

    @Test
    public void createTransaction_shouldThrowIfSenderAccountNotFound() {
        when(userRepository.existsById(userId)).thenReturn(true);
        when(accountRepository.existsByAccountNumber(requestDto.getFromAccountNumber())).thenReturn(false);

        assertThrows(AccountNotFoundException.class, () -> transactionService.createTransaction(requestDto));
    }

    @Test
    public void createTransaction_shouldThrowIfReceiverAccountNotFound() {
        when(userRepository.existsById(userId)).thenReturn(true);
        when(accountRepository.existsByAccountNumber(requestDto.getFromAccountNumber())).thenReturn(true);
        when(accountRepository.existsByAccountNumber(requestDto.getToAccountNumber())).thenReturn(false);

        assertThrows(AccountNotFoundException.class, () -> transactionService.createTransaction(requestDto));
    }

    @Test
    public void createTransaction_shouldThrowIfInsufficientFunds() {
        Account sender = new Account(UUID.randomUUID(), requestDto.getFromAccountNumber(), userId, 50.0, LocalDateTime.now());

        when(userRepository.existsById(userId)).thenReturn(true);
        when(accountRepository.existsByAccountNumber(requestDto.getFromAccountNumber())).thenReturn(true);
        when(accountRepository.existsByAccountNumber(requestDto.getToAccountNumber())).thenReturn(true);
        when(accountRepository.findByAccountNumber(requestDto.getFromAccountNumber())).thenReturn(Optional.of(sender));

        assertThrows(InsufficientFundsException.class, () -> transactionService.createTransaction(requestDto));
    }


    @Test
    public void getAllTransactionsByAccountNumber_shouldReturnList() {
        String accountNumber = "1111222233334444";

        List<Transaction> transactions = List.of(
                createTransaction(accountNumber, "to1", 50.0),
                createTransaction(accountNumber, "to2", 75.0)
        );

        when(transactionRepository.findByFromAccountNumber(accountNumber)).thenReturn(transactions);

        List<CreateTransactionDto> result = transactionService.getAllTransactionsByAccountNumber(accountNumber);

        assertEquals(2, result.size());
        assertEquals("to1", result.get(0).getToAccountNumber());
        assertEquals(75.0, result.get(1).getAmount());
    }

    private Transaction createTransaction(String from, String to, Double amount) {
        Transaction tx = new Transaction();
        tx.setId(UUID.randomUUID());
        tx.setUserId(UUID.randomUUID());
        tx.setFromAccountNumber(from);
        tx.setToAccountNumber(to);
        tx.setAmount(amount);
        tx.setTimestamp(LocalDateTime.now());
        return tx;
    }
}
