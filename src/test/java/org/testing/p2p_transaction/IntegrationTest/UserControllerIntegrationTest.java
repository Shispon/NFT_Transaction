package org.testing.p2p_transaction.IntegrationTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.testing.p2p_transaction.dto.RequestTransactionDto;
import org.testing.p2p_transaction.dto.UserRegistrationDto;
import org.testing.p2p_transaction.entity.Account;
import org.testing.p2p_transaction.entity.User;
import org.testing.p2p_transaction.repository.AccountRepository;
import org.testing.p2p_transaction.repository.TransactionRepository;
import org.testing.p2p_transaction.repository.UserRepository;

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@WithMockUser(username = "alex")
class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @BeforeEach
    void setUp() {
        transactionRepository.deleteAll();
        accountRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void testRegisterUser_Success() throws Exception {
        // Создаём DTO запроса
        UserRegistrationDto request = new UserRegistrationDto();
        request.setFullName("Иван Иванов");
        request.setMail("ivan@example.com");
        request.setUserName("ivan123");
        request.setPassword("123456");

        mockMvc.perform(post("/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName").value("Иван Иванов"))
                .andExpect(jsonPath("$.mail").value("ivan@example.com"))  // ← исправлено с "email" на "mail"
                .andExpect(jsonPath("$.userName").value("ivan123"));
    }

    @Test
    void testCreateTransaction_Success() throws Exception {
        // Шаг 1: создаём пользователя (без установки ID вручную)
        User user = new User();
        user.setFullName("Алексей");
        user.setMail("alex@example.com");
        user.setUserName("alex");
        user.setPassword("123456");
        user.setCreatedAt(LocalDateTime.now());
        user = userRepository.save(user); // получаем сгенерированный ID

        // Шаг 2: создаём отправителя
        Account sender = new Account();
        sender.setAccountNumber("SENDER123");
        sender.setBalance(200.0);
        sender.setUserId(user.getId());
        sender.setCreatedAt(LocalDateTime.now());
        accountRepository.save(sender);

        // Шаг 3: создаём получателя
        Account receiver = new Account();
        receiver.setAccountNumber("RECEIVER456");
        receiver.setBalance(100.0);
        receiver.setUserId(user.getId());
        receiver.setCreatedAt(LocalDateTime.now());
        accountRepository.save(receiver);

        // Шаг 4: готовим тело запроса
        RequestTransactionDto request = new RequestTransactionDto();
        request.setFromAccountNumber("SENDER123");
        request.setToAccountNumber("RECEIVER456");
        request.setAmount(50.0);
        request.setUserId(user.getId());

        // Шаг 5: отправляем запрос
        mockMvc.perform(post("/user/createTransaction")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.amount").value(50.0))
                .andExpect(jsonPath("$.fromAccountNumber").value("SENDER123"))
                .andExpect(jsonPath("$.toAccountNumber").value("RECEIVER456"));
    }


    @Test
    void testCreateTransaction_InsufficientFunds() throws Exception {
        // Создание пользователя
        User user = new User();
        user.setFullName("Недостаточно Средств");
        user.setMail("fail@example.com");
        user.setUserName("lowbalanceuser");
        user.setPassword("password123");
        user.setCreatedAt(LocalDateTime.now());
        user = userRepository.save(user);

        // Создание счёта отправителя с низким балансом
        Account sender = new Account();
        sender.setAccountNumber("LOWBAL123");
        sender.setBalance(10.0); // недостаточно
        sender.setUserId(user.getId());
        sender.setCreatedAt(LocalDateTime.now());
        accountRepository.save(sender);

        // Создание счёта получателя
        Account receiver = new Account();
        receiver.setAccountNumber("RECV999");
        receiver.setBalance(100.0);
        receiver.setUserId(user.getId());
        receiver.setCreatedAt(LocalDateTime.now());
        accountRepository.save(receiver);

        // Формируем DTO запроса
        RequestTransactionDto request = new RequestTransactionDto();
        request.setFromAccountNumber("LOWBAL123");
        request.setToAccountNumber("RECV999");
        request.setAmount(100.0); // больше, чем есть
        request.setUserId(user.getId());

        // Выполняем запрос и проверяем статус и сообщение
        mockMvc.perform(post("/user/createTransaction")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Ошибка транзакции")));
    }

}


