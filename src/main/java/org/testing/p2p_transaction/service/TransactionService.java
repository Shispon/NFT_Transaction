package org.testing.p2p_transaction.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Сервис для обработки транзакций между счетами пользователей.
 */
@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final AccountRepository accountRepository;

    /**
     * Создает новую транзакцию между двумя счетами.
     *
     * @param createTransactionDto объект с данными транзакции: ID пользователя, номер счета отправителя,
     *                             номер счета получателя и сумма перевода.
     * @return объект {@link CreateTransactionDto} с деталями созданной транзакции.
     *
     * @throws UserNotFoundException если пользователь с указанным ID не найден.
     * @throws AccountNotFoundException если один из указанных счетов не найден.
     * @throws InsufficientFundsException если на счете отправителя недостаточно средств.
     */
    @Transactional
    public CreateTransactionDto createTransaction(RequestTransactionDto createTransactionDto) {

        if (!userRepository.existsById(createTransactionDto.getUserId())) {
            throw new UserNotFoundException(createTransactionDto.getUserId());
        }

        String fromAccount = createTransactionDto.getFromAccountNumber();
        String toAccount = createTransactionDto.getToAccountNumber();

        if (!accountRepository.existsByAccountNumber(fromAccount)) {
            throw new AccountNotFoundException(fromAccount);
        }
        if (!accountRepository.existsByAccountNumber(toAccount)) {
            throw new AccountNotFoundException(toAccount);
        }

        Account senderAccount = accountRepository.findByAccountNumber(fromAccount)
                .orElseThrow(() -> new AccountNotFoundException(fromAccount));

        double transferAmount = createTransactionDto.getAmount();
        if (senderAccount.getBalance() < transferAmount) {
            throw new InsufficientFundsException(fromAccount);
        }

        Account receiverAccount = accountRepository.findByAccountNumber(toAccount)
                .orElseThrow(() -> new AccountNotFoundException(toAccount));

        senderAccount.setBalance(senderAccount.getBalance() - transferAmount);
        receiverAccount.setBalance(receiverAccount.getBalance() + transferAmount);

        accountRepository.save(senderAccount);
        accountRepository.save(receiverAccount);

        Transaction transaction = new Transaction();
        transaction.setUserId(createTransactionDto.getUserId());
        transaction.setFromAccountNumber(fromAccount);
        transaction.setToAccountNumber(toAccount);
        transaction.setAmount(transferAmount);
        transaction.setTimestamp(LocalDateTime.now());

        transactionRepository.save(transaction);

        return new CreateTransactionDto(
                transaction.getFromAccountNumber(),
                transaction.getToAccountNumber(),
                transaction.getAmount()
        );
    }

    /**
     * Возвращает список всех транзакций, совершённых с указанного счета.
     *
     * @param accountNumber номер счета, по которому нужно получить список транзакций.
     * @return список объектов {@link CreateTransactionDto}, описывающих транзакции.
     */
    public List<CreateTransactionDto> getAllTransactionsByAccountNumber(String accountNumber) {
        List<Transaction> transactions = transactionRepository.findByFromAccountNumber(accountNumber);
        return transactions.stream()
                .map(transaction -> new CreateTransactionDto(
                        transaction.getFromAccountNumber(),
                        transaction.getToAccountNumber(),
                        transaction.getAmount()))
                .collect(Collectors.toList());
    }
}


