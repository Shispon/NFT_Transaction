package org.testing.p2p_transaction.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.testing.p2p_transaction.entity.Transaction;

import java.util.List;
import java.util.UUID;

@Repository
public interface TransactionRepository extends CrudRepository<Transaction, UUID> {
    List<Transaction> findByFromAccountNumber(String fromAccountNumber);
}
