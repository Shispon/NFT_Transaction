package org.testing.nft_transaction.repository;

import org.springframework.data.repository.CrudRepository;
import org.testing.nft_transaction.entity.User;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends CrudRepository<User, UUID> {
    Optional<User> findByUserName(String userName);
}
