package org.testing.p2p_transaction.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.testing.p2p_transaction.entity.Account;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
@Repository
public interface AccountRepository extends CrudRepository<Account, UUID> {
    boolean existsByAccountNumber(String accountNumber);
    List<Account> findByUserId(UUID userId);
    Optional<Account> findByAccountNumber(String accountNumber);
}
